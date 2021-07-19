package sample.J2J.ClientServerStack;
import java.util.prefs.Preferences;

public class BaseNode {
    protected final String PortNumber = "port_number";
    protected final String IPAddress = "IP_Address";
    String data = "Ping";

    public void setPortNumber(int portNumber){
        Preferences preferences = Preferences.userNodeForPackage(BaseNode.class);
        preferences.putInt(PortNumber,portNumber);

    }
    public int getPortNumber(){
        Preferences preferences = Preferences.userNodeForPackage(BaseNode.class);
        return preferences.getInt(PortNumber,80);
    }
    public void setIPAddress(String ipAddress){
        Preferences preferences = Preferences.userNodeForPackage(BaseNode.class);
        preferences.put(IPAddress,ipAddress);
    }
    public String getIPAddress(){
        Preferences preferences = Preferences.userNodeForPackage(BaseNode.class);
        return preferences.get(IPAddress,"192.168.0.1");
    }
    public String getData(){
        return data;
    }
    public void setData(String data){
        this.data = data;
    }
    public BaseNode(){
        //default constructor
    }
    BaseNode(int portNumber, String ipAddress){
        setPortNumber(portNumber);
        setIPAddress(ipAddress);
    }
}
