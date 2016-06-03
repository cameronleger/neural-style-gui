package com.cameronleger.neuralstyle;

import java.io.File;

public class Image {
    private File path;

    public Image(File path) {
        this.path = path;
    }

    public File getPath() {
        return path;
    }
}
