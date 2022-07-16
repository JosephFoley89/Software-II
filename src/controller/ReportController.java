package controller;

import Helper.StageTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Appointment;
import model.Contact;
import model.Customer;
import mySQL.GeneralQuery;
import mySQL.Reports;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
/**Controller class for the Reports stage
 *@author Joe Foley*/
public class ReportController implements Initializable {
    public ComboBox contactCB;
    public ComboBox customerCB;
    public Label serverStatusLabel;
    public ComboBox filterType;
    public Label label1;
    public Label label2;
    public Label label3;
    public Label label4;
    public Label label5;
    public Label label6;
    public Label label7;
    public Label label8;
    public Label label9;
    public Label label10;
    public Label label11;
    public Label label12;
    public Label label13;
    public Label label14;
    public Label label15;
    public Label label16;
    public Label label17;
    public Label label18;
    public Label label19;
    public Label label20;
    public Label label21;
    public Label label22;
    public Label label23;
    public Label label24;
    public Tab timeTab;
    public ComboBox timeCB;
    public Tab aggregateTab;
    public Tab customerTab;
    public Tab contactTab;
    @FXML
    private Parent root;
    public TableView Table;
    public TableColumn IDCol;
    public TableColumn TitleCol;
    public TableColumn DescriptionCol;
    public TableColumn LocationCol;
    public TableColumn ContactCol;
    public TableColumn TypeCol;
    public TableColumn DateCol;
    public TableColumn StartCol;
    public TableColumn EndCol;
    public TableColumn CustomerCol;
    public TableColumn UserCol;
    public TableView cTable;
    public TableColumn cIDCol;
    public TableColumn cTitleCol;
    public TableColumn cDescriptionCol;
    public TableColumn cLocationCol;
    public TableColumn cContactCol;
    public TableColumn cTypeCol;
    public TableColumn cDateCol;
    public TableColumn cStartCol;
    public TableColumn cEndCol;
    public TableColumn cCustomerCol;
    public TableColumn cUserCol;
    public TableView customerTable;
    public TableColumn customerIDCol;
    public TableColumn customerTitleCol;
    public TableColumn customerDescriptionCol;
    public TableColumn customerLocationCol;
    public TableColumn customerContactCol;
    public TableColumn customerTypeCol;
    public TableColumn customerDateCol;
    public TableColumn customerStartCol;
    public TableColumn customerEndCol;
    public TableColumn customerCustomerCol;
    public TableColumn customerUserCol;
    private ObservableList<Appointment> appointmentsByTime;
    private ObservableList<Appointment> cAppointments;
    private ObservableList<Appointment> customerAppointments;
    private ObservableList<ObservableList<Appointment>> appointmentsByType;
    private ObservableList<Integer> appointmentsByMonth;

    private StageTransition stage;
    /**Function that handles the close menu item click event. Closes the application */
    @FXML
    public void exitApplication(ActionEvent e) { System.exit(0); }

    /**Function that handles the appointment menu item click event. Changes the stage to appointments */
    @FXML
    public void appointmentStage(ActionEvent e) throws IOException { stage.changeWindow(root, "Appointments"); }

    /**Function that handles the customer menu item click event. Changes the stage to customers */
    @FXML void customerStage(ActionEvent e) throws IOException { stage.changeWindow(root, "Customers");}

    /**Function that handles the report menu item click event. Changes the stage to reports */
    @FXML void reportStage(ActionEvent e) throws IOException { stage.changeWindow(root, "Reports");}

    /**Function that handles the sign-out menu item click event. Changes the stage to the login screen. */
    @FXML
    public void signOut(ActionEvent e) throws IOException { stage.changeWindow(root, "view/main"); }

    /**This function handles the filter combobox change event and updates the table*/
    @FXML
    public void updateFilter(ActionEvent e) throws SQLException { setReportLabels(); }

    /**This function handles the Interval combobox change event and updates the table*/
    @FXML
    public void timeChanged(ActionEvent e) throws SQLException { setTimeValues(timeCB.getSelectionModel().getSelectedItem().toString()); }


