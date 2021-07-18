package sample.J2J;

import java.io.IOException;

public interface p2pServer {
    public String sendToClient(String data) throws IOException;
    public String receiveFromClient() throws IOException;
}
