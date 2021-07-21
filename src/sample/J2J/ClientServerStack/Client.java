package sample.J2J.ClientServerStack;

import java.io.*;
import java.net.Socket;

public class Client extends BaseNode {

    private Socket mySocket;
    PrintWriter outputWriter;
    BufferedReader inputReader;

    public String sendToServer(String message) throws IOException {
        System.out.println("\nIn client...");
        outputWriter.println(message);
        this.data = inputReader.readLine();
        System.out.println(this.data);
        return this.data;
    }

    public void startConnection() throws IOException {
        mySocket = new Socket(getIPAddress(), getPortNumber());
        outputWriter = new PrintWriter(mySocket.getOutputStream(), true);
        inputReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
    }

    public Client(){
        //default
    }
    public Client(int portNumber, String ipAddress) throws IOException {
        super(portNumber,ipAddress);
        System.out.println("Client created");
    }
    public void closeSocket() throws IOException {
        inputReader.close();
        outputWriter.close();
        mySocket.close();
    }
}
