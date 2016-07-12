package com.cameronleger.neuralstyle;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class NeuralStyle {
    private File neuralStylePath;
    private File[] styleImages;
    private double[] styleWeights;
    private File contentImage;
    private File outputFolder;
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
    private String pooling = "max";
    private boolean normalizeGradients = false;
    private int gpu = 0;
    private String backend = "nn";
    private String optimizer = "lbfgs";
    private int learningRate = 10;
    private boolean autotune = false;
    private File protoFile;
    private File modelFile;

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

    public int getGpu() {
        return gpu;
    }

    public void setGpu(int gpu) {
        this.gpu = gpu;
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

    public boolean checkArguments() {
        File[] styleImages = getStyleImages();
        double[] styleWeights = getStyleWeights();
        String[] styleLayers = getStyleLayers();
        String[] contentLayers = getContentLayers();
        return styleImages != null && styleImages.length > 0 &&
                styleWeights != null && styleWeights.length == styleImages.length &&
                styleLayers != null && styleLayers.length > 0 &&
                contentLayers != null && contentLayers.length > 0 &&
                FileUtils.checkFilesExists(styleImages) &&
                FileUtils.checkFileExists(getContentImage()) &&
                FileUtils.checkFolderExists(getNeuralStylePath()) &&
                FileUtils.checkFolderExists(FileUtils.getTempDir());
    }

    public String[] buildCommand() {
        // Format decimals without scientific notation and with a period
        DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        df.setMaximumFractionDigits(340);

        StringBuilder styleImagesBuilder = new StringBuilder();
        File[] styleFiles = getStyleImages();
        for (int i = 0; i < styleFiles.length; i++) {
            styleImagesBuilder.append(styleFiles[i].getAbsolutePath());
            if (i != styleFiles.length - 1)
                styleImagesBuilder.append(",");
        }

        StringBuilder styleWeightsBuilder = new StringBuilder();
        double[] styleWeights = getStyleWeights();
        for (int i = 0; i < styleWeights.length; i++) {
            styleWeightsBuilder.append(df.format(styleWeights[i]));
            if (i != styleWeights.length - 1)
                styleWeightsBuilder.append(",");
        }

        StringBuilder styleLayersBuilder = new StringBuilder();
        String[] styleLayers = getStyleLayers();
        for (int i = 0; i < styleLayers.length; i++) {
            styleLayersBuilder.append(styleLayers[i]);
            if (i != styleLayers.length - 1)
                styleLayersBuilder.append(",");
        }

        StringBuilder contentLayersBuilder = new StringBuilder();
        String[] contentLayers = getContentLayers();
        for (int i = 0; i < contentLayers.length; i++) {
            contentLayersBuilder.append(contentLayers[i]);
            if (i != contentLayers.length - 1)
                contentLayersBuilder.append(",");
        }

        ArrayList<String> commandList = new ArrayList<>(
                Arrays.asList(new String[] {
                        "th",
                        "neural_style.lua",
                        "-style_image",
                        styleImagesBuilder.toString(),
                        "-style_blend_weights",
                        styleWeightsBuilder.toString(),
                        "-content_image",
                        getContentImage().getAbsolutePath(),
                        "-output_image",
                        FileUtils.getTempOutputImage().getAbsolutePath(),
                        "-print_iter",
                        String.valueOf(getIterationsPrint()),
                        "-save_iter",
                        String.valueOf(getIterationsSave()),
                        "-num_iterations",
                        String.valueOf(getIterations()),
                        "-seed",
                        String.valueOf(getSeed()),
                        "-style_layers",
                        styleLayersBuilder.toString(),
                        "-content_layers",
                        contentLayersBuilder.toString(),
                        "-image_size",
                        String.valueOf(getOutputSize()),
                        "-style_scale",
                        df.format(getStyleSize()),
                        "-content_weight",
                        String.valueOf(getContentWeight()),
                        "-style_weight",
                        String.valueOf(getStyleWeight()),
                        "-tv_weight",
                        df.format(getTvWeight()),
                        "-init",
                        getInit(),
                        "-pooling",
                        getPooling(),
                        "-gpu",
                        String.valueOf(getGpu()),
                        "-backend",
                        getBackend(),
                        "-optimizer",
                        getOptimizer(),
                        "-learning_rate",
                        String.valueOf(getLearningRate()),
                }));

        commandList.add("-original_colors");
        if (isOriginalColors())
            commandList.add("1");
        else
            commandList.add("0");

        if (isNormalizeGradients())
            commandList.add("-normalize_gradients");

        if (isAutotune())
            commandList.add("-cudnn_autotune");

        if (FileUtils.checkFileExists(getProtoFile())) {
            commandList.add("-proto_file");
            commandList.add(getProtoFile().getAbsolutePath());
        }

        if (FileUtils.checkFileExists(getModelFile())) {
            commandList.add("-model_file");
            commandList.add(getModelFile().getAbsolutePath());
        }

        String[] command = new String[commandList.size()];
        return commandList.toArray(command);
    }
}
