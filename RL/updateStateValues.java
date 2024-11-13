import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class updateStateValues {
    public static void main(String[] args) throws IOException,ClassNotFoundException{
        try {
        int result = Integer.parseInt(args[0]);
        double resultVal = 0.5;
        if (result == 0) resultVal = 0.0;
        else if (result == 1) resultVal = 1.0; 
        
        HashMap<String, Double> stateValueTable;
        FileInputStream fis;
        ObjectInputStream ois;

        File stateValuesFile = new File("StateValues.dat");
        if (stateValuesFile.exists() && stateValuesFile.length()>0) {
            fis = new FileInputStream(stateValuesFile);
            ois = new ObjectInputStream(fis);
            stateValueTable = (HashMap<String, Double>) ois.readObject();
            ois.close();
            fis.close();
        } else {
            stateValueTable = new HashMap<>();
        }
        System.out.println("Size of State Value Table: " + stateValueTable.size());


        List<String> p1Move = readDataFromFile("p1Move.txt");
        List<String> p2Move = readDataFromFile("p2Move.txt");
        double maxError = 0;
        double sumError=0;

        for (String state : p1Move){
            Double score = stateValueTable.get(state);
            if (score == null) score = 0.5;
            double error = resultVal - score;
            sumError+=Math.abs(error);
            maxError = Math.max(Math.abs(error), maxError);
            score += 0.1*(error);
            stateValueTable.put(state, score);
        }

        double invResult = 1-resultVal;
        for (String state : p2Move){
            String invState = reverseString(state);
            Double score = stateValueTable.get(invState);
            if (score == null) score = 0.5;
            double error = invResult - score;
            sumError+=Math.abs(error);
            maxError = Math.max(Math.abs(error), maxError);
            score += 0.1*(error);
            stateValueTable.put(invState, score);
        }



        System.out.println("Maximum Error: " + maxError);
        System.out.println("Average Error: " + sumError/(p1Move.size()+p2Move.size()));
        FileOutputStream fos = new FileOutputStream("StateValues.dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(stateValueTable);
        oos.close();
        fos.close();


        } catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
    }

    public static List<String> readDataFromFile(String fileName) {
        List<String> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {                
                dataList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    static String reverseString(String board){
        String[] vals = board.split(",");
        int[] reverse = new int[vals.length];
        for (int i =0; i< vals.length;i++){
            reverse[i] = -Integer.parseInt(vals[vals.length-1-i]);
        }

        String[] reversed = new String[vals.length];
        for (int i=0;i<vals.length;i++){
            reversed[i] = String.valueOf(reverse[i]);
        }

        return String.join(",", reversed);

    }

}
