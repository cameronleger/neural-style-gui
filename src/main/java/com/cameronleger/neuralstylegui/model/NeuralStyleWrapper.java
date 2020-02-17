package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.NeuralStyle;
import com.cameronleger.neuralstylegui.model.properties.NeuralInt;

public class NeuralStyleWrapper {

    private NeuralInt iterations = new NeuralInt("iterations", "Iterations", 1000);
    private NeuralInt iterationsPrint = new NeuralInt("iterationsPrint", "Progress Update", 20);
    private NeuralInt iterationsSave = new NeuralInt("iterationsSave", "Image Update", 20);

    public NeuralStyleWrapper() {

    }

    public NeuralStyle getNeuralStyle() {
        NeuralStyle s = new NeuralStyle();
        s.setIterations(iterations.getValue().intValue());
        return s;
    }

    public NeuralInt getIterations() {
        return iterations;
    }

    public NeuralInt getIterationsPrint() {
        return iterationsPrint;
    }

    public NeuralInt getIterationsSave() {
        return iterationsSave;
    }
}
