import Helper.Search;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UTest {
    static Search search;

    /**ZDT*/
    static ZonedDateTime zdt = ZonedDateTime.of(2022, 7, 2, 14, 36, 0, 0, ZoneId.systemDefault());
    static ZonedDateTime zdt2 = ZonedDateTime.of(2022, 7, 2, 16, 36, 0, 0, ZoneId.systemDefault());

    /**UTest Data*/
    static Customer customer = new Customer( 1, 1, "The First Customer!", "UTest Address", "8675309", "98133", "Washington", "U.S." );
    static Customer customer2 = new Customer(2, 1, "UTest Customer2", "UTest Address", "8675310", "98133", "Washington", "U.S.");
    static Customer customer3 = new Customer(3, 1, "UTest Customer3", "UTest Address", "8675311", "98133", "Washington", "U.S.");
    static User user = new User(1, "testAdmin", "pw");
    static User user2 = new User(2, "testUser", "pw");
    static User user3 = new User(3, "testTest", "pw");
    static Contact contact = new Contact(1, "Joe DiFolio", "JDifo@FPR.com");
    static  Contact contact2 = new Contact(2, "Joe DiDolio", "JDido@FPR.com");
    static Contact contact3 = new Contact(3, "Joe DiColio", "JDico@FPR.com");
    static Appointment appointment = new Appointment( 1, 1, 1, 1, "UTest Title", "UTest Description", "UTest Location", "UTest Type", "UTest ContactName", zdt, zdt2, zdt);
    static Appointment appointment2 = new Appointment( 2, 1, 2, 2, "UTest Title", "UTest {[0]} Description", "UTest Location", "UTest Type", "UTest ContactName", zdt, zdt2, zdt);
    static Appointment appointment3 = new Appointment( 3, 3, 3, 3, "UTest Title", "({(|)}) UTest Description", "UTest Location", "UTest Type", "UTest ContactName", zdt, zdt2, zdt);

    static ObservableList<Customer> customers = FXCollections.observableArrayList();
    static ObservableList<Appointment> apps = FXCollections.observableArrayList();

    static {
        customers.add(customer);
        customers.add(customer2);
        customers.add(customer3);

        apps.add(appointment);
        apps.add(appointment2);
        apps.add(appointment3);

        search = new Search(apps, customers);
    }

    @Test
    public void appSearchStringTest() {
        ObservableList<Appointment> _appointment = search.getAppointmentsByString("{[0]}");

        Assert.assertTrue(_appointment.size() == 1);
        Assert.assertTrue(_appointment.get(0).getDescription() == "UTest {[0]} Description");
    }

    @Test
    public void appSearchIntegerTest() {
        ObservableList<Appointment> _appointment = search.getAppointmentsByInteger("3");

        Assert.assertTrue(_appointment.size() == 1);
        Assert.assertTrue(_appointment.get(0).getDescription().contains("({(|)}) UTest Description"));
    }

    @Test
    public void appSearchNoResultsByString() {
        ObservableList<Appointment> _appointment = search.getAppointmentsByString("8-D");

        Assert.assertTrue(_appointment.size() == 0);
    }

    @Test
    public void appSearchNoResultsByInteger() {
        ObservableList<Appointment> _appointment = search.getAppointmentsByInteger("23");

        Assert.assertTrue(_appointment.size() == 0);
    }

    @Test
    public void multipleAppointmentsByString() {
        ObservableList<Appointment> _appointment = search.getAppointmentsByString("Test");

        Assert.assertTrue(_appointment.size() == 3);
    }

    @Test
    public void multipleAppointmentsByInt() {
        ObservableList<Appointment> _appointment = search.getAppointmentsByInteger("1");

        Assert.assertTrue(_appointment.size() == 2);
    }

    @Test
    public void customerSearchStringTest() {
        ObservableList<Customer> _customer = search.getCustomersByString("The First Customer!");

        Assert.assertTrue(_customer.size() == 1);
        Assert.assertTrue(_customer.get(0).getPhone() == "8675309");
    }

    @Test
    public void customerSearchIntegerTest() {
        ObservableList<Customer> _customer = search.getCustomersByInteger("2");

        Assert.assertTrue(_customer.size() == 1);
        Assert.assertTrue(_customer.get(0).getPhone() == "8675310");
    }

    @Test
    public void customerSearchNoResultsByInteger() {
        ObservableList<Customer> _customer = search.getCustomersByInteger("45");

        Assert.assertTrue(_customer.size() == 0);
    }

    @Test
    public void customerSearchNoResultsByString() {
        ObservableList<Customer> _customer = search.getCustomersByInteger("8-D");

        Assert.assertTrue(_customer.size() == 0);
    }

    @Test
    public void multipleCustomersByString() {
        ObservableList<Customer> _customer = search.getCustomersByString("Test");

        Assert.assertTrue(_customer.size() == 3);
    }

    @Test
    public void multipleCustomersByInt() {
        ObservableList<Customer> _customer = search.getCustomersByInteger("1");

        Assert.assertTrue(_customer.size() == 3);
    }
}