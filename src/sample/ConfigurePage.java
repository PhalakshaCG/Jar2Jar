package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.J2J.ClientServerStack.BaseNode;

import javafx.event.ActionEvent;
import sample.SorterStack.PrefWriter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class ConfigurePage implements Initializable {

    @FXML
    TextField portNumber;

    @FXML
    TextField ipAddress;

    @FXML
    TextField range;

    @FXML
    TextField length;

    @FXML
    void saveButton(ActionEvent e){
        try{
            new BaseNode().setPortNumber(Integer.parseInt(portNumber.getCharacters().toString()));
            new BaseNode().setIPAddress(ipAddress.getCharacters().toString());
            new PrefWriter().writeData(
                    Integer.parseInt(length.getCharacters().toString()),
                    Integer.parseInt(range.getCharacters().toString())
            );
            System.out.println("Saved");
        }catch (NumberFormatException exception){
            new BaseNode().setPortNumber(80);
            new BaseNode().setIPAddress(ipAddress.getCharacters().toString());
            new PrefWriter().writeData(10,100);
        }
        ((Stage)((Node)e.getSource()).getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        portNumber.setText(Integer.toString(new BaseNode().getPortNumber()));
        ipAddress.setText(new BaseNode().getIPAddress());
        length.setText(Integer.toString( new PrefWriter().getLength()));
        range.setText(Integer.toString( new PrefWriter().getRange()));
    }
}
