package sample.J2J;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.prefs.Preferences;

public class p2pNode {
    protected final String PortNumber = "port_number";
    protected final String IPAddress = "IP_Address";
    String data;

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
}
