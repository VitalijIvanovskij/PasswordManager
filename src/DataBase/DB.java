package DataBase;

import MyUtils.AssocItem;
import lib.Configuration;

import java.sql.*;
import java.util.*;


/**
 * Singleton class for working with SQLite DataBase
 * @author V.Ivanovskij
 * @version 1.0
 *
 */
public class DB {
    /**
     *@param statement
     *@param tablePrefix - prefix of DataBase table
     *@param symQueryReplaceable - symbol which will be replace in query string
     * */
    private static DB db;
    private Statement statement;
    private String tablePrefix = Configuration.TABLE_PREFIX;
    private String symQueryReplaceable = Configuration.SYM_QUERY_STRING;

    private DB()throws ClassNotFoundException, SQLException {
        Class.forName(Configuration.SQL_DRIVER);
        Connection connection = DriverManager.getConnection(Configuration.CONNECT_PREFIX + Configuration.DB_PATH);
        statement = connection.createStatement();

        String createTableUsersQuery = "CREATE TABLE if NOT EXISTS "
                + tablePrefix +"users (id INTEGER PRIMARY KEY, " +
                                        "name TEXT, " +
                                        "password TEXT, " +
                                        "UNIQUE(name))";

        String createTablePasswordsQuery = "CREATE TABLE if NOT EXISTS "
                + tablePrefix +"passwords (id INTEGER PRIMARY KEY, " +
                                            "user_id TEXT, " +
                                            "source TEXT, " +
                                            "login TEXT, " +
                                            "password TEXT, " +
                                            "note TEXT, " +
                                            "create_date TEXT)";


        statement.execute(createTableUsersQuery);
        statement.execute(createTablePasswordsQuery);
    }


    public static DB getDB()throws ClassNotFoundException, SQLException{
        if (db == null) db = new DB();
        return db;
    }


    public Statement getStatement() {
        return statement;
    }

    public String getSQ(){return symQueryReplaceable;}

    /**
     * @param tableName - name without prefix
     * table name in database built-up from prefix + tablename
     */
    public String getTableName(String tableName){
        return tablePrefix + tableName;
    }






//-------------------SELECT METHODS--------------------------------------------

    public Vector<Vector<AssocItem>> select(Select select){
        try {
            Vector<Vector<AssocItem>> resultVector = getResultVector(select, true, true);
            return resultVector;
        } catch (SQLException e) {
            return null;
        }
    }

    public Vector<AssocItem> selectRow(Select select){
        try {
            Vector<Vector<AssocItem>> resultVector = getResultVector(select, false, true);

            if (resultVector == null) return null;
            return resultVector.get(0);
        } catch (SQLException e) {
            return null;
        }
    }

    public Vector<String> selectCol(Select select){
        try {
            Vector<Vector<AssocItem>> resultVector = getResultVector(select, true, true);
            Vector<String> result = new Vector<String>();
            for(Vector<AssocItem> vectorAI : resultVector) {
                for(AssocItem ai : vectorAI){
                    result.add(ai.getValue());
                    break;
                }
            }
            return result;
        } catch (SQLException e) {
            return null;
        }
    }

