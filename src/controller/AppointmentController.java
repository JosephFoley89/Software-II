package controller;

import Helper.Message;
import Helper.Search;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import model.Appointment;
import model.Contact;
import model.Customer;
import model.User;
import mySQL.AppQuery;
import mySQL.GeneralQuery;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**controller class for the homepage.
 * @author Joe Foley*/
public class AppointmentController implements Initializable {
    public TextField searchTB;
    @FXML
    private TextField idTB;
    @FXML
    private TableColumn idCol;
    @FXML
    private TableColumn titleCol;
    @FXML
    private TableColumn descriptionCol;
    @FXML
    private TableColumn locationCol;
    @FXML
    private TableColumn contactCol;
    @FXML
    private TableColumn typeCol;
    @FXML
    private TableColumn dateCol;
    @FXML
    private TableColumn startCol;
    @FXML
    private TableColumn endCol;
    @FXML
    private TableColumn customerCol;
    @FXML
    private TableColumn userCol;
    @FXML
    private TextField titleTB;
    @FXML
    private TextField locationTB;
    @FXML
    private ComboBox contactCB;
    @FXML
    private ComboBox typeCB;
    @FXML
    private DatePicker dateDP;
    @FXML
    private ComboBox startCB;
    @FXML
    private ComboBox endCB;
    @FXML
    private Label errorLabel;
    @FXML
    private Label successLabel;
    @FXML
    private TextField descriptionTB;
    @FXML
    private ComboBox userCB;
    @FXML
    private ComboBox customerCB;
    @FXML
    private TableView table;
    @FXML
    private Parent root;

    private Search search;

    private static StageTransition stage;
    private static Message message;
    private static ObservableList<Appointment> appointments;
    private static ObservableList<Contact> contacts;
    private static ObservableList<Customer> customers;
    private static ObservableList<User> users;

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

    /**Event handler for clear button press. Calls the clearFields function to reset fields to default values*/
    @FXML
    public void clearButtonPressed(ActionEvent e) throws SQLException { clearFields(); }

