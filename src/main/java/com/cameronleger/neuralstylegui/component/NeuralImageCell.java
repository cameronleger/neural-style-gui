package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.NeuralImage;
import com.cameronleger.neuralstylegui.model.properties.NeuralDouble;
import javafx.beans.value.ObservableBooleanValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import java.io.IOException;
import java.util.logging.Logger;

public class NeuralImageCell extends AnchorPane {

    private static final Logger log = Logger.getLogger(NeuralImageCell.class.getName());

    private NeuralImage neuralImage;
    private Subscription weightChanges;

    @FXML
    private ImageView image;
    @FXML
    private CheckBox selected;
    @FXML
    private TextField weight;

    public NeuralImageCell(ObservableBooleanValue editable) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/neuralImageCell.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setEditable(false);
        if (editable != null) {
            EventStreams.changesOf(editable).subscribe(editableChange -> {
                setEditable(editableChange.getNewValue());
            });
        }
    }

    @FXML
    void initialize() {
        assert image != null : "fx:id=\"image\" was not injected.";
        assert selected != null : "fx:id=\"selected\" was not injected.";
        assert weight != null : "fx:id=\"weight\" was not injected.";
        EventStreams.changesOf(selected.selectedProperty()).subscribe(selectedChange -> {
            if (selectedChange.getNewValue()) {
                getStyleClass().setAll("selected-image-cell");
            } else {
                getStyleClass().clear();
            }
        });
    }

    public void setNeuralImage(NeuralImage newNeuralImage) {
        // Remove previous bindings if applicable
        image.imageProperty().unbind();
        if (neuralImage != null)
            selected.selectedProperty().unbindBidirectional(neuralImage.selectedProperty());
        if (weightChanges != null)
            weightChanges.unsubscribe();

        neuralImage = newNeuralImage;

        if (neuralImage != null) {
            image.imageProperty().bind(neuralImage.imageProperty());
            selected.selectedProperty().bindBidirectional(neuralImage.selectedProperty());

            // Event Streams for Weight to convert between double and string
            weight.setText(String.valueOf(neuralImage.getWeight()));
            weightChanges = EventStreams.changesOf(weight.focusedProperty()).subscribe(focusChange -> {
                if (!focusChange.getNewValue()) { // focusing away from input
                    double newWeight = NeuralDouble.DOUBLE_CONVERTER.fromString(weight.getText()).doubleValue();
                    neuralImage.setWeight(newWeight);
                    if (newWeight == 0)
                        weight.setText("1.0");
                }
            });
        }
    }

    public void setEditable(boolean editable) {
        selected.setVisible(editable);
        weight.setVisible(editable);
    }

    public ImageView getImage() {
        return image;
    }

}
