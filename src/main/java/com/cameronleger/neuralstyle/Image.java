package com.cameronleger.neuralstyle;

import java.io.File;

public class Image {
    private File file;

    public Image(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
