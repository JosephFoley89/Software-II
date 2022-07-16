package controller;

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
import javafx.scene.input.MouseEvent;
import model.Country;
import model.Customer;
import model.Division;
import mySQL.AppQuery;
import mySQL.CustomerQuery;
import mySQL.GeneralQuery;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**Controller class for the Customers stage
 * @author Joe Foley*/
public class CustomerController implements Initializable {
    @FXML
    public Parent root;
    public TableColumn idCol;
    public TableColumn addressCol;
    public TableView table;
    public TableColumn nameCol;
    public TableColumn stateCol;
    public TableColumn postalCol;
    public TableColumn countryCol;
    public TextField idTB;
    public TextField nameTB;
    public TextField addressTB;
    public TextField postalTB;
    public TextField phoneTB;
    public ComboBox countryCB;
    public ComboBox divisionCB;
    public Label errorLabel;
    public Label successLabel;
    public TableColumn phoneCol;
    public TextField searchTB;
    private StageTransition stage;
    private Search search;
    private ObservableList<Customer> customers;
    private ObservableList<Country> countries;
    private ObservableList<Division> divisions;

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
    public void signOut(ActionEvent e) throws IOException { stage.changeWindow(root, "main"); }

    /**Event handler for the Country combobox change event*/
    @FXML
    public void countryChanged(ActionEvent e) throws SQLException {
        setDivisionCB(countryCB.getSelectionModel().getSelectedItem().toString());
        divisionCB.getSelectionModel().selectFirst();
    }

    /**Event handler for the create customer button click event.
     * Attempts to create a new customer and throws an error
     * should that creation fail.*/
    @FXML
    public void createCustomer(ActionEvent e) throws SQLException {
        boolean customerDoesNotExist =
            CustomerQuery.customerDoesNotExist(nameTB.getText(), phoneTB.getText(), addressTB.getText(), divisionCB.getSelectionModel().getSelectedItem().toString(), postalTB.getText());
        if (customerDoesNotExist) {
            boolean isSuccessful =
                CustomerQuery.createCustomer(nameTB.getText(), phoneTB.getText(), addressTB.getText(), divisionCB.getSelectionModel().getSelectedItem().toString(), postalTB.getText());

            if (isSuccessful) {
                errorLabel.setText("");
                successLabel.setText("Customer successfully added!");
                setTableValues();
                clearSelection();
                search.setAllCustomers(GeneralQuery.getAllFromTable("customers"));
            } else {
                errorLabel.setText("Customer addition failed.");
                successLabel.setText("");
            }
        } else {
            errorLabel.setText("This customer already exists in the database.");
            successLabel.setText("");
        }
    }

    /**Event handler for the update customer button click event.
     * Attempts to update a customer and throws an error
     * should that creation fail.*/
    @FXML
    public void updateCustomer(ActionEvent e) throws SQLException {
        if (allFieldsContainValues()) {
            boolean customerUpdated = CustomerQuery.customerUpdated(nameTB.getText(), phoneTB.getText(), addressTB.getText(), divisionCB.getSelectionModel().getSelectedItem().toString(), postalTB.getText(), Integer.parseInt(idTB.getText()));
            if (customerUpdated) {
                errorLabel.setText("");
                successLabel.setText("Customer successfully updated!");
                setTableValues();
                clearSelection();
                search.setAllCustomers(GeneralQuery.getAllFromTable("customers"));
            } else {
                errorLabel.setText("Customer update failed.");
                successLabel.setText("");
            }
        } else {
            errorLabel.setText("Please select a customer to update.");
            successLabel.setText("");
        }
    }

