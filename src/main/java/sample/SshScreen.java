package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import sample.TerminalStack.CommandHandler;
import sample.TerminalStack.RemoteExecutor;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SshScreen implements Initializable {

    @FXML
    TextArea terminal;

    @FXML
    ToggleButton autoFetch;

    Thread messengerThread;
    Thread fetcherThread;
    sample.J2J.p2pNode p2pInstance;
    Runnable fetchFunction;

    String whoami = "anonymous$ ";


    CommandHandler commandHandler = new CommandHandler();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        p2pInstance = new sample.J2J.p2pNode();
        p2pInstance.connectToPeer();
        autoFetch.setSelected(true);
        fetchFunction = () -> {
            while (autoFetch.isSelected()) {
                try {
                    System.out.println(p2pInstance.fetchMessage());
                }catch (NullPointerException e){
                    //System.out.println("Received null");
                }
            }
        };
        fetcherThread = new Thread(fetchFunction);
        fetcherThread.setDaemon(true);
        fetcherThread.start();

        terminal.addEventHandler(KeyEvent.KEY_PRESSED,(keyEvent) -> {
            //System.out.println(keyEvent.getCode().toString());
            if(keyEvent.getCode().toString().equals("ENTER")){
                System.out.println(getLastLine());
                p2pInstance.sendMessage(getLastLine());
                terminal.appendText(p2pInstance.fetchMessage());

            }
        });
        terminal.addEventHandler(KeyEvent.KEY_RELEASED,(keyEvent) -> {
            if(keyEvent.getCode().toString().equals("ENTER")){
                terminal.appendText(whoami);
            }

        });

    }

    private String getLastLine(){
        String[] lines = terminal.getText().split("\n");
        return lines[lines.length - 1].replace(whoami,"");
    }

    @FXML
    public void fetchFunction(ActionEvent event) {
        System.out.println("ActionEvent");
    }
}
