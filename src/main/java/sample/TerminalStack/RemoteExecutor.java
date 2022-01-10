package sample.TerminalStack;

import sample.J2J.p2pNode;

import java.io.IOException;
import java.util.List;

public class RemoteExecutor extends CommandHandler{

    Thread messengerThread;
    Thread fetcherThread;
    sample.J2J.p2pNode p2pInstance;
    Runnable fetchFunction;

    public RemoteExecutor(){

    }

    public void init(){
        p2pInstance = new p2pNode();
        p2pInstance.connectToPeer();
        fetchFunction = () ->{
            while(true){
                if(p2pInstance.fetchMessage().equals("SSH-EXEC-1337")){
                    String command = p2pInstance.fetchMessage();
                    try {
                        List<String> outputs = executeCommand(command);
                        String op = String.join("\n",outputs);
                        p2pInstance.sendMessage(op);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        };
        
        fetcherThread = new Thread(fetchFunction);
        fetcherThread.setDaemon(true);
        fetcherThread.start();
    }

    public void stop(){
        fetcherThread.interrupt();
    }

    public String[] executeRemoteCommand(String command){
        p2pInstance.sendMessage("SSH-EXEC-1337");
        p2pInstance.sendMessage(command);
        String output = p2pInstance.fetchMessage();
        return output.split("\n");
    }

}
