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

    String whoami;

    RemoteExecutor remoteExecutor = new RemoteExecutor();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        p2pInstance = new sample.J2J.p2pNode();
        p2pInstance.connectToPeer();
        autoFetch.setSelected(true);
        remoteExecutor.init();

        terminal.appendText(whoami);

        terminal.addEventHandler(KeyEvent.KEY_PRESSED,(keyEvent) -> {
            //System.out.println(keyEvent.getCode().toString());
            if(keyEvent.getCode().toString().equals("ENTER")){
                System.out.println("Key pressed");
                System.out.println(getLastLine());
                for(String line: remoteExecutor.executeRemoteCommand(getLastLine()))
                    terminal.appendText("\n" + line);

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
        if(autoFetch.isSelected()){
            remoteExecutor.init();
        }
        else{
            remoteExecutor.stop();
        }
    }
}
