package com.cameronleger.neuralstylegui;

import javafx.beans.property.*;

public class NeuralLayer {
    private BooleanProperty selected;
    private StringProperty name;

    public NeuralLayer(String name, boolean selected) {
        this.selected = new SimpleBooleanProperty(selected);
        this.name = new SimpleStringProperty(name);
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
}
