package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.Image;
import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private NeuralService neuralService = new NeuralService();
    private NeuralStyle neuralStyle = new NeuralStyle();

    @FXML
    private TextField stylePath;
    @FXML
    private TextField contentPath;
    @FXML
    private TextArea logTextArea;

    public void initialize(URL location, ResourceBundle resources) {
        assert stylePath != null : "fx:id=\"stylePath\" was not injected.";
        assert contentPath != null : "fx:id=\"contentPath\" was not injected.";
        assert logTextArea != null : "fx:id=\"logTextArea\" was not injected.";

        setupServiceListeners();

        neuralStyle.contentImage = new Image(new File("/home/cameron/input/41406_1_miscellaneous_digital_art_trippy_funky_trippy_tree.jpg"));
        neuralStyle.styleImage = new Image(new File("/home/cameron/input/2015-10-09 03.18.42.JPG"));
        neuralStyle.outputImage = new Image(new File("/home/cameron/output/test.jpg"));

        neuralService.setNeuralStyle(neuralStyle);
        neuralService.start();
    }

    private void setupServiceListeners() {
        // easily bind some properties
        stylePath.textProperty().bind(neuralService.messageProperty());

        // handle each Worker.State
        neuralService.stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue<? extends Worker.State> observableValue,
                                Worker.State oldState, Worker.State newState) {
                switch (newState) {
                    case SCHEDULED:
                        contentPath.setText("SCHEDULED");
                        break;
                    case READY:
                        contentPath.setText("READY");
                        break;
                    case RUNNING:
                        contentPath.setText("RUNNING");
                        break;
                    case SUCCEEDED:
                        contentPath.setText("SUCCEEDED");
                        break;
                    case CANCELLED:
                        contentPath.setText("CANCELLED");
                        break;
                    case FAILED:
                        contentPath.setText("FAILED");
                        break;
                }
            }
        });
    }
}
