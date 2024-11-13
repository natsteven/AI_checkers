import java.io.Serializable;
import java.util.List;

public class NeuralNet implements Serializable{
    
    Matrix inputWeights, hiddenWeights, biasHidden, biasOutput;

    double learningRate = 0.1;

    public NeuralNet(int hidden) {
        inputWeights = new Matrix(hidden, 32);
        hiddenWeights = new Matrix(1,hidden );

        biasHidden = new Matrix(hidden, 1);
        biasOutput = new Matrix(1, 1);
    }

    public List<Double> predict(double[] features){

        Matrix input = Matrix.fromArray(features);
        Matrix hidden = Matrix.multiply(inputWeights, input);
        hidden.add(biasHidden);
        hidden.sigmoid();

        Matrix output = Matrix.multiply(hiddenWeights, hidden);
        output.add(biasOutput);
        output.sigmoid();

        return output.toArray();

    }

    public void train(double[] features, double[] result) {
        Matrix input = Matrix.fromArray(features);
		Matrix hidden = Matrix.multiply(inputWeights, input);
		hidden.add(biasHidden);
		hidden.sigmoid();
		
		Matrix output = Matrix.multiply(hiddenWeights,hidden);
		output.add(biasOutput);
		output.sigmoid();
		
		Matrix target = Matrix.fromArray(result);
		
		Matrix error = Matrix.subtract(target, output);
		Matrix gradient = output.dsigmoid();
		gradient.multiply(error);
		gradient.multiply(learningRate);
		
		Matrix hidden_T = Matrix.transpose(hidden);
		Matrix who_delta =  Matrix.multiply(gradient, hidden_T);
		
		hiddenWeights.add(who_delta);
		biasOutput.add(gradient);
		
		Matrix who_T = Matrix.transpose(hiddenWeights);
		Matrix hidden_errors = Matrix.multiply(who_T, error);
		
		Matrix h_gradient = hidden.dsigmoid();
		h_gradient.multiply(hidden_errors);
		h_gradient.multiply(learningRate);
		
		Matrix i_T = Matrix.transpose(input);
		Matrix wih_delta = Matrix.multiply(h_gradient, i_T);
		
		inputWeights.add(wih_delta);
		biasHidden.add(h_gradient);
		        
    }

}
