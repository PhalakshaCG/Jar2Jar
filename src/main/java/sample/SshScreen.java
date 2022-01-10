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

    private String whoami = "anonymous$ ";
    private final String cmdCode = "SSH-CMD-1337";
    private final String thisIsMe = "THIS-IS-ME$";


    CommandHandler commandHandler = new CommandHandler();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        p2pInstance = new sample.J2J.p2pNode();
        p2pInstance.connectToPeer();

        fetchFunction = () -> {
            while (true) {
                try {
                    String message = p2pInstance.fetchMessage();
                    if(isCommand(message)){
                        commandHandler.executeCommand(justCommand(message));
                        p2pInstance.sendMessage(String.join("\n",commandHandler.getOutput()));
                    }
                    else if(message.contains(thisIsMe)){
                        whoami = message.replace(thisIsMe,"").concat("$ ");
                        terminal.appendText(whoami);
                    }
                    else{
                        terminal.appendText(message);
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
        if(autoFetch.isSelected()){
            try {
                p2pInstance.sendMessage(getWhoAmI().concat(thisIsMe));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    private String getWhoAmI() throws IOException, InterruptedException {
        return String.join(" ",commandHandler.executeCommand("whoami"));
    }
}
