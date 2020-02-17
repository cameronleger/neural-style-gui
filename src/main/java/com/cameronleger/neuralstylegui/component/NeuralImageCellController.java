package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.model.NeuralImage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;

import java.io.IOException;
import java.util.logging.Logger;

public class NeuralImageCellController {
    private static final Logger log = Logger.getLogger(NeuralImageCellController.class.getName());
    private NeuralImage neuralImage;
    private Subscription weightChanges;

    private StringConverter<Number> doubleConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number t) {
            return String.valueOf(t.doubleValue());
        }

        @Override
        public Number fromString(String string) {
            try {
                return Double.parseDouble(string);
            } catch (Exception e) {
                return 0;
            }
        }
    };

    @FXML
    private GridPane gridPane;
    @FXML
    private ImageView image;
    @FXML
    private CheckBox selected;
    @FXML
    private TextField weight;

    public NeuralImageCellController(boolean editable) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/neuralImageCell.fxml"));
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        setEditable(editable);
    }

    @FXML
    void initialize() {
        assert gridPane != null : "fx:id=\"gridPane\" was not injected.";
        assert image != null : "fx:id=\"image\" was not injected.";
        assert selected != null : "fx:id=\"selected\" was not injected.";
        assert weight != null : "fx:id=\"weight\" was not injected.";
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
            // Bind Image and Selection
            image.imageProperty().bind(neuralImage.imageProperty());
            selected.selectedProperty().bindBidirectional(neuralImage.selectedProperty());

            // Event Streams for Weight to convert between double and string
            weight.setText(String.valueOf(neuralImage.getWeight()));
            weightChanges = EventStreams.changesOf(weight.focusedProperty()).subscribe(focusChange -> {
                if (!focusChange.getNewValue()) { // focusing away from input
                    double newWeight = doubleConverter.fromString(weight.getText()).doubleValue();
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

    public GridPane getCellLayout() {
        return gridPane;
    }

    public ImageView getImage() {
        return image;
    }

    public CheckBox getSelected() {
        return selected;
    }

    public TextField getWeight() {
        return weight;
    }
}
