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
import java.util.List;
import java.util.ResourceBundle;

public class SortScreen implements Initializable {

    int[] array;
    p2pNode resourceSharer;
    boolean isConnected = false;

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
    }

    @FXML
    public void establishConnection(){
        new Thread(()->{
            if(array != null)
                resourceSharer.sendMessage(Arrays.toString(array));
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
        List<String> arrList = Arrays.asList(strArr.substring(1,strArr.length() - 1).split(", "));
        int[] arr = new int[arrList.size()];
        for(int i = 0; i < arr.length; i++){
            arr[i] = Integer.parseInt(arrList.get(i));
        }
        return arr;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourceSharer = new p2pNode(new BaseNode().getPortNumber(), new BaseNode().getIPAddress());
        Runnable fetchFunction = () -> {
            String message = "";
            while (!isConnected) {
                try {
                    message = resourceSharer.fetchMessage();
                    //System.out.println(message);
                }catch (Exception ignored){}
            }
            array = convertToIntArray(message);
        };
        Thread fetcherThread = new Thread(fetchFunction);
        fetcherThread.setDaemon(true);
        fetcherThread.start();
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
        resourceSharer.sendMessage(Arrays.toString(arrayToSort));
        int[] sortedArray = convertToIntArray(resourceSharer.fetchMessage());
        return new MergeSort().merge(arrayToSort,sortedArray);
    }

    private void initScrollPane(ScrollPane numberPane){
        Text listOfNumbers = new Text();
        String stringArray = Arrays.toString(array);
        listOfNumbers.setFont(Font.font("Monaco",12));
        listOfNumbers.setWrappingWidth(numberPane.getWidth());
        listOfNumbers.setText(stringArray.substring(1,stringArray.length() - 1));
        numberPane.setContent(listOfNumbers);
    }
}
