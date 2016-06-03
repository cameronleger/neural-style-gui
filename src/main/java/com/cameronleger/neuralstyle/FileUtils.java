package com.cameronleger.neuralstyle;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

public class FileUtils {

    public static boolean checkFileExists(File file) {
        return file != null && file.exists() && file.isFile();
    }

    public static boolean checkFolderExists(File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    public static String getFileName(File file) {
        return FilenameUtils.removeExtension(file.getName());
    }
}
