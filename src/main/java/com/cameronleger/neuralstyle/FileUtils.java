package com.cameronleger.neuralstyle;

import com.cameronleger.neuralstylegui.NeuralImage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUtils {
    private static final Logger log = Logger.getLogger(FileUtils.class.getName());
    private static final Pattern ITERATION_PATTERN = Pattern.compile(".*_(\\d+)\\.png");
    private static final String[] EXTENSIONS = new String[] {
            "jpg", "jpeg", "png"
    };

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

    public static File[] getTempOutputImageIterations(File outputDir, File outputImage) {
        if (outputDir == null || !outputDir.isDirectory() || outputImage == null)
            return null;

        // Unix-like searching for image iterations
        String outputImageBase = getFileName(outputImage);
        FileFilter fileFilter = new WildcardFileFilter(String.format("%s_*.png", outputImageBase));
        File[] files = outputDir.listFiles(fileFilter);

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
        String uniqueText = String.valueOf(System.nanoTime());
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

    static int parseImageIteration(File image) {
        int iteration = -1;
        if (image == null)
            return iteration;
        Matcher matcher = ITERATION_PATTERN.matcher(image.getAbsolutePath());
        if (matcher.matches())
            iteration = Integer.parseInt(matcher.group(1));
        return iteration;
    }

    private static int partition(int arr[], File arr2[], int firstIndex, int lastIndex) {
        int i = firstIndex, j = lastIndex;
        int tmp;
        File tmp2;
        int pivot = arr[(firstIndex + lastIndex) / 2];

        while (i <= j) {
            while (arr[i] < pivot)
                i++;
            while (arr[j] > pivot)
                j--;
            if (i <= j) {
                tmp = arr[i];
                tmp2 = arr2[i];
                arr[i] = arr[j];
                arr2[i] = arr2[j];
                arr[j] = tmp;
                arr2[j] = tmp2;
                i++;
                j--;
            }
        }
        return i;
    }

    static void quickSort(int arr[], File arr2[], int firstIndex, int lastIndex) {
        int index = partition(arr, arr2, firstIndex, lastIndex);
        if (firstIndex < index - 1)
            quickSort(arr, arr2, firstIndex, index - 1);
        if (index < lastIndex)
            quickSort(arr, arr2, index, lastIndex);
    }
}
