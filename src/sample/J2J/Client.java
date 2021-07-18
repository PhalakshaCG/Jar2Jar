package sample.J2J;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.prefs.Preferences;

public class Client extends p2pNode{

    public void sendToServer(String data) throws IOException {
        int portNumber = getPortNumber();
        String ipAddress = getIPAddress();
        Socket socket = new Socket(ipAddress,portNumber);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(data);
        socket.close();
    }
    public String receiveFromServer() throws IOException {
        int portNumber = getPortNumber();
        String ipAddress = getIPAddress();
        Socket socket = new Socket(ipAddress,portNumber);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        String data = inputStream.readUTF();
        socket.close();
        return data;
    }
}
