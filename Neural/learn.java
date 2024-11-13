import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class learn {
    public static void main(String[] args)throws IOException, ClassNotFoundException{

        NeuralNet myNet;

        File netFile = new File("myNeuralNet.dat");

        FileInputStream fis;
        ObjectInputStream ois;

        if (netFile.exists()){
            fis = new FileInputStream(netFile);
            ois = new ObjectInputStream(fis);
            myNet = (NeuralNet) ois.readObject();
            ois.close();
            fis.close();
        } else myNet = new NeuralNet(23);

        List<double[]> p1Move = readDataFromFile("p1Move.txt");

        double[] result = new double[1];
        result[0] = Double.parseDouble(args[0]);

        for (double[] board : p1Move){
            myNet.train(board, result);
        }

        List<double[]> p2Move = readDataFromFile("p2Move.txt");

        double[] p2Result = new double[1];
        p2Result[0] = 1 - result[0];        // training on both players moves where player 2 board and results are inverted

        for (double[] board : p2Move) {
            double[] invBoard = new double[32];
            for (int i = 0; i< 32; i++) {
                invBoard[i] = -board[31-i];
            }
            myNet.train(invBoard, p2Result);
        }

        FileOutputStream fos = new FileOutputStream("myNeuralNet.dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(myNet);
        oos.close();
        fos.close();
    }

public static List<double[]> readDataFromFile(String fileName) {
        List<double[]> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into an array of strings using a comma as the delimiter
                String[] values = line.split(",");
                
                // Convert the array of strings to a List<Integer>
                double[] data = new double[values.length];
                for (int i =0;i < values.length;i++) {
                    data[i] = Double.parseDouble(values[i]);
                }
                
                dataList.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}
