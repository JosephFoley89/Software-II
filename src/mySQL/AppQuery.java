package mySQL;

import Helper.Message;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**Class used to query the mySQL server about appointments
 *@author Joe Foley*/
public class AppQuery {
    private static PreparedStatement statement;
    private static DataConnection connection;
    private static Message message = Message.getInstance();

    /**This method creates new appointments using the data in the form. Additionally, this is responsible for converting localtime to UTC prior to saving it to the server.*/
    public static boolean createAppointment(String customerName, String userName, String contactName, String title, String description, String location, String type, ZonedDateTime start, ZonedDateTime end, LocalDate date) throws SQLException {
        int contactID = GeneralQuery.getIDByName("Contact", contactName);
        int customerID = GeneralQuery.getIDByName("Customer", customerName);
        int userID = GeneralQuery.getIDByName("User", userName);

        statement = connection.getConnection().prepareStatement(
            String.format("INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, Contact_ID, User_ID) VALUES(\"%s\", \"%s\", \"%s\", \"%s\", ?, ?, ?, ?, ?)",
            title, description, location, type)
        );

        ZonedDateTime zStart = ZonedDateTime.of(start.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), start.toInstant().atZone(ZoneId.of("UTC")).toLocalTime(), ZoneId.of("UTC"));
        ZonedDateTime zEnd = ZonedDateTime.of(end.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), end.toInstant().atZone(ZoneId.of("UTC")).toLocalTime(), ZoneId.of("UTC"));

        statement.setTimestamp(1, Timestamp.valueOf(zStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        statement.setTimestamp(2, Timestamp.valueOf(zEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        statement.setInt(3, customerID);
        statement.setInt(4, contactID);
        statement.setInt(5, userID);

        try {
            statement.execute();
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**This method is responsible for updating appointment records by ID. Additionally, it will convert the time to UTC prior to submitting to the server.*/
    public static boolean updateAppointment(String customerName, String userName, String contactName, String title, String description, String location, String type, ZonedDateTime start, ZonedDateTime end, int ID) throws SQLException {
        int contactID = GeneralQuery.getIDByName("Contact", contactName);
        int customerID = GeneralQuery.getIDByName("Customer", customerName);
        int userID = GeneralQuery.getIDByName("User", userName);

        statement = connection.getConnection().prepareStatement(
            String.format("UPDATE appointments SET Title=\"%s\", Description=\"%s\", Location=\"%s\", Type=\"%s\", Start=?, End=?, Customer_ID=?, Contact_ID=?, User_ID=? WHERE Appointment_ID=?", title, description, location, type)
        );

        ZonedDateTime zStart = ZonedDateTime.of(start.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), start.toInstant().atZone(ZoneId.of("UTC")).toLocalTime(), ZoneId.of("UTC"));
        ZonedDateTime zEnd = ZonedDateTime.of(end.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), end.toInstant().atZone(ZoneId.of("UTC")).toLocalTime(), ZoneId.of("UTC"));

        statement.setTimestamp(1, Timestamp.valueOf(zStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        statement.setTimestamp(2, Timestamp.valueOf(zEnd.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        statement.setInt(3, customerID);
        statement.setInt(4, contactID);
        statement.setInt(5, userID);
        statement.setInt(6, ID);

        try {
            statement.execute();
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**This method is responsible for checking to confirm if a customer is available.
     * If the customer has a start or end date*/
    public static boolean customerAvailable(String customerName, ZonedDateTime start, ZonedDateTime end, int appointmentID, String action) throws SQLException {
        int customer = GeneralQuery.getIDByName("customer", customerName);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ZonedDateTime zStart = ZonedDateTime.of(start.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), start.toInstant().atZone(ZoneId.of("UTC")).toLocalTime(), ZoneId.of("UTC"));
        ZonedDateTime zEnd = ZonedDateTime.of(end.toInstant().atZone(ZoneId.of("UTC")).toLocalDate(), end.toInstant().atZone(ZoneId.of("UTC")).toLocalTime(), ZoneId.of("UTC"));

        if (action == "update") {
            statement = connection.getConnection().prepareStatement("SELECT * FROM appointments WHERE Customer_ID=? AND Appointment_ID!=? AND (Start BETWEEN ? AND ? OR End BETWEEN ? AND ?) AND End!=? AND Start!=?");
            statement.setInt(1, customer);
            statement.setInt(2, appointmentID);
            statement.setTimestamp(3, Timestamp.valueOf(zStart.minus(1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(4, Timestamp.valueOf(zEnd.minus(-1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(5, Timestamp.valueOf(zStart.minus(1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(6, Timestamp.valueOf(zEnd.minus(-1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(7, Timestamp.valueOf(zStart.format(formatter)));
            statement.setTimestamp(8, Timestamp.valueOf(zEnd.format(formatter)));
        } else {
            statement = connection.getConnection().prepareStatement("SELECT * FROM appointments WHERE Customer_ID=? AND (Start BETWEEN ? AND ? OR End BETWEEN ? AND ?) AND End!=? AND Start!=?");
            statement.setInt(1, customer);
            statement.setTimestamp(2, Timestamp.valueOf(zStart.minus(1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(3, Timestamp.valueOf(zEnd.minus(-1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(4, Timestamp.valueOf(zStart.minus(1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(5, Timestamp.valueOf(zEnd.minus(-1, ChronoUnit.MINUTES).format(formatter)));
            statement.setTimestamp(6, Timestamp.valueOf(zStart.format(formatter)));
            statement.setTimestamp(7, Timestamp.valueOf(zEnd.format(formatter)));
        }

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

            int i = 0;
            while (set.next()) { i++; }
            if (i > 0) { return false; }
            else { return true; }

        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**This is the method responsible for deleting all appointments prior to deleting a customer*/
    public static boolean deleteAppointmentsByCustomerID(int ID) throws SQLException {
        statement = connection.getConnection().prepareStatement("SELECT * FROM appointments WHERE Customer_ID=?");
        statement.setInt(1, ID);

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();
            if (set.next()) {
                while (set.next()) {
                    statement = connection.getConnection().prepareStatement("DELETE FROM appointments WHERE Appointment_ID=?");
                    statement.setInt(1, set.getInt("Appointment_ID"));
                    statement.execute();
                }
            }
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }

    /**This method is used to check if the current signed-in user has a meeting within 15 minutes of signing-in.
     * An alert will display in either event but if there is a meeting within 15 minutes (plus and minus) of sign-in
     * the appointment's data will be displayed in the alert.*/
    public static boolean hasUpcomingAppointment(String currentUser) throws SQLException {
        ObservableList<Integer> ids = FXCollections.observableArrayList();
        ObservableList<String> dates = FXCollections.observableArrayList();

        statement = connection.getConnection().prepareStatement("SELECT * FROM appointments WHERE User_ID=? AND Start BETWEEN ? AND ?");
        statement.setInt(1, GeneralQuery.getIDByName("user", currentUser));
        statement.setTimestamp(2, Timestamp.valueOf(OffsetDateTime.now(ZoneOffset.UTC).minus(15, ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        statement.setTimestamp(3, Timestamp.valueOf(OffsetDateTime.now(ZoneOffset.UTC).minus(-15, ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();
            while (set.next()) {
                ZonedDateTime zone = ZonedDateTime.of(
                    set.getTimestamp("Start").toLocalDateTime().toLocalDate(),
                    set.getTimestamp("Start").toLocalDateTime().toLocalTime(),
                    ZoneId.of("UTC")
                );

                ids.add(set.getInt("Appointment_ID"));
                dates.add(zone.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a")));

            }

            message.setAppointmentID(ids);
            message.setFormattedAppointmentDateTime(dates);

            if (dates.size() == 0) { return false; }
            else { return true; }
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }
}
