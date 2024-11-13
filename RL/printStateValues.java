import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;

public class printStateValues {
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException{
        HashMap<List<Integer>, Double> stateValueTable = new HashMap<>();
        FileInputStream fis;
        ObjectInputStream ois;

        File stateValuesFile = new File("StateValues.dat");
        if (stateValuesFile.exists() && stateValuesFile.length()>0) {
            fis = new FileInputStream(stateValuesFile);
            ois = new ObjectInputStream(fis);
            stateValueTable = (HashMap<List<Integer>, Double>) ois.readObject();
            ois.close();
            fis.close();
        } else {
            System.out.println("EMPTY");
            return;
        }

        System.out.println(stateValueTable);


    }
}
