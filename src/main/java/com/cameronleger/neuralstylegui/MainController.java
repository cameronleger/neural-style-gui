package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
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
import java.nio.file.Files;
import java.time.Duration;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class MainController implements Initializable {
    private static final Logger log = Logger.getLogger(MainController.class.getName());
    private NvidiaService nvidiaService = new NvidiaService();
    private NeuralService neuralService = new NeuralService();
    private NeuralStyle neuralStyle = new NeuralStyle();
    private Stage stage;
    private ResourceBundle bundle;
    private Timer imageTimer;
    private Timer nvidiaTimer;

    @FXML
    private TextField stylePath;
    @FXML
    private TextField contentPath;
    @FXML
    private TextField outputPath;
    @FXML
    private TextField outputName;
    @FXML
    private Button styleFileButton;
    @FXML
    private Button contentFileButton;
    @FXML
    private Button outputFolderButton;
    @FXML
    private Button outputImageButton;

    @FXML
    private ProgressBar vramBar;

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
    private Slider seedSlider;
    @FXML
    private TextField seedField;

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
    private ChoiceBox<String> initChoice;
    @FXML
    private ChoiceBox<String> poolingChoice;
    @FXML
    private CheckBox originalColors;
    @FXML
    private CheckBox normalizeGradients;

    @FXML
    private Slider gpuSlider;
    @FXML
    private TextField gpuField;
    @FXML
    private ChoiceBox<String> optimizerChoice;
    @FXML
    private ChoiceBox<String> backendChoice;
    @FXML
    private Slider learningRateSlider;
    @FXML
    private TextField learningRateField;
    @FXML
    private CheckBox autotune;

    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button imageViewModeFit;
    @FXML
    private Button imageViewModeActual;

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
        log.log(Level.FINER, "Setting nvidia listener.");
        setupNvidiaListener();
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
        File outputImage = neuralStyle.getTempOutputImage();
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
        assert outputName != null : "fx:id=\"outputName\" was not injected.";
        assert styleFileButton != null : "fx:id=\"styleFileButton\" was not injected.";
        assert contentFileButton != null : "fx:id=\"contentFileButton\" was not injected.";
        assert outputFolderButton != null : "fx:id=\"outputFolderButton\" was not injected.";
        assert outputImageButton != null : "fx:id=\"outputImageButton\" was not injected.";
        assert vramBar != null : "fx:id=\"vramBar\" was not injected.";
        assert printIterSlider != null : "fx:id=\"printIterSlider\" was not injected.";
        assert printIterField != null : "fx:id=\"printIterField\" was not injected.";
        assert saveIterSlider != null : "fx:id=\"saveIterSlider\" was not injected.";
        assert saveIterField != null : "fx:id=\"saveIterField\" was not injected.";
        assert maxIterSlider != null : "fx:id=\"maxIterSlider\" was not injected.";
        assert maxIterField != null : "fx:id=\"maxIterField\" was not injected.";
        assert seedSlider != null : "fx:id=\"seedSlider\" was not injected.";
        assert seedField != null : "fx:id=\"seedField\" was not injected.";
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
        assert initChoice != null : "fx:id=\"initChoice\" was not injected.";
        assert poolingChoice != null : "fx:id=\"poolingChoice\" was not injected.";
        assert originalColors != null : "fx:id=\"originalColors\" was not injected.";
        assert normalizeGradients != null : "fx:id=\"normalizeGradients\" was not injected.";
        assert gpuSlider != null : "fx:id=\"gpuSlider\" was not injected.";
        assert gpuField != null : "fx:id=\"gpuField\" was not injected.";
        assert backendChoice != null : "fx:id=\"backendChoice\" was not injected.";
        assert optimizerChoice != null : "fx:id=\"optimizerChoice\" was not injected.";
        assert learningRateSlider != null : "fx:id=\"learningRateSlider\" was not injected.";
        assert learningRateField != null : "fx:id=\"learningRateField\" was not injected.";
        assert autotune != null : "fx:id=\"autotune\" was not injected.";
        assert startButton != null : "fx:id=\"startButton\" was not injected.";
        assert stopButton != null : "fx:id=\"stopButton\" was not injected.";
        assert imageViewModeFit != null : "fx:id=\"imageViewModeFit\" was not injected.";
        assert imageViewModeActual != null : "fx:id=\"imageViewModeActual\" was not injected.";
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

        log.log(Level.FINER, "Setting Output Image listener.");
        EventStreams.eventsOf(outputImageButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Output Image button hit, checking images.");

            Tooltip tooltip = new Tooltip();
            // Check for generated image iterations to show
            File outputFolder = neuralStyle.getGeneralOutputFolder();
            File[] images = neuralStyle.getTempOutputImageIterations();
            if (outputFolder == null) {
                tooltip.setText("Unable to save the image without an output folder.");
            } else if (images == null) {
                tooltip.setText("Unable to check for image iterations.");
            } else if (images.length <= 0) {
                tooltip.setText("No image iterations to save.");
            } else {
                File latestImage = images[images.length - 1];
                String uniqueText = String.valueOf(System.nanoTime());
                String possibleFileName = outputName.getText();

                File savedImage;
                if (possibleFileName != null && !possibleFileName.isEmpty()) {
                    savedImage = new File(outputFolder, possibleFileName + ".png");
                    if (savedImage.exists() && savedImage.isFile())
                        savedImage = new File(outputFolder, possibleFileName + "_" + uniqueText + ".png");
                } else
                    savedImage = new File(outputFolder, uniqueText + ".png");

                try {
                    Files.copy(latestImage.toPath(), savedImage.toPath(), REPLACE_EXISTING);
                    tooltip.setText(String.format("Saved image as:\n%s", savedImage.getName()));
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.toString(), e);
                    tooltip.setText("Exception saving the image, please check the log.");
                }
            }
            tooltip.setAutoHide(true);

            Point2D p = outputImageButton.localToScene(0.0, 0.0);
            tooltip.show(outputImageButton,
                    p.getX() + outputImageButton.getScene().getX() +
                            outputImageButton.getScene().getWindow().getX() + outputImageButton.getWidth(),
                    p.getY() + outputImageButton.getScene().getY() +
                            outputImageButton.getScene().getWindow().getY());
        });

        log.log(Level.FINER, "Setting Start listener.");
        EventStreams.eventsOf(startButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Start button hit.");
            outputImageView.fitToView();
            startService();
        });

        log.log(Level.FINER, "Setting Stop listener.");
        EventStreams.eventsOf(stopButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Stop button hit.");
            stopService();
        });

        log.log(Level.FINER, "Setting Fit View listener.");
        EventStreams.eventsOf(imageViewModeFit, ActionEvent.ACTION).subscribe(actionEvent -> {
            outputImageView.fitToView();
        });

        log.log(Level.FINER, "Setting Actual Size listener.");
        EventStreams.eventsOf(imageViewModeActual, ActionEvent.ACTION).subscribe(actionEvent -> {
            outputImageView.scaleImageViewport(1);
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

        // keep seed slider and text field synced and the slider updates the style
        seedField.textProperty().bindBidirectional(seedSlider.valueProperty(), intConverter);
        EventStreams.changesOf(seedSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setSeed(numberChange.getNewValue().intValue()));

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

        // init choicebox updates the style
        EventStreams.changesOf(initChoice.valueProperty())
                .subscribe(stringChange -> neuralStyle.setInit(stringChange.getNewValue()));

        // pooling choicebox updates the style
        EventStreams.changesOf(poolingChoice.valueProperty())
                .subscribe(stringChange -> neuralStyle.setPooling(stringChange.getNewValue()));

        // original colors checkbox updates the style
        EventStreams.changesOf(originalColors.selectedProperty()).subscribe(booleanChange -> {
            if (booleanChange.getNewValue())
                neuralStyle.setOriginalColors(1);
            else
                neuralStyle.setOriginalColors(0);
        });

        // normalize gradients checkbox updates the style
        EventStreams.changesOf(normalizeGradients.selectedProperty())
                .subscribe(booleanChange -> neuralStyle.setNormalizeGradients(booleanChange.getNewValue()));

        // keep gpu slider and text field synced and the slider updates the style
        gpuField.textProperty().bindBidirectional(gpuSlider.valueProperty(), intConverter);
        EventStreams.changesOf(gpuSlider.valueProperty()).subscribe(numberChange ->  {
                int device = numberChange.getNewValue().intValue();
                neuralStyle.setGpu(device);
                nvidiaService.setDevice(device);
            });

        // backend choicebox updates the style and toggles autotune
        EventStreams.changesOf(backendChoice.valueProperty()).subscribe(stringChange -> {
            String backend = stringChange.getNewValue();
            neuralStyle.setBackend(backend);
            if (backend.equalsIgnoreCase("cudnn")) {
                autotune.setDisable(false);
            } else {
                autotune.setDisable(true);
                autotune.setSelected(false);
            }
        });

        // optimizer choicebox updates the style and toggles learning rate
        EventStreams.changesOf(optimizerChoice.valueProperty()).subscribe(stringChange -> {
            String optimizer = stringChange.getNewValue();
            neuralStyle.setOptimizer(optimizer);
            if (optimizer.equalsIgnoreCase("adam")) {
                learningRateSlider.setDisable(false);
                learningRateField.setDisable(false);
            } else {
                learningRateSlider.setDisable(true);
                learningRateField.setDisable(true);
                learningRateField.setText("10");
            }
        });

        // keep learning rate slider and text field synced and the slider updates the style
        learningRateField.textProperty().bindBidirectional(learningRateSlider.valueProperty(), intConverter);
        EventStreams.changesOf(learningRateSlider.valueProperty())
                .subscribe(numberChange -> neuralStyle.setLearningRate(numberChange.getNewValue().intValue()));

        // autotune checkbox updates the style
        EventStreams.changesOf(autotune.selectedProperty())
                .subscribe(booleanChange -> neuralStyle.setAutotune(booleanChange.getNewValue()));
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
            File[] images = neuralStyle.getTempOutputImageIterations();
            if (images != null && images.length > 0) {
                setImageView(images[images.length - 1]);
            }
        });
    }

    private void setupNvidiaListener() {
        log.log(Level.FINER, "Setting nvidia ram listener.");
        nvidiaService.progressProperty().addListener((observable, oldValue, newValue) -> {
            double progress = newValue.doubleValue();
            if (progress > 0)
                vramBar.setProgress(progress);
        });

        log.log(Level.FINER, "Setting nvidia timer.");
        nvidiaTimer = FxTimer.createPeriodic(Duration.ofMillis(250), () -> {
            log.log(Level.FINER, "Timer: checking service");
            if (nvidiaService == null || nvidiaService.isRunning())
                return;

            log.log(Level.FINER, "Timer: starting service");
            nvidiaService.restart();
            nvidiaTimer.restart();
        });
        nvidiaTimer.restart();
    }
}
