package Helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Customer;

import java.util.Locale;

public class Search {
    private ObservableList<Appointment> allAppointments;
    private ObservableList<Customer> allCustomers;

    public Search(ObservableList<Appointment> appointments, ObservableList<Customer> customers) {
        this.allAppointments = appointments;
        this.allCustomers = customers;
    }

    public ObservableList<Appointment> getAllAppointments() {
        return allAppointments;
    }

    public void setAllAppointments(ObservableList<Appointment> allAppointments) {
        this.allAppointments = allAppointments;
    }

    public ObservableList<Customer> getAllCustomers() {
        return allCustomers;
    }

    public void setAllCustomers(ObservableList<Customer> allCustomers) {
        this.allCustomers = allCustomers;
    }

    /**this method searches all appointments and searches the description, type, title, and location
     * for the user input string. This search method is not case-sensitive*/
    public ObservableList<Appointment> getAppointmentsByString(String search) {
        search = search.toLowerCase();
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        for (Appointment a : allAppointments) {
            if (a.getTitle().toLowerCase(Locale.ROOT).contains(search) ||
                a.getType().toLowerCase(Locale.ROOT).contains(search) ||
                a.getDescription().toLowerCase(Locale.ROOT).contains(search) ||
                a.getLocation().toLowerCase(Locale.ROOT).contains(search)) {
                appointments.add(a);
            }
        }
        return appointments;
    }

    /**this method searches all appointments for the ID values to see if they match the input string*/
    public ObservableList<Appointment> getAppointmentsByInteger(String search) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        for (Appointment a : allAppointments) {
            String appID = String.valueOf(a.getAppID());
            String customerID = String.valueOf(a.getCustomerID());
            String userID = String.valueOf(a.getUserID());

            if (appID.contains(search) || customerID.contains(search) || userID.contains(search)) {
                appointments.add(a);
            }
        }
        return appointments;
    }

    /**This method searches all customers and returns a list of customers that have a name, address, country,
     * or division that contains the user-provided search string. This is not case-sensitive.*/
    public ObservableList<Customer> getCustomersByString(String search) {
        search = search.toLowerCase();
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        for (Customer c : allCustomers) {
            if (c.getName().toLowerCase().contains(search) ||
                c.getAddress().toLowerCase().contains(search) ||
                c.getCountry().toLowerCase().contains(search) ||
                c.getDivision().toLowerCase().contains(search)
            ) {
                customers.add(c);
            }
        }
        return customers;
    }

    /**This method searches all customers and provides a list of customers that have a customer ID,
     * Division ID, phone, or postal code that contain the user-input string.*/
    public ObservableList<Customer> getCustomersByInteger(String search) {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        for (Customer c : allCustomers) {
            String customerID = String.valueOf(c.getCustomerID());
            String divisionID = String.valueOf(c.getDivisionID());
            String phone = String.valueOf(c.getPhone());
            String postalCode = String.valueOf(c.getPostalCode());
            String address = c.getAddress();
            if (customerID.contains(search) || divisionID.contains(search)
                || phone.contains(search) || postalCode.contains(search) ||
                address.contains(search)) {
                customers.add(c);
            }
        }
        return customers;
    }
}
