package mySQL;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import java.sql.*;
import java.time.*;
/**This class is used for gathering reporting data.
 * This class was created so that all reports located
 * in the reporting stage are grouped together and
 * easy to find.
 * @author Joe Foley*/
public class Reports {
    private static DataConnection connection;
    private static PreparedStatement statement;

    /**This method is responsible for getting all appointments by week, month, or year.*/
    public static ObservableList<Appointment> getAppointmentsByTime(String query, int key, int year) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM appointments WHERE %s(Start)=? && YEAR(start)=?", query));
        statement.setInt(1, key);
        statement.setInt(2, year);

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

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
                        GeneralQuery.getNameByID("Contact", set.getInt("Contact_ID")),
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

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return appointments;
    }

    /**This method is responsible for getting all appointments that have the same contact.*/
    public static ObservableList<Appointment> getAppointmentByContact(String contact) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        statement = connection.getConnection().prepareStatement("SELECT * FROM appointments WHERE Contact_ID=?");
        statement.setInt(1, GeneralQuery.getIDByName("Contact", contact));

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

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
                        GeneralQuery.getNameByID("Contact", set.getInt("Contact_ID")),
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
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return appointments;
    }

    /**This method is responsible for getting all appointments that have the same customer.*/
    public static ObservableList<Appointment> getAppointmentByCustomer(String customer) throws SQLException {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        statement = connection.getConnection().prepareStatement("SELECT * FROM appointments WHERE Customer_ID=?");
        statement.setInt(1, GeneralQuery.getIDByName("Customer", customer));

        try {
            statement.execute();
            ResultSet set = statement.getResultSet();

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
                        GeneralQuery.getNameByID("Contact", set.getInt("Contact_ID")),
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
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return appointments;
    }

    /**This method is responsible for getting all appointments by type. I left it as an observable list of appointments
     * as a proof of concept - i.e. if a requirement comes along where the records need to be retrieved we will
     * be in a spot where it's an easy change to make.*/
    public static ObservableList<ObservableList<Appointment>> getAppointmentsByType(ObservableList<String> type) throws SQLException {
        ObservableList<ObservableList<Appointment>> listOfLists = FXCollections.observableArrayList();

        for (String s : type) {
            ObservableList<Appointment> appointments = FXCollections.observableArrayList();
            statement = connection.getConnection().prepareStatement(String.format("SELECT * FROM appointments WHERE Type=\"%s\"", s));

            try {
                statement.execute();
                ResultSet set = statement.getResultSet();

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
                            GeneralQuery.getNameByID("Contact", set.getInt("Contact_ID")),
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
                listOfLists.add(appointments);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return listOfLists;
    }

    /**This method is responsible for getting all appointments by month. This could be easily updated to the
     * getAppointmentsByType model (observable list of appointments) if necessary. As it currently stands,
     * the sum of the queries are added to an observable list of Integers.*/
    public static ObservableList<Integer> getAppointmentsByMonth() throws SQLException {
        ObservableList<Integer> listOfInts = FXCollections.observableArrayList();

        for(int i = 1; i < 13; i++) {
            statement = connection.getConnection().prepareStatement("SELECT COUNT(*) FROM appointments WHERE MONTH(Start) = ?");
            statement.setInt(1, i);
            try {
                statement.execute();
                ResultSet set = statement.getResultSet();
                if (set.next()) {
                    listOfInts.add(set.getInt("COUNT(*)"));
                } else {
                    listOfInts.add(0);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
        return listOfInts;
    }

}
