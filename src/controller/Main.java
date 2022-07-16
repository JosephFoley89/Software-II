package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import Helper.*;
import mySQL.GeneralQuery;

/**Controller for the Main window
 * @author Joseph Foley*/
public class Main implements Initializable {
    private static Message message;
    private static StageTransition stage;
    private static SystemLog log;
    @FXML
    private Parent root;
    @FXML
    private TextField usernameTB;
    @FXML
    private TextField passwordTB;
    @FXML
    private Label zoneLabel;
    @FXML
    private Label errorLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button signInButton;

    /**Event handler for the signIN button click event - attempts to log the user in*/
    @FXML
    public void signIn(ActionEvent e) throws IOException, SQLException {
        sendSignIn();
    }

    /**Event handler for the key down event. Checks to see if the enter key was pressed
     * if it is, it attempts to sign in.*/
    @FXML
    public void checkKey(KeyEvent e) throws IOException, SQLException {
        if (e.getCode() == KeyCode.ENTER) {
            sendSignIn();
        }
    }

    /**Function responsible for sending the user's credentials to the server for validation.
     * The credentials are sent to message class to check for incorrect/incomplete values.
     * if nothing is found the credentials are sent to the server. Finally, the login attempt
     * is appended to the "login_activity" text file.*/
    private void sendSignIn() throws IOException, SQLException {
        String username = usernameTB.getText();
        String[] validatedCreds = validateCredentials(username, passwordTB.getText()).split(",");
        boolean wasSuccessful = Boolean.parseBoolean(validatedCreds[0]);
        String issue = validatedCreds[1];
        if (wasSuccessful) {
            message.setLoggingIn(true);
            message.setCurrentUser(username);
            stage.changeWindow(root, "Appointments");
        }
        log.logSignInAttempt(loginStamp(usernameTB.getText(), wasSuccessful, issue));
    }

    /**loginStamp creates the stamp to be appended to the login_activity.txt file.
     * The format is "LOGIN (login result( - error if applicable)): (DATE(MM/DD/YYYY) TIME(24Hour)) USER: (user)"
     * Each login attempt is added on a new line of the text file for readability. Also sets the error messaging
     * should that be required.*/
    private String loginStamp(String username, boolean wasSuccessful, String issue) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date loginDate = new Date();

        String loginResult = wasSuccessful ? "SUCCESSFUL" : "FAILED";
        String error = wasSuccessful ? "" : String.format(" - (%s)", issue);

        errorLabel.setText(ResourceBundle.getBundle("Language/language").getString(issue));
        return String.format("\nLOGIN %s%s: %s TIMEZONE: %S USER: %s", loginResult, error, dateFormat.format(loginDate), ZoneId.of(TimeZone.getDefault().getID()), username);
    }

    /**The validateCredentials method handles username and password validation,
     * First it checks for empty values, then special characters, then an empty
     * password field and finally, if all that passes it sends the info to the
     * server for validation there. The method also returns a string to be parsed
     * by the main controller.*/
    public String validateCredentials(String username, String password) throws SQLException {
        String response = "";
        if (username.isEmpty() && password.isEmpty()) {
            response = "false,missingLoginCredentials";
        } else if (username.isEmpty()) {
            response = "false,missingUsername";
        } else if (!username.matches("^[a-zA-Z0-9_]+$")) {
            response = "false,invalidUsername";
        } else if (password.isEmpty()) {
            response = "false,missingPassword";
        } else {
            boolean queryResult = GeneralQuery.validateCredentials(username, password);
            if (queryResult) {
                response = "true,success";
            } else {
                response = "false,invalidCredentials";
            }
        }
        return response;
    }

    /**Initializing the window. resourceBundle handles the localization between the two languages.
     * If another language is selected there will be no labels but there will be an error message
     * displayed to the user. They just may not be able to understand it.
     * */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        message = Message.getInstance();
        stage = new StageTransition();
        log = new SystemLog();

        resourceBundle = ResourceBundle.getBundle("Language/language", Locale.getDefault());

        if (Locale.getDefault().getLanguage().equals("fr") || Locale.getDefault().getLanguage().equals("en")) {
            zoneLabel.setText(resourceBundle.getString("timezone") + ": " +  ZoneId.systemDefault().toString());
            descriptionLabel.setText(resourceBundle.getString("description"));
            usernameLabel.setText(resourceBundle.getString("username"));
            passwordLabel.setText(resourceBundle.getString("password"));
            signInButton.setText(resourceBundle.getString("confirm"));
        } else {
            errorLabel.setText("Unsupported language selected. Apologies for that!");
        }
    }
}
