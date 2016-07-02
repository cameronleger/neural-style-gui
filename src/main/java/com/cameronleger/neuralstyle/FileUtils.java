package com.cameronleger.neuralstyle;

import caffe.Loadcaffe;
import com.cameronleger.neuralstylegui.model.NeuralImage;
import com.google.protobuf.TextFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtils {
    private static final Logger log = Logger.getLogger(FileUtils.class.getName());
    private static final Pattern ITERATION_PATTERN = Pattern.compile(".*_(\\d+)\\.png");
    private static final String[] EXTENSIONS = new String[] {
            "jpg", "jpeg", "png"
    };
    private static File tempDir;
    private static String uniqueText;

    public static void generateUniqueText() {
        uniqueText = String.valueOf(System.currentTimeMillis());
    }

    private static String getUniqueText() {
        if (uniqueText == null)
            generateUniqueText();
        return uniqueText;
    }

    public static boolean checkFileExists(File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean checkFilesExists(File[] files) {
        if (files == null)
            return false;
        for (File file : files)
            if (file == null || !file.exists() || !file.isFile())
                return false;
        return true;
    }

    public static boolean checkFolderExists(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    public static String getFileName(File file) {
        return FilenameUtils.removeExtension(file.getName());
    }

    public static File getTempDir() {
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

    public static File getTempOutputImage() {
        File tempOutputDir = FileUtils.getTempDir();
        if (tempOutputDir == null)
            return null;
        return new File(tempOutputDir, getUniqueText() + ".png");
    }

    public static NeuralImage[] getImages(File dir) {
        if (dir == null || !dir.isDirectory())
            return null;

        FilenameFilter imageFilter = (dir1, name) -> {
            for (final String ext : EXTENSIONS) {
                final String nameExt = FilenameUtils.getExtension(name);
                if (nameExt.equalsIgnoreCase(ext))
                    return true;
            }
            return false;
        };
        File[] files = dir.listFiles(imageFilter);

        NeuralImage[] images = new NeuralImage[]{};
        if (files != null && files.length >= 1) {
            images = new NeuralImage[files.length];
            for (int i = 0; i < files.length; i++)
                images[i] = new NeuralImage(files[i]);
        }
        return images;
    }

    public static File[] getTempOutputImageIterations() {
        File outputImage = getTempOutputImage();
        if (tempDir == null || !tempDir.isDirectory() || outputImage == null)
            return null;

        // Unix-like searching for image iterations
        String outputImageBase = getFileName(outputImage);
        FileFilter fileFilter = new WildcardFileFilter(String.format("%s_*.png", outputImageBase));
        File[] files = tempDir.listFiles(fileFilter);

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

    public static File saveTempOutputImageTo(File tempImage, File outputFolder, String possibleName) {
        String uniqueText = getUniqueText();
        File savedImage;
        if (possibleName != null && !possibleName.isEmpty()) {
            savedImage = new File(outputFolder, possibleName + ".png");
            if (savedImage.exists() && savedImage.isFile())
                savedImage = new File(outputFolder, possibleName + "_" + uniqueText + ".png");
        } else
            savedImage = new File(outputFolder, uniqueText + ".png");

        try {
            Files.copy(tempImage.toPath(), savedImage.toPath(), REPLACE_EXISTING);
            return savedImage;
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString(), e);
            return null;
        }
    }

    public static int parseImageIteration(File image) {
        int iteration = -1;
        if (image == null)
            return iteration;
        Matcher matcher = ITERATION_PATTERN.matcher(image.getAbsolutePath());
        if (matcher.matches())
            iteration = Integer.parseInt(matcher.group(1));
        return iteration;
    }

    private static int partition(int iterations[], File iterationFiles[], int firstIndex, int lastIndex) {
        int i = firstIndex, j = lastIndex;
        int tmp;
        File tmp2;
        int pivot = iterations[(firstIndex + lastIndex) / 2];

        while (i <= j) {
            while (iterations[i] < pivot)
                i++;
            while (iterations[j] > pivot)
                j--;
            if (i <= j) {
                tmp = iterations[i];
                tmp2 = iterationFiles[i];
                iterations[i] = iterations[j];
                iterationFiles[i] = iterationFiles[j];
                iterations[j] = tmp;
                iterationFiles[j] = tmp2;
                i++;
                j--;
            }
        }
        return i;
    }

    public static void quickSort(int iterations[], File iterationFiles[], int firstIndex, int lastIndex) {
        int index = partition(iterations, iterationFiles, firstIndex, lastIndex);
        if (firstIndex < index - 1)
            quickSort(iterations, iterationFiles, firstIndex, index - 1);
        if (index < lastIndex)
            quickSort(iterations, iterationFiles, index, lastIndex);
    }

    public static String[] parseLoadcaffeProto(File protoFile) {
        // Load the text-based prototxt file into the NetParameter descriptor
        Loadcaffe.NetParameter netParams;
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(protoFile), "ASCII");
            Loadcaffe.NetParameter.Builder builder = Loadcaffe.NetParameter.newBuilder();
            TextFormat.merge(reader, builder);
            netParams = builder.build();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.toString(), e);
            return null;
        }

        // Retrieve all of the layer names that are ReLUs
        List<String> layersList = netParams.getLayersList().stream()
                .filter(layer -> layer.getType() == Loadcaffe.V1LayerParameter.LayerType.RELU)
                .map(Loadcaffe.V1LayerParameter::getName).collect(Collectors.toList());

        String[] layers = new String[layersList.size()];
        for (int i = 0; i < layersList.size(); i++)
            layers[i] = layersList.get(i);
        return layers;
    }
}
