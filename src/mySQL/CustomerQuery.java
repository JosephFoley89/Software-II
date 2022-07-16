package mySQL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**Class used to query the mySQL server about customers.
 * @author Joe Foley*/
public class CustomerQuery {
    private static PreparedStatement statement;
    private static DataConnection connection;

    /**Method responsible for creating customers*/
    public static boolean createCustomer(String name, String phone, String address, String division, String postalCode) throws SQLException {
        statement = connection.getConnection().prepareStatement(
            String.format("INSERT INTO customers (Customer_Name,Phone,Address,Division_ID,Postal_Code) VALUES(\"%s\", \"%s\", \"%s\", ?, \"%s\")",
            name,phone,address,postalCode)
        );
        statement.setInt(1, GeneralQuery.getDivisionIDByName(division));

        try {
            statement.execute();
            if (statement.getUpdateCount() > 0) { return true; }
            else { return false; }
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**This method is responsible for updating customer records*/
    public static boolean customerUpdated(String name, String phone, String address, String division, String postalCode, int ID) throws SQLException {
        statement = connection.getConnection().prepareStatement(
            String.format("UPDATE customers SET Customer_Name=\"%s\", Phone=\"%s\", Address=\"%s\", Division_ID=?, Postal_Code=\"%s\" WHERE Customer_ID=?",
            name,phone,address,postalCode)
        );
        statement.setInt(1, GeneralQuery.getDivisionIDByName(division));
        statement.setInt(2, ID);

        try {
            statement.execute();
            if (statement.getUpdateCount() > 0) { return true; }
            else { return false; }
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**This method checks to confirm whether a customer already exists or not*/
    public static boolean customerDoesNotExist(String name, String phone, String address, String division, String postalCode) throws SQLException {
        statement = connection.getConnection().prepareStatement(
            String.format("SELECT * FROM customers WHERE Customer_Name=\"%s\" AND Phone=\"%s\" AND Address=\"%s\" AND Division_ID=? AND Postal_Code=\"%s\"",
            name, phone, address, postalCode)
        );
        statement.setInt(1, GeneralQuery.getDivisionIDByName(division));

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

            if (set.next()) { return false; }
            else { return true; }
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }
}
