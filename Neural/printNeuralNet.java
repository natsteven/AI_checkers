import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class printNeuralNet {
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

        for (int i = 0; i < myNet.inputWeights.rows;i++){
            for (int j = 0; j < myNet.inputWeights.cols;j++){
                System.out.println(myNet.inputWeights.data[i][j]);
            }
        }

    }
}
