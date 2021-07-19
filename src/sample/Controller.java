package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.J2J.*;
import sample.J2J.ClientServerStack.BaseNode;

import java.io.IOException;

public class Controller {
    @FXML
    Text messageFetched;

    @FXML
    TextField messageToSend;

    @FXML
    public void sendMessage(ActionEvent e) throws IOException {
        new Thread(() -> {
            p2pNode instance = new p2pNode(new BaseNode().getPortNumber(),new BaseNode().getIPAddress());
            instance.sendMessage(messageToSend.getCharacters().toString());
        }).start();

    }

    @FXML
    public void fetchMessage(ActionEvent e) throws IOException {
        new Thread(() -> {
            p2pNode instance = new p2pNode(new BaseNode().getPortNumber(),new BaseNode().getIPAddress());
            messageFetched.setText(instance.fetchMessage());
        }).start();
    }
}
