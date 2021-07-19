package sample.J2J;
import sample.J2J.ClientServerStack.*;

import java.io.IOException;

public class p2pNode {
    BaseNode instance = new BaseNode();
    boolean isConnected;
    Client client;
    Server server;
    Mode mode;
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
            mode = Mode.CLIENT;
        } catch (IOException e1) {
            try {
                server.sendToClient("Ping");
                System.out.println("Server mode");
                mode = Mode.SERVER;
            } catch (IOException e2) {
                System.out.println("Node not found");
                isConnected = false;
                mode = Mode.NULL;
            }
        }finally {
            isConnected = true;
            System.out.println("Connected");
        }
    }

    public String fetchMessage(){
        String message = null;
        connectToPeer();
        switch (mode){
            case CLIENT -> {
                try{
                    message = client.sendToServer(client.receiveFromServer());
                } catch (IOException ignored) {}
            }
            case SERVER -> {
                try {
                    message = server.sendToClient(server.receiveFromClient());
                } catch (IOException ignored){}
            }
            case NULL -> message = "No message sent";
        }
        return message;
    }

    public void sendMessage(String message){
        connectToPeer();
        switch (mode){
            case CLIENT -> {
                try{
                    client.sendToServer(message);
                } catch (IOException ignored) {}
            }
            case SERVER -> {
                try {
                    server.sendToClient(message);
                } catch (IOException ignored){}
            }
            case NULL -> System.out.println("Nobody to broadcast");
        }
    }
}