    /**Event handler for the remove customer button click event which
     * attempts to remove a customer and throws an error if unsuccessful.*/
    @FXML
    public void deleteCustomer(ActionEvent event) throws SQLException {
        Customer customer = (Customer) table.getSelectionModel().getSelectedItem();
        if (customer == null) {
            successLabel.setText("");
            errorLabel.setText("Please make a selection and try again.");
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Please confirm");
            alert.setContentText("Please confirm you wish to remove " + customer.getName() + " as a customer.");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean removedAppointments = AppQuery.deleteAppointmentsByCustomerID(customer.getCustomerID());
                if (removedAppointments) {
                    boolean isSuccessful = GeneralQuery.deleteByID("customer", customer.getCustomerID());
                    if (isSuccessful) {
                        errorLabel.setText("");
                        successLabel.setText("Customer " + customer.getName() + " and their scheduled appointments have been successfully removed. ");
                        setTableValues();
                        clearSelection();
                        search.setAllCustomers(GeneralQuery.getAllFromTable("customers"));
                    } else {
                        successLabel.setText("");
                        errorLabel.setText("Customer removal failed.");
                    }
                } else {
                    successLabel.setText("");
                    errorLabel.setText("Customer removal failed.");
                }
            }
        }
    }

    /**Event handler for when the table is clicked to determine whether something has been selected.
     * and propogates that information into the fields for editing.*/
    @FXML
    public void setSelection(MouseEvent mouseEvent) throws SQLException {
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            Customer customer = (Customer) table.getSelectionModel().getSelectedItem();
            if (customer != null) {
                idTB.setText(String.valueOf(customer.getCustomerID()));
                nameTB.setText(customer.getName());
                addressTB.setText(customer.getAddress());
                postalTB.setText(customer.getPostalCode());
                phoneTB.setText(customer.getPhone());
                countryCB.getSelectionModel().select(customer.getCountry());
                divisionCB.getSelectionModel().select(customer.getDivision());
            }
        }
    }

    /**Event handler for the clear selection button click event.
     * Clear form data and selection from the table*/
    public void removeSelection(ActionEvent e) throws SQLException {
        clearSelection();
    }

    /**Function to clear the selection from the table and the associated field's values*/
    private void clearSelection() throws SQLException {
        idTB.setText("");
        nameTB.setText("");
        addressTB.setText("");
        postalTB.setText("");
        phoneTB.setText("");
        searchTB.setText("");
        countryCB.getSelectionModel().selectFirst();
        divisionCB.getSelectionModel().selectFirst();
        table.getSelectionModel().clearSelection();
        table.setItems(GeneralQuery.getAllFromTable("customers"));
    }

    /**This method confirms all fields contain a value.*/
    private boolean allFieldsContainValues() {
        if (nameTB.getText().isEmpty()) { return false; }
        else if (addressTB.getText().isEmpty()) { return false; }
        else if (postalTB.getText().isEmpty()) { return false; }
        else if (phoneTB.getText().isEmpty()) { return false; }
        else { return true; }
    }

    /**Function for setting country combobox values*/
    public void setCountryValues() throws SQLException {
        ObservableList<String> countryNames = FXCollections.observableArrayList();
        countries = GeneralQuery.getAllFromTable("countries");
        for (Country country : countries) {
            countryNames.add(country.getName());
        }
        countryCB.setItems(countryNames);
        countryCB.getSelectionModel().selectFirst();
        setDivisionCB(countryCB.getSelectionModel().getSelectedItem().toString());
    }

    /**Function for setting table values*/
    public void setTableValues() throws SQLException {
        customers = GeneralQuery.getAllFromTable("customers");
        table.setItems(customers);
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        stateCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        countryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
    }

    /**Function to filter the divisions based on Country*/
    private void setDivisionCB(String country) throws SQLException {
        ObservableList<String> divisionNames = FXCollections.observableArrayList();
        divisions = GeneralQuery.getDivisionByCountry(country);
        for (Division division : divisions) {
            divisionNames.add(division.getName());
        }
        divisionCB.setItems(divisionNames);
    }

    /**This function is responsible for triggering the user searches.*/
    public void search(KeyEvent keyEvent) throws SQLException {
        boolean isNum = false;
        if (!searchTB.getText().isEmpty()) {
            try {
                Integer.parseInt(searchTB.getText());
                isNum = true;
            } catch (Exception ex) {
                System.out.println("Search string is not a number.");
            }
            if (isNum) {
                table.setItems(search.getCustomersByInteger(searchTB.getText()));
            } else {
                table.setItems(search.getCustomersByString(searchTB.getText()));
            }

        } else {
            table.setItems(GeneralQuery.getAllFromTable("customers"));
        }
    }

    /**This method initializes the stage. Used lambda to reset success and error messaging
     * every 8 seconds. I used a lambda as the label is inaccessible from the timer task
     * otherwise.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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

        try {
            search = new Search(GeneralQuery.getAllFromTable("appointments"), GeneralQuery.getAllFromTable("customers"));
            setTableValues();
            setCountryValues();
            divisionCB.getSelectionModel().selectFirst();
            nameTB.requestFocus();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}
