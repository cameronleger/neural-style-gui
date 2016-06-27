package com.cameronleger.neuralstylegui;

import javafx.beans.property.*;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class NeuralImage {
    static final int THUMBNAIL_SIZE = 150;
    private BooleanProperty selected;
    private StringProperty name;
    private ObjectProperty<File> imageFile;
    private ObjectProperty<Image> image;
    private DoubleProperty weight;

    public NeuralImage(File imageFile) {
        this.selected = new SimpleBooleanProperty(false);
        this.name = new SimpleStringProperty(imageFile.getName());
        this.imageFile = new SimpleObjectProperty<>(imageFile);
        Image image = null;
        try {
            image = new Image(new FileInputStream(imageFile), THUMBNAIL_SIZE, THUMBNAIL_SIZE, true, false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.image = new SimpleObjectProperty<>(image);
        this.weight = new SimpleDoubleProperty(1);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObjectProperty<File> imageFileProperty() {
        return imageFile;
    }

    public File getImageFile() {
        return imageFile.get();
    }

    public void setImageFile(File imageFile) {
        this.imageFile.set(imageFile);
    }

    public ObjectProperty<Image> imageProperty() {
        return image;
    }

    public Image getImage() {
        return image.get();
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public DoubleProperty weightProperty() {
        return weight;
    }

    public double getWeight() {
        return weight.get();
    }

    public void setWeight(double weight) {
        this.weight.set(weight);
    }
}
