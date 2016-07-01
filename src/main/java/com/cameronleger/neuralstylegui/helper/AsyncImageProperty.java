package com.cameronleger.neuralstylegui.helper;

import com.cameronleger.neuralstyle.FileUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncImageProperty extends SimpleObjectProperty<Image> {
    private static final Logger log = Logger.getLogger(AsyncImageProperty.class.getName());
    private final ImageLoadService imageLoadService = new ImageLoadService();
    private final ObjectProperty<File> imageFile = new SimpleObjectProperty<>();

    public AsyncImageProperty(int width, int height) {
        imageLoadService.setSize(width, height);

        imageLoadService.stateProperty().addListener((observable, oldValue, value) -> {
            if (value == Worker.State.SUCCEEDED)
                set(imageLoadService.getValue());
            if (value == Worker.State.FAILED)
                set(null);

            if (value == Worker.State.SUCCEEDED || value == Worker.State.CANCELLED || value == Worker.State.FAILED) {
                File handle = imageFile.get();
                if (handle != null && !handle.equals(imageLoadService.imageFile))
                    loadImageInBackground(handle);
            }
        });

        imageFile.addListener((observable, oldValue, value) -> {
            if(!imageLoadService.isRunning()) {
                loadImageInBackground(imageFile.getValue());
            }
        });
    }

    public ObjectProperty<File> imageFileProperty() {
        return imageFile;
    }

    private void loadImageInBackground(File imageFile) {
        synchronized(imageLoadService) {
            if (FileUtils.checkFileExists(imageFile)) {
                imageLoadService.setImageFile(imageFile);
                imageLoadService.restart();
            }
        }
    }

    private static class ImageLoadService extends Service<Image> {
        private File imageFile;
        private int width = 0;
        private int height = 0;

        public void setImageFile(File imageFile) {
            this.imageFile = imageFile;
        }

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected Task<Image> createTask() {
            final File imageFile = this.imageFile;
            final int width = this.width;
            final int height = this.height;
            return new Task<Image>() {
                @Override
                protected Image call() {
                    try {
                        return new Image(new FileInputStream(imageFile), width, height, true, false);
                    } catch (IOException e) {
                        log.log(Level.SEVERE, e.toString(), e);
                        return null;
                    }
                }
            };
        }
    }
}