    /**This function handles the Contact combobox change event and updates the schedule table*/
    @FXML
    public void contactChanged(ActionEvent e) throws SQLException { setContactValues();  }

    /**This function handles the Customer combobox change event and updates the schedule table*/
    @FXML
    public void customerChanged(ActionEvent e) throws SQLException { setCustomerValues(); }

    /**Sets time table values based on combobox selection*/
    private void setTimeValues(String selectedValue) throws SQLException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Date.from(Instant.now()));
        timeTab.setText("Appointments by Current " + selectedValue);
        if (selectedValue == "Week") { appointmentsByTime = Reports.getAppointmentsByTime("WEEK", calendar.get(Calendar.WEEK_OF_YEAR)-1, calendar.get(Calendar.YEAR)); }
        else if (selectedValue == "Month") { appointmentsByTime = Reports.getAppointmentsByTime("MONTH", calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.YEAR)); }
        else { appointmentsByTime = Reports.getAppointmentsByTime("YEAR", calendar.get(Calendar.YEAR), calendar.get(Calendar.YEAR)); }

        Table.setItems(appointmentsByTime);
        IDCol.setCellValueFactory(new PropertyValueFactory<>("appID"));
        CustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        UserCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        ContactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        TitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        DescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        LocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        TypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        DateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        StartCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        EndCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
    }

    /**Set ContactTable values*/
    private void setContactValues() throws SQLException {
        contactTab.setText(contactCB.getSelectionModel().getSelectedItem().toString() + "'s Appointments");

        cAppointments = Reports.getAppointmentByContact(contactCB.getSelectionModel().getSelectedItem().toString());
        cTable.setItems(cAppointments);
        cIDCol.setCellValueFactory(new PropertyValueFactory<>("appID"));
        cCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        cUserCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        cContactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        cTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        cDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        cLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        cTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        cDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        cStartCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        cEndCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
    }

    /**Set Customer Table values*/
    private void setCustomerValues() throws SQLException {
        customerTab.setText(customerCB.getSelectionModel().getSelectedItem().toString() + "'s Appointments");

        customerAppointments = Reports.getAppointmentByCustomer(customerCB.getSelectionModel().getSelectedItem().toString());
        customerTable.setItems(customerAppointments);
        customerIDCol.setCellValueFactory(new PropertyValueFactory<>("appID"));
        customerCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerUserCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        customerContactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        customerTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        customerDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        customerLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        customerTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        customerDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        customerStartCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        customerEndCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
    }

    /**Method responsible for setting table values*/
    private void setTableValues() throws SQLException {
        serverStatusLabel.setText("Reports refreshed");
        setTimeValues(timeCB.getSelectionModel().getSelectedItem().toString());
        setContactValues();
        setCustomerValues();
    }

    /**Function responsible for populating contact combobox*/
    private void setContacts() throws SQLException {
        ObservableList<Contact> contacts = GeneralQuery.getAllFromTable("contacts");
        ObservableList<String> contactNames = FXCollections.observableArrayList();

        for (Contact contact : contacts){
            contactNames.add(contact.getName());
        }
        contactCB.setItems(contactNames);
        contactCB.getSelectionModel().selectFirst();
    }

    /**Function responsible for populating contact combobox*/
    private void setCustomers() throws SQLException {
        ObservableList<Customer> customers = GeneralQuery.getAllFromTable("customers");
        ObservableList<String> customerNames = FXCollections.observableArrayList();

        for (Customer customer : customers){
            customerNames.add(customer.getName());
        }
        customerCB.setItems(customerNames);
        customerCB.getSelectionModel().selectFirst();
    }

    /**Function Responsible for populating values into the month/type combobox*/
    private void setFilterValues() throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Type");
        list.add("Month");
        filterType.setItems(list);
        filterType.getSelectionModel().selectFirst();
        setReportLabels();
    }

    /**Function responsible for setting time intervals*/
    private void setTimeIntervals() {
        ObservableList<String> intervals = FXCollections.observableArrayList();
        intervals.add("Week");
        intervals.add("Month");
        intervals.add("Year");
        timeCB.setItems(intervals);
        timeCB.getSelectionModel().selectFirst();
    }

    /**Function responsible for setting labels with their appropriate values based on filter value*/
    private void setReportLabels() throws SQLException {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("De-Briefing");
        list.add("Planning Session");
        list.add("Sales Call");
        list.add("Quarterly Review");
        list.add("Sprint Retrospective");

        if (filterType.getSelectionModel().getSelectedItem().toString() == "Type") {
            aggregateTab.setText("Appointments Aggregated by Type");
            appointmentsByType = Reports.getAppointmentsByType(list);
            label1.setText("De-Briefing Appointments:");
            label2.setText("Planning Session Appointments:");
            label3.setText("Sales Call Appointments:");
            label4.setText("Quarterly Review Appointments:");
            label5.setText("Sprint Retroactive Appointments:");
            label6.setText("");
            label7.setText("");
            label8.setText("");
            label9.setText("");
            label10.setText("");
            label11.setText("");
            label12.setText("");
            label13.setText(String.valueOf(appointmentsByType.get(0).size()));
            label14.setText(String.valueOf(appointmentsByType.get(1).size()));
            label15.setText(String.valueOf(appointmentsByType.get(2).size()));
            label16.setText(String.valueOf(appointmentsByType.get(3).size()));
            label17.setText(String.valueOf(appointmentsByType.get(4).size()));
            label18.setText("");
            label19.setText("");
            label20.setText("");
            label21.setText("");
            label22.setText("");
            label23.setText("");
            label24.setText("");
        } else if (filterType.getSelectionModel().getSelectedItem().toString() == "Month") {
            aggregateTab.setText("Appointments Aggregated by Month");
            appointmentsByMonth = Reports.getAppointmentsByMonth();
            label1.setText("January Appointments:");
            label2.setText("February Appointments:");
            label3.setText("March Appointments:");
            label4.setText("April Appointments:");
            label5.setText("May Appointments:");
            label6.setText("June Appointments:");
            label7.setText("July Appointments:");
            label8.setText("August Appointments:");
            label9.setText("September Appointments:");
            label10.setText("October Appointments:");
            label11.setText("November Appointments:");
            label12.setText("December Appointments:");
            label13.setText(String.valueOf(appointmentsByMonth.get(0)));
            label14.setText(String.valueOf(appointmentsByMonth.get(1)));
            label15.setText(String.valueOf(appointmentsByMonth.get(2)));
            label16.setText(String.valueOf(appointmentsByMonth.get(3)));
            label17.setText(String.valueOf(appointmentsByMonth.get(4)));
            label18.setText(String.valueOf(appointmentsByMonth.get(5)));
            label19.setText(String.valueOf(appointmentsByMonth.get(6)));
            label20.setText(String.valueOf(appointmentsByMonth.get(7)));
            label21.setText(String.valueOf(appointmentsByMonth.get(8)));
            label22.setText(String.valueOf(appointmentsByMonth.get(9)));
            label23.setText(String.valueOf(appointmentsByMonth.get(10)));
            label24.setText(String.valueOf(appointmentsByMonth.get(11)));
        }
    }

    /**Initializing the stage. Additionally, I used two lambdas to update the tableviews
     * every 30 seconds and to clear text. I used the lambdas in this scenario as the
     * label is not accessible without it.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stage = new StageTransition();

        Timer refresh = new Timer();
        Timer clearText = new Timer();

        refresh.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    try {
                        setTableValues();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        },  1000, 30000);

        clearText.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    serverStatusLabel.setText("");
                });
            }
        }, 0, 5000);

        try {
            setContacts();
            setCustomers();
            setFilterValues();
            setTimeIntervals();
            setTableValues();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
