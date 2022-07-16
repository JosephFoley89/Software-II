package Helper;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mySQL.DataConnection;
import java.io.IOException;

/**Helper class to transition between stages.
 * @author Joe Foley*/
public class StageTransition {
    private static DataConnection connection;

    /**Method for moving the user between windows. The destination is determined in the controller.*/
    public void changeWindow(Parent root, String destination) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource(String.format("/view/%s.fxml", destination)));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) root.getScene().getWindow();

        if (destination != "view/main") { stage.setTitle("ScheduleBuddy - " + destination); }
        else { stage.setTitle("ScheduleBuddy"); }

        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
    }
}
