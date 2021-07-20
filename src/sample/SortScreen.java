package sample;

import javafx.fxml.FXML;

import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.text.Text;
import sample.J2J.ClientServerStack.BaseNode;
import sample.J2J.p2pNode;
import sample.SorterStack.MergeSort;
import sample.SorterStack.PrefWriter;

import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SortScreen implements Initializable {

    int[] primaryArray = null;
    int[] sharedArray = null;

    p2pNode resourceSharer;
    boolean isConnected = false;
    long totalTimeTaken;

    Thread connectionThread;
    Thread syncThread;
    Thread localSort;
    Thread peerSort;
    Runnable syncRunnable;
    @FXML
    Text arrayText;

    @FXML
    Text infoText;

    @FXML
    ToggleButton enableSync;

    @FXML
    public void generateRandomNumbers(){
        stopThreads();
        isConnected = false;
        int[] fullArray = new MergeSort().generateRandomArray(new PrefWriter().getLength(),new PrefWriter().getRange());
        primaryArray = synchroniseArrays(fullArray,true);
        System.out.println(Arrays.toString(primaryArray));
        sharedArray = synchroniseArrays(fullArray, false);
        System.out.println(Arrays.toString(sharedArray));
        arrayText.setText(Arrays.toString(fullArray));
        infoText.setText("Random numbers generated!");
        connectionThread = new Thread(()->{
            resourceSharer.sendMessage(arrayToSend(fullArray,SortStatus.UN_SORTED));
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
        if(enableSync.isSelected()){
            localSort = new Thread(()->{
                long startTime1 = System.nanoTime();
                primaryArray = new MergeSort().sortArray(primaryArray);
                System.out.println("Sorted: " + Arrays.toString(primaryArray));
                long endTime1 = System.nanoTime();
                resourceSharer.sendMessage(arrayToSend(primaryArray,SortStatus.IS_SORTED));
                totalTimeTaken = (endTime1 - startTime1);
            });
            localSort.start();

            peerSort = new Thread(()->{
                long startTime2 = System.nanoTime();
                resourceSharer.sendMessage(arrayToSend(new int[]{1},SortStatus.TO_BE_SORTED));
                sharedArray = convertToIntArray(resourceSharer.fetchMessage());
                long endTime2 = System.nanoTime();
                System.out.println(endTime2 - startTime2);
            });
            peerSort.start();
        }
        else{
            long startTime = System.nanoTime();
            primaryArray = new MergeSort().sortArray(primaryArray);
            sharedArray = new MergeSort().sortArray(sharedArray);
            arrayText.setText(Arrays.toString(new MergeSort().merge(primaryArray,sharedArray)));
            long endTime = System.nanoTime();
            System.out.println(endTime - startTime);
        }
        double timeToSort = (double) (totalTimeTaken/1000.0);
        infoText.setText("Time taken: " + timeToSort + "ms");

    }

    private int[] convertToIntArray(String strArr) throws NumberFormatException{
        String[] arrOfStr = strArr.split(", ");
        int[] arr = new int[arrOfStr.length - 1];
        for(int i = 0; i < arr.length; i++){
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
                    int[] temporarySharedArray = convertToIntArray(sharedStrArray);

                    if(getStatus(sharedStrArray).equals(SortStatus.UN_SORTED.toString())){
                        arrayText.setText(Arrays.toString(temporarySharedArray));
                        primaryArray = synchroniseArrays(temporarySharedArray,false);
                        sharedArray = synchroniseArrays(temporarySharedArray,true);
                    }
                    else if(getStatus(sharedStrArray).equals(SortStatus.TO_BE_SORTED.toString())){
                        primaryArray = new MergeSort().sortArray(primaryArray);
                        System.out.println("Sorted: " + Arrays.toString(primaryArray));
                        resourceSharer.sendMessage(arrayToSend(primaryArray,SortStatus.IS_SORTED));
                    }
                    else if(getStatus(sharedStrArray).equals(SortStatus.IS_SORTED.toString())){
                        sharedArray = temporarySharedArray;
                        long startTime1 = System.nanoTime();
                        int[] fullArray = new MergeSort().merge(primaryArray,sharedArray);
                        long endTime1 = System.nanoTime();
                        long mergeTime = endTime1 - startTime1;
                        totalTimeTaken += mergeTime;
                        resourceSharer.sendMessage(arrayToSend(fullArray,SortStatus.UN_SORTED));
                    }
                }catch (NumberFormatException ignored){}
            }
        };
        syncThread = new Thread(syncRunnable);
        syncThread.setDaemon(true);
        syncThread.start();
    }

    private int[] synchroniseArrays(int[] primaryArray, boolean sortStarter){
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

    public void stopThreads(){
        if(syncThread != null && syncThread.isAlive())
            syncThread.interrupt();
        if(connectionThread != null && connectionThread.isAlive())
            connectionThread.interrupt();
        if(peerSort != null && localSort != null){
            peerSort.interrupt();
            localSort.interrupt();
        }
    }
}