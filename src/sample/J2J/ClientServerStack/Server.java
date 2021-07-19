package sample.J2J.ClientServerStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends BaseNode {

    public String sendToClient(String data) throws IOException {
        ServerSocket serverSocket = new ServerSocket(getPortNumber());
        Socket socket = serverSocket.accept();
        socket.setSoTimeout(5*1000);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(data);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        this.data = inputStream.readUTF();
        socket.close();
        serverSocket.close();
        //System.out.println("Sent: " + data);
        //System.out.println("Received: " + this.data);
        return this.data;
    }
    public String receiveFromClient() throws IOException {
        return data;
    }
    public Server(){
        super();
        //default
    }
    public Server(int portNumber, String ipAddress){
        super(portNumber,ipAddress);
    }


}