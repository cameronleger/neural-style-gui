package com.cameronleger.neuralstyle;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class NeuralStyleV3 implements Cloneable {
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

    public boolean isOriginalColors() {
        return originalColors;
    }

    public void setOriginalColors(boolean originalColors) {
        this.originalColors = originalColors;
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

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public double getIterationsRatio() {
        return iterationsRatio;
    }

    public void setIterationsRatio(double iterationsRatio) {
        this.iterationsRatio = iterationsRatio;
    }

    public int getIterationsPrint() {
        return iterationsPrint;
    }

    public void setIterationsPrint(int iterationsPrint) {
        this.iterationsPrint = iterationsPrint;
    }

    public double getIterationsPrintRatio() {
        return iterationsPrintRatio;
    }

    public void setIterationsPrintRatio(double iterationsPrintRatio) {
        this.iterationsPrintRatio = iterationsPrintRatio;
    }

    public int getIterationsSave() {
        return iterationsSave;
    }

    public void setIterationsSave(int iterationsSave) {
        this.iterationsSave = iterationsSave;
    }

    public double getIterationsSaveRatio() {
        return iterationsSaveRatio;
    }

    public void setIterationsSaveRatio(double iterationsSaveRatio) {
        this.iterationsSaveRatio = iterationsSaveRatio;
    }

    public int getSeed() {
        return seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public double getSeedRatio() {
        return seedRatio;
    }

    public void setSeedRatio(double seedRatio) {
        this.seedRatio = seedRatio;
    }

    public int getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(int outputSize) {
        this.outputSize = outputSize;
    }

    public double getOutputSizeRatio() {
        return outputSizeRatio;
    }

    public void setOutputSizeRatio(double outputSizeRatio) {
        this.outputSizeRatio = outputSizeRatio;
    }

    public double getStyleSize() {
        return styleSize;
    }

    public void setStyleSize(double styleSize) {
        this.styleSize = styleSize;
    }

    public double getStyleSizeRatio() {
        return styleSizeRatio;
    }

    public void setStyleSizeRatio(double styleSizeRatio) {
        this.styleSizeRatio = styleSizeRatio;
    }

    public int getContentWeight() {
        return contentWeight;
    }

    public void setContentWeight(int contentWeight) {
        this.contentWeight = contentWeight;
    }

    public double getContentWeightRatio() {
        return contentWeightRatio;
    }

    public void setContentWeightRatio(double contentWeightRatio) {
        this.contentWeightRatio = contentWeightRatio;
    }

    public int getStyleWeight() {
        return styleWeight;
    }

    public void setStyleWeight(int styleWeight) {
        this.styleWeight = styleWeight;
    }

    public double getStyleWeightRatio() {
        return styleWeightRatio;
    }

    public void setStyleWeightRatio(double styleWeightRatio) {
        this.styleWeightRatio = styleWeightRatio;
    }

    public double getTvWeight() {
        return tvWeight;
    }

    public void setTvWeight(double tvWeight) {
        this.tvWeight = tvWeight;
    }

    public double getTvWeightRatio() {
        return tvWeightRatio;
    }

    public void setTvWeightRatio(double tvWeightRatio) {
        this.tvWeightRatio = tvWeightRatio;
    }

    public int getnCorrection() {
        return nCorrection;
    }

    public void setnCorrection(int nCorrection) {
        this.nCorrection = nCorrection;
    }

    public double getnCorrectionRatio() {
        return nCorrectionRatio;
    }

    public void setnCorrectionRatio(double nCorrectionRatio) {
        this.nCorrectionRatio = nCorrectionRatio;
    }

    public int getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(int learningRate) {
        this.learningRate = learningRate;
    }

    public double getLearningRateRatio() {
        return learningRateRatio;
    }

    public void setLearningRateRatio(double learningRateRatio) {
        this.learningRateRatio = learningRateRatio;
    }

    public void generateUniqueName() {
        FileUtils.generateUniqueText();
        outputFile = FileUtils.getTempOutputImage();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean checkArguments() {
        if (outputFile == null)
            generateUniqueName();
        return styleImages != null && styleImages.length > 0 &&
                styleWeights != null && styleWeights.length == styleImages.length &&
                styleLayers != null && styleLayers.length > 0 &&
                contentLayers != null && contentLayers.length > 0 &&
                (cpu || gpu.length > 0) &&
                FileUtils.checkFilesExists(styleImages) &&
                FileUtils.checkFileExists(contentImage) &&
                FileUtils.checkFolderExists(neuralStylePath) &&
                FileUtils.checkFolderExists(FileUtils.getTempDir());
    }

    public String[] buildCommand() {
        // Format decimals without scientific notation and with a period
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        StringBuilder styleImagesBuilder = new StringBuilder();
        for (int i = 0; i < styleImages.length; i++) {
            styleImagesBuilder.append(styleImages[i].getAbsolutePath());
            if (i != styleImages.length - 1)
                styleImagesBuilder.append(",");
        }

        StringBuilder styleWeightsBuilder = new StringBuilder();
        for (int i = 0; i < styleWeights.length; i++) {
            styleWeightsBuilder.append(df.format(styleWeights[i]));
            if (i != styleWeights.length - 1)
                styleWeightsBuilder.append(",");
        }

        StringBuilder styleLayersBuilder = new StringBuilder();
        for (int i = 0; i < styleLayers.length; i++) {
            styleLayersBuilder.append(styleLayers[i]);
            if (i != styleLayers.length - 1)
                styleLayersBuilder.append(",");
        }

        StringBuilder contentLayersBuilder = new StringBuilder();
        for (int i = 0; i < contentLayers.length; i++) {
            contentLayersBuilder.append(contentLayers[i]);
            if (i != contentLayers.length - 1)
                contentLayersBuilder.append(",");
        }

        StringBuilder gpuIndicesBuilder = new StringBuilder();
        for (int i = 0; i < gpu.length; i++) {
            gpuIndicesBuilder.append(gpu[i]);
            if (i != gpu.length - 1)
                gpuIndicesBuilder.append(",");
        }

        String th = "th";
        if (thPath != null)
            th = thPath.getAbsolutePath();

        ArrayList<String> commandList = new ArrayList<>(
                Arrays.asList(th,
                        "neural_style.lua",
                        "-style_image",
                        styleImagesBuilder.toString(),
                        "-style_blend_weights",
                        styleWeightsBuilder.toString(),
                        "-content_image",
                        contentImage.getAbsolutePath(),
                        "-output_image",
                        outputFile.getAbsolutePath(),
                        "-print_iter",
                        String.valueOf(iterationsPrint),
                        "-save_iter",
                        String.valueOf(iterationsSave),
                        "-num_iterations",
                        String.valueOf(iterations),
                        "-seed",
                        String.valueOf(seed),
                        "-style_layers",
                        styleLayersBuilder.toString(),
                        "-content_layers",
                        contentLayersBuilder.toString(),
                        "-image_size",
                        String.valueOf(outputSize),
                        "-style_scale",
                        df.format(styleSize),
                        "-content_weight",
                        String.valueOf(contentWeight),
                        "-style_weight",
                        String.valueOf(styleWeight),
                        "-tv_weight",
                        df.format(tvWeight),
                        "-init",
                        init,
                        "-pooling",
                        pooling,
                        "-backend",
                        backend,
                        "-optimizer",
                        optimizer,
                        "-learning_rate",
                        String.valueOf(learningRate)));

        commandList.add("-original_colors");
        if (originalColors)
            commandList.add("1");
        else
            commandList.add("0");

        if (normalizeGradients)
            commandList.add("-normalize_gradients");

        if (autotune)
            commandList.add("-cudnn_autotune");

        commandList.add("-gpu");
        if (cpu)
            commandList.add("-1");
        else {
            commandList.add(gpuIndicesBuilder.toString());
            if (multiGpuStrategy != null && !multiGpuStrategy.isEmpty()) {
                commandList.add("-multigpu_strategy");
                commandList.add(multiGpuStrategy);
            }
        }

        if (init.equals("image") && FileUtils.checkFileExists(initImage)) {
            commandList.add("-init_image");
            commandList.add(initImage.getAbsolutePath());
        }

        if (nCorrection > 0) {
            commandList.add("-lbfgs_num_correction");
            commandList.add(String.valueOf(nCorrection));
        }

        if (FileUtils.checkFileExists(protoFile)) {
            commandList.add("-proto_file");
            commandList.add(protoFile.getAbsolutePath());
        }

        if (FileUtils.checkFileExists(modelFile)) {
            commandList.add("-model_file");
            commandList.add(modelFile.getAbsolutePath());
        }

        String[] command = new String[commandList.size()];
        return commandList.toArray(command);
    }
}
