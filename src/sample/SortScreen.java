package sample;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import sample.J2J.ClientServerStack.BaseNode;
import sample.J2J.Mode;
import sample.J2J.p2pNode;
import sample.SorterStack.MergeSort;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SortScreen implements Initializable {

    int[] primaryArray = null;
    int[] sharedArray = null;

    p2pNode resourceSharer;
    boolean isConnected = false;
    boolean isSorterHost = false;

    Thread connectionThread;
    Thread syncThread;

    @FXML
    Text unsortedArrayText;

    @FXML
    Text sortedArrayText;

    @FXML
    Text infoText;

    @FXML
    public void generateRandomNumbers(){
        isConnected = false;
        primaryArray = new MergeSort().generateRandomArray(100,1000);
        unsortedArrayText.setText(convertToStrArray(primaryArray));
        infoText.setText("Random numbers generated!");
        connectionThread = new Thread(()->{
            resourceSharer.sendMessage(Arrays.toString(primaryArray));
            infoText.setText("Connected!");
            isConnected = true;
            syncThread.start();
        });
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    @FXML
    public void establishConnection(){
        connectionThread = new Thread(()->{
            String message = resourceSharer.fetchMessage();
            primaryArray = convertToIntArray(message);
            infoText.setText("Connected!");
            isConnected = true;
            if(primaryArray != null){
                unsortedArrayText.setText(convertToStrArray(primaryArray));
                syncThread.start();
            }
        });
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    @FXML
    public void performSort(){
        isSorterHost = true;
        if(isConnected){
            syncThread.interrupt();
            primaryArray = syncSort();
            System.out.println(primaryArray.length);
            resourceSharer.sendMessage(Arrays.toString(primaryArray));
        }
        else{
            primaryArray = new MergeSort().sortArray(primaryArray);
        }
        sortedArrayText.setText(convertToStrArray(primaryArray));
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
        resourceSharer = new p2pNode(new BaseNode().getPortNumber(), new BaseNode().getIPAddress());

        syncThread = new Thread(()->{
            while(sharedArray == null){
                try{
                    String sharedStrArray = resourceSharer.fetchMessage();
                    sharedArray = convertToIntArray(sharedStrArray);
                }catch (NumberFormatException ignored){}
            }
            primaryArray = syncSort();
            sortedArrayText.setText(convertToStrArray(new MergeSort().merge(primaryArray,sharedArray)));
        });
        syncThread.setDaemon(true);
    }

    private int[] syncSort(){
        int[] arrayToSort = primaryArray;
        if(resourceSharer.getMode() == Mode.CLIENT){
            arrayToSort = new int[primaryArray.length/2];
            System.arraycopy(primaryArray,0,arrayToSort,0,arrayToSort.length);
            arrayToSort = new MergeSort().sortArray(arrayToSort);
        }
        else if(resourceSharer.getMode() == Mode.SERVER){
            arrayToSort = new int[primaryArray.length - primaryArray.length/2];
            System.arraycopy(primaryArray, primaryArray.length/2,arrayToSort,0,arrayToSort.length);
        }
        return arrayToSort;
    }

    private String convertToStrArray(int[] array){
        String strArr = Arrays.toString(array);
        return strArr.substring(1,strArr.length() - 1);
    }
}
