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

    private final String cmdCode = "SSH-CMD-1337";


    CommandHandler commandHandler = new CommandHandler();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        p2pInstance = new sample.J2J.p2pNode();
        p2pInstance.connectToPeer();
        autoFetch.setSelected(true);
        fetchFunction = () -> {
            while (autoFetch.isSelected()) {
                try {
                    String message = p2pInstance.fetchMessage();
                    if(isCommand(message)){
                        commandHandler.executeCommand(justCommand(message));
                        p2pInstance.sendMessage(String.join("\n",commandHandler.getOutput()));
                    }
                    else{
                        addTerminalText(message);
                    }
                }catch (NullPointerException e){
                    //System.out.println("Received null");
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
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
                p2pInstance.sendMessage(asCommand(getLastLine()));
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

    private String asCommand(String cmd){
        return cmdCode.concat(cmd);
    }

    private boolean isCommand(String cmd){
        return cmd.contains(cmdCode);
    }

    private String justCommand(String cmd){
        return cmd.replace(cmdCode,"");
    }

    private void addTerminalText(String text){
        terminal.appendText(text);
        terminal.appendText("\n" + whoami);
    }
}
