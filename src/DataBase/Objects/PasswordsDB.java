package DataBase.Objects;

import DataBase.DB;
import MyUtils.AssocItem;

import java.sql.SQLException;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Виталий on 09.10.2016.
 */
public class PasswordsDB {
    private static String tableName = "passwords";
    private String userID;
    private SharedObjectDB shDB;
    private UserDB userDB;

    public PasswordsDB(String userID)throws ClassNotFoundException, SQLException{
        this.userID = userID;
        shDB = new SharedObjectDB(DB.getDB(), tableName);
        userDB = new UserDB();
    }



    public void setUser(String name){
        userID = this.userDB.getUserID(name);
    }



    public Integer createNewSource(String source, String login, String password, String note){

        AssocItem usID = new AssocItem("user_id", userID);
        AssocItem newSource = new AssocItem("source", source);
       // System.out.println(newSource.getKey() + ": " + newSource.getValue());

        Vector<AssocItem> row = new Vector<AssocItem>();
        row.add(usID);
        row.add(newSource);

        shDB.insertNewRow(row);
        return updateSource(source, login, password, note);
    }

    public void deleteSource(String source){
        AssocItem sourceField = new AssocItem("source", source);
        AssocItem userIDfield = new AssocItem("user_id", userID);
        Vector<AssocItem> row = new Vector<AssocItem>();
        row.add(sourceField);
        row.add(userIDfield);
        Vector<AssocItem> searchRow;
        searchRow = shDB.getRowByFields(row);
        if(searchRow == null) {
            System.out.println("sharedObjectDB: getRowByFields returned null");
        }
        else {
            AssocItem deleteBySourceID = new AssocItem();
            for (AssocItem ai : searchRow) {
                if (ai.getKey().equals("id")) {
                    deleteBySourceID = ai;
                    break;
                }
            }
            shDB.deleteRowByField(deleteBySourceID);
        }
    }

    public String getPassword(String source){
        return shDB.getCell(source, "password", userID);
    }

    public String getLogin(String source){
        return shDB.getCell(source, "login", userID);
    }

    public String getNote(String source){
        return shDB.getCell(source, "note", userID);
    }

    public String getUserID(){return userID;}

    public Integer updateSource(String source, String login, String password, String note){

        AssocItem newPassword = new AssocItem("password", password);
        AssocItem newLogin = new AssocItem("login", login);
        AssocItem newNote = new AssocItem("note", note);
        Date currentDate = new Date();
        long currDateMilliSeconds = currentDate.getTime(); //делим на 1000 т.к. getTime() возвр. long в миллисекундах
        currDateMilliSeconds /= 1000;
        int currDateSec = (int) currDateMilliSeconds;
        String currDateString = Integer.toString(currDateSec);
        AssocItem creatingDate = new AssocItem("create_date", currDateString);

        Vector<AssocItem> row = new Vector<AssocItem>();
        row.add(newPassword);
        row.add(newLogin);
        row.add(newNote);
        row.add(creatingDate);


        AssocItem field = new AssocItem("source", source);
        return shDB.updateRowByField(row, field);
    }

    public Vector<String> getSources(){
        return shDB.getColumnByUserID("source", userID);
    }


}
