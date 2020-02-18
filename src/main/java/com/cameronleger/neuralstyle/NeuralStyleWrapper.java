package com.cameronleger.neuralstyle;

import com.cameronleger.neuralstylegui.model.properties.*;

import java.io.File;
import java.io.IOException;

import static javafx.application.Platform.exit;

public class NeuralStyleWrapper {

    public static File tmpFolder;

    static {
        try {
            tmpFolder = File.createTempFile("neuralStyle", null);
            if (!tmpFolder.delete())
                throw new IOException("Unable to delete temporary file.");
            if (!tmpFolder.mkdir())
                throw new IOException("Unable to create temporary directory.");
        } catch (IOException e) {
            e.printStackTrace();
            exit();
        }
    }

    public static NeuralString workingFolder = new NeuralString("workingFolder", "Working Dir.", tmpFolder.getAbsolutePath());

    public static File getWorkingFolder() {
        return new File(workingFolder.getValue());
    }

    private NeuralString thPath = new NeuralString("thPath", "th", "");
    private NeuralString neuralStylePath = new NeuralString("neuralStylePath", "Neural Style", "");
    private NeuralString outputFolder = new NeuralString("outputFolder", "Output Dir.", "");

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
    private NeuralDouble tvWeight = new NeuralDouble("tvWeight", "TV Weight", 0.001);
    private NeuralChoice pooling = new NeuralChoice("pooling", "Pooling", "max", new String[]{"max", "avg"});
    private NeuralBoolean cpu = new NeuralBoolean("cpu", "CPU", false);
    private NeuralString multiGpuStrategy = new NeuralString("multiGpuStrategy", "Multi GPU Split");
    private NeuralChoice backend = new NeuralChoice("backend", "Backend", "nn", new String[]{"nn", "cudnn", "clnn"});
    private NeuralBoolean autotune = new NeuralBoolean("autotune", "Autotune", false);
    private NeuralChoice optimizer = new NeuralChoice("optimizer", "Optimizer", "lbfgs", new String[]{"lbfgs", "adam"});
    private NeuralDouble nCorrection = new NeuralDouble("nCorrection", "nCorrection", -1);
    private NeuralDouble learningRate = new NeuralDouble("learningRate", "Learning Rate", 10);
    private NeuralString protoFile = new NeuralString("protoFile", "Proto File", "");
    private NeuralString modelFile = new NeuralString("modelFile", "Model File", "");

    private File[] styleImages;
    private double[] styleWeights;
    private File contentImage;
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
    private String[] gpu = new String[] {
            "0"
    };

    public NeuralStyleWrapper() {

    }

    private File file(String fileName) {
        if (fileName == null || fileName.isEmpty())
            return null;
        return new File(fileName);
    }

    private String file(File file) {
        if (file == null)
            return "";
        return file.getAbsolutePath();
    }

    public NeuralStyleV3 getNeuralStyle() {
        NeuralStyleV3 s = new NeuralStyleV3();
        s.setThPath(file(thPath.getValue()));
        s.setNeuralStylePath(file(neuralStylePath.getValue()));
        s.setStyleImages(styleImages);
        s.setStyleWeights(styleWeights);
        s.setContentImage(contentImage);
        s.setOutputFolder(file(outputFolder.getValue()));
        s.setInit(init.getValue());
        s.setInitImage(file(initImage.getValue()));
        s.setPooling(pooling.getValue());
        s.setStyleLayers(styleLayers);
        s.setContentLayers(contentLayers);
        s.setOriginalColors(originalColors.getValue());
        s.setNormalizeGradients(normalizeGradients.getValue());
        s.setCpu(cpu.getValue());
        s.setGpu(gpu);
        s.setMultiGpuStrategy(multiGpuStrategy.getValue());
        s.setBackend(backend.getValue());
        s.setOptimizer(optimizer.getValue());
        s.setAutotune(autotune.getValue());
        s.setProtoFile(file(protoFile.getValue()));
        s.setModelFile(file(modelFile.getValue()));
        s.setChainLength(chainLength.getValue().intValue());
        s.setIterations(iterations.getValue().intValue());
        s.setIterationsRatio(iterations.getRatio());
        s.setIterationsPrint(iterationsPrint.getValue().intValue());
        s.setIterationsPrintRatio(iterationsPrint.getRatio());
        s.setIterationsSave(iterationsSave.getValue().intValue());
        s.setIterationsSaveRatio(iterationsSave.getRatio());
        s.setSeed(seed.getValue().intValue());
        s.setSeedRatio(seed.getRatio());
        s.setOutputSize(outputSize.getValue().intValue());
        s.setOutputSizeRatio(outputSize.getRatio());
        s.setStyleSize(styleSize.getValue().doubleValue());
        s.setStyleSizeRatio(styleSize.getRatio());
        s.setContentWeight(contentWeight.getValue().intValue());
        s.setContentWeightRatio(contentWeight.getRatio());
        s.setStyleWeight(styleWeight.getValue().intValue());
        s.setStyleWeightRatio(styleWeight.getRatio());
        s.setTvWeight(tvWeight.getValue().doubleValue());
        s.setTvWeightRatio(tvWeight.getRatio());
        s.setnCorrection(nCorrection.getValue().intValue());
        s.setnCorrectionRatio(nCorrection.getRatio());
        s.setLearningRate(learningRate.getValue().intValue());
        s.setLearningRateRatio(learningRate.getRatio());
        return s;
    }

