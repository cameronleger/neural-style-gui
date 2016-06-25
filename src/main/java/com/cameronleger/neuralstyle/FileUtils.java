package com.cameronleger.neuralstyle;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileUtils {
    private static final Pattern iterationPattern = Pattern.compile(".*_(\\d+)\\.png");

    static boolean checkFileExists(File file) {
        return file != null && file.exists() && file.isFile();
    }

    static boolean checkFolderExists(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    static String getFileName(File file) {
        return FilenameUtils.removeExtension(file.getName());
    }

    static int parseImageIteration(File image) {
        int iteration = -1;
        if (image == null)
            return iteration;
        Matcher matcher = iterationPattern.matcher(image.getAbsolutePath());
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
