package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mySQL.DataConnection;
/**Application launching point.*/
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/main.fxml"));
        stage.setTitle("ScheduleBuddy");
        stage.setScene(new Scene(root, 275, 250));
        stage.show();
    }

    public static void main(String[] args) {
        DataConnection.openConnection();
        launch(args);
    }
}