    /**Event handler for when the table is clicked to determine whether something has been selected.
     * and propogates that information into the fields for editing.*/
    @FXML
    public void setSelection(MouseEvent mouseEvent) throws SQLException {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            Appointment appointment = (Appointment) table.getSelectionModel().getSelectedItem();
            if (appointment != null) {
                idTB.setText(String.valueOf(appointment.getAppID()));
                titleTB.setText(appointment.getTitle());
                locationTB.setText(appointment.getLocation());
                descriptionTB.setText(appointment.getDescription());
                typeCB.getSelectionModel().select(appointment.getType());
                dateDP.setValue(appointment.getStartDate());
                startCB.getSelectionModel().select(appointment.getStartTimeString());
                endCB.getSelectionModel().select(appointment.getEndTimeString());
                contactCB.getSelectionModel().select(appointment.getContactName());
                customerCB.getSelectionModel().select(GeneralQuery.getNameByID("customer", appointment.getCustomerID()));
                userCB.getSelectionModel().select(GeneralQuery.getNameByID("user", appointment.getUserID()));
            }
        }
    }

    /**Event handler that handles the create appointment button click event.
     * Attempts to create a new appointment. Provides error messaging should it
     * fail and a success message under the appointment table.*/
    @FXML
    public void createAppointment(ActionEvent e) throws SQLException {
        if (allFieldsContainValues()) {
            ZonedDateTime startTime = ZonedDateTime.of(dateDP.getValue(), LocalTime.parse(startCB.getSelectionModel().getSelectedItem().toString(),DateTimeFormatter.ofPattern("h:mm a")), ZoneId.systemDefault());
            ZonedDateTime endTime = ZonedDateTime.of(dateDP.getValue(), LocalTime.parse(endCB.getSelectionModel().getSelectedItem().toString(), DateTimeFormatter.ofPattern("h:mm a")), ZoneId.systemDefault());
            if (isPossibleAppointment(startTime.toLocalTime(), endTime.toLocalTime())) {
                LocalDate date = dateDP.getValue();
                if (AppQuery.customerAvailable(customerCB.getSelectionModel().getSelectedItem().toString(), startTime, endTime, 0, "create")) {
                    boolean successful = AppQuery.createAppointment(
                        customerCB.getSelectionModel().getSelectedItem().toString(), userCB.getSelectionModel().getSelectedItem().toString(),
                        contactCB.getSelectionModel().getSelectedItem().toString(), titleTB.getText(), descriptionTB.getText(), locationTB.getText(),
                        typeCB.getSelectionModel().getSelectedItem().toString(), startTime, endTime, date
                    );
                    if (successful) {
                        errorLabel.setText("");
                        successLabel.setText("Appointment Successfully Created!");
                        search.setAllAppointments(GeneralQuery.getAllFromTable("appointments"));
                        setTableValues();
                        clearFields();
                    } else {
                        errorLabel.setText("Unable to add appointment");
                        successLabel.setText("");
                    }
                } else {
                    errorLabel.setText("The customer is not available during this appointment time.");
                    successLabel.setText("");
                }
            } else {
                errorLabel.setText("It is not possible to start after the meeting is over or to have meetings last overnight.");
                successLabel.setText("");
            }
        } else {
            errorLabel.setText("Unable to add appointment due to missing fields");
            successLabel.setText("");
        }
    }

    /**Event handler that handles the update appointment button click event.
     * Attempts to update an appointment. An error message is displayed should
     * that fail.*/
    @FXML
    public void updateAppointment(ActionEvent e) throws SQLException {
        Appointment appointment = (Appointment) table.getSelectionModel().getSelectedItem();

        if (appointment == null) {
            errorLabel.setText("Please make a selection and try again.");
        } else {
            ZonedDateTime startTime = ZonedDateTime.of(dateDP.getValue(), LocalTime.parse(startCB.getSelectionModel().getSelectedItem().toString(), DateTimeFormatter.ofPattern("h:mm a")), ZoneId.systemDefault());
            ZonedDateTime endTime = ZonedDateTime.of(dateDP.getValue(), LocalTime.parse(endCB.getSelectionModel().getSelectedItem().toString(), DateTimeFormatter.ofPattern("h:mm a")), ZoneId.systemDefault());

            if (allFieldsContainValues()) {
                if (isPossibleAppointment(startTime.toLocalTime(), endTime.toLocalTime())) {
                    if (AppQuery.customerAvailable(customerCB.getSelectionModel().getSelectedItem().toString(), startTime, endTime, Integer.parseInt(idTB.getText()), "update")) {
                        boolean isSuccessful = AppQuery.updateAppointment(
                                customerCB.getSelectionModel().getSelectedItem().toString(), userCB.getSelectionModel().getSelectedItem().toString(), contactCB.getSelectionModel().getSelectedItem().toString(), titleTB.getText(),
                                descriptionTB.getText(), locationTB.getText(), typeCB.getSelectionModel().getSelectedItem().toString(), startTime, endTime, appointment.getAppID()
                        );
                        if (isSuccessful) {
                            errorLabel.setText("");
                            successLabel.setText("Appointment Successfully Updated!");
                            search.setAllAppointments(GeneralQuery.getAllFromTable("appointments"));
                            setTableValues();
                            clearFields();
                        } else {
                            errorLabel.setText("Unable to update Appointment");
                            successLabel.setText("");
                        }
                    } else {
                        errorLabel.setText("The customer is not available during this appointment time.");
                        successLabel.setText("");
                    }
                } else {
                    errorLabel.setText("It is not possible to start after the meeting is over or to have appointments last overnight.");
                    successLabel.setText("");
                }
            } else {
                errorLabel.setText("Unable to update appointment due to missing fields");
                successLabel.setText("");
            }
        }
    }

    /**Event handler that handles the cancel appointment button click event.
     * Attempts to delete an appointment. Provides an error message should it fail.*/
    @FXML
    public void deleteAppointment(ActionEvent e) throws SQLException {
        Appointment appointment = (Appointment) table.getSelectionModel().getSelectedItem();
        if (appointment == null) {
            successLabel.setText("");
            errorLabel.setText("Please make a selection and try again.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Please confirm");
            alert.setContentText("Please confirm you wish to cancel " + appointment.getAppID() + " " +  appointment.getTitle() + ".");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean isSuccessful = GeneralQuery.deleteByID("appointment", ((Appointment) table.getSelectionModel().getSelectedItem()).getAppID());

                if (isSuccessful) {
                    errorLabel.setText("");
                    successLabel.setText("Appointment " + appointment.getAppID() + " - " + appointment.getTitle() + " - " + appointment.getType() +" successfully canceled. ");
                    search.setAllAppointments(GeneralQuery.getAllFromTable("appointments"));
                    setTableValues();
                    clearFields();
                } else {
                    successLabel.setText("");
                    errorLabel.setText("Deletion failed.");
                }
            }
        }
    }

    /**This method is responsible for verifying all the textfields have data in them.*/
    private boolean allFieldsContainValues() {
        if (titleTB.getText().isEmpty()) { return false; }
        if (locationTB.getText().isEmpty()) { return false; }
        if (descriptionTB.getText().isEmpty()) { return false; }
        if (dateDP.getValue() == null) { return false; }
        else { return true; }
    }

    /**This method is responsible for checking if the start date is before the end date. Provides an error message
     * to the user if not.*/
    private boolean isPossibleAppointment(LocalTime start, LocalTime end) {
        if (!start.equals(end)) { return start.isBefore(end); }
        else { return false; }
    }

    /**Function responsible for populating the contact combobox
     * when the page is initialized*/
    private void setContacts(ComboBox box, ObservableList<Contact> contacts) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Contact contact : contacts) {
            if (!list.contains(contact.getName())) {
                list.add(contact.getName());
            }
        }
        box.setItems(list);
    }

    /**Function responsible for populating the user combobox
     * when the page is initialized*/
    private void setCustomers(ComboBox box, ObservableList<Customer> customers) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (Customer customer : customers) {
            if (!list.contains(customer.getName())) {
                list.add(customer.getName());
            }
        }
        box.setItems(list);
    }

    /**Function responsible for populating the user combobox
     * when the page is initialized*/
    private void setUsers(ComboBox box, ObservableList<User> users) {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (User user : users) {
            if (!list.contains(user.getName())) {
                list.add(user.getName());
            }
        }
        box.setItems(list);
    }

    /**Function responsible for establishing the values for both start and end
     * comboboxes*/
    private void setTimeCombo() {
        ObservableList<String> timeValues = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        ZonedDateTime zStart = ZonedDateTime.of(LocalDate.now(), LocalTime.of(8,0), ZoneId.of("America/New_York"));
        ZonedDateTime zEnd = ZonedDateTime.of(LocalDate.now(), LocalTime.of(22,0), ZoneId.of("America/New_York"));

        timeValues.add(zStart.withZoneSameInstant(ZoneId.systemDefault()).format(formatter));
        while (zStart.isBefore(zEnd)) {
            zStart = zStart.plusMinutes(30);
            timeValues.add(zStart.withZoneSameInstant(ZoneId.systemDefault()).format(formatter));
        }
        startCB.setItems(timeValues);
        endCB.setItems(timeValues);
    }

    /**Function responsible for setting the meeting type values*/
    private void setTypes() throws SQLException {
        ObservableList<String> types = FXCollections.observableArrayList();
        types.add("De-Briefing");
        types.add("Planning Session");
        types.add("Sales Call");
        types.add("Quarterly Review");
        types.add("Sprint Retrospective");
        typeCB.setItems(types);
        typeCB.getSelectionModel().selectFirst();
        table.setItems(GeneralQuery.getAllFromTable("appointments"));
    }

    /**Function responsible for clearing textfield and DatePicker values, combobox index.
     * Called on button click as well as once an appointment is added successfully.*/
    private void clearFields() throws SQLException {
        idTB.setText("");
        searchTB.setText("");
        titleTB.setText("");
        locationTB.setText("");
        descriptionTB.setText("");
        dateDP.setValue(null);
        contactCB.getSelectionModel().selectFirst();
        startCB.getSelectionModel().selectFirst();
        endCB.getSelectionModel().selectFirst();
        customerCB.getSelectionModel().selectFirst();
        userCB.getSelectionModel().selectFirst();
        typeCB.getSelectionModel().selectFirst();
        table.getSelectionModel().clearSelection();
        table.setItems(GeneralQuery.getAllFromTable("appointments"));
    }

    /**This function is responsible for handling search events.*/
    public void search(KeyEvent keyEvent) throws SQLException {
        boolean isNum = false;
        if (!searchTB.getText().isEmpty()) {
            try {
                Integer.parseInt(searchTB.getText());
                isNum = true;
            } catch (Exception ex) {
                System.out.println("Search value is not a number.");
            }
            if (isNum) {
                table.setItems(search.getAppointmentsByInteger(searchTB.getText()));
            } else {
                table.setItems(search.getAppointmentsByString(searchTB.getText()));
            }
        } else {
            table.setItems(GeneralQuery.getAllFromTable("appointments"));
        }
    }


    /**Function responsible for setting the values of the cells in the table view. Called
     * when the page is loaded as well as when a record is added.*/
    private void setTableValues() throws SQLException {
        appointments = GeneralQuery.getAllFromTable("appointments");
        table.setItems(appointments);
        idCol.setCellValueFactory(new PropertyValueFactory<>("appID"));
        customerCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startTimeString"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endTimeString"));
    }

    /**This Method initializes the stage - sets Message, StageTransition class instances
     * sets the necessary ObservableLists (appointments, contacts, customers, and users)
     * catches the exception should that fail. Used Lambda inside the timer to clear
     * error/success message values. Also used a Lambda to delay the alert window until
     * after the stage loaded. I used the lambdas in this situation as the labels are
     * inaccessible in timers without the lambda. Addtionally, I used the second lambda
     * to delay the display of the alert until after the appointments window is loaded.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateDP.getEditor().setDisable(true);

        Timer messageTimer = new Timer();
        messageTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    errorLabel.setText("");
                    successLabel.setText("");
                });
            }
        },  1000, 8000);

        stage = new StageTransition();
        message = Message.getInstance();

        try {
            search = new Search(GeneralQuery.getAllFromTable("appointments"), GeneralQuery.getAllFromTable("customers"));
            contacts = GeneralQuery.getAllFromTable("contacts");
            customers = GeneralQuery.getAllFromTable("customers");
            users = GeneralQuery.getAllFromTable("users");
            setTableValues();
            setTimeCombo();
            setContacts(contactCB, contacts);
            setCustomers(customerCB, customers);
            setUsers(userCB, users);
            setTypes();
            contactCB.getSelectionModel().selectFirst();
            startCB.getSelectionModel().selectFirst();
            endCB.getSelectionModel().selectFirst();
            customerCB.getSelectionModel().selectFirst();
            userCB.getSelectionModel().selectFirst();
            Platform.runLater(() -> {
                try {
                    message.upcomingAppointmentAlert();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
