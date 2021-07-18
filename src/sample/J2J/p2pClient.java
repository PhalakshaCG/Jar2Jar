package sample.J2J;

import java.io.IOException;

public interface p2pClient {
    public void sendToServer(String data) throws IOException;
    public String receiveFromServer() throws IOException;
}
