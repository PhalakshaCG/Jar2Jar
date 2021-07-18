package sample.J2J;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.prefs.Preferences;

public class p2pNode implements p2pClient, p2pServer{
    protected final String PortNumber = "port_number";
    protected final String IPAddress = "IP_Address";

    public void setPortNumber(int portNumber){
        Preferences preferences = Preferences.userNodeForPackage(Server.class);
        preferences.putInt(PortNumber,portNumber);

    }
    public int getPortNumber(){
        Preferences preferences = Preferences.userNodeForPackage(Server.class);
        return preferences.getInt(PortNumber,80);
    }
    public void setIPAddress(String ipAddress){
        Preferences preferences = Preferences.userNodeForPackage(Server.class);
        preferences.put(IPAddress,ipAddress);
    }
    public String getIPAddress(){
        Preferences preferences = Preferences.userNodeForPackage(Server.class);
        return preferences.get(IPAddress,"192.168.0.1");
    }
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
