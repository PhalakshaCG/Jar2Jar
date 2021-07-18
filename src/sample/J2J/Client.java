package sample.J2J;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends p2pNode{

    public String sendToServer(String data) throws IOException {
        int portNumber = getPortNumber();
        String ipAddress = getIPAddress();
        Socket socket = new Socket(ipAddress,portNumber);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(data);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        data = inputStream.readUTF();
        socket.close();
        return data;
    }
    public String receiveFromServer() throws IOException {
        return data;
    }
}
