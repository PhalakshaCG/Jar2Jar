package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
import sample.J2J.*;
import sample.J2J.ClientServerStack.BaseNode;

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
    Thread fetcherThread;
    p2pNode p2pInstance;

    @FXML
    public void sendMessage(ActionEvent e) {
        messengerThread = new Thread(() -> {
            p2pInstance.sendMessage(messageToSend.getCharacters().toString());
            messageStatus.setText("Message sent!");
        });
        messengerThread.start();

    }

    @FXML
    public void fetchMessage(ActionEvent e) {
        messengerThread = new Thread(() -> {
            String previousText = messageFetched.getText();
            messageFetched.setText(p2pInstance.fetchMessage());
            if(messageFetched.getText().equals("ping"))
                messageStatus.setText("Connected");
            else if(!messageFetched.getText().equals(previousText))
                messageStatus.setText("Message received!");
        });
        messengerThread.start();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        p2pInstance = new p2pNode(new BaseNode().getPortNumber(),new BaseNode().getIPAddress());
        fetcherThread = new Thread(() -> {
            while(true){
                String previousText = messageFetched.getText();
                messageFetched.setText(p2pInstance.fetchMessage());
                messageStatus.setText("Connected");
            }
        });
        fetcherThread.setDaemon(true);
        fetcherThread.start();
    }

    public void closeWindow(){
        p2pInstance = null;
        fetcherThread.interrupt();
    }
}