   public String selectCell(Select select){
       try {
           Vector<Vector<AssocItem>> assocVector = getResultVector(select, false, true);
           String resultString = assocVector.firstElement().firstElement().getValue();
           return resultString;
       } catch (SQLException e) {
           return null;
       }
   }

//-------------------------INSERT-----------------------------------------------------
    public Integer insert(String tableName, Vector<AssocItem> row){

        int rowSize = row.size();
        if(rowSize == 0) return null;

        String tblName = getTableName(tableName);
        String fields = "(";
        String values = " VALUES(";
        Vector<String> params = new Vector<String>();
        for(int i = 0; i < rowSize ; i++){
            fields += "`" + row.get(i).getKey() + "`, ";
            values += getSQ() + ", ";
            params.add(row.get(i).getValue());
        }
        fields = fields.substring(0, fields.length() - 2);
        values = values.substring(0, values.length() - 2);
        fields += ")";
        values += ")";
        String query = "INSERT INTO `" + tblName + "`" + fields + values;

        return query(query, params);
    }

//-----------------------UPDATE-------------------------------------------------------
    /**
     * @param tblName table name from data base
     * @param row list of update fields
     * @param where
     * @param params new values
     * */
    public Integer update(String tblName, Vector<AssocItem> row, String where, Vector<String> paramsWhere){

        if(row.size() == 0) return -1;
        String tableName = getTableName(tblName);
        String query = "UPDATE `" + tableName + "` SET ";
        Vector<String> paramsAdd = new Vector<String>();
        for(AssocItem ai : row){
            query += "`" + ai.getKey() + "` = " + getSQ() + ", ";
            paramsAdd.add(ai.getValue());
        }
        query = query.substring(0, query.length() - 2);
        if(where != null){
            for(String s : paramsWhere)
                paramsAdd.add(s);
            query += " WHERE " + where;
            query = getQuery(query, paramsAdd);
        }
        return query(query, paramsAdd);
    }
//---------------------DELETE---------------------------------------------------------
    public void delete(String tableName, String where, String valueOfWhere){
        String tblName = getTableName(tableName);
        String query = "DELETE FROM `" + tblName + "`";
        Vector<String> params = new Vector<String>();
        if (where != null) {
            query += " WHERE " + where;
            params.add(valueOfWhere);
        }
        try {
            statement.execute(getQuery(query, params));
        } catch (SQLException e) {
            System.out.println("Ошибка удаления!");
        }
    }
//---------------------PRIVATE METHODS---------------------------------------------------------

    //@return набор rows, состоящих из  , хранящей ключ(имя столбца)->его значение
    private Vector<Vector<AssocItem>> getResultVector(Select select, boolean zero, boolean one) throws SQLException{
        ResultSet resultSet = statement.executeQuery(select.toString());
        if(resultSet == null) return null;
        Vector<Vector<AssocItem>> resultVector = new Vector<Vector<AssocItem>>();
        while(resultSet.next()){
            resultVector.add(fetchAssoc(resultSet));
        }

        resultSet.close();
        if(!zero && resultVector.size() == 0) return null;
        if(!one && resultVector.size() == 1) return null;
        return resultVector;
    }

    /*
     * Аналог mysqli->fetch_assoc в PHP
     * только возвращает вектор наборов ключ->значение
     *
     */
    private Vector<AssocItem> fetchAssoc(ResultSet resultSet)throws SQLException{
        Vector<AssocItem> resultVector = new Vector<AssocItem>();
        String key;
        String value;

        int columnNum = resultSet.getMetaData().getColumnCount();
        for(int i = 1; i <= columnNum ; i++) {
            key = resultSet.getMetaData().getColumnName(i);
            value = resultSet.getString(i);
            AssocItem assocItem = new AssocItem(key, value);
            resultVector.add(assocItem);
        }

        return resultVector;
    }

    /**
     * @param query - query string
     * @param params
     * @return last insert record id or -1 if query not insert records
     * */
    private Integer query(String query, Vector<String> params){

        try {
            boolean success = statement.execute(getQuery(query, params));
            if(success) {
                ResultSet resultSet = statement.executeQuery("SELECT last_insert_rowid()");
                int lastInsertId = resultSet.getInt(Configuration.ID_COLUMN_NUMBER);
                resultSet.close();
                return lastInsertId;
            }
            else{
                return null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * @param query - query string with "?" for replacement params
     * @param params - set of params which will be replace "?" in query
     * @return protected query string against SQL Injections
     * */
    public String getQuery(String query, Vector<String> params){
        if(params.size() != 0){
            for (int i = 0; i < params.size(); i++) {
                String s = params.get(i);
                String arg = "NULL";
                if (s != null) {
                    arg = MySQLUtils.quote(s);
                }

                String regExpSymQueryReplaceable = "\\" + symQueryReplaceable;
                query = query.replaceFirst(regExpSymQueryReplaceable, arg);
            }
        }
       //System.out.println(query);
        return query;
    }


}