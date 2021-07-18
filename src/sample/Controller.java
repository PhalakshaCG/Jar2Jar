package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import sample.J2J.Server;
import java.io.IOException;

public class Controller {
    @FXML
    Text message;

    @FXML
    public void sendMessage(ActionEvent e) throws IOException {
        Server server = new Server();
        server.setPortNumber(6066);
        message.setText(server.sendToClient("Hello from the other side"));

    }
}
