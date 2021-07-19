package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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

    @FXML
    ToggleButton autoFetch;

    Thread messengerThread;
    Thread fetcherThread;
    p2pNode p2pInstance;
    Runnable fetchFunction;

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
        messageFetched.setText(p2pInstance.fetchMessage());
        messageStatus.setText("Message received!");
        new Thread(() -> {
            messageFetched.setText(p2pInstance.fetchMessage());
            messageStatus.setText("Message received!");
        }).start();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException{
        p2pInstance = new p2pNode(new BaseNode().getPortNumber(),new BaseNode().getIPAddress());
        autoFetch.setSelected(true);
        fetchFunction = () -> {
            while (!autoFetch.isSelected()) {
                try {
                    messageFetched.setText(p2pInstance.fetchMessage());
                    messageStatus.setText("Connected");
                }catch (NullPointerException ignored){}
            }
        };
        fetcherThread = new Thread(fetchFunction);
        fetcherThread.setDaemon(true);
        fetcherThread.start();
    }

    public void closeWindow(){
        fetcherThread.interrupt();
        p2pInstance = null;
        messageStatus.setText(" ");
    }

    @FXML
    public void pauseThread(ActionEvent e) throws InterruptedException {
        if(!autoFetch.isSelected()){
            fetcherThread.interrupt();
        }
        else{
            fetcherThread = new Thread(fetchFunction);
            fetcherThread.setDaemon(true);
            fetcherThread.start();
        }
    }

}