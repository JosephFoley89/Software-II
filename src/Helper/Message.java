package Helper;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import mySQL.AppQuery;
import java.sql.SQLException;


/**Helper singleton class to handle Error Messaging throughout the application
 * @author Joe Foley*/
public class Message {
    public static final Message message = new Message();

    /**Constructor*/
    private Message(){};
    private String currentUser;
    private boolean isLoggingIn = true;

    private ObservableList<Integer> appointmentID;
    private ObservableList<String> formattedAppointmentDateTime;

    /**Instance accessor.*/
    public static Message getInstance() { return message; }

    /**Getter and Setter methods used to check to see if the current user has an upcoming appointment.*/
    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String user) { this.currentUser = user; }
    public ObservableList<Integer> getAppointmentID() { return appointmentID;}
    public void setAppointmentID(ObservableList<Integer> id) { this.appointmentID = id; }
    public ObservableList<String> getFormattedAppointmentDateTime() { return formattedAppointmentDateTime; }
    public void setFormattedAppointmentDateTime(ObservableList<String> formattedDateTime) { this.formattedAppointmentDateTime = formattedDateTime; }
    public void setLoggingIn(boolean loggingIn) { this.isLoggingIn = loggingIn; }

    /**This method is used to display an alert upon login. The alert indicates whether the user has upcoming appointment(s) or not.
     * This method will not display if the user navigates back to the appointments screen unless they sign out first.
     * There is no requirement that users are not assigned multiple appointments, therefore, each appointment that the
     * user has asssigned to them will be listed in the appointment alert. */
    public void upcomingAppointmentAlert() throws SQLException {
        if (isLoggingIn) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (AppQuery.hasUpcomingAppointment(this.currentUser)) {
                String appointmentInfo = "";
                alert.setTitle("Upcoming appointment!");
                for (Integer id : getAppointmentID()) {
                    appointmentInfo += id.toString() + ": ";
                    for (String s : getFormattedAppointmentDateTime()) {
                        appointmentInfo += s + "\n";
                        break;
                    }
                }
                alert.setContentText("You have an appointment:\n"+appointmentInfo);
            } else {
                alert.setTitle("No upcoming appointments.");
                alert.setContentText("You have no upcoming appointments.");
            }
            alert.showAndWait();
            isLoggingIn = false;
        }
    }

}
