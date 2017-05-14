package com.cameronleger.neuralstyle;

import caffe.Loadcaffe;
import com.cameronleger.neuralstylegui.model.NeuralImage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.protobuf.TextFormat;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
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
    private static final String LAST_STYLE_JSON = "lastStyle.json";
    private static File tempDir;
    private static String uniqueText;
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(File.class, new FileAdapter())
            .create();

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

    private static File getTempOutputFile(String extension) {
        File tempOutputDir = FileUtils.getTempDir();
        if (tempOutputDir == null)
            return null;
        return new File(tempOutputDir, getUniqueText() + "." + extension);
    }

    public static File getTempOutputImage() {
        return getTempOutputFile("png");
    }

    public static File getTempOutputStyle() {
        return getTempOutputFile("json");
    }

    public static File saveTempOutputStyle(NeuralStyle neuralStyle) {
        File tempOutputStyle = FileUtils.getTempOutputStyle();
        if (tempOutputStyle == null) {
            log.log(Level.FINE, "Unable to open file to save output style.");
            return null;
        }
        return saveOutputStyle(neuralStyle, tempOutputStyle);
    }

    public static File saveLastUsedOutputStyle(NeuralStyle neuralStyle) {
        File lastUsedOutputStyle = new File(".", LAST_STYLE_JSON);
        if (lastUsedOutputStyle.exists() && !lastUsedOutputStyle.canWrite()) {
            log.log(Level.FINE, "Unable to open file to save output style.");
            return null;
        }
        return saveOutputStyle(neuralStyle, lastUsedOutputStyle);
    }

    public static File getLastUsedOutputStyle() {
        return new File(".", LAST_STYLE_JSON);
    }

    public static File saveOutputStyle(NeuralStyle neuralStyle, File outputFile) {
        try (FileWriter file = new FileWriter(outputFile)) {
            file.write(gson.toJson(neuralStyle));
            log.log(Level.FINE, "Output style saved: " + outputFile.getAbsolutePath());
            return outputFile;
        } catch (IOException e) {
            log.log(Level.FINE, "IOException saving output style.");
            log.log(Level.SEVERE, e.toString(), e);
            return null;
        }
    }

    private static NeuralStyle loadStyleV1(File styleFile) {
        final FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(styleFile);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
            NeuralStyleV1 oldNeuralStyle = gson.fromJson(reader, NeuralStyleV1.class);
            return oldNeuralStyle.upgrade();
        } catch (Exception e) {
            log.log(Level.FINE, "Exception loading input style.");
            log.log(Level.SEVERE, e.toString(), e);
            return null;
        }
    }

    public static NeuralStyle loadStyle(File styleFile) {
        if (!FileUtils.checkFileExists(styleFile)) {
            log.log(Level.FINE, "Cannot load a missing file.");
            return null;
        }
        final FileInputStream fileStream;
        try {
            fileStream = new FileInputStream(styleFile);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream));
            return gson.fromJson(reader, NeuralStyle.class);
        } catch(JsonSyntaxException e) {
            log.log(Level.FINE, "Exception loading input style, trying another version.");
            log.log(Level.SEVERE, e.toString(), e);
            return loadStyleV1(styleFile);
        } catch (Exception e) {
            log.log(Level.FINE, "Exception loading input style.");
            log.log(Level.SEVERE, e.toString(), e);
            return null;
        }
    }

    public static List<NeuralImage> getImages(File dir) {
        List<NeuralImage> images = new ArrayList<>();
        if (dir == null || !dir.isDirectory())
            return images;

        FilenameFilter imageFilter = (dir1, name) -> {
            for (final String ext : EXTENSIONS) {
                final String nameExt = FilenameUtils.getExtension(name);
                if (nameExt.equalsIgnoreCase(ext))
                    return true;
            }
            return false;
        };
        File[] files = dir.listFiles(imageFilter);

        if (files != null && files.length >= 1) {
            for (File file : files) images.add(new NeuralImage(file));
            images.sort((neuralImage1, neuralImage2) ->
                    neuralImage1.getName().compareToIgnoreCase(neuralImage2.getName()));
        }
        return images;
    }

    public static File[] getTempOutputStyles() {
        if (tempDir == null || !tempDir.isDirectory())
            return null;

        // Unix-like searching for styles
        FileFilter styleFileFilter = new WildcardFileFilter("*.json");
        File[] files = tempDir.listFiles(styleFileFilter);

        if (files != null && files.length > 1) {
            long[] fileIters = new long[files.length];
            for (int i = 0; i < files.length; i++) {
                try {
                    fileIters[i] = Long.valueOf(FileUtils.getFileName(files[i]));
                } catch (Exception e) {
                    fileIters[i] = 0;
                }
            }
            FileUtils.quickSort(fileIters, files, 0, files.length - 1);
        }

        return files;
    }

    public static Map<String, Set<String>> getTempOutputs() {
        Map<String, Set<String>> outputs = new LinkedHashMap<>();

        File[] styleFiles = getTempOutputStyles();

        if (styleFiles == null)
            return outputs;

        for (File styleFile : styleFiles) {
            Set<String> imageFilesList = new LinkedHashSet<>();

            // Unix-like searching for images
            FileFilter imageFileFilter = new WildcardFileFilter(String.format("%s_*.png", getFileName(styleFile)));
            File[] imageFiles = tempDir.listFiles(imageFileFilter);

            if (imageFiles != null && imageFiles.length > 1) {
                int[] imageFileIters = new int[imageFiles.length];
                for (int i = 0; i < imageFiles.length; i++)
                    imageFileIters[i] = FileUtils.parseImageIteration(imageFiles[i]);
                FileUtils.quickSort(imageFileIters, imageFiles, 0, imageFiles.length - 1);

                // if the latest file was still being written to during the check
                // then replace it with the previous file (set will remove it)
                if (isFileBeingWritten(imageFiles[imageFiles.length - 1]))
                    imageFiles[imageFiles.length - 1] = imageFiles[imageFiles.length - 2];

                for (File imageFile : imageFiles)
                    imageFilesList.add(imageFile.getAbsolutePath());
            }

            outputs.put(styleFile.getAbsolutePath(), imageFilesList);
        }

        return outputs;
    }

    public static File[] getTempOutputImageIterations(File outputImage) {
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

            // if the latest file was still being written to during the check
            // then replace it with the previous file (set will remove it)
            if (isFileBeingWritten(files[files.length - 1]))
                files[files.length - 1] = files[files.length - 2];
        }

        return files;
    }

    public static File getTempOutputImageStyle() {
        File outputStyle = getTempOutputStyle();
        if (tempDir == null || !tempDir.isDirectory() || outputStyle == null || !checkFileExists(outputStyle))
            return null;
        return outputStyle;
    }

    public static File[] saveTempOutputsTo(File tempImage, File tempStyle, File outputFolder, String possibleName) {
        String uniqueText = String.valueOf(System.currentTimeMillis());
        File savedImage;
        File savedStyle;

        if (possibleName != null && !possibleName.isEmpty()) {
            savedImage = new File(outputFolder, possibleName + ".png");
            savedStyle = new File(outputFolder, possibleName + ".json");
            if ((savedImage.exists() && savedImage.isFile()) || savedStyle.exists() && savedStyle.isFile()) {
                savedImage = new File(outputFolder, possibleName + "_" + uniqueText + ".png");
                savedStyle = new File(outputFolder, possibleName + "_" + uniqueText + ".json");
            }
        } else {
            savedImage = new File(outputFolder, uniqueText + ".png");
            savedStyle = new File(outputFolder, uniqueText + ".json");
        }

        try {
            Files.copy(tempImage.toPath(), savedImage.toPath(), REPLACE_EXISTING);
            Files.copy(tempStyle.toPath(), savedStyle.toPath(), REPLACE_EXISTING);
            return new File[]{savedImage, savedStyle};
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

    public static boolean isFileBeingWritten(File fileToCheck) {
        // try 4 times over 400ms to see if the file size stagnates
        int retries = 4;
        int sleep = 100;
        long previousFileSize = fileToCheck.length();
        while (retries > 0) {
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                // safely ignored
                log.log(Level.FINER, "Sleep while checking file size interrupted");
            }

            long updatedFileSize = fileToCheck.length();

            // update file size or return if it stagnated
            if (updatedFileSize > previousFileSize)
                previousFileSize = updatedFileSize;
            else
                return false;

            retries--;
        }

        // file size was always different, still being written to
        return true;
    }

    private static int partition(long iterations[], File iterationFiles[], int firstIndex, int lastIndex) {
        int i = firstIndex, j = lastIndex;
        long tmp;
        File tmp2;
        long pivot = iterations[(firstIndex + lastIndex) / 2];

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

    public static void quickSort(long iterations[], File iterationFiles[], int firstIndex, int lastIndex) {
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
                .filter(layer1 -> layer1.getType() == Loadcaffe.V1LayerParameter.LayerType.RELU)
                .map(Loadcaffe.V1LayerParameter::getName).collect(Collectors.toList());

        layersList.addAll(netParams.getLayerList().stream()
                .filter(lp -> lp.getType().equalsIgnoreCase("ReLU"))
                .map(Loadcaffe.LayerParameter::getName).collect(Collectors.toList()));

        // Create the array of ReLU layer names
        String[] layers = new String[layersList.size()];
        for (int i = 0; i < layersList.size(); i++)
            layers[i] = layersList.get(i);
        return layers;
    }
}
