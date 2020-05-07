package com.cameronleger.neuralstyle;

import java.io.File;

public class NeuralStyleV3 implements Cloneable {
    private int version = 3;

    private File thPath;
    private File neuralStylePath;
    private File[] styleImages;
    private double[] styleWeights;
    private File contentImage;
    private File outputFolder;
    private File outputFile;
    private String init = "image";
    private File initImage;
    private String pooling = "max";
    private String[] styleLayers = new String[] {
            "relu1_1",
            "relu2_1",
            "relu3_1",
            "relu4_1",
            "relu5_1"
    };
    private String[] contentLayers = new String[] {
            "relu4_2"
    };
    private boolean originalColors = false;
    private boolean normalizeGradients = false;
    private boolean cpu = false;
    private String[] gpu = new String[] {
            "0"
    };
    private String multiGpuStrategy = "";
    private String backend = "nn";
    private String optimizer = "lbfgs";
    private boolean autotune = false;
    private File protoFile;
    private File modelFile;

    private int chainLength = 1;

    private int iterations = 1000;
    private double iterationsRatio = 1.0;

    private int iterationsPrint = 10;
    private double iterationsPrintRatio = 1.0;

    private int iterationsSave = 10;
    private double iterationsSaveRatio = 1.0;

    private int seed = -1;
    private double seedRatio = 1.0;

    private int outputSize = 500;
    private double outputSizeRatio = 1.0;

    private double styleSize = 1.0;
    private double styleSizeRatio = 1.0;

    private int contentWeight = 5;
    private double contentWeightRatio = 1.0;

    private int styleWeight = 100;
    private double styleWeightRatio = 1.0;

    private double tvWeight = 0.0001;
    private double tvWeightRatio = 1.0;

    private int nCorrection = -1;
    private double nCorrectionRatio = 1.0;

    private int learningRate = 10;
    private double learningRateRatio = 1.0;

    public void setThPath(File thPath) {
        this.thPath = thPath;
    }

    public void setNeuralStylePath(File neuralStylePath) {
        this.neuralStylePath = neuralStylePath;
    }

    public void setStyleImages(File[] styleImages) {
        this.styleImages = styleImages;
    }

    public void setStyleWeights(double[] styleWeights) {
        this.styleWeights = styleWeights;
    }

    public void setContentImage(File contentImage) {
        this.contentImage = contentImage;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public void setInitImage(File initImage) {
        this.initImage = initImage;
    }

    public void setPooling(String pooling) {
        this.pooling = pooling;
    }

    public void setStyleLayers(String[] styleLayers) {
        this.styleLayers = styleLayers;
    }

    public void setContentLayers(String[] contentLayers) {
        this.contentLayers = contentLayers;
    }

    public void setOriginalColors(boolean originalColors) {
        this.originalColors = originalColors;
    }

    public void setNormalizeGradients(boolean normalizeGradients) {
        this.normalizeGradients = normalizeGradients;
    }

    public void setCpu(boolean cpu) {
        this.cpu = cpu;
    }

    public void setGpu(String[] gpu) {
        this.gpu = gpu;
    }

    public void setMultiGpuStrategy(String multiGpuStrategy) {
        this.multiGpuStrategy = multiGpuStrategy;
    }

    public String getBackend() {
        return backend;
    }

    public void setBackend(String backend) {
        this.backend = backend;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public void setAutotune(boolean autotune) {
        this.autotune = autotune;
    }

    public void setProtoFile(File protoFile) {
        this.protoFile = protoFile;
    }

    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public void setChainLength(int chainLength) {
        this.chainLength = chainLength;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setIterationsRatio(double iterationsRatio) {
        this.iterationsRatio = iterationsRatio;
    }

    public void setIterationsPrint(int iterationsPrint) {
        this.iterationsPrint = iterationsPrint;
    }

    public void setIterationsSave(int iterationsSave) {
        this.iterationsSave = iterationsSave;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public void setOutputSizeRatio(double outputSizeRatio) {
        this.outputSizeRatio = outputSizeRatio;
    }

    public void setStyleSize(double styleSize) {
        this.styleSize = styleSize;
    }

    public void setContentWeight(int contentWeight) {
        this.contentWeight = contentWeight;
    }

    public void setStyleWeight(int styleWeight) {
        this.styleWeight = styleWeight;
    }

    public void setTvWeight(double tvWeight) {
        this.tvWeight = tvWeight;
    }

    public void setnCorrection(int nCorrection) {
        this.nCorrection = nCorrection;
    }

    public void setLearningRate(int learningRate) {
        this.learningRate = learningRate;
    }

    public NeuralStyleV4 upgrade() {
        NeuralStyleV4 newStyle = new NeuralStyleV4();
        newStyle.setRunnerPath(this.thPath);
        newStyle.setNeuralStylePath(new File(this.neuralStylePath, "neural_style.lua"));
        newStyle.setStyleImages(this.styleImages);
        newStyle.setStyleWeights(this.styleWeights);
        newStyle.setContentImage(this.contentImage);
        newStyle.setOutputFolder(this.outputFolder);
        newStyle.setOutputFile(this.outputFile);
        newStyle.setInit(this.init);
        newStyle.setInitImage(this.initImage);
        newStyle.setPooling(this.pooling);
        newStyle.setStyleLayers(this.styleLayers);
        newStyle.setContentLayers(this.contentLayers);
        newStyle.setOriginalColors(this.originalColors);
        newStyle.setNormalizeGradients(this.normalizeGradients);
        newStyle.setCpu(this.cpu);
        newStyle.setGpu(this.gpu);
        newStyle.setMultiGpuStrategy(this.multiGpuStrategy);
        newStyle.setBackend(this.backend);
        newStyle.setOptimizer(this.optimizer);
        newStyle.setAutotune(this.autotune);
        newStyle.setProtoFile(this.protoFile);
        newStyle.setModelFile(this.modelFile);
        newStyle.setChainLength(this.chainLength);
        newStyle.setIterations(this.iterations);
        newStyle.setIterationsRatio(this.iterationsRatio);
        newStyle.setIterationsPrint(this.iterationsPrint);
        newStyle.setIterationsPrintRatio(this.iterationsPrintRatio);
        newStyle.setIterationsSave(this.iterationsSave);
        newStyle.setIterationsSaveRatio(this.iterationsSaveRatio);
        newStyle.setSeed(this.seed);
        newStyle.setSeedRatio(this.seedRatio);
        newStyle.setOutputSize(this.outputSize);
        newStyle.setOutputSizeRatio(this.outputSizeRatio);
        newStyle.setStyleSize(this.styleSize);
        newStyle.setStyleSizeRatio(this.styleSizeRatio);
        newStyle.setContentWeight(this.contentWeight);
        newStyle.setContentWeightRatio(this.contentWeightRatio);
        newStyle.setStyleWeight(this.styleWeight);
        newStyle.setStyleWeightRatio(this.styleWeightRatio);
        newStyle.setTvWeight(this.tvWeight);
        newStyle.setTvWeightRatio(this.tvWeightRatio);
        newStyle.setnCorrection(this.nCorrection);
        newStyle.setnCorrectionRatio(this.nCorrectionRatio);
        newStyle.setLearningRate(this.learningRate);
        newStyle.setLearningRateRatio(this.learningRateRatio);

        return newStyle;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