    public void loadNeuralStyle(NeuralStyleV3 s) {
        thPath.setValue(file(s.getThPath()));
        neuralStylePath.setValue(file(s.getNeuralStylePath()));
        styleImages = s.getStyleImages();
        styleWeights = s.getStyleWeights();
        contentImage = s.getContentImage();
        outputFolder.setValue(file(s.getOutputFolder()));
        init.setValue(s.getInit());
        initImage.setValue(file(s.getInitImage()));
        pooling.setValue(s.getPooling());
        styleLayers = s.getStyleLayers();
        contentLayers = s.getContentLayers();
        originalColors.setValue(s.isOriginalColors());
        normalizeGradients.setValue(s.isNormalizeGradients());
        cpu.setValue(s.isCpu());
        gpu = s.getGpu();
        multiGpuStrategy.setValue(s.getMultiGpuStrategy());
        backend.setValue(s.getBackend());
        optimizer.setValue(s.getOptimizer());
        autotune.setValue(s.isAutotune());
        protoFile.setValue(file(s.getProtoFile()));
        modelFile.setValue(file(s.getModelFile()));
        chainLength.setValue(s.getChainLength());
        iterations.setValue(s.getIterations());
        iterations.setRatio(s.getIterationsRatio());
        iterationsPrint.setValue(s.getIterationsPrint());
        iterationsPrint.setRatio(s.getIterationsPrintRatio());
        iterationsSave.setValue(s.getIterationsSave());
        iterationsSave.setRatio(s.getIterationsSaveRatio());
        seed.setValue(s.getSeed());
        seed.setRatio(s.getSeedRatio());
        outputSize.setValue(s.getOutputSize());
        outputSize.setRatio(s.getOutputSizeRatio());
        styleSize.setValue(s.getStyleSize());
        styleSize.setRatio(s.getStyleSizeRatio());
        contentWeight.setValue(s.getContentWeight());
        contentWeight.setRatio(s.getContentWeightRatio());
        styleWeight.setValue(s.getStyleWeight());
        styleWeight.setRatio(s.getStyleWeightRatio());
        tvWeight.setValue(s.getTvWeight());
        tvWeight.setRatio(s.getTvWeightRatio());
        nCorrection.setValue(s.getnCorrection());
        nCorrection.setRatio(s.getnCorrectionRatio());
        learningRate.setValue(s.getLearningRate());
        learningRate.setRatio(s.getLearningRateRatio());
    }

    public boolean checkArguments() {
        return styleImages != null && styleImages.length > 0 &&
                styleWeights != null && styleWeights.length == styleImages.length &&
                styleLayers != null && styleLayers.length > 0 &&
                contentLayers != null && contentLayers.length > 0 &&
                (cpu.getValue() || gpu.length > 0) &&
                FileUtils.checkFilesExists(styleImages) &&
                FileUtils.checkFileExists(contentImage) &&
                FileUtils.checkFolderExists(file(neuralStylePath.getValue())) &&
                FileUtils.checkFolderExists(NeuralStyleWrapper.getWorkingFolder());
    }

    public NeuralString getThPath() {
        return thPath;
    }

    public NeuralString getNeuralStylePath() {
        return neuralStylePath;
    }

    public NeuralString getOutputFolder() {
        return outputFolder;
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

    public NeuralDouble getTvWeight() {
        return tvWeight;
    }

    public NeuralChoice getPooling() {
        return pooling;
    }

    public NeuralBoolean getCpu() {
        return cpu;
    }

    public NeuralString getMultiGpuStrategy() {
        return multiGpuStrategy;
    }

    public NeuralChoice getBackend() {
        return backend;
    }

    public NeuralBoolean getAutotune() {
        return autotune;
    }

    public NeuralChoice getOptimizer() {
        return optimizer;
    }

    public NeuralDouble getnCorrection() {
        return nCorrection;
    }

    public NeuralDouble getLearningRate() {
        return learningRate;
    }

    public NeuralString getProtoFile() {
        return protoFile;
    }

    public NeuralString getModelFile() {
        return modelFile;
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

    public String[] getGpu() {
        return gpu;
    }

    public void setGpu(String[] gpu) {
        this.gpu = gpu;
    }

}
