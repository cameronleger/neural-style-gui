package com.cameronleger.neuralstyle;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NeuralStyle {
    private static final Logger log = Logger.getLogger(NeuralStyle.class.getName());
    private static String executable = "th";
    private static File neuralStylePath = new File("/home/cameron/neural-style");
    private File tempDir;
    private File styleImage;
    private File contentImage;
    private File outputFolder;
    private int iterations = 1000;
    private int iterationsPrint = 10;
    private int iterationsSave = 10;
    private int seed = -1;
    private int outputSize = 500;
    private double styleSize = 1.0;
    private int contentWeight = 5;
    private int styleWeight = 100;
    private double tvWeight = 0.0001;
    private int originalColors = 0;
    private String init = "image";
    private String pooling = "max";
    private boolean normalizeGradients = false;
    private int gpu = 0;
    private String backend = "nn";
    private String optimizer = "lbfgs";
    private int learningRate = 10;
    private boolean autotune = false;
    private String uniqueText;

    public static String getExecutable() {
        return executable;
    }

    public static void setExecutable(String executable) {
        NeuralStyle.executable = executable;
    }

    public static File getNeuralStylePath() {
        return neuralStylePath;
    }

    public static void setNeuralStylePath(File neuralStylePath) {
        NeuralStyle.neuralStylePath = neuralStylePath;
    }

    public File getStyleImage() {
        return styleImage;
    }

    public void setStyleImage(File styleImage) {
        this.styleImage = styleImage;
    }

    public File getContentImage() {
        return contentImage;
    }

    public void setContentImage(File contentImage) {
        this.contentImage = contentImage;
    }

    public File getGeneralOutputFolder() {
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

    public int getOriginalColors() {
        return originalColors;
    }

    public void setOriginalColors(int originalColors) {
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

    public File getTempDir() {
        if (tempDir == null) {
            try {
                tempDir = File.createTempFile("neuralStyle", null);
                if (!tempDir.delete())
                    throw new IOException("Unable to delete temporary file.");
                if (!tempDir.mkdir())
                    throw new IOException("Unable to create temporary directory.");
            } catch (Exception e) {
                log.log(Level.SEVERE, e.toString(), e);
            }
        }
        return tempDir;
    }

    public void generateUniqueText() {
        uniqueText = String.valueOf(System.nanoTime());
    }

    private String getUniqueText() {
        if (uniqueText == null)
            generateUniqueText();
        return uniqueText;
    }

    public boolean checkArguments() {
        return FileUtils.checkFileExists(getStyleImage()) &&
                FileUtils.checkFileExists(getContentImage()) &&
                FileUtils.checkFolderExists(getNeuralStylePath()) &&
                FileUtils.checkFolderExists(getTempDir());
    }

    public File getTempOutputImage() {
        if (!checkArguments())
            return null;
        File tempOutputDir = getTempDir();
        if (tempOutputDir == null)
            return null;
        return new File(tempOutputDir, getUniqueText() + ".png");
    }

    public File[] getTempOutputImageIterations() {
        File tempOutputImage = getTempOutputImage();
        File tempOutputDir = getTempDir();
        if (tempOutputImage == null || tempOutputDir == null)
            return null;

        // Unix-like searching for image iterations
        String outputImageBase = FileUtils.getFileName(tempOutputImage);
        FileFilter fileFilter = new WildcardFileFilter(String.format("%s_*.png", outputImageBase));
        File[] files = tempOutputDir.listFiles(fileFilter);

        // sort the files by the iteration progress
        if (files != null && files.length > 1) {
            int[] fileIters = new int[files.length];
            for (int i = 0; i < files.length; i++)
                fileIters[i] = FileUtils.parseImageIteration(files[i]);
            FileUtils.quickSort(fileIters, files, 0, files.length - 1);

            // check that the latest file is valid (could still be written to)
            if (files[files.length - 1].length() / files[files.length - 2].length() <= 0.5)
                files[files.length - 1] = files[files.length - 2];
        }


        return files;
    }

    public String[] buildCommand() {
        ArrayList<String> commandList = new ArrayList<>(
                Arrays.asList(new String[] {
                        getExecutable(),
                        "neural_style.lua",
                        "-style_image",
                        getStyleImage().getAbsolutePath(),
                        "-content_image",
                        getContentImage().getAbsolutePath(),
                        "-output_image",
                        getTempOutputImage().getAbsolutePath(),
                        "-print_iter",
                        String.valueOf(getIterationsPrint()),
                        "-save_iter",
                        String.valueOf(getIterationsSave()),
                        "-num_iterations",
                        String.valueOf(getIterations()),
                        "-seed",
                        String.valueOf(getSeed()),
                        "-image_size",
                        String.valueOf(getOutputSize()),
                        "-style_scale",
                        String.valueOf(getStyleSize()),
                        "-content_weight",
                        String.valueOf(getContentWeight()),
                        "-style_weight",
                        String.valueOf(getStyleWeight()),
                        "-tv_weight",
                        String.valueOf(getTvWeight()),
                        "-original_colors",
                        String.valueOf(getOriginalColors()),
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

        if(isNormalizeGradients())
            commandList.add("-normalize_gradients");

        if(isAutotune())
            commandList.add("-cudnn_autotune");

        String[] command = new String[commandList.size()];
        return commandList.toArray(command);
    }
}
