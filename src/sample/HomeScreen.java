package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Objects;

public class HomeScreen {
    @FXML
    public void MessageInterface(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MessagePage.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Jar2Jar");
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    public void ConfigureInterface(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ConfigurePage.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Configure");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
