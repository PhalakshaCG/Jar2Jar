package sample.J2J;
import sample.J2J.ClientServerStack.*;

import java.io.IOException;

public class p2pNode {
    BaseNode instance = new BaseNode();
    boolean isConnected;
    Client client;
    Server server;
    public p2pNode(int portNumber, String ipAddress){
        assert false;
        instance.setIPAddress(ipAddress);
        instance.setPortNumber(portNumber);
        client = new Client(instance.getPortNumber(), instance.getIPAddress());
        server = new Server(instance.getPortNumber(), instance.getIPAddress());

    }

    public void connectToPeer() {
        try {
            client.sendToServer("Ping");
        } catch (IOException e1) {
            try {
                server.sendToClient("Ping");
            } catch (IOException e2) {
                System.out.println("Node not found");
                isConnected = false;
            }
        }finally {
            isConnected = true;
        }
    }

    public String fetchMessage(){
        String message = null;
        connectToPeer();
        if(isConnected) {
            try {
                message = client.sendToServer("Ping");
            } catch (IOException e) {
                try {
                    message = server.sendToClient("Ping");
                } catch (IOException ignored){}
            }
        }
        else {
            message = "Not connected to a peer";
        }
        return message;
    }

    public void sendMessage(String message){
        connectToPeer();
        if(isConnected){
            try {
                client.sendToServer(message);
            } catch (IOException e) {
                try {
                     server.sendToClient(message);
                } catch (IOException ignored){}
            }
        }
    }
}
