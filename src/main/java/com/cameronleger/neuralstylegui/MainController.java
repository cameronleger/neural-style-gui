package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.reactfx.EventStreams;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {
    private static final Logger log = Logger.getLogger(MainController.class.getName());
    private NeuralService neuralService = new NeuralService();
    private NeuralStyle neuralStyle = new NeuralStyle();
    private Stage stage;
    private ResourceBundle bundle;
    private Timer imageTimer;

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
    private Slider printIterSlider;
    @FXML
    private TextField printIterField;
    @FXML
    private Slider saveIterSlider;
    @FXML
    private TextField saveIterField;
    @FXML
    private Slider maxIterSlider;
    @FXML
    private TextField maxIterField;
    @FXML
    private Slider styleSizeSlider;
    @FXML
    private TextField styleSizeField;
    @FXML
    private Slider outputSizeSlider;
    @FXML
    private TextField outputSizeField;
    @FXML
    private Slider styleWeightSlider;
    @FXML
    private TextField styleWeightField;
    @FXML
    private Slider contentWeightSlider;
    @FXML
    private TextField contentWeightField;
    @FXML
    private Slider tvWeightSlider;
    @FXML
    private TextField tvWeightField;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;

    @FXML
    private ImageView imageView;
    private MovingImageView outputImageView;
    @FXML
    private HBox imageViewSizer;

    @FXML
    private ProgressBar progress;
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
        log.log(Level.FINER, "Checking that all FXML items were injected.");
        checkInjections();

        bundle = resources;
        outputImageView = new MovingImageView(imageView);

        log.log(Level.FINER, "Setting button listeners.");
        setupButtonListeners();
        log.log(Level.FINER, "Setting field listeners.");
        setupFieldListeners();
        log.log(Level.FINER, "Setting service listeners.");
        setupServiceListeners();
        log.log(Level.FINER, "Setting image listeners.");
        setupImageListeners();
        log.log(Level.FINER, "Setting neural service log handler.");
        neuralService.addLogHandler(new TextAreaLogHandler(logTextArea));

        // TODO: Temporary time saver
        setStyleFile(new File("/home/cameron/input/75171b3cdec4b3727d8e71f68434c084.jpg"));
        setContentFile(new File("/home/cameron/input/cloudy.jpg"));
        setOutputFolder(new File("/home/cameron/output/TESTING"));
        toggleStartButton();
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    void startService() {
        if (!neuralService.isRunning()) {
            log.log(Level.FINE, "Starting neural service.");
            neuralStyle.generateUniqueText();
            neuralService.setNeuralStyle(neuralStyle);
            logTextArea.clear();
            neuralService.reset();
            neuralService.start();
            imageTimer.restart();
        }
    }

    void stopService() {
        if (neuralService.isRunning()) {
            log.log(Level.FINE, "Cancelling neural service.");
            neuralService.cancel();
            imageTimer.stop();
        }
    }

    private void toggleStartButton() {
        File outputImage = neuralStyle.getOutputImage();
        if (outputImage != null) {
            log.log(Level.FINE, "Output image available: {0}", outputImage.getAbsolutePath());
            startButton.setDisable(false);
        }
    }

    private void setImageView(File styleFile) {
        try {
            outputImageView.setImage(new Image(new FileInputStream(styleFile)));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    private void setStyleFile(File styleFile) {
        neuralStyle.setStyleImage(styleFile);
        stylePath.setText(styleFile.getAbsolutePath());
        imageFileChooser.setInitialDirectory(styleFile.getParentFile());
    }

    private void setContentFile(File contentFile) {
        neuralStyle.setContentImage(contentFile);
        contentPath.setText(contentFile.getAbsolutePath());
        imageFileChooser.setInitialDirectory(contentFile.getParentFile());
    }

    private void setOutputFolder(File outputFolder) {
        neuralStyle.setOutputFolder(outputFolder);
        outputPath.setText(outputFolder.getAbsolutePath());
        directoryChooser.setInitialDirectory(outputFolder);
    }

    private void checkInjections() {
        assert stylePath != null : "fx:id=\"stylePath\" was not injected.";
        assert contentPath != null : "fx:id=\"contentPath\" was not injected.";
        assert outputPath != null : "fx:id=\"outputPath\" was not injected.";
        assert styleFileButton != null : "fx:id=\"styleFileButton\" was not injected.";
        assert contentFileButton != null : "fx:id=\"contentFileButton\" was not injected.";
        assert outputFolderButton != null : "fx:id=\"outputFolderButton\" was not injected.";
        assert printIterSlider != null : "fx:id=\"printIterSlider\" was not injected.";
        assert printIterField != null : "fx:id=\"printIterField\" was not injected.";
        assert saveIterSlider != null : "fx:id=\"saveIterSlider\" was not injected.";
        assert saveIterField != null : "fx:id=\"saveIterField\" was not injected.";
        assert maxIterSlider != null : "fx:id=\"maxIterSlider\" was not injected.";
        assert maxIterField != null : "fx:id=\"maxIterField\" was not injected.";
        assert styleSizeSlider != null : "fx:id=\"styleSizeSlider\" was not injected.";
        assert styleSizeField != null : "fx:id=\"styleSizeField\" was not injected.";
        assert outputSizeSlider != null : "fx:id=\"outputSizeSlider\" was not injected.";
        assert outputSizeField != null : "fx:id=\"outputSizeField\" was not injected.";
        assert styleWeightSlider != null : "fx:id=\"styleWeightSlider\" was not injected.";
        assert styleWeightField != null : "fx:id=\"styleWeightField\" was not injected.";
        assert contentWeightSlider != null : "fx:id=\"contentWeightSlider\" was not injected.";
        assert contentWeightField != null : "fx:id=\"contentWeightField\" was not injected.";
        assert tvWeightSlider != null : "fx:id=\"tvWeightSlider\" was not injected.";
        assert tvWeightField != null : "fx:id=\"tvWeightField\" was not injected.";
        assert startButton != null : "fx:id=\"startButton\" was not injected.";
        assert stopButton != null : "fx:id=\"stopButton\" was not injected.";
        assert imageView != null : "fx:id=\"imageView\" was not injected.";
        assert imageViewSizer != null : "fx:id=\"imageViewSizer\" was not injected.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected.";
        assert progress != null : "fx:id=\"progress\" was not injected.";
        assert logTextArea != null : "fx:id=\"logTextArea\" was not injected.";
        log.log(Level.FINER, "All FXML items were injected.");
    }

    private void setupButtonListeners() {
        log.log(Level.FINER, "Setting Style File listener.");
        EventStreams.eventsOf(styleFileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing style file chooser.");
            imageFileChooser.setTitle(bundle.getString("styleFileChooser"));
            File styleFile = imageFileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "Style file chosen: {0}", styleFile);
            if (styleFile != null) {
                setStyleFile(styleFile);
                setImageView(styleFile);
                toggleStartButton();
            }
        });

        log.log(Level.FINER, "Setting Content File listener.");
        EventStreams.eventsOf(contentFileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing content file chooser.");
            imageFileChooser.setTitle(bundle.getString("contentFileChooser"));
            File contentFile = imageFileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "Content file chosen: {0}", contentFile);
            if (contentFile != null) {
                setContentFile(contentFile);
                setImageView(contentFile);
                toggleStartButton();
            }
        });

        log.log(Level.FINER, "Setting Output Folder listener.");
        EventStreams.eventsOf(outputFolderButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing output folder chooser.");
            directoryChooser.setTitle(bundle.getString("outputFolderChooser"));
            File outputFolder = directoryChooser.showDialog(stage);
            log.log(Level.FINE, "Output folder chosen: {0}", outputFolder);
            if (outputFolder != null) {
                setOutputFolder(outputFolder);
                toggleStartButton();
            }
        });

        log.log(Level.FINER, "Setting Start listener.");
        EventStreams.eventsOf(startButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Start button hit.");
            outputImageView.reset();
            startService();
        });

        log.log(Level.FINER, "Setting Stop listener.");
        EventStreams.eventsOf(stopButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Stop button hit.");
            stopService();
        });
    }

    private void setupFieldListeners() {
        // useful to keep sliders synced to text fields
        StringConverter<Number> intConverter = new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return String.valueOf(t.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
        StringConverter<Number> doubleConverter = new StringConverter<Number>() {
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

        // keep print slider and text field synced and the slider updates the style
        printIterField.textProperty().bindBidirectional(printIterSlider.valueProperty(), intConverter);
        EventStreams.changesOf(printIterSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setIterationsPrint(numberChange.getNewValue().intValue()));

        // keep save slider and text field synced and the slider updates the style
        saveIterField.textProperty().bindBidirectional(saveIterSlider.valueProperty(), intConverter);
        EventStreams.changesOf(saveIterSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setIterationsSave(numberChange.getNewValue().intValue()));

        // keep max slider and text field synced and the slider updates the style
        maxIterField.textProperty().bindBidirectional(maxIterSlider.valueProperty(), intConverter);
        EventStreams.changesOf(maxIterSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setIterations(numberChange.getNewValue().intValue()));

        // keep output size slider and text field synced and the slider updates the style
        outputSizeField.textProperty().bindBidirectional(outputSizeSlider.valueProperty(), intConverter);
        EventStreams.changesOf(outputSizeSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setOutputSize(numberChange.getNewValue().intValue()));

        // keep style size slider and text field synced and the slider updates the style
        styleSizeField.textProperty().bindBidirectional(styleSizeSlider.valueProperty(), doubleConverter);
        EventStreams.changesOf(styleSizeSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setStyleSize(numberChange.getNewValue().doubleValue()));

        // keep output weight slider and text field synced and the slider updates the style
        contentWeightField.textProperty().bindBidirectional(contentWeightSlider.valueProperty(), intConverter);
        EventStreams.changesOf(contentWeightSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setContentWeight(numberChange.getNewValue().intValue()));

        // keep style weight slider and text field synced and the slider updates the style
        styleWeightField.textProperty().bindBidirectional(styleWeightSlider.valueProperty(), intConverter);
        EventStreams.changesOf(styleWeightSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setStyleWeight(numberChange.getNewValue().intValue()));

        // keep TV weight slider and text field synced and the slider updates the style
        tvWeightField.textProperty().bindBidirectional(tvWeightSlider.valueProperty(), doubleConverter);
        EventStreams.changesOf(tvWeightSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setTvWeight(numberChange.getNewValue().doubleValue()));
    }

    private void setupServiceListeners() {
        // handle each Worker.State
        log.log(Level.FINER, "Setting state listener.");
        neuralService.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue,
                                Worker.State oldState, Worker.State newState) {
                switch (newState) {
                    case SCHEDULED:
                        log.log(Level.FINER, "Neural service: Scheduled.");
                        statusLabel.setText("Scheduled");
                        startButton.setDisable(true);
                        stopButton.setDisable(false);
                        progress.setProgress(0);
                        break;
                    case READY:
                        log.log(Level.FINER, "Neural service: Ready.");
                        statusLabel.setText("Ready to Run");
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        break;
                    case RUNNING:
                        log.log(Level.FINER, "Neural service: Running.");
                        statusLabel.setText("Running");
                        startButton.setDisable(true);
                        stopButton.setDisable(false);
                        break;
                    case SUCCEEDED:
                        log.log(Level.FINER, "Neural service: Succeeded.");
                        statusLabel.setText("Finished");
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        progress.setProgress(100);
                        imageTimer.stop();
                        break;
                    case CANCELLED:
                        log.log(Level.FINER, "Neural service: Cancelled.");
                        statusLabel.setText("Cancelled");
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        imageTimer.stop();
                        break;
                    case FAILED:
                        log.log(Level.FINER, "Neural service: Failed.");
                        statusLabel.setText("Failed");
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        imageTimer.stop();
                        break;
                }
            }
        });

        log.log(Level.FINER, "Setting progress listener.");
        neuralService.progressProperty().addListener((observable, oldValue, newValue) -> {
            progress.setProgress(newValue.doubleValue());
        });

        log.log(Level.FINER, "Setting running listener.");
        final ColorAdjust highlighted = new ColorAdjust(0, 0, 0.3, 0);
        neuralService.runningProperty().addListener((observableValue, aBoolean, isRunning) -> {
            if (isRunning) {
                statusLabel.setEffect(highlighted);
            } else {
                statusLabel.setEffect(null);
            }
        });
    }

    private void setupImageListeners() {
        imageView.fitWidthProperty().bind(imageViewSizer.widthProperty());
        imageView.fitHeightProperty().bind(imageViewSizer.heightProperty());

        log.log(Level.FINER, "Setting image timer.");
        imageTimer = FxTimer.createPeriodic(Duration.ofMillis(250), () -> {
            log.log(Level.FINER, "Timer: checking service");
            if (neuralService == null || !neuralService.isRunning())
                return;
            NeuralStyle neuralStyle = neuralService.getNeuralStyle();
            if (neuralStyle == null)
                return;

            log.log(Level.FINER, "Timer: checking images");
            // Check for generated image iterations to show
            File[] images = neuralStyle.getOutputImageIterations();
            if (images != null && images.length > 0) {
                setImageView(images[images.length - 1]);
            }
        });
    }
}
