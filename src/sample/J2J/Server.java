package sample.J2J;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.prefs.Preferences;

public class Server extends p2pNode{

    public void sendToClient(String data) throws IOException {
        ServerSocket serverSocket = new ServerSocket(getPortNumber());
        Socket socket = serverSocket.accept();
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(data);
        socket.close();
        serverSocket.close();
    }
    public String receiveFromClient() throws IOException {
        ServerSocket serverSocket = new ServerSocket(getPortNumber());
        Socket socket = serverSocket.accept();
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        String data = inputStream.readUTF();
        socket.close();
        serverSocket.close();
        return data;
    }


}