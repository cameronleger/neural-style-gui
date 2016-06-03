package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.reactfx.EventStreams;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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
    private TextArea logTextArea;

    private static FileChooser imageFileChooser = new FileChooser();
    private static DirectoryChooser directoryChooser = new DirectoryChooser();

    public void initialize(URL location, ResourceBundle resources) {
        assert stylePath != null : "fx:id=\"stylePath\" was not injected.";
        assert contentPath != null : "fx:id=\"contentPath\" was not injected.";
        assert outputPath != null : "fx:id=\"outputPath\" was not injected.";
        assert styleFileButton != null : "fx:id=\"styleFileButton\" was not injected.";
        assert contentFileButton != null : "fx:id=\"contentFileButton\" was not injected.";
        assert outputFolderButton != null : "fx:id=\"outputFolderButton\" was not injected.";
        assert logTextArea != null : "fx:id=\"logTextArea\" was not injected.";

        bundle = resources;

        imageFileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));

        setupButtonListeners();
        setupServiceListeners();

//        neuralService.setNeuralStyle(neuralStyle);
//        neuralService.start();
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
    }

    private void setupServiceListeners() {
        // easily bind some properties
//        stylePath.textProperty().bind(neuralService.messageProperty());

        // handle each Worker.State
//        neuralService.stateProperty().addListener(new ChangeListener<Worker.State>() {
//            public void changed(ObservableValue<? extends Worker.State> observableValue,
//                                Worker.State oldState, Worker.State newState) {
//                switch (newState) {
//                    case SCHEDULED:
//                        contentPath.setText("SCHEDULED");
//                        break;
//                    case READY:
//                        contentPath.setText("READY");
//                        break;
//                    case RUNNING:
//                        contentPath.setText("RUNNING");
//                        break;
//                    case SUCCEEDED:
//                        contentPath.setText("SUCCEEDED");
//                        break;
//                    case CANCELLED:
//                        contentPath.setText("CANCELLED");
//                        break;
//                    case FAILED:
//                        contentPath.setText("FAILED");
//                        break;
//                }
//            }
//        });
    }
}
