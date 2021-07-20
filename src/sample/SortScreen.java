package sample;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;
import sample.J2J.ClientServerStack.BaseNode;
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

    Thread connectionThread;
    Thread syncThread;
    Runnable syncRunnable;
    @FXML
    Text arrayText;

    @FXML
    Text infoText;

    @FXML
    ToggleButton enableSync;

    @FXML
    public void generateRandomNumbers(){
        isConnected = false;
        primaryArray = new MergeSort().generateRandomArray(20,1000);
        arrayText.setText(Arrays.toString(primaryArray));
        infoText.setText("Random numbers generated!");
        connectionThread = new Thread(()->{
            resourceSharer.sendMessage(arrayToSend(primaryArray,SortStatus.UN_SORTED));
            infoText.setText("Connected!");
            isConnected = true;
        });
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    @FXML
    public void establishConnection(){
        if(!enableSync.isSelected()){
            isConnected = false;
            syncThread.interrupt();
        }
        else{
            isConnected = true;
            syncThread = new Thread(syncRunnable);
            syncThread.setDaemon(true);
            syncThread.start();
        }
    }

    @FXML
    public void performSort(){
        if(isConnected){
            new Thread(()->{
                primaryArray = synchroniseArrays(true);
                System.out.println(Arrays.toString(primaryArray));
                primaryArray = new MergeSort().sortArray(primaryArray);
                resourceSharer.sendMessage(arrayToSend(sharedArray,SortStatus.IS_SORTED));
            }).start();

            new Thread(()->{
                sharedArray = synchroniseArrays(false);
                System.out.println(Arrays.toString(sharedArray));
                resourceSharer.sendMessage(arrayToSend(sharedArray,SortStatus.TO_BE_SORTED));
                sharedArray = convertToIntArray(resourceSharer.fetchMessage());
            }).start();
        }
        else{
            primaryArray = new MergeSort().sortArray(primaryArray);
            arrayText.setText(Arrays.toString(primaryArray));
        }
        infoText.setText("Sorted!");
    }

    private int[] convertToIntArray(String strArr) throws NumberFormatException{
        String[] arrOfStr = strArr.split(", ");
        int[] arr = new int[arrOfStr.length];
        for(int i = 0; i < arr.length - 1; i++){
            arr[i] = Integer.parseInt(arrOfStr[i]);
        }
        return arr;
    }

    private String getStatus(String a){
        String[] stat = a.split(", ");
        return stat[stat.length - 1];
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resourceSharer = new p2pNode(new BaseNode().getPortNumber(), new BaseNode().getIPAddress());
        syncRunnable = ()->{
            while(enableSync.isSelected()){
                try{
                    String sharedStrArray = resourceSharer.fetchMessage();
                    sharedArray = convertToIntArray(sharedStrArray);

                    if(getStatus(sharedStrArray).equals(SortStatus.UN_SORTED.toString())){
                        arrayText.setText(Arrays.toString(sharedArray));
                    }
                    else if(getStatus(sharedStrArray).equals(SortStatus.TO_BE_SORTED.toString())){
                        primaryArray = new MergeSort().sortArray(sharedArray);
                        resourceSharer.sendMessage(arrayToSend(primaryArray,SortStatus.IS_SORTED));
                    }
                    else if(getStatus(sharedStrArray).equals(SortStatus.IS_SORTED.toString())){
                        arrayText.setText(Arrays.toString(new MergeSort().merge(primaryArray,sharedArray)));
                    }
                }catch (NumberFormatException ignored){}
            }
        };
        syncThread = new Thread(syncRunnable);
        syncThread.setDaemon(true);
        syncThread.start();
    }

    private int[] synchroniseArrays(boolean sortStarter){
        int[] arrayToSort;
        if(sortStarter){
            arrayToSort = new int[primaryArray.length/2];
            System.arraycopy(primaryArray,0,arrayToSort,0,arrayToSort.length);
        }
        else {
            arrayToSort = new int[primaryArray.length - primaryArray.length/2];
            System.arraycopy(primaryArray, primaryArray.length/2,arrayToSort,0,arrayToSort.length);
        }
        return arrayToSort;
    }

    private String arrayToSend(int[] array, SortStatus status){
        String strArr = Arrays.toString(array);
        strArr = strArr.substring(1,strArr.length() - 1);
        strArr = strArr + ", " + status;
        return  strArr;
    }
}
