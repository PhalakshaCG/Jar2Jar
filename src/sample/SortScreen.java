package sample;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sample.J2J.ClientServerStack.BaseNode;
import sample.J2J.Mode;
import sample.J2J.p2pNode;
import sample.SorterStack.MergeSort;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SortScreen implements Initializable {

    int[] array;
    p2pNode resourceSharer;
    boolean isConnected = false;

    @FXML
    Text unsortedNumberList;

    @FXML
    ScrollPane unsortedArrayPane;

    @FXML
    ScrollPane sortedArrayPane;

    @FXML
    Text infoText;

    @FXML
    public void generateRandomNumbers(){
        isConnected = false;
        array = new MergeSort().generateRandomArray(100,1000);
        initScrollPane(unsortedArrayPane);
        infoText.setText("Random numbers generated!");
        new Thread(()->{
            resourceSharer.sendMessage(Arrays.toString(array));
            infoText.setText("Connected!");
            isConnected = true;
        }).start();
    }

    @FXML
    public void establishConnection(){
        new Thread(()->{
            String message = resourceSharer.fetchMessage();
            array = convertToIntArray(message);
            infoText.setText("Connected!");
            isConnected = true;
        }).start();
    }

    @FXML
    public void performSort(){
        if(isConnected){
            array = syncSort();
        }
        else{
            array = new MergeSort().sortArray(array);
        }
        initScrollPane(sortedArrayPane);
        infoText.setText("Sorted!");
    }

    private int[] convertToIntArray(String strArr) throws NumberFormatException{
        String[] arrOfStr = strArr.substring(1,strArr.length() - 1).split(", ");
        int[] arr = new int[arrOfStr.length];
        for(int i = 0; i < arr.length; i++){
            arr[i] = Integer.parseInt(arrOfStr[i]);
        }
        return arr;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        unsortedNumberList.setFont(Font.font("Monaco",12));
        resourceSharer = new p2pNode(new BaseNode().getPortNumber(), new BaseNode().getIPAddress());
    }

    private int[] syncSort(){
        int[] arrayToSort = array;
        if(resourceSharer.getMode() == Mode.CLIENT){
            arrayToSort = new int[array.length/2];
            System.arraycopy(array,0,arrayToSort,0,arrayToSort.length);
            arrayToSort = new MergeSort().sortArray(arrayToSort);
        }
        else if(resourceSharer.getMode() == Mode.SERVER){
            arrayToSort = new int[array.length - array.length/2];
            System.arraycopy(array,array.length/2,arrayToSort,0,arrayToSort.length);
        }
        System.out.println(Arrays.toString(arrayToSort));
        resourceSharer.sendMessage(Arrays.toString(arrayToSort));
        int[] sortedArray = convertToIntArray(resourceSharer.fetchMessage());
        return new MergeSort().merge(arrayToSort,sortedArray);
    }

    private void initScrollPane(ScrollPane numberPane){
        String stringArray = Arrays.toString(array);
        unsortedNumberList.setFont(Font.font("Monaco",12));
        unsortedNumberList.setWrappingWidth(numberPane.getWidth());
        unsortedNumberList.setText(stringArray.substring(1,stringArray.length() - 1));
    }
}
