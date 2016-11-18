package DataBase;
/**
 * Mysql Utilities
 *
 * @author Ralph Ritoch <rritoch@gmail.com>
 * @copyright Ralph Ritoch 2011 ALL RIGHTS RESERVED
 * @link http://www.vnetpublishing.com
 * correction by Vitalij Ivanovskij 2016
 */



public class MySQLUtils {

    /**
     * Escape string to protected against SQL Injection
     * <p/>
     * You must add a single quote ' around the result of this function for data, or a
     * backtick ` around table and row identifiers. If this function returns null than the
     * result should be changed to "NULL" without any quote or backtick.
     *
     * @param str
     * @return
     * @throws Exception
     */

    public static String mysql_real_escape_string(String str) {
        if (str == null) {
            return null;
        }

        if (str.replaceAll("[а-яА-ЯёЁa-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]", "").length() < 1) {
            return str;
        }

        String clean_string = str;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\n", "\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");
        clean_string = clean_string.replaceAll("'", "\\\\'");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");

        if (clean_string.replaceAll("[а-яА-ЯёЁa-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]", "").length() < 1) {
            return clean_string;
        }
        return null;
    }

    /**
     * Escape data to protected against SQL Injection
     *
     * @param str
     * @return
     * @throws Exception
     */

    public static String quote(String str) {
        if (str == null) {
            return "NULL";
        }


        return "'" + mysql_real_escape_string(str) + "'";
    }
}
