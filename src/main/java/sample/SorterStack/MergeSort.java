package sample.SorterStack;

public class MergeSort implements Sorter{

    public int[] sortArray(int[] array){
        if(array.length == 1)
            return array;
        int[] arr1 = new int[array.length/2];
        int[] arr2 = new int[array.length - array.length/2];
        System.arraycopy(array,0,arr1,0,arr1.length);
        System.arraycopy(array,array.length/2,arr2,0,arr2.length);
        arr1=sortArray(arr1);
        arr2 = sortArray(arr2);
        return merge(arr1,arr2);
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
    public int[] generateRandomArray(int length, int range){
        int[] array = new int[length];
        for(int i = 0; i < array.length; i++){
            array[i] = (int)Math.floor(Math.random()*range + 1);
        }
        return array;
    }
}
