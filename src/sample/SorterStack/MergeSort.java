package sample.SorterStack;

public class MergeSort extends Sorter{

    public void sortArray(){

    }

    public int[] merge(int[] arr1, int[] arr2){
        int[] combinedArr = new int[arr1.length + arr2.length];
        for(int i = 0, j = 0, k = 0; k < combinedArr.length ;k++){
            if(i < arr1.length && j < arr2.length){
                if(arr1[i] <= arr2[j]){
                    combinedArr[k] = arr1[i++];
                }
                else{
                    combinedArr[k] = arr2[j++];
                }
            }
            else{
                if(i < arr1.length){
                    combinedArr[k] = arr1[i++];
                }
                if(j < arr2.length){
                    combinedArr[k] = arr2[j++];
                }
            }
        }
        return combinedArr;
    }
}
