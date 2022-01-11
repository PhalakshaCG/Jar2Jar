package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.SomeDeffiePacket.DHHandler;
import sample.TerminalStack.CommandHandler;
import sample.TerminalStack.RemoteExecutor;

import javax.swing.text.Utilities;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
    private final String delim = "`↵";
    private final String keyGenA = "Dej(85((";
    private final String keyGenB = "ekodnoa8*";
    private byte[] secretKey = null;
    private DHHandler DHA = new DHHandler();
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
                        p2pInstance.sendMessage(String.join(delim,commandHandler.getOutput()));
                    }
                    else if(message.contains(thisIsMe)){
                        whoami = message.replace(thisIsMe,"").concat("$ ");
                        terminal.appendText(whoami);
                    }
                    else if(message.contains(keyGenA)){
                        autoFetch.setSelected(true);
                        message=message.replace(delim,"\n");
                        byte[] thatPubKey = message.replace(keyGenA,"").getBytes();
                        byte[][] ret = new DHHandler().PublishGenPubKey(thatPubKey);
                        secretKey = ret[1];
                        String send =new String(ret[0]);
                        send = send.replace("\n",delim);
                        p2pInstance.sendMessage(keyGenB.concat(send));
                    }
                    else if(message.contains(keyGenB)){
                        message=message.replace(delim,"\n");
                        byte[] thatPubKey = message.replace(keyGenB,"").getBytes();
                        System.out.println(2);
                        System.out.println("A    "+thatPubKey);
                        secretKey = DHA.GenerateSecretKey(thatPubKey);
                        System.out.println(secretKey.length);
                        System.out.println(3);
                        System.out.println(secretKey);
                    }
                    else{
                        addTerminalText(message.replace(delim,"\n"));
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
            if(keyEvent.getCode().toString().equals("ENTER") && autoFetch.isSelected()){
                System.out.println(getLastLine());
                p2pInstance.sendMessage(asCommand(getLastLine()));
            }
        });

        terminal.addEventHandler(KeyEvent.KEY_RELEASED,(keyEvent) -> {
            if(keyEvent.getCode().toString().equals("ENTER")){
                terminal.appendText("");
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
            if(secretKey==null) {
                byte[] thisPubKey = DHA.PublicKeyGenerator();
                String pubKey = new String(thisPubKey);
                pubKey.replace("\n",delim);
                p2pInstance.sendMessage(keyGenA.concat(pubKey));
                System.out.println(1);
            }


        }
    }

    @FXML
    public void exitShell(ActionEvent event){
        fetcherThread.interrupt();
        p2pInstance.disconnect();
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    public void clearTerminal(ActionEvent event){
        terminal.clear();
        terminal.appendText(whoami);
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
        //System.out.println("Function called");
        terminal.appendText(text);
        terminal.appendText("\n" + whoami);
    }

    private String getWhoAmI() throws IOException, InterruptedException {
        return String.join(" ",commandHandler.executeCommand("whoami"));
    }
}
