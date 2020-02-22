package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstylegui.helper.MovingImageView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.reactfx.EventStreams;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class FullImageView extends VBox {

    private static final Logger log = Logger.getLogger(FullImageView.class.getName());

    @FXML
    private ImageView image;
    @FXML
    private Button style;
    @FXML
    private Button content;
    @FXML
    private Button init;

    MovingImageView imageView;

    public FullImageView(File imageFile, Consumer<ActionEvent> styleEvent, Consumer<ActionEvent> contentEvent, Consumer<ActionEvent> initEvent, ResourceBundle resources) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/imagePreviewTab.fxml"));
        fxmlLoader.setResources(resources);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        image.fitWidthProperty().bind(widthProperty());
        image.fitHeightProperty().bind(heightProperty());

        imageView = new MovingImageView(image);
        imageView.setImage(imageFile);

        EventStreams.eventsOf(style, ActionEvent.ACTION)
                .subscribe(styleEvent);
        EventStreams.eventsOf(content, ActionEvent.ACTION)
                .subscribe(contentEvent);
        EventStreams.eventsOf(init, ActionEvent.ACTION)
                .subscribe(initEvent);
    }

    @FXML
    void initialize() {
        assert image != null : "fx:id=\"image\" was not injected.";
    }

}
