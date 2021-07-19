package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.J2J.*;
import java.io.IOException;

public class Controller {
    @FXML
    Text messageFetched;

    @FXML
    TextField messageToSend;

    @FXML
    public void sendMessage(ActionEvent e) throws IOException {
        p2pNode instance = new p2pNode(6066,"192.168.0.110");
        instance.sendMessage(messageToSend.getCharacters().toString());
    }

    @FXML
    public void fetchMessage(ActionEvent e) throws IOException {
        p2pNode instance = new p2pNode(6066,"192.168.0.110");
        messageFetched.setText(instance.fetchMessage());
    }
}
