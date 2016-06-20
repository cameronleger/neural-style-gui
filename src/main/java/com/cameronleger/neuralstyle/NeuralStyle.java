package com.cameronleger.neuralstyle;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.util.logging.Logger;

public class NeuralStyle {
    private static final Logger log = Logger.getLogger(NeuralStyle.class.getName());
    private static String executable = "th";
    private static File neuralStylePath = new File("/home/cameron/neural-style");
    private File styleImage;
    private File contentImage;
    private File outputFolder;
    private int iterations = 1000;
    private int iterationsPrint = 10;
    private int iterationsSave = 10;
    private int outputSize = 500;
    private double styleSize = 1.0;
    private int contentWeight = 5;
    private int styleWeight = 100;
    private double tvWeight = 0.001;
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

    public File getOutputFolder() {
        return new File(getGeneralOutputFolder(), FileUtils.getFileName(getStyleImage()));
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
                FileUtils.checkFolderExists(getGeneralOutputFolder());
    }

    public File getOutputImage() {
        if (!checkArguments())
            return null;
        File styleOutputFolder = getOutputFolder();
        if (!styleOutputFolder.exists() && !styleOutputFolder.mkdir())
            return null;
        return new File(styleOutputFolder, FileUtils.getFileName(getContentImage()) + "_u" + getUniqueText() + ".jpg");
    }

    public File[] getOutputImageIterations() {
        // Unix-like searching for image iterations
        String outputImageBase = FileUtils.getFileName(getOutputImage());
        FileFilter fileFilter = new WildcardFileFilter(String.format("%s_*.jpg", outputImageBase));
        File[] files = getOutputFolder().listFiles(fileFilter);

        // sort the files by the iteration progress
        if (files != null && files.length > 1) {
            int[] fileIters = new int[files.length];
            for (int i = 0; i < files.length; i++)
                fileIters[i] = FileUtils.parseImageIteration(files[i]);
            FileUtils.quickSort(fileIters, files, 0, files.length - 1);
        }

        return files;
    }

    public String[] buildCommand() {
        return new String[] {
                getExecutable(),
                "neural_style.lua",
                "-style_image",
                getStyleImage().getAbsolutePath(),
                "-content_image",
                getContentImage().getAbsolutePath(),
                "-output_image",
                getOutputImage().getAbsolutePath(),
                "-print_iter",
                String.valueOf(getIterationsPrint()),
                "-save_iter",
                String.valueOf(getIterationsSave()),
                "-num_iterations",
                String.valueOf(getIterations()),
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
                "-backend",
                "cudnn",
                "-cudnn_autotune"
        };
    }
}
