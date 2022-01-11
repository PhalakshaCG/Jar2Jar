package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;

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
    sample.J2J.p2pNode p2pInstance;
    Runnable fetchFunction;

    @FXML
    public void sendMessage(ActionEvent e) {
        messengerThread = new Thread(() -> {
            messageStatus.setText(p2pInstance.sendMessage(messageToSend.getCharacters().toString()));
        });
        messengerThread.start();

    }

    @FXML
    public void fetchMessage(ActionEvent e) {
        messageFetched.setText(p2pInstance.fetchMessage());
        messageStatus.setText("Message received!");
        if(fetcherThread != null)
            fetcherThread.interrupt();
        fetcherThread = new Thread(() -> {
            messageFetched.setText(p2pInstance.fetchMessage());
        });
        fetcherThread.start();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) throws NullPointerException{
        p2pInstance = new sample.J2J.p2pNode();
        p2pInstance.connectToPeer();
        autoFetch.setSelected(true);
        fetchFunction = () -> {
            while (autoFetch.isSelected()) {
                try {
                    messageFetched.setText(p2pInstance.fetchMessage());
                }catch (NullPointerException e){
                    //System.out.println("Received null");
                }
            }
        };
        fetcherThread = new Thread(fetchFunction);
        fetcherThread.setDaemon(true);
        fetcherThread.start();

    }

    public void closeWindow(){
        autoFetch.setSelected(false);
        fetcherThread.interrupt();
        System.out.println("Fetcher interrupted");
        p2pInstance.disconnect();
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