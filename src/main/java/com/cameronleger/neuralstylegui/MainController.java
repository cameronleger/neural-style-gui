package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.reactfx.EventStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {
    private NeuralService neuralService = new NeuralService();
    private NeuralStyle neuralStyle = new NeuralStyle();
    private Stage stage;
    private ResourceBundle bundle;

    @FXML
    private TextField stylePath;
    @FXML
    private TextField contentPath;
    @FXML
    private TextField outputPath;
    @FXML
    private Button styleFileButton;
    @FXML
    private Button contentFileButton;
    @FXML
    private Button outputFolderButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private ImageView imageView;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea logTextArea;

    private static FileChooser imageFileChooser = new FileChooser();
    static {
        imageFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
    }
    private static DirectoryChooser directoryChooser = new DirectoryChooser();

    public void initialize(URL location, ResourceBundle resources) {
        assert stylePath != null : "fx:id=\"stylePath\" was not injected.";
        assert contentPath != null : "fx:id=\"contentPath\" was not injected.";
        assert outputPath != null : "fx:id=\"outputPath\" was not injected.";
        assert styleFileButton != null : "fx:id=\"styleFileButton\" was not injected.";
        assert contentFileButton != null : "fx:id=\"contentFileButton\" was not injected.";
        assert outputFolderButton != null : "fx:id=\"outputFolderButton\" was not injected.";
        assert startButton != null : "fx:id=\"startButton\" was not injected.";
        assert stopButton != null : "fx:id=\"stopButton\" was not injected.";
        assert imageView != null : "fx:id=\"imageView\" was not injected.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected.";
        assert logTextArea != null : "fx:id=\"logTextArea\" was not injected.";

        bundle = resources;

        setupButtonListeners();
        setupServiceListeners();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void stopService() {
        neuralService.cancel();
    }

    private void setupButtonListeners() {
        EventStreams.eventsOf(styleFileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            imageFileChooser.setTitle(bundle.getString("styleFileChooser"));
            File styleFile = imageFileChooser.showOpenDialog(stage);
            if (styleFile != null) {
                neuralStyle.setStyleImage(styleFile);
                stylePath.setText(styleFile.getAbsolutePath());
                imageFileChooser.setInitialDirectory(styleFile.getParentFile());

                try {
                    imageView.setImage(new Image(new FileInputStream(styleFile)));
                } catch (FileNotFoundException e) {
                    Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
                }

                File outputImage = neuralStyle.getOutputImage();
                if (outputImage != null)
                    System.out.println(outputImage.getAbsolutePath());
            }
        });

        EventStreams.eventsOf(contentFileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            imageFileChooser.setTitle(bundle.getString("contentFileChooser"));
            File contentFile = imageFileChooser.showOpenDialog(stage);
            if (contentFile != null) {
                neuralStyle.setContentImage(contentFile);
                contentPath.setText(contentFile.getAbsolutePath());
                imageFileChooser.setInitialDirectory(contentFile.getParentFile());

                File outputImage = neuralStyle.getOutputImage();
                if (outputImage != null)
                    System.out.println(outputImage.getAbsolutePath());
            }
        });

        EventStreams.eventsOf(outputFolderButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            directoryChooser.setTitle(bundle.getString("outputFolderChooser"));
            File outputFolder = directoryChooser.showDialog(stage);
            if (outputFolder != null) {
                neuralStyle.setOutputFolder(outputFolder);
                outputPath.setText(outputFolder.getAbsolutePath());
                directoryChooser.setInitialDirectory(outputFolder);

                File outputImage = neuralStyle.getOutputImage();
                if (outputImage != null)
                    System.out.println(outputImage.getAbsolutePath());
            }
        });

        EventStreams.eventsOf(startButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            neuralService.setNeuralStyle(neuralStyle);
            if (!neuralService.isRunning()) {
                neuralService.reset();
                neuralService.start();
            }
        });

        EventStreams.eventsOf(stopButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            if (neuralService.isRunning())
                neuralService.cancel();
        });
    }

    private void setupServiceListeners() {
        // easily bind some properties
        logTextArea.textProperty().bind(neuralService.messageProperty());

        // handle each Worker.State
        neuralService.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue,
                                Worker.State oldState, Worker.State newState) {
                switch (newState) {
                    case SCHEDULED:
                        statusLabel.setText("Scheduled");
                        break;
                    case READY:
                        statusLabel.setText("Ready to Run");
                        break;
                    case RUNNING:
                        statusLabel.setText("Running");
                        break;
                    case SUCCEEDED:
                        statusLabel.setText("Finished");
                        break;
                    case CANCELLED:
                        statusLabel.setText("Cancelled");
                        break;
                    case FAILED:
                        statusLabel.setText("Failed");
                        break;
                }
            }
        });

        final ColorAdjust highlighted = new ColorAdjust(0, 0, 0.3, 0);
        neuralService.runningProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean isRunning) {
                if (isRunning) {
                    statusLabel.setEffect(highlighted);
                } else {
                    statusLabel.setEffect(null);
                }
            }
        });
    }
}
