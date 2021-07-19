package sample.SorterStack;

abstract public class Sorter {
    int[] array;

    public void generateRandomArray(int length, int range){
        array = new int[length];
        for(int i = 0; i < array.length; i++){
            array[i] = (int)Math.floor(Math.random()*range + 1);
        }
    }
    public abstract void sortArray();

    public int[] getArray(){
        return array;
    }
}
