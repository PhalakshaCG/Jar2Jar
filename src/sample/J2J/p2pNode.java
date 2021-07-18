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
            System.out.println("Client mode");
        } catch (IOException e1) {
            try {
                server.sendToClient("Ping");
                System.out.println("Server mode");
            } catch (IOException e2) {
                System.out.println("Node not found");
                isConnected = false;
            }
        }finally {
            isConnected = true;
            System.out.println("Connected");
        }
    }

    public String fetchMessage(){
        String message = null;
        connectToPeer();
        if(isConnected) {
            try {
                message = client.receiveFromServer();
            } catch (IOException e) {
                try {
                    message = server.receiveFromClient();
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
