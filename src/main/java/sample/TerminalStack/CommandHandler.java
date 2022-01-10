package sample.TerminalStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    Runtime rt;
    Process proc;

    ArrayList<String> errorStream = new ArrayList<>();
    ArrayList<String> dataStream = new ArrayList<>();


    public List<String> executeCommand(String command) throws IOException, InterruptedException {
        String[] commands = command.split(" ");
        proc = rt.exec(commands);
        proc.waitFor();

        dataStream.clear();
        errorStream.clear();

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String s;

        while((s = stdInput.readLine()) != null){
            dataStream.add(s);
            System.out.println(s);
        }

        while((s = stdError.readLine()) != null){
            errorStream.add(s);
            System.out.println(s);
        }

        return dataStream;
    }

    List<String> getOutput(){
        return dataStream;
    }

    List<String> errorMessage(){
        return errorStream;
    }

    public CommandHandler() {
        rt = Runtime.getRuntime();
    }

}
