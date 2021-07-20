package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.Objects;

public class HomeScreen {
    @FXML
    public void MessageInterface(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("MessagePage.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Jar2Jar");
        Scene scene = new Scene(root);
        scene.addEventHandler(KeyEvent.KEY_PRESSED,(keyEvent) -> {
            //System.out.println(keyEvent.getCode().toString());
            if(keyEvent.getCode().toString().equals("ENTER")){
                Controller controller = loader.getController();
                controller.sendMessage(new ActionEvent());
            }
        });
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            Controller controller = loader.getController();
            controller.closeWindow();
        });
    }
    @FXML
    public void ConfigureInterface(ActionEvent e) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ConfigurePage.fxml")));
        Stage stage = new Stage();
        stage.setTitle("Configure");
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    void SorterInterface(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("sort_screen.fxml")));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Configure");
        stage.setScene(new Scene(root));
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            SortScreen sortScreen = loader.getController();
            sortScreen.stopThreads();
        });
    }
}
