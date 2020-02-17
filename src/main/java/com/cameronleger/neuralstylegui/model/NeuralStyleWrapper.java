package com.cameronleger.neuralstylegui.model;

import com.cameronleger.neuralstyle.NeuralStyle;
import com.cameronleger.neuralstylegui.model.properties.*;

public class NeuralStyleWrapper {

    private NeuralInt chainLength = new NeuralInt("chainLength", "Chaining", 1);
    private NeuralInt iterations = new NeuralInt("iterations", "Iterations", 1000);
    private NeuralInt iterationsPrint = new NeuralInt("iterationsPrint", "Progress Update", 20);
    private NeuralInt iterationsSave = new NeuralInt("iterationsSave", "Image Update", 20);
    private NeuralInt outputSize = new NeuralInt("outputSize", "Output (px)", 500);
    private NeuralDouble styleSize = new NeuralDouble("styleSize", "Style Scale", 1.0);
    private NeuralInt seed = new NeuralInt("seed", "Seed", -1);
    private NeuralInt contentWeight = new NeuralInt("contentWeight", "Content Weight", 5);
    private NeuralInt styleWeight = new NeuralInt("styleWeight", "Style Weight", 100);
    private NeuralChoice init = new NeuralChoice("init", "Initialize", "random", new String[]{"image", "random"});
    private NeuralString initImage = new NeuralString("initImage", "Init. Image", "");
    private NeuralBoolean originalColors = new NeuralBoolean("originalColors", "Original Colors", false);
    private NeuralBoolean normalizeGradients = new NeuralBoolean("normalizeGradients", "Normalize Gradients", true);

    public NeuralStyleWrapper() {

    }

    public NeuralStyle getNeuralStyle() {
        NeuralStyle s = new NeuralStyle();
        s.setIterations(iterations.getValue().intValue());
        return s;
    }

    public NeuralInt getChainLength() {
        return chainLength;
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

    public NeuralInt getOutputSize() {
        return outputSize;
    }

    public NeuralDouble getStyleSize() {
        return styleSize;
    }

    public NeuralInt getSeed() {
        return seed;
    }

    public NeuralInt getContentWeight() {
        return contentWeight;
    }

    public NeuralInt getStyleWeight() {
        return styleWeight;
    }

    public NeuralChoice getInit() {
        return init;
    }

    public NeuralString getInitImage() {
        return initImage;
    }

    public NeuralBoolean getOriginalColors() {
        return originalColors;
    }

    public NeuralBoolean getNormalizeGradients() {
        return normalizeGradients;
    }
}
