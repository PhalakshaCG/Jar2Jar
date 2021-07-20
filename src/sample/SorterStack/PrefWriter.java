package sample.SorterStack;

import java.util.prefs.Preferences;

public class PrefWriter {
    final String range = "RANGE";
    final String length = "LENGTH";

    public void writeData(int len, int rng){
        Preferences preferences = Preferences.userNodeForPackage(PrefWriter.class);
        preferences.putInt(length,len);
        preferences.putInt(range,rng);
    }

    public int getLength(){
        Preferences preferences = Preferences.userNodeForPackage(PrefWriter.class);
        return preferences.getInt(length,10);
    }

    public int getRange(){
        Preferences preferences = Preferences.userNodeForPackage(PrefWriter.class);
        return preferences.getInt(range,100);
    }
}
