package com.cameronleger.neuralstyle;

import com.cameronleger.neuralstylegui.model.properties.*;

import java.io.File;

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

    public NeuralStyleWrapper() {

    }

    public NeuralStyleV3 getNeuralStyle() {
        NeuralStyleV3 s = new NeuralStyleV3();
//        s.setQueueStatus();
//        s.setThPath();
//        s.setNeuralStylePath();
//        s.setStyleImages();
//        s.setStyleWeights();
//        s.setContentImage();
//        s.setOutputFolder();
//        s.setOutputFile();
        s.setInit(init.getValue());
        s.setInitImage(new File(initImage.getValue()));
        s.setPooling(pooling.getValue());
//        s.setStyleLayers();
//        s.setContentLayers();
        s.setOriginalColors(originalColors.getValue());
        s.setNormalizeGradients(normalizeGradients.getValue());
        s.setCpu(cpu.getValue());
//        s.setGpu();
        s.setMultiGpuStrategy(multiGpuStrategy.getValue());
        s.setBackend(backend.getValue());
        s.setOptimizer(optimizer.getValue());
        s.setAutotune(autotune.getValue());
        s.setProtoFile(new File(protoFile.getValue()));
        s.setModelFile(new File(modelFile.getValue()));
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
}
