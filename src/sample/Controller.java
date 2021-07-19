package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import sample.J2J.*;
import sample.J2J.ClientServerStack.BaseNode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Text messageFetched;

    @FXML
    TextField messageToSend;

    @FXML
    Text messageStatus;

    Thread messengerThread;
    p2pNode instance = new p2pNode(new BaseNode().getPortNumber(),new BaseNode().getIPAddress());

    @FXML
    public void sendMessage(ActionEvent e) {
        messengerThread = new Thread(() -> {
            instance.sendMessage(messageToSend.getCharacters().toString());
            messageStatus.setText("Message sent!");
        });
        messengerThread.start();

    }

    @FXML
    public void fetchMessage(ActionEvent e) {
        messengerThread = new Thread(() -> {
            messageFetched.setText(instance.fetchMessage());
            messageStatus.setText("Message received!");
        });
        messengerThread.start();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread fetcher = new Thread(() -> {
            while(true){
                messageFetched.setText(instance.fetchMessage());
                messageStatus.setText("Message received!");
            }
        });
        fetcher.setDaemon(true);
        fetcher.start();
    }
}