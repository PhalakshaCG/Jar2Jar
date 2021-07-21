package sample.J2J.ClientServerStack;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends BaseNode {

    private Socket myClientSocket;
    private ServerSocket myServerSocket;
    private PrintWriter outputWriter;
    private BufferedReader inputReader;

    public String receiveFromClient() throws IOException {
        System.out.println("\nReceiving from client...");
        this.data = inputReader.readLine();
        System.out.println(data);
        outputWriter.println("Received");
        return data;
    }

    public void start() throws IOException {
        System.out.println("\nIn server start...");
        myServerSocket = new ServerSocket(getPortNumber());
        myClientSocket = myServerSocket.accept();
        outputWriter = new PrintWriter(myClientSocket.getOutputStream(), true);
        inputReader = new BufferedReader(new InputStreamReader(myClientSocket.getInputStream()));
    }
    public Server(){
        //default
    }

    public Server(int portNumber, String ipAddress) throws IOException{
        super(portNumber,ipAddress);
        System.out.println("\nIn server constructor");
    }
    public void closeSocket() throws IOException {

        inputReader.close();
        outputWriter.close();

        System.out.println("In server close socket");
        myClientSocket.close();
        System.out.println("Server socket closed");

        myServerSocket.close();
        System.out.println("MyServerSocket closed");

    }
}