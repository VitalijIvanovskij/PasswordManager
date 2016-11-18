package DataBase.Objects;

import DataBase.Select;
import DataBase.DB;
import MyUtils.AssocItem;
import lib.Configuration;


import java.sql.SQLException;
import java.util.Vector;

/**
 * Created by Виталий on 06.10.2016.
 */
public class UserDB {
    private SharedObjectDB sharedObjectDB = null;
    private String userID = null;
    private static String tableName = "users";

    public UserDB()throws SQLException, ClassNotFoundException{
        SharedObjectDB shDB = new SharedObjectDB(DB.getDB(), tableName);
        this.sharedObjectDB = shDB;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        UserDB test = new UserDB();
        for (int i = 1; i <= 10; i++) {
            String name = "user" + Integer.toString(i);
            test.createAccount(name, "1111111");
        }
    }

    public boolean hasName(String name){

        Select select = new Select(sharedObjectDB.db);
        Vector<String> fromParams = new Vector<String>();
        fromParams.add("name");
        select = select.from(sharedObjectDB.tableName, fromParams);
        Vector<String> whereParams = new Vector<String>();
        whereParams.add(name);
        select = select.where("name = " + sharedObjectDB.db.getSQ(), whereParams);
        Vector<AssocItem> row = sharedObjectDB.db.selectRow(select);
        if(row != null) return true;
        else return false;
    }
//---------------------------PASSWORD-----------------------------------------------
    public String getPassword(String name){
        return sharedObjectDB.getValueOnField("password", "name", name);
    }

    public void setPassword(String name, String password){
        AssocItem nameField = new AssocItem("name", name);
        AssocItem passwordField = new AssocItem("password", password);
        sharedObjectDB.setNewValueByField(nameField, passwordField);
    }

//--------------------------------------------------------------------------------
    public boolean authUser(String name, String password){
        if(hasName(name) && (getPassword(name).equals(sharedObjectDB.md5(password)))) {
            userID = sharedObjectDB.getValueOnField("id", "name", name);
            return true;
        }
        else return false;
    }

    public void logout(){
        userID = null;
    }

    public boolean isAuth(){
        return (userID != null)? true : false;
    }

    public Integer createAccount(String login, String password){
        if(hasName(login)) {
            System.out.println("This login is exist");
            return Configuration.USERNAME_EXISTS;   //если такой логин уже существует возвращаем -1
        }
        else{
            Vector<AssocItem> row = new Vector<AssocItem>(2);
            row.add(new AssocItem("name", login));
            password = sharedObjectDB.md5(password);
            row.add(new AssocItem("password", password));
            return sharedObjectDB.insertNewRow(row);
        }
    }

    public void deleteAccount(String name){
        AssocItem nameAI = new AssocItem("name", name);
        sharedObjectDB.deleteRowByField(nameAI);
    }

    public String getUserID(String name){
        Vector<AssocItem> row;
        row = sharedObjectDB.getRowOnFieldValue("name", name);
        for(AssocItem ai : row){
            if(ai.getKey().equals("id")) {
                return ai.getValue();
            }
        }
        return null;
    }

    public Vector<String> getNames(){
        Vector<String> names = new Vector<String>();
        names = sharedObjectDB.getColumnByName("name");
        return names;
    }

}
