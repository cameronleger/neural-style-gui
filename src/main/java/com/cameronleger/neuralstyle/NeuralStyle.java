package com.cameronleger.neuralstyle;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

public class NeuralStyle implements Cloneable {
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

    public void generateUniqueName() {
        FileUtils.generateUniqueText();
        outputFile = FileUtils.getTempOutputImage();
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public List<NeuralStyle> getQueueItems() {
        List<NeuralStyle> queueItems = new ArrayList<>();

        // Simple one-run case
        if (getChainLength() == 1)
            queueItems.add(this);

        // Chaining case
        else if (getChainLength() > 1) {
            // Need some variables for the math
            final int outputSize = getOutputSize();
            final double chainSizeRatio = getChainSizeRatio();
            final int iterations = getIterations();
            final double chainIterationRatio = getChainIterationRatio();
            File previousChainOutput = null;
            NeuralStyle queueItem;

            for (int i = 1; i <= getChainLength(); i++) {
                // This power increases further back in the chain
                int ratioPower = getChainLength() - i;
                try {
                    queueItem = (NeuralStyle) this.clone();

                    // ratioPower is an exponent for the ratio and is finally applied to the original 'final' value
                    queueItem.setOutputSize((int) Math.round(Math.pow(chainSizeRatio, ratioPower) * outputSize));
                    queueItem.setIterations((int) Math.round(Math.pow(chainIterationRatio, ratioPower) * iterations));

                    // The first link in the chain uses the original initialization values
                    // Each subsequent link is initialized with the output of the previous
                    if (previousChainOutput != null) {
                        queueItem.setInit("image");
                        queueItem.setInitImage(previousChainOutput);
                    }

                    // Generate a new unique chain output and prepare to link it to the next
                    queueItem.setOutputFile(FileUtils.getTempOutputImage(i));
                    previousChainOutput = queueItem.getOutputFile();

                    queueItems.add(queueItem);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        return queueItems;
    }

    public boolean checkArguments() {
        File[] styleImages = getStyleImages();
        double[] styleWeights = getStyleWeights();
        String[] styleLayers = getStyleLayers();
        String[] contentLayers = getContentLayers();
        if (outputFile == null)
            generateUniqueName();
        return styleImages != null && styleImages.length > 0 &&
                styleWeights != null && styleWeights.length == styleImages.length &&
                styleLayers != null && styleLayers.length > 0 &&
                contentLayers != null && contentLayers.length > 0 &&
                (isCpu() || getGpu().length > 0) &&
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

        StringBuilder gpuIndicesBuilder = new StringBuilder();
        String[] gpuIndices = getGpu();
        for (int i = 0; i < gpuIndices.length; i++) {
            gpuIndicesBuilder.append(gpuIndices[i]);
            if (i != gpuIndices.length - 1)
                gpuIndicesBuilder.append(",");
        }

        String th = "th";
        if (getThPath() != null)
            th = getThPath().getAbsolutePath();

        ArrayList<String> commandList = new ArrayList<>(
                Arrays.asList(th,
                        "neural_style.lua",
                        "-style_image",
                        styleImagesBuilder.toString(),
                        "-style_blend_weights",
                        styleWeightsBuilder.toString(),
                        "-content_image",
                        getContentImage().getAbsolutePath(),
                        "-output_image",
                        getOutputFile().getAbsolutePath(),
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
                        "-backend",
                        getBackend(),
                        "-optimizer",
                        getOptimizer(),
                        "-learning_rate",
                        String.valueOf(getLearningRate())));

        commandList.add("-original_colors");
        if (isOriginalColors())
            commandList.add("1");
        else
            commandList.add("0");

        if (isNormalizeGradients())
            commandList.add("-normalize_gradients");

        if (isAutotune())
            commandList.add("-cudnn_autotune");

        commandList.add("-gpu");
        if (isCpu())
            commandList.add("-1");
        else {
            commandList.add(gpuIndicesBuilder.toString());
            String multiGpuStrategy = getMultiGpuStrategy();
            if (multiGpuStrategy != null && !multiGpuStrategy.isEmpty()) {
                commandList.add("-multigpu_strategy");
                commandList.add(multiGpuStrategy);
            }
        }

        if (getInit().equals("image") && FileUtils.checkFileExists(getInitImage())) {
            commandList.add("-init_image");
            commandList.add(getInitImage().getAbsolutePath());
        }

        if (getNCorrection() > 0) {
            commandList.add("-lbfgs_num_correction");
            commandList.add(String.valueOf(getNCorrection()));
        }

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
