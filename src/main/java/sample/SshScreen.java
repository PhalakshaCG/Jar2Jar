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
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
    int count=0;
    private String whoami = "anonymous$ ";
    private final String cmdCode = "SSH-CMD-1337";
    private final String thisIsMe = "THIS-IS-ME$";
    private final String delim = "`↵";
    private final String keyGenA = "keyGenA";
    private final String keyGenB = "keyGenBFrickingBitch";
    private byte[] secretKey = null;
    private final String endTagA = "frickA";
    private final String endTagB = "frickB";
    private final String opCode = "tatakae";
    //F
    private DHHandler DHA = new DHHandler();
    CommandHandler commandHandler = new CommandHandler();
    StringBuilder messageA=new StringBuilder(),messageB = new StringBuilder();
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
                        p2pInstance.sendMessage(String.join(delim,commandHandler.getOutput()).concat(opCode));
                    }
                    else if(message.contains(thisIsMe)){
                        whoami = message.replace(thisIsMe,"").concat("$ ");
                        terminal.appendText(whoami);
                    }
                    else if(message.contains(keyGenA)&&!message.contains(endTagA)) {
                        autoFetch.setSelected(true);
                        message = message.replace(delim, "\n");
                        message = message.replace(keyGenA, "");
                        messageA = new StringBuilder(message);
                        System.out.println("\n"+keyGenA);
                    }
                    else if(message.contains(endTagA)){
                        message = message.replace(delim, "\n");
                        message = message.replace(endTagA, "");
                        message = message.replace(keyGenA, "");
                        //messageA.append(message);
                        //String s = messageA.toString().split("@$H")[1];
                        String s = message;
                        System.out.println(s);
                        byte[] thatPubKey = StringtoByte(s);
                        byte[][] ret = new DHHandler().PublishGenPubKey(thatPubKey);
                        secretKey = ret[1];
                        System.out.println("B    "+ Arrays.toString(secretKey));
                        String send =bytetoString(ret[0]);
                        send = send.replace("\n",delim);
                        send = send.concat(endTagB);
                        p2pInstance.sendMessage(keyGenB.concat(send));
                        System.out.println("\n"+endTagA);
                    }
                    else if(message.contains(keyGenB)&&!message.contains(endTagB)) {
                        message = message.replace(delim, "\n");
                        message = message.replace(keyGenB, "");
                        messageB = new StringBuilder(message);
                        System.out.println("\n"+keyGenB);
                    }
                    else if(message.contains(endTagB)){
                        message = message.replace(delim,"\n");
                        message = message.replace(endTagB,"");
                        message = message.replace(keyGenB, "");
                        messageB.append(message);
                        byte[] thatPubKey = StringtoByte(messageB.toString());
                        System.out.println(2);
                        System.out.println("A    "+Arrays.toString(thatPubKey));
                        secretKey = DHA.GenerateSecretKey(thatPubKey);
                        System.out.println(secretKey.length);
                        System.out.println(3);
                        //System.out.println(secretKey);
                        System.out.println("\n"+endTagB);
                    }
                    else if(message.contains(opCode)){
                        addTerminalText(message.replace(delim,"\n").replace(opCode,""));
                    }
                    else{
                        message = message.replace(delim,"\n");
                        messageA.append(message);
                        messageB.append(message);
                        System.out.println("\n"+"Appended");
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
            autoFetch.setSelected(true);
            return;
        }
        if(autoFetch.isSelected()){
           try {
                p2pInstance.sendMessage(getWhoAmI().concat(thisIsMe));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            if(secretKey==null) {
                byte[] thisPubKey = DHA.PublicKeyGenerator();
                String pubKey = bytetoString(thisPubKey);
                System.out.println(pubKey);
                pubKey = pubKey.replace("\n",delim);
                pubKey = pubKey.concat(endTagA);
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
    public String bytetoString(byte[] byteArray){

            String hex = "";
            // Iterating through each byte in the array
            for (byte i : byteArray) {
                hex += String.format("%02X", i);
            }
            return (hex);

    }
    public byte[] StringtoByte(String s){
        byte[] ans = new byte[s.length() / 2];
        //System.out.println("Hex String : "+s);
        for (int i = 0; i < ans.length; i++) {
            int index = i * 2;
            // Using parseInt() method of Integer class
            int val = Integer.parseInt(s.substring(index, index + 2), 16);
            ans[i] = (byte)val;
        }

        // Printing the required Byte Array
        System.out.print("Byte Array : ");
        for (int i = 0; i < ans.length; i++) {
            System.out.print(ans[i] + " ");
        }
        return ans;
    }

    private String getWhoAmI() throws IOException, InterruptedException {
        return String.join(" ",commandHandler.executeCommand("whoami"));
    }
}
