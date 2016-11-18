package lib;

/**
 * Created by Виталий on 27.09.2016.
 */
public abstract class Configuration {
    public static final String CONNECT_PREFIX = "jdbc:sqlite:";//имя драйвера БД
    public static final String TABLE_PREFIX = "pass_";  //prefix of database table
    public static final String DB_PATH = "db/PassManager.db";
    public static final String SQL_DRIVER = "org.sqlite.JDBC";
    public static final String SYM_QUERY_STRING = "?";  //symbol which will be replace in query string
    public static final String SECRET = "VaHaHa";  //испльзуется для создания md5 хэш-кода
    public static final String NONE_USER = "0";  //
    public static final Integer USERNAME_EXISTS = -1;  //
    public static final int DEFAULT_NUM_USER = 0;  //
    public static final int ID_COLUMN_NUMBER = 1; //column "id" in tables has to be first;
}
