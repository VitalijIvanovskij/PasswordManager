package DataBase;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Виталий on 28.09.2016.
 * @author V.Ivanovskij
 *
 * This class create SELECT query string
 *
 *
 * @param db DataBase;
 * @param from element of SELECT query
 * @param where element of SELECT query
 * @param order element of SELECT query
 * @param limit element of SELECT query
 *
 */
public class Select {
    private DB db;
    private String from = "";
    private String where = "";
    private String order = "";
    private String limit = "";



    public Select(DB db){
        this.db = db;
    }

    /**
     * @param tableName имя необходимой таблицы
     * @param fields набор необходимых полей из таблицы
     * @return method возвращает объект select
     */
    public Select from(String tableName, Vector<String> fields){

        tableName = db.getTableName(tableName);
        String from = "";
        if (fields.get(0) == "*")
            from = "*";
        else {
            for (String s : fields) {
                //проверяем, если параметр является функцией, например COUNT(ID),
                //то приводим к виду COUNT(`ID`)
                int pos1 = s.indexOf("(");
                if (pos1 != -1) {
                    int pos2 = s.indexOf(")");
                    from += s.substring(0, pos1) + "(`" + s.substring(pos1 + 1, pos2) + "`)";
                } else from += "`" + s + "`,";
            }
            from = from.substring(0, from.length() - 1);
        }
        from += " FROM `" + tableName + "`";
        this.from = from;
        return this;
    }

    /**
     * overload method
     * @param where query given by user
     * @param values kit of args given by user
     *
     * */
    public Select where(String where, Vector<String> values, boolean and){
        if(where != null){
            where = this.db.getQuery(where, values);
            addWhere(where, and);
        }
        return this;
    }

    public Select where(String where, Vector<String> values){
        if(where != null){
            where = this.db.getQuery(where, values);
            addWhere(where, true);
        }
        return this;
    }

    public Select where(String where){
        if(where != null){
            Vector<String> emptyVector = new Vector<String>();
            where = this.db.getQuery(where, emptyVector);
            addWhere(where, true);
        }
        return this;
    }

    /**
     * method adds WHERE, OR or AND to query string
     * */
    private void addWhere(String where, boolean and){
        if (this.where != ""){
            if (and) this.where += " AND ";
            else this.where += " OR ";
            this.where += where;
        }
        else this.where = "WHERE " + where;
    }

    public String getWhere(){
        return this.where;
    }

    /**
     * overload method creates query string for search value in column from database
     * @param colName - column from database
     * @param value
     * */
    public Select whereFIS(String colName, String value, boolean and){
        String where = "FIND_IN_SET (" + db.getSQ() + ", `" + colName + "`) > 0";
        Vector<String> vectorValue = new Vector<String>();
        vectorValue.add(value);
        return this.where(where, vectorValue, and);
    }

    public Select whereFIS(String colName, String value){
        String where = "FIND_IN_SET (" + db.getSQ() + ", `" + colName + "`) > 0";
        Vector<String> vectorValue = new Vector<String>();
        vectorValue.add(value);
        return this.where(where, vectorValue, true);
    }

    /**
     * метод для поиска записей с полем field равным значениям values напр. `id` IN (3, 5, 7)
     * @param field - field from database
     * @param values - array of values
     * */
    public Select whereIN(String field, Vector<String> values, boolean and){
        return where(whereINOverload(field, values), values, and);
    }
    public Select whereIN(String field, Vector<String> values){

        return where(whereINOverload(field, values), values, true);
    }

    private String whereINOverload(String field, Vector<String> values){
        String where = "`" + field + "` IN (";
        for(String value : values){
            where += db.getSQ() + ",";
        }
        where = where.substring(0, where.length() - 1);
        where += ")";
        return where;
    }

    /**
     * @param field - field on which will be order result set
     * @param ask - sort by ascend or descend
     * */
    public Select order(Object field, Object ask){
        if(field.getClass().equals(String[].class)){
            String[] arrField = (String[])field; //приведение Object к String[]

            order = "ORDER BY ";
            if(!ask.getClass().equals(Boolean[].class)){
                ArrayList<Boolean> temp = new ArrayList<Boolean>();
                for(String f : arrField ) temp.add((Boolean)ask);

                //вспомогательный массив, если в ask передаётся
                //один элемент, то создаётся этот массив с кол-
                //личеством элементов равным массиву field
                //и заполняется значениями ask
                Boolean[] arrAsk = new Boolean[temp.size()];
                arrAsk = temp.toArray(arrAsk);
                ask = arrAsk;
            }
            for(int i = 0; i < arrField.length; i++){
                Boolean[] arrAsk = (Boolean[])ask;
                order += "`" + arrField[i] + "`";
                if(arrAsk[i] != true) order += " DESC, ";
                else order += ", ";
            }
            order = order.substring(0, order.length() - 2);
        }
        else {
            String strField = (String) field;
            Boolean bAsk = (Boolean)ask;
            order = "`" + strField + "`";
            if(!bAsk) order += " DESC";
        }
        return this;
    }

    public Select limit(int count, int offset){
        if(count < 0 || offset < 0) return null;
        limit = "LIMIT " + Integer.toString(offset)+ ", " + Integer.toString(count);
        return this;
    }

    public Select rand(){
        order = "ORDER BY RAND()";
        return this;
    }

    @Override
    public String toString(){
        String ret = "";
        if(from != "")
            ret = "SELECT " + from + " " + where + " " +
                    order + " " + limit;
        return ret;
    }
}
