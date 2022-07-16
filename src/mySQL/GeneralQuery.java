package mySQL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

/**This class is designed to handle general queries to the server.
 * @author Joe Foley*/
public class GeneralQuery {
    private static PreparedStatement statement;
    private static DataConnection connection;

    /**Method for checking whether the user's inputted credentials are valid
     * this is called after the checks on invalid characters and empty text
     * fields are confirmed to not be an issue. */
    public static boolean validateCredentials(String username, String password) throws SQLException {
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM users WHERE User_Name=\"%s\" AND Password=\"%s\"", username, password));
        try {
            statement.execute();
            ResultSet set = statement.getResultSet();
            return (set.next());
        } catch (Exception e){
            System.out.println("Error - " + e.toString());
            return false;
        }
    }

    /**Method for querying the provided table for the provided name
     * to return the selected ID.*/
    public static int getIDByName(String table, String name) throws SQLException {
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM %ss WHERE %s_Name=\"%s\"", table.toLowerCase(Locale.ROOT), table, name));

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

            if (set.next()) {
                return set.getInt(String.format("%s_ID", table));
            }
        } catch (Exception ex) {
            System.out.println(ex + " received in the getIDByName method.");
        }
        return 0;
    }

    /**Method for querying the provided table for the provided ID
     * to return the selected Name*/
    public static String getNameByID(String table, int ID) throws SQLException {
        String divisions = table.contains("division") ? "first_level_" : " ";
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM %s%ss WHERE %s_ID=?", divisions, table.toLowerCase(Locale.ROOT), table));
        statement.setInt(1, ID);

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

            if (set.next()) {
                return table.contains("division") ? set.getString("Division") : set.getString(String.format("%s_Name", table));
            }
        } catch (Exception ex) {
            System.out.println(ex + " received in the getNameByID method.");
        }
        return "failed to find";
    }

    /**Method responsible for retrieving the country based on their DivisionID*/
    public static String getCountryByID(int ID) throws SQLException {
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM first_level_divisions WHERE DIVISION_ID=?"), ID);
        statement.setInt(1, ID);
        try {
            statement.execute();
            ResultSet set = statement.getResultSet();
            if (set.next()) {
                statement = connection.getConnection().prepareStatement("SELECT * FROM countries WHERE Country_ID=?");
                statement.setInt(1, set.getInt("Country_ID"));
                try {
                    statement.execute();
                    ResultSet countrySet = statement.getResultSet();
                    if (countrySet.next()) { return countrySet.getString("Country"); }
                } catch (Exception e) {
                    System.out.println(e + " received in the getCountryByID method");
                }

            }
        } catch (Exception ex) {
            System.out.println(ex + " received in the getCountryByID method");
        }
        return "no country for old men";
    }

    /**Method responsible for deleting a record by the given ID from a given table
     * returns true if successful and false if not. Catches SQLExceptions.*/
    public static boolean deleteByID(String table, int ID) throws SQLException {
        statement = connection.getConnection().prepareStatement(String.format("DELETE FROM %ss WHERE %s_ID=?", table, table));
        statement.setInt(1, ID);

        try {
            statement.execute();
            return statement.getUpdateCount() > 0 ? true : false;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**Method responsible for filtering divisions based on Country*/
    public static ObservableList<Division> getDivisionByCountry(String country) throws SQLException {
        ObservableList<Division> divisions = FXCollections.observableArrayList();
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM countries WHERE Country=\"%s\"", country));
        try {
            statement.execute();
            ResultSet queryRes = statement.getResultSet();
            if (queryRes.next()) {
                statement = connection.getConnection().prepareStatement("SELECT * FROM first_level_divisions WHERE Country_ID=?");
                statement.setInt(1, queryRes.getInt("Country_ID"));

                try {
                    statement.execute();
                    ResultSet set = statement.getResultSet();
                    while (set.next()) {
                        Division division = new Division(
                                set.getInt("Division_ID"),
                                set.getInt("Country_ID"),
                                set.getString("Division")
                        );
                        divisions.add(division);
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
        return divisions;
    }

    /**This method is responsbile for gathering the first level divisions by name.*/
    public static int getDivisionIDByName(String name) throws SQLException {
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM first_level_divisions WHERE Division=\"%s\"", name));

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();
            if (set.next()) { return set.getInt("Division_ID"); }
            else { return 0; }
        } catch (Exception ex) {
            System.out.println(ex);
            return 0;
        }

    }

    /**Method responsible for getting all items from a table
     * The user supplies the table name, the method handles the
     * SQL statement. Then it attempts the query and if successful
     * goes through a switch to generate the appropriate list, i.e.
     * a list of customers or users or countries, etc.
     * otherwise the error is logged.*/
    public static ObservableList getAllFromTable(String table) throws SQLException {
        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM %s", table));
        try {
            statement.execute();
            ResultSet set = statement.getResultSet();
                switch (table) {
                    case "appointments":
                        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
                        while (set.next()) {
                            Appointment appointment = new Appointment(
                                set.getInt("Appointment_ID"),
                                set.getInt("Customer_ID"),
                                set.getInt("User_ID"),
                                set.getInt("Contact_ID"),
                                set.getString("Title"),
                                set.getString("Description"),
                                set.getString("location"),
                                set.getString("Type"),
                                getNameByID("Contact", set.getInt("Contact_ID")),
                                ZonedDateTime.of(
                                    set.getTimestamp("Start").toLocalDateTime().toLocalDate(),
                                    set.getTimestamp("Start").toLocalDateTime().toLocalTime(),
                                    ZoneId.of("UTC")),
                                ZonedDateTime.of(
                                    set.getTimestamp("End").toLocalDateTime().toLocalDate(),
                                    set.getTimestamp("End").toLocalDateTime().toLocalTime(),
                                    ZoneId.of("UTC")),
                                ZonedDateTime.of(
                                    set.getTimestamp("Start").toLocalDateTime().toLocalDate(),
                                    set.getTimestamp("Start").toLocalDateTime().toLocalTime(),
                                    ZoneId.of("UTC"))
                            );
                            appointments.add(appointment);
                        }
                        return appointments;
                    case "contacts":
                        ObservableList<Contact> contacts = FXCollections.observableArrayList();
                        while (set.next()) {
                            Contact contact = new Contact(
                                set.getInt("Contact_ID"),
                                set.getString("Contact_Name"),
                                set.getString("Email")
                            );
                            contacts.add(contact);
                        }
                        return contacts;
                    case "countries":
                        ObservableList<Country> countries = FXCollections.observableArrayList();
                        while (set.next()) {
                            Country country = new Country(
                                set.getInt("Country_ID"),
                                set.getString("Country")
                            );
                            countries.add(country);
                        }
                        return countries;
                    case "customers":
                        ObservableList<Customer> customers = FXCollections.observableArrayList();
                        while (set.next()) {
                            Customer customer = new Customer(
                                set.getInt("Customer_ID"),
                                set.getInt("Division_ID"),
                                set.getString("Customer_Name"),
                                set.getString("Address"),
                                set.getString("Phone"),
                                set.getString("Postal_Code"),
                                getNameByID("division", set.getInt("Division_ID")),
                                getCountryByID(set.getInt("Division_ID"))
                            );
                            customers.add(customer);
                        }
                        return customers;
                    case "first_level_divisions":
                        ObservableList<Division> divisions = FXCollections.observableArrayList();
                        while (set.next()) {
                            Division division = new Division(
                                set.getInt("Division_ID"),
                                set.getInt("Country_ID"),
                                set.getString("Division")
                            );
                            divisions.add(division);
                        }
                        return divisions;
                    case "users":
                        ObservableList<User> users = FXCollections.observableArrayList();
                        while (set.next()) {
                            User user = new User (
                                set.getInt("User_ID"),
                                set.getString("User_Name"),
                                set.getString("Password")
                            );
                            users.add(user);
                        }
                        return users;
                }
            } catch (Exception ex) {
                System.out.println(ex + " received in the getAllFromTable method.");
            }
        return null;
    }
}
