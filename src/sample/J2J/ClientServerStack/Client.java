package sample.J2J.ClientServerStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends BaseNode {

    public String sendToServer(String data) throws IOException {
        int portNumber = getPortNumber();
        String ipAddress = getIPAddress();
        Socket socket = new Socket(ipAddress,portNumber);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        outputStream.writeUTF(data);
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        this.data = inputStream.readUTF();
        socket.close();
        return this.data;
    }
    public String receiveFromServer() throws IOException {
        return data;
    }
    public Client(){
        //default
    }
    public Client(int portNumber, String ipAddress){
        super(portNumber,ipAddress);
    }

}
