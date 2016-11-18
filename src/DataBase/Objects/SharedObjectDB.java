package DataBase.Objects;

import DataBase.MySQLUtils;
import DataBase.Select;
import DataBase.DB;
import MyUtils.AssocItem;
import MyUtils.MD5;
import lib.Configuration;

import java.util.Vector;

/**
 * Created by Виталий on 08.10.2016.
 */
public class SharedObjectDB {
    protected static DB db = null;
    protected String tableName = "";

    public SharedObjectDB(DB db, String tableName){
        this.db = db;
        this.tableName = tableName;
    }

    public void setDB(DB db){
        this.db = db;
    }

    public String getValueOnField(String requiredValue, String field, String fieldValue){
        Select select = new Select(db);
        Vector<String> fromParams = new Vector<String>();
        fromParams.add(requiredValue);
        select = select.from(this.tableName, fromParams);
        Vector<String> whereParams = new Vector<String>();
        whereParams.add(fieldValue);
        select = select.where(field + " = ?", whereParams);
        String result = db.selectCell(select);
        return result;
    }

    /**
     * устанавливает полю новое значение
     * @param fieldValue contains name of field and his value
     * @param newValue contains new value
     * */
    public void setNewValueByField(AssocItem fieldValue, AssocItem newValue){
        Vector<AssocItem> row = new Vector<AssocItem>();
        row.add(newValue);
        String field = fieldValue.getKey();
        String value = fieldValue.getValue();
        String where = MySQLUtils.mysql_real_escape_string(field) + " = ";
        value = MySQLUtils.quote(value);
        where += value;
        Vector<String> newParam = new Vector<String>();
        newParam.add(newValue.getValue());
        db.update(tableName, row, where, newParam);
    }

    /**
     * Возвращает строку из БД по полю и его значению
     *
     * */
    public Vector<AssocItem> getRowOnFieldValue(String field, String value){
        Select select = new Select(SharedObjectDB.db);
        Vector<String> paramFrom = new Vector<String>();
        paramFrom.add("*");
        select = select.from(tableName, paramFrom);
        Vector<String> paramWhere = new Vector<String>();
        paramWhere.add(value);

        select = select.where(field + " = ?", paramWhere);
        return db.selectRow(select);
    }

    public Vector<AssocItem> getRowByFields(Vector<AssocItem> fields){
        Select select = new Select(db);
        Vector<String> allFields = new Vector<String>();
        allFields.add("*");
        select = select.from(tableName, allFields);
        String where = "";
        Vector<String> whereValues = new Vector<String>();
        for(AssocItem ai : fields){
            where += ai.getKey() + " = " + db.getSQ() + " AND ";
            whereValues.add(ai.getValue());
        }
        where = where.substring(0, where.length() - 5);
        select = select.where(where, whereValues);
        //System.out.println(select);

        return db.selectRow(select);
    }

    public Vector<AssocItem> getRowOnUserID(String id){
        return getRowOnFieldValue("user_id", id);
    }

    public Vector<String> getColumnByUserID(String columnName, String userID){
        Select select = new Select(db);
        Vector<String> queryField = new Vector<String>();
        queryField.add(columnName);
        select = select.from(tableName, queryField);
        Vector<String> uID = new Vector<String>();
        uID.add(userID);
        select = select.where("user_id = ?", uID);

        return db.selectCol(select);
    }

    public Vector<String> getColumnByName(String columnName){
        Vector<String> colName = new Vector<String>();
        colName.add(columnName);
        Select select = new Select(db);
        select = select.from(tableName, colName);
        return db.selectCol(select);
    }

    //метод возвращает содержимое ячейки
    public String getCell(String source, String cellName, String userID){
        Select select = new Select(db);
        Vector<String> cellField = new Vector<String>();
        cellField.add(cellName);
        select = select.from(tableName, cellField);
        String where = "";
        Vector<String> whereValues = new Vector<String>();
        where += "source = " + db.getSQ() + " AND user_id = " + db.getSQ();
        whereValues.add(source);
        whereValues.add(userID);
        select = select.where(where, whereValues);
       // System.out.println(select);
        return db.selectCell(select);
    }

    public Integer insertNewRow(Vector<AssocItem> items){

        return db.insert(tableName, items);
    }

    public Integer updateRowByField(Vector<AssocItem> items, AssocItem field){

        String where = field.getKey() + " = " + db.getSQ();
        Vector<String> whereValue = new Vector<String>();
        whereValue.add(field.getValue());
        return db.update(tableName, items, where, whereValue);
    }

    public void deleteRowByField(AssocItem field){
        String where = field.getKey() + " = " + db.getSQ();
        db.delete(tableName, where, field.getValue());
    }

    public static String md5(String input){
        return MD5.getMD5(input + Configuration.SECRET);
    }



}
