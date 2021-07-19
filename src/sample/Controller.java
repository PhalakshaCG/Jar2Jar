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
import java.util.EventListener;
import java.util.ResourceBundle;

public class Controller implements Initializable, EventListener {
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
            messageFetched.setText(p2pInstance.fetchMessage());
            messageStatus.setText("Connected");
        });
        messengerThread.start();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException{
        p2pInstance = new p2pNode(new BaseNode().getPortNumber(),new BaseNode().getIPAddress());
        fetcherThread = new Thread(() -> {
            while (true) {
                try {
                    messageFetched.setText(p2pInstance.fetchMessage());
                    messageStatus.setText("Connected");
                }catch (NullPointerException ignored){}
            }
        });
        fetcherThread.setDaemon(true);
        fetcherThread.start();
    }

    public void closeWindow(){
        fetcherThread.interrupt();
        p2pInstance = null;
        messageStatus.setText(" ");
    }

}