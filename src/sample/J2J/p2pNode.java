package sample.J2J;
import sample.J2J.ClientServerStack.*;

import java.io.IOException;

public class p2pNode {
    BaseNode instance = new BaseNode();
    boolean isConnected;
    Client client;
    Server server;
    Mode mode;
    String sendMessage = "ping";
    String fetchMessage = "ping";

    public p2pNode(int portNumber, String ipAddress){
        assert false;
        instance.setIPAddress(ipAddress);
        instance.setPortNumber(portNumber);
        client = new Client(instance.getPortNumber(), instance.getIPAddress());
        server = new Server(instance.getPortNumber(), instance.getIPAddress());

    }

    public boolean connectToPeer() {
        try {
            client.sendToServer(sendMessage);
            System.out.println("Client mode");
            mode = Mode.CLIENT;
            isConnected = true;
        } catch (IOException e1) {
            try {
                server.sendToClient(sendMessage);
                System.out.println("Server mode");
                mode = Mode.SERVER;
                isConnected = true;
            } catch (IOException e2) {
                //System.out.println("Node not found");
                isConnected = false;
                mode = Mode.NULL;
            }
            //System.out.println("Connected");
        }
        return isConnected;
    }

    public String fetchMessage(){
        String message = null;
        switch (mode){
            case CLIENT -> {
                try{
                    fetchMessage = client.sendToServer(sendMessage);
                } catch (IOException ignored) {}
            }
            case SERVER -> {
                try {
                    fetchMessage = server.sendToClient(sendMessage);
                } catch (IOException ignored){}
            }
            case NULL -> message = "No message sent";
        }
        //System.out.println(fetchMessage);
        return fetchMessage;
    }

    public void sendMessage(String message){
        sendMessage = message;
        switch (mode){
            case CLIENT -> {
                try{
                    fetchMessage = client.sendToServer(sendMessage);
                } catch (IOException ignored) {}
            }
            case SERVER -> {
                try {
                    fetchMessage = server.sendToClient(sendMessage);
                } catch (IOException ignored){}
            }
            case NULL -> System.out.println("Nobody to broadcast");
        }
    }
    public Mode getMode(){
        return mode;
    }
}
