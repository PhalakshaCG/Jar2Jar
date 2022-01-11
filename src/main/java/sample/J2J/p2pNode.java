package sample.J2J;
import sample.DHA;
import sample.J2J.ClientServerStack.*;

import java.io.IOException;

public class p2pNode {
    BaseNode instance = new BaseNode();
    boolean isConnected;
    Client client;
    Server server;
    Mode mode;
    String sendMessage = "...";
    String fetchMessage = "...";
    Thread serverInitThread;
    Thread clientInitThread;
    boolean connect = true;
    Thread DH = new Thread(new DHA(this));
    public p2pNode(int portNumber, String ipAddress){
        assert false;
        instance.setIPAddress(ipAddress);
        instance.setPortNumber(portNumber);
        try {
            client = new Client(instance.getPortNumber(), instance.getIPAddress());
            server = new Server(instance.getPortNumber(), instance.getIPAddress());
        } catch (IOException e){
            System.out.println("Instantiation problem ");
        }

    }
    public p2pNode(){
        new Thread(() -> {
        try {
            server = new Server(instance.getPortNumber(), instance.getIPAddress());
        } catch (IOException e){
            System.out.println("Instantiation problem");
        }}).start();
        try {
            client = new Client(instance.getPortNumber(), instance.getIPAddress());
        } catch (IOException e){
            System.out.println("Instantiation problem");
        }
    }

    public boolean connectToPeer() {
        isConnected = true;
        serverInitThread = new Thread(() -> {
            try {
                server.start();
            } catch (IOException e) {
                System.out.println("Could not start server");
                isConnected = false;
            }
        });
        clientInitThread = new Thread(() -> {
            boolean isConnected = false;
            while(!isConnected){
                try{
                    client.startConnection();
                    isConnected = true;
                } catch (IOException ignored) {}
            }
            System.out.println("Connection established");
            if(connect){
                DH.start();
                connect = false;
            }
        });
        serverInitThread.setDaemon(true);
        clientInitThread.setDaemon(true);
        serverInitThread.start();
        clientInitThread.start();

        return isConnected;
    }

    public String fetchMessage() {
        try {
            fetchMessage = server.receiveFromClient();
            System.out.println("Message fetched");
        }catch (IOException e){
            System.out.println("Fetch message exception");
            e.printStackTrace();
        }
        //System.out.println(fetchMessage);
        return fetchMessage;
    }

    public String sendMessage(String message){
        sendMessage = message;
        String ack = "";
        try {
            ack = client.sendToServer(sendMessage);
        }catch (IOException e){
            System.out.println("Send to server exception");
        }
        return ack;
    }
    public Mode getMode(){
        return mode;
    }
    public void disconnect(){
        System.out.println("in \'disconnect\'");
        new Thread(() -> {
            try {
                client.closeSocket();
                System.out.println("Client closed");
                server.closeSocket();
                System.out.println("Server closed");
                serverInitThread.interrupt();
                clientInitThread.interrupt();
            }catch (Exception e){
                System.out.println("Server close exception");
            }
        }).start();
    }
}
