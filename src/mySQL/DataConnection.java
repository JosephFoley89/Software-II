package mySQL;

import java.sql.Connection;
import java.sql.DriverManager;

/**Class used to establish mySQL server connectivity
 * @author Joe Foley*/
public class DataConnection {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcURL = protocol + vendor + location + databaseName;
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String username = "sqlUser";
    private static String password = "Passw0rd!";
    private static Connection connection;

    /**This method is responsible for opening the connection*/
    public static void openConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("CONNECTED");
        } catch (Exception e) {
            System.out.println("FAILED TO CONNECT. - " + e.toString());
        }
    }

    /**This method is responsible for closing the connection. It is not used.*/
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("DISCONNECTED");
        } catch (Exception e) {
            System.out.println("FAILED TO CLOSE CONNECTION. - " + e.toString());
        }
    }
    /**This method is responsbile for getting the connection and is used frequently
     * when submitting queries to the mySQL server.*/
    public static Connection getConnection() { return connection; }

}
