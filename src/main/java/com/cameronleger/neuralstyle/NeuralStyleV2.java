package com.cameronleger.neuralstyle;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class NeuralStyleV2 implements Cloneable {
    public final static int INVALID_ARGUMENTS = -2;
    public final static int INVALID_FILE = -1;
    public final static int QUEUED = 0;
    public final static int IN_PROGRESS = 1;
    public final static int CANCELLED = 2;
    public final static int FAILED = 3;
    public final static int FINISHED = 4;
    private int queueStatus = QUEUED;

    private File thPath;
    private File neuralStylePath;
    private File[] styleImages;
    private double[] styleWeights;
    private File contentImage;
    private File outputFolder;
    private File outputFile;
    private int iterations = 1000;
    private int iterationsPrint = 10;
    private int iterationsSave = 10;
    private int seed = -1;
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
    private int outputSize = 500;
    private double styleSize = 1.0;
    private int contentWeight = 5;
    private int styleWeight = 100;
    private double tvWeight = 0.0001;
    private boolean originalColors = false;
    private String init = "image";
    private File initImage;
    private String pooling = "max";
    private boolean normalizeGradients = false;
    private boolean cpu = false;
    private String[] gpu = new String[] {
            "0"
    };
    private String multiGpuStrategy = "";
    private String backend = "nn";
    private String optimizer = "lbfgs";
    private int nCorrection = -1;
    private int learningRate = 10;
    private boolean autotune = false;
    private File protoFile;
    private File modelFile;
    private int chainLength = 1;
    private double chainIterationRatio = 0.5;
    private double chainSizeRatio = 0.5;

    public int getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(int queueStatus) {
        this.queueStatus = queueStatus;
    }

    public File getThPath() {
        return thPath;
    }

    public void setThPath(File thPath) {
        this.thPath = thPath;
    }

    public File getNeuralStylePath() {
        return neuralStylePath;
    }

    public void setNeuralStylePath(File neuralStylePath) {
        this.neuralStylePath = neuralStylePath;
    }

    public File[] getStyleImages() {
        return styleImages;
    }

    public void setStyleImages(File[] styleImages) {
        this.styleImages = styleImages;
    }

    public double[] getStyleWeights() {
        return styleWeights;
    }

    public void setStyleWeights(double[] styleWeights) {
        this.styleWeights = styleWeights;
    }

    public File getContentImage() {
        return contentImage;
    }

    public void setContentImage(File contentImage) {
        this.contentImage = contentImage;
    }

    public File getOutputFolder() {
        return outputFolder;
    }

    public void setOutputFolder(File outputFolder) {
        this.outputFolder = outputFolder;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getIterationsPrint() {
        return iterationsPrint;
    }

    public void setIterationsPrint(int iterationsPrint) {
        this.iterationsPrint = iterationsPrint;
    }

    public int getIterationsSave() {
        return iterationsSave;
    }

    public void setIterationsSave(int iterationsSave) {
        this.iterationsSave = iterationsSave;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public String[] getStyleLayers() {
        return styleLayers;
    }

    public void setStyleLayers(String[] styleLayers) {
        this.styleLayers = styleLayers;
    }

    public String[] getContentLayers() {
        return contentLayers;
    }

    public void setContentLayers(String[] contentLayers) {
        this.contentLayers = contentLayers;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public double getStyleSize() {
        return styleSize;
    }

    public void setStyleSize(double styleSize) {
        this.styleSize = styleSize;
    }

    public int getContentWeight() {
        return contentWeight;
    }

    public void setContentWeight(int contentWeight) {
        this.contentWeight = contentWeight;
    }

    public int getStyleWeight() {
        return styleWeight;
    }

    public void setStyleWeight(int styleWeight) {
        this.styleWeight = styleWeight;
    }

    public double getTvWeight() {
        return tvWeight;
    }

    public void setTvWeight(double tvWeight) {
        this.tvWeight = tvWeight;
    }

    public boolean isOriginalColors() {
        return originalColors;
    }

    public void setOriginalColors(boolean originalColors) {
        this.originalColors = originalColors;
    }

    public String getInit() {
        return init;
    }

    public void setInit(String init) {
        this.init = init;
    }

    public File getInitImage() {
        return initImage;
    }

    public void setInitImage(File initImage) {
        this.initImage = initImage;
    }

    public String getPooling() {
        return pooling;
    }

    public void setPooling(String pooling) {
        this.pooling = pooling;
    }

    public boolean isNormalizeGradients() {
        return normalizeGradients;
    }

    public void setNormalizeGradients(boolean normalizeGradients) {
        this.normalizeGradients = normalizeGradients;
    }

    public boolean isCpu() {
        return cpu;
    }

    public void setCpu(boolean cpu) {
        this.cpu = cpu;
    }

    public String[] getGpu() {
        return gpu;
    }

    public void setGpu(String[] gpu) {
        this.gpu = gpu;
    }

    public String getMultiGpuStrategy() {
        return multiGpuStrategy;
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

    public String getOptimizer() {
        return optimizer;
    }

    public void setOptimizer(String optimizer) {
        this.optimizer = optimizer;
    }

    public int getNCorrection() {
        return nCorrection;
    }

    public void setNCorrection(int nCorrection) {
        this.nCorrection = nCorrection;
    }

    public int getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(int learningRate) {
        this.learningRate = learningRate;
    }

    public boolean isAutotune() {
        return autotune;
    }

    public void setAutotune(boolean autotune) {
        this.autotune = autotune;
    }

    public File getProtoFile() {
        return protoFile;
    }

    public void setProtoFile(File protoFile) {
        this.protoFile = protoFile;
    }

    public File getModelFile() {
        return modelFile;
    }

    public void setModelFile(File modelFile) {
        this.modelFile = modelFile;
    }

    public int getChainLength() {
        return chainLength;
    }

    public void setChainLength(int chainLength) {
        this.chainLength = chainLength;
    }

    public double getChainIterationRatio() {
        return chainIterationRatio;
    }

    public void setChainIterationRatio(double chainIterationRatio) {
        this.chainIterationRatio = chainIterationRatio;
    }

    public double getChainSizeRatio() {
        return chainSizeRatio;
    }

    public void setChainSizeRatio(double chainSizeRatio) {
        this.chainSizeRatio = chainSizeRatio;
    }

    public NeuralStyleV3 upgrade() {
        NeuralStyleV3 newStyle = new NeuralStyleV3();
        newStyle.setNeuralStylePath(this.neuralStylePath);
        newStyle.setStyleImages(this.styleImages);
        newStyle.setStyleWeights(this.styleWeights);
        newStyle.setContentImage(this.contentImage);
        newStyle.setOutputFolder(this.outputFolder);
        newStyle.setIterations(this.iterations);
        newStyle.setIterationsPrint(this.iterationsPrint);
        newStyle.setIterationsSave(this.iterationsSave);
        newStyle.setSeed(this.seed);
        newStyle.setStyleLayers(this.styleLayers);
        newStyle.setContentLayers(this.contentLayers);
        newStyle.setOutputSize(this.outputSize);
        newStyle.setStyleSize(this.styleSize);
        newStyle.setContentWeight(this.contentWeight);
        newStyle.setStyleWeight(this.styleWeight);
        newStyle.setTvWeight(this.tvWeight);
        newStyle.setOriginalColors(this.originalColors);
        newStyle.setInit(this.init);
        newStyle.setPooling(this.pooling);
        newStyle.setNormalizeGradients(this.normalizeGradients);
        newStyle.setBackend(this.backend);
        newStyle.setOptimizer(this.optimizer);
        newStyle.setLearningRate(this.learningRate);
        newStyle.setAutotune(this.autotune);
        newStyle.setProtoFile(this.protoFile);
        newStyle.setModelFile(this.modelFile);

        newStyle.setChainLength(this.chainLength);
        newStyle.setIterationsRatio(this.chainIterationRatio);
        newStyle.setOutputSizeRatio(this.chainSizeRatio);
        newStyle.setCpu(this.cpu);
        newStyle.setGpu(this.gpu);
        newStyle.setnCorrection(this.nCorrection);
        newStyle.setMultiGpuStrategy(this.multiGpuStrategy);
        newStyle.setInitImage(this.initImage);
        newStyle.setThPath(this.thPath);

        return newStyle;
    }

    public void generateUniqueName() {
        FileUtils.generateUniqueText();
        outputFile = FileUtils.getTempOutputImage();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
