package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyle;
import com.cameronleger.neuralstylegui.helper.MovingImageView;
import com.cameronleger.neuralstylegui.helper.TextAreaLogHandler;
import com.cameronleger.neuralstylegui.model.NeuralImage;
import com.cameronleger.neuralstylegui.model.NeuralLayer;
import com.cameronleger.neuralstylegui.service.NeuralService;
import com.cameronleger.neuralstylegui.service.NvidiaService;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.io.FilenameUtils;
import org.reactfx.EventStreams;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    private static final Logger log = Logger.getLogger(MainController.class.getName());

    private Stage stage;
    private ResourceBundle bundle;

    private NvidiaService nvidiaService = new NvidiaService();
    private NeuralService neuralService = new NeuralService();

    private NeuralStyle neuralStyle = new NeuralStyle();

    private Timer imageOutputTimer;
    private Timer nvidiaTimer;

    private ObservableList<NeuralImage> styleImages;
    private ObservableList<NeuralImage> contentImages;
    private ObservableList<NeuralLayer> styleLayers;
    private ObservableList<NeuralLayer> contentLayers;

    @FXML
    private Button neuralPathButton;
    @FXML
    private TextField neuralPath;

    @FXML
    private Button saveStyleButton;
    @FXML
    private Button loadStyleButton;

    @FXML
    private TabPane tabs;
    @FXML
    private Tab outputTab;

    @FXML
    private TextField styleFolderPath;
    @FXML
    private TextField contentFolderPath;
    @FXML
    private TextField outputPath;
    @FXML
    private TextField outputName;
    @FXML
    private Button styleFolderButton;
    @FXML
    private Button contentFolderButton;
    @FXML
    private Button outputFolderButton;
    @FXML
    private Button outputImageButton;

    @FXML
    private TableView<NeuralImage> styleImageTable;
    @FXML
    private TableColumn<NeuralImage, Boolean> styleImageTableSelected;
    @FXML
    private TableColumn<NeuralImage, String> styleImageTableName;
    @FXML
    private TableColumn<NeuralImage, Image> styleImageTableImage;
    @FXML
    private TableColumn<NeuralImage, Double> styleImageTableWeight;

    @FXML
    private TableView<NeuralImage> contentImageTable;
    @FXML
    private TableColumn<NeuralImage, String> contentImageTableName;
    @FXML
    private TableColumn<NeuralImage, Image> contentImageTableImage;

    @FXML
    private Button styleLayerAdd;
    @FXML
    private Button styleLayerRemove;
    @FXML
    private TableView<NeuralLayer> styleLayersTable;
    @FXML
    private TableColumn<NeuralLayer, Boolean> styleLayersTableSelected;
    @FXML
    private TableColumn<NeuralLayer, String> styleLayersTableName;

    @FXML
    private Button contentLayerAdd;
    @FXML
    private Button contentLayerRemove;
    @FXML
    private TableView<NeuralLayer> contentLayersTable;
    @FXML
    private TableColumn<NeuralLayer, Boolean> contentLayersTableSelected;
    @FXML
    private TableColumn<NeuralLayer, String> contentLayersTableName;

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
    private Button protoFileButton;
    @FXML
    private TextField protoFilePath;
    @FXML
    private Button modelFileButton;
    @FXML
    private TextField modelFilePath;

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

    private static FileChooser fileChooser = new FileChooser();
    private static DirectoryChooser directoryChooser = new DirectoryChooser();

    public void initialize(URL location, ResourceBundle resources) {
        log.log(Level.FINER, "Checking that all FXML items were injected.");
        checkInjections();

        bundle = resources;
        outputImageView = new MovingImageView(imageView);

        log.log(Level.FINER, "Setting observable lists.");
        setupObservableLists();
        log.log(Level.FINER, "Setting button listeners.");
        setupButtonListeners();
        log.log(Level.FINER, "Setting field listeners.");
        setupFieldListeners();
        log.log(Level.FINER, "Setting service listeners.");
        setupServiceListeners();
        log.log(Level.FINER, "Setting image listeners.");
        setupOutputImageListeners();
        log.log(Level.FINER, "Setting nvidia listener.");
        setupNvidiaListener();

        setupStyleImageTable();
        setupContentImageTable();
        setupStyleLayersTable();
        setupContentLayersTable();

        log.log(Level.FINER, "Setting neural service log handler.");
        neuralService.addLogHandler(new TextAreaLogHandler(logTextArea));
    }

    void setStage(Stage stage) {
        this.stage = stage;
    }

    void startService() {
        if (!neuralService.isRunning()) {
            log.log(Level.FINE, "Starting neural service.");
            FileUtils.generateUniqueText();
            neuralService.setNeuralStyle(neuralStyle);
            logTextArea.clear();
            neuralService.reset();
            neuralService.start();
            imageOutputTimer.restart();
            tabs.getSelectionModel().select(outputTab);

            FileUtils.saveTempOutputStyle(neuralStyle);
        }
    }

    void stopService() {
        if (neuralService.isRunning()) {
            log.log(Level.FINE, "Cancelling neural service.");
            neuralService.cancel();
            imageOutputTimer.stop();
        }
    }

    private void toggleStartButton() {
        startButton.setDisable(!neuralStyle.checkArguments() || neuralService.isRunning());
    }

    private void setImageView(File styleFile) {
        try {
            outputImageView.setImage(new Image(new FileInputStream(styleFile)));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    private void setNeuralPath(File neuralStylePath) {
        if (neuralStylePath == null)
            neuralPath.setText("");
        else
            neuralPath.setText(neuralStylePath.getAbsolutePath());
        neuralStyle.setNeuralStylePath(neuralStylePath);
    }

    private void setModelFile(File modelFile) {
        neuralStyle.setModelFile(modelFile);
        if (modelFile != null) {
            modelFilePath.setText(modelFile.getAbsolutePath());
            fileChooser.setInitialDirectory(modelFile.getParentFile());
        } else {
            modelFilePath.setText("");
        }
    }

    private void setProtoFile(File protoFile) {
        neuralStyle.setProtoFile(protoFile);
        if (protoFile != null) {
            protoFilePath.setText(protoFile.getAbsolutePath());
            fileChooser.setInitialDirectory(protoFile.getParentFile());

            String[] newLayers = FileUtils.parseLoadcaffeProto(protoFile);

            if (newLayers == null) {
                showTooltipNextTo(protoFileButton, bundle.getString("protoFileInvalid"));
                updateLayers(new String[]{});
            } else if (newLayers.length <= 0) {
                showTooltipNextTo(protoFileButton, bundle.getString("protoFileNoLayers"));
                updateLayers(new String[]{});
            } else {
                showTooltipNextTo(protoFileButton, bundle.getString("protoFileNewLayers"));
                updateLayers(newLayers);
            }
        } else {
            protoFilePath.setText("");
            setDefaultLayers();
        }
    }

    private void setStyleFolder(File styleFolder) {
        styleFolderPath.setText(styleFolder.getAbsolutePath());
        directoryChooser.setInitialDirectory(styleFolder);
        NeuralImage[] images = FileUtils.getImages(styleFolder);
        styleImages.setAll(images);
    }

    private void setContentFolder(File contentFolder) {
        contentFolderPath.setText(contentFolder.getAbsolutePath());
        directoryChooser.setInitialDirectory(contentFolder);
        NeuralImage[] images = FileUtils.getImages(contentFolder);
        contentImages.setAll(images);
    }

    private void setOutputFolder(File outputFolder) {
        neuralStyle.setOutputFolder(outputFolder);
        if (outputFolder != null) {
            outputPath.setText(outputFolder.getAbsolutePath());
            directoryChooser.setInitialDirectory(outputFolder);
        }
    }

    private void setDefaultLayers() {
        styleLayers.setAll(
                new NeuralLayer("relu1_1", true),
                new NeuralLayer("relu1_2", false),
                new NeuralLayer("relu2_1", true),
                new NeuralLayer("relu2_2", false),
                new NeuralLayer("relu3_1", true),
                new NeuralLayer("relu3_2", false),
                new NeuralLayer("relu3_3", false),
                new NeuralLayer("relu3_4", false),
                new NeuralLayer("relu4_1", true),
                new NeuralLayer("relu4_2", false),
                new NeuralLayer("relu4_3", false),
                new NeuralLayer("relu4_4", false),
                new NeuralLayer("relu5_1", true),
                new NeuralLayer("relu5_2", false),
                new NeuralLayer("relu5_3", false),
                new NeuralLayer("relu5_4", false),
                new NeuralLayer("relu6", false),
                new NeuralLayer("relu7", false)
        );
        contentLayers.setAll(
                new NeuralLayer("relu1_1", false),
                new NeuralLayer("relu1_2", false),
                new NeuralLayer("relu2_1", false),
                new NeuralLayer("relu2_2", false),
                new NeuralLayer("relu3_1", false),
                new NeuralLayer("relu3_2", false),
                new NeuralLayer("relu3_3", false),
                new NeuralLayer("relu3_4", false),
                new NeuralLayer("relu4_1", false),
                new NeuralLayer("relu4_2", true),
                new NeuralLayer("relu4_3", false),
                new NeuralLayer("relu4_4", false),
                new NeuralLayer("relu5_1", false),
                new NeuralLayer("relu5_2", false),
                new NeuralLayer("relu5_3", false),
                new NeuralLayer("relu5_4", false),
                new NeuralLayer("relu6", false),
                new NeuralLayer("relu7", false)
        );
    }

    private void updateLayers(String[] layers) {
        List<NeuralLayer> newStyleLayers = new ArrayList<>();
        List<NeuralLayer> newContentLayers = new ArrayList<>();
        for (String layer : layers) {
            newStyleLayers.add(new NeuralLayer(layer, false));
            newContentLayers.add(new NeuralLayer(layer, false));
        }
        styleLayers.setAll(newStyleLayers);
        contentLayers.setAll(newContentLayers);
    }

    private void updateLayerSelections(String[] selectedLayers, ObservableList<NeuralLayer> existingLayers) {
        // ensure all NeuralLayers are deselected
        List<NeuralLayer> newNeuralLayers = existingLayers.stream()
                .map(neuralLayer -> new NeuralLayer(neuralLayer.getName(), false))
                .collect(Collectors.toList());

        if (selectedLayers != null && selectedLayers.length > 0) {
            // select NeuralLayers
            for (String selectedLayer : selectedLayers) {
                boolean existed = false;
                for (NeuralLayer neuralLayer : newNeuralLayers) {
                    if (neuralLayer.getName().equalsIgnoreCase(selectedLayer)) {
                        neuralLayer.setSelected(true);
                        existed = true;
                        break;
                    }
                }

                // create new layer for selection if necessary
                if (!existed) {
                    newNeuralLayers.add(new NeuralLayer(selectedLayer, true));
                }
            }

            existingLayers.setAll(newNeuralLayers);
        }
    }

    private List<NeuralImage> updateStyleImageSelections(File[] selectedImages, double[] weights,
                                                         ObservableList<NeuralImage> existingImages) {
        // ensure all NeuralImages are deselected and non-weighted
        List<NeuralImage> newNeuralImages = existingImages.stream()
                .map(neuralLayer -> new NeuralImage(neuralLayer.getImageFile()))
                .collect(Collectors.toList());
        List<NeuralImage> selectedNeuralImages = new ArrayList<>();

        if (selectedImages != null && selectedImages.length > 0) {
            // select NeuralImages
            for (int i = 0; i < selectedImages.length; i++) {
                File selectedImage = selectedImages[i];
                double weight;
                try {
                    weight = weights[i];
                } catch (Exception e) {
                    weight = 1.0;
                }
                boolean existed = false;
                for (NeuralImage neuralImage : newNeuralImages) {
                    if (neuralImage.getName().equalsIgnoreCase(selectedImage.getName())) {
                        neuralImage.setSelected(true);
                        neuralImage.setWeight(weight);
                        selectedNeuralImages.add(neuralImage);
                        existed = true;
                        break;
                    }
                }

                // create new image for selection if necessary
                if (!existed) {
                    NeuralImage neuralImage = new NeuralImage(selectedImage);
                    neuralImage.setSelected(true);
                    neuralImage.setWeight(weight);
                    newNeuralImages.add(neuralImage);
                    selectedNeuralImages.add(neuralImage);
                }
            }

            existingImages.setAll(newNeuralImages);
        }
        return selectedNeuralImages;
    }

    private void updateContentImageSelections(File selectedImage, ObservableList<NeuralImage> existingImages) {
        // ensure all NeuralImages are deselected and non-weighted
        List<NeuralImage> newNeuralImages = existingImages.stream()
                .map(neuralLayer -> new NeuralImage(neuralLayer.getImageFile()))
                .collect(Collectors.toList());

        if (selectedImage != null) {
            NeuralImage selectedNeuralImage = null;
            boolean existed = false;
            for (NeuralImage neuralImage : newNeuralImages) {
                if (neuralImage.getName().equalsIgnoreCase(selectedImage.getName())) {
                    selectedNeuralImage = neuralImage;
                    existed = true;
                    break;
                }
            }

            // create new image for selection if necessary
            if (!existed) {
                NeuralImage neuralImage = new NeuralImage(selectedImage);
                selectedNeuralImage = neuralImage;
                newNeuralImages.add(neuralImage);
            }

            // select the new image in the table
            existingImages.setAll(newNeuralImages);
            contentImageTable.getSelectionModel().select(selectedNeuralImage);
        }
    }

    private void loadStyle(NeuralStyle loadedNeuralStyle) {
        neuralStyle = loadedNeuralStyle;

        // Retrieve these before paths because that will change them
        File[] selectedStyleImages = neuralStyle.getStyleImages();
        double[] selectedStyleWeights = neuralStyle.getStyleWeights();
        File contentImage = neuralStyle.getContentImage();
        String[] selectedStyleLayers = neuralStyle.getStyleLayers();
        String[] selectedContentLayers = neuralStyle.getContentLayers();

        // Set paths
        setNeuralPath(neuralStyle.getNeuralStylePath());
        setProtoFile(neuralStyle.getProtoFile());
        setModelFile(neuralStyle.getModelFile());
        setOutputFolder(neuralStyle.getGeneralOutputFolder());

        // Set selected layers after updating layers from paths
        updateLayerSelections(selectedStyleLayers, this.styleLayers);
        updateLayerSelections(selectedContentLayers, this.contentLayers);

        // Set simple inputs
        maxIterSlider.setValue(neuralStyle.getIterations());
        printIterSlider.setValue(neuralStyle.getIterationsPrint());
        saveIterSlider.setValue(neuralStyle.getIterationsSave());
        seedSlider.setValue(neuralStyle.getSeed());
        outputSizeSlider.setValue(neuralStyle.getOutputSize());
        styleSizeSlider.setValue(neuralStyle.getStyleSize());
        contentWeightSlider.setValue(neuralStyle.getContentWeight());
        styleWeightSlider.setValue(neuralStyle.getStyleWeight());
        tvWeightSlider.setValue(neuralStyle.getTvWeight());
        originalColors.setSelected(neuralStyle.isOriginalColors());
        initChoice.setValue(neuralStyle.getInit());
        poolingChoice.setValue(neuralStyle.getPooling());
        normalizeGradients.setSelected(neuralStyle.isNormalizeGradients());
        gpuSlider.setValue(neuralStyle.getGpu());
        backendChoice.setValue(neuralStyle.getBackend());
        optimizerChoice.setValue(neuralStyle.getOptimizer());
        learningRateSlider.setValue(neuralStyle.getLearningRate());
        autotune.setSelected(neuralStyle.isAutotune());

        // Set input folders and image selections last
        if (selectedStyleImages != null && selectedStyleImages.length > 0) {
            setStyleFolder(new File(FilenameUtils.getFullPath(selectedStyleImages[0].getAbsolutePath())));
            updateStyleImageSelections(selectedStyleImages, selectedStyleWeights, styleImages);
        }
        // Set selected item in Content Images table (it's not connected to observable)
        if (contentImage != null) {
            setContentFolder(new File(FilenameUtils.getFullPath(contentImage.getAbsolutePath())));
            updateContentImageSelections(contentImage, contentImages);
        }
    }

    private void showTooltipNextTo(Region region, String text) {
        Tooltip tooltip = new Tooltip();
        tooltip.setAutoHide(true);
        tooltip.setText(text);
        Point2D p = region.localToScene(0.0, 0.0);
        tooltip.show(region,
                p.getX() + region.getScene().getX() + region.getScene().getWindow().getX() + region.getWidth(),
                p.getY() + region.getScene().getY() + region.getScene().getWindow().getY());
    }

    private void checkInjections() {
        assert neuralPathButton != null : "fx:id=\"neuralPathButton\" was not injected.";
        assert neuralPath != null : "fx:id=\"neuralPath\" was not injected.";
        assert saveStyleButton != null : "fx:id=\"saveStyleButton\" was not injected.";
        assert loadStyleButton != null : "fx:id=\"loadStyleButton\" was not injected.";
        assert tabs != null : "fx:id=\"tabs\" was not injected.";
        assert outputTab != null : "fx:id=\"outputTab\" was not injected.";
        assert styleFolderPath != null : "fx:id=\"styleFolderPath\" was not injected.";
        assert contentFolderPath != null : "fx:id=\"contentFolderPath\" was not injected.";
        assert outputPath != null : "fx:id=\"outputPath\" was not injected.";
        assert outputName != null : "fx:id=\"outputName\" was not injected.";
        assert styleFolderButton != null : "fx:id=\"styleFolderButton\" was not injected.";
        assert contentFolderButton != null : "fx:id=\"contentFolderButton\" was not injected.";
        assert outputFolderButton != null : "fx:id=\"outputFolderButton\" was not injected.";
        assert outputImageButton != null : "fx:id=\"outputImageButton\" was not injected.";
        assert styleImageTable != null : "fx:id=\"styleImageTable\" was not injected.";
        assert styleImageTableSelected != null : "fx:id=\"styleImageTableSelected\" was not injected.";
        assert styleImageTableName != null : "fx:id=\"styleImageTableName\" was not injected.";
        assert styleImageTableImage != null : "fx:id=\"styleImageTableImage\" was not injected.";
        assert styleImageTableWeight != null : "fx:id=\"styleImageTableWeight\" was not injected.";
        assert contentImageTable != null : "fx:id=\"contentImageTable\" was not injected.";
        assert contentImageTableName != null : "fx:id=\"contentImageTableName\" was not injected.";
        assert contentImageTableImage != null : "fx:id=\"contentImageTableImage\" was not injected.";
        assert styleLayerAdd != null : "fx:id=\"styleLayerAdd\" was not injected.";
        assert styleLayerRemove != null : "fx:id=\"styleLayerRemove\" was not injected.";
        assert styleLayersTable != null : "fx:id=\"styleLayersTable\" was not injected.";
        assert styleLayersTableSelected != null : "fx:id=\"styleLayersTableSelected\" was not injected.";
        assert styleLayersTableName != null : "fx:id=\"styleLayersTableName\" was not injected.";
        assert contentLayerAdd != null : "fx:id=\"contentLayerAdd\" was not injected.";
        assert contentLayerRemove != null : "fx:id=\"contentLayerRemove\" was not injected.";
        assert contentLayersTable != null : "fx:id=\"contentLayersTable\" was not injected.";
        assert contentLayersTableSelected != null : "fx:id=\"contentLayersTableSelected\" was not injected.";
        assert contentLayersTableName != null : "fx:id=\"contentLayersTableName\" was not injected.";
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
        assert protoFileButton != null : "fx:id=\"protoFileButton\" was not injected.";
        assert protoFilePath != null : "fx:id=\"protoFilePath\" was not injected.";
        assert modelFileButton != null : "fx:id=\"modelFileButton\" was not injected.";
        assert modelFilePath != null : "fx:id=\"modelFilePath\" was not injected.";
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

    private void setupObservableLists() {
        styleImages = FXCollections.observableArrayList(new Callback<NeuralImage, Observable[]>() {
            @Override
            public Observable[] call(NeuralImage neuralImage) {
                return new Observable[] {neuralImage.selectedProperty(), neuralImage.weightProperty()};
            }
        });
        contentImages = FXCollections.observableArrayList(new Callback<NeuralImage, Observable[]>() {
            @Override
            public Observable[] call(NeuralImage neuralImage) {
                return new Observable[] {neuralImage.selectedProperty()};
            }
        });
        styleLayers = FXCollections.observableArrayList(new Callback<NeuralLayer, Observable[]>() {
            @Override
            public Observable[] call(NeuralLayer neuralLayer) {
                return new Observable[] {neuralLayer.selectedProperty(), neuralLayer.nameProperty()};
            }
        });
        contentLayers = FXCollections.observableArrayList(new Callback<NeuralLayer, Observable[]>() {
            @Override
            public Observable[] call(NeuralLayer neuralLayer) {
                return new Observable[] {neuralLayer.selectedProperty(), neuralLayer.nameProperty()};
            }
        });

        setDefaultLayers();
    }

    private void setupButtonListeners() {
        log.log(Level.FINER, "Setting Neural Path listener.");
        EventStreams.eventsOf(neuralPathButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing neural-style folder chooser.");
            directoryChooser.setTitle(bundle.getString("neuralPathChooser"));
            File neuralStylePath = directoryChooser.showDialog(stage);
            log.log(Level.FINE, "neural-style folder chosen: {0}", neuralStylePath);
            setNeuralPath(neuralStylePath);
        });

        log.log(Level.FINER, "Setting Style Save listener.");
        EventStreams.eventsOf(saveStyleButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing save style file chooser.");
            fileChooser.setTitle(bundle.getString("saveStyleChooser"));
            File styleFile = fileChooser.showSaveDialog(stage);
            log.log(Level.FINE, "Style file chosen: {0}", styleFile);
            if (styleFile != null) {
                fileChooser.setInitialDirectory(styleFile.getParentFile());
                File savedStyle = FileUtils.saveOutputStyle(neuralStyle, styleFile);
                if (savedStyle == null)
                    showTooltipNextTo(saveStyleButton, bundle.getString("saveStyleFailed"));
                else
                    showTooltipNextTo(saveStyleButton, bundle.getString("saveStyleSuccess"));
            }
        });

        log.log(Level.FINER, "Setting Style Load listener.");
        EventStreams.eventsOf(loadStyleButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing save style file chooser.");
            fileChooser.setTitle(bundle.getString("loadStyleChooser"));
            File styleFile = fileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "Style file chosen: {0}", styleFile);
            if (styleFile != null) {
                fileChooser.setInitialDirectory(styleFile.getParentFile());
                NeuralStyle loadedStyle = FileUtils.loadStyle(styleFile);
                if (loadedStyle == null)
                    showTooltipNextTo(loadStyleButton, bundle.getString("loadStyleFailed"));
                else {
                    loadStyle(loadedStyle);
                    showTooltipNextTo(loadStyleButton, bundle.getString("loadStyleSuccess"));
                }
            }
        });

        log.log(Level.FINER, "Setting Style Folder listener.");
        EventStreams.eventsOf(styleFolderButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing style folder chooser.");
            directoryChooser.setTitle(bundle.getString("styleFolderChooser"));
            File styleFolder = directoryChooser.showDialog(stage);
            log.log(Level.FINE, "Style folder chosen: {0}", styleFolder);
            if (styleFolder != null) {
                setStyleFolder(styleFolder);
            }
        });

        log.log(Level.FINER, "Setting Content Folder listener.");
        EventStreams.eventsOf(contentFolderButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing content folder chooser.");
            directoryChooser.setTitle(bundle.getString("contentFolderChooser"));
            File contentFolder = directoryChooser.showDialog(stage);
            log.log(Level.FINE, "Content folder chosen: {0}", contentFolder);
            if (contentFolder != null) {
                setContentFolder(contentFolder);
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

            // Check for generated image iterations to show
            File outputFolder = neuralStyle.getGeneralOutputFolder();
            File[] images = FileUtils.getTempOutputImageIterations();
            if (outputFolder == null) {
                showTooltipNextTo(outputImageButton, bundle.getString("outputImageNoOutputFolder"));
            } else if (images == null) {
                showTooltipNextTo(outputImageButton, bundle.getString("outputImageNullIterations"));
            } else if (images.length <= 0) {
                showTooltipNextTo(outputImageButton, bundle.getString("outputImageNoIterations"));
            } else {
                File latestImage = images[images.length - 1];
                String possibleName = outputName.getText();

                File savedImage = FileUtils.saveTempOutputImageTo(latestImage, outputFolder, possibleName);
                if (savedImage == null) {
                    showTooltipNextTo(outputImageButton, bundle.getString("outputImageNoSavedImage"));
                } else {
                    showTooltipNextTo(outputImageButton,
                            bundle.getString("outputImageSavedImage") + "\n" + savedImage.getName());
                }
            }
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

        log.log(Level.FINER, "Setting Proto File listener.");
        EventStreams.eventsOf(protoFileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing proto file chooser.");
            fileChooser.setTitle(bundle.getString("protoFileChooser"));
            File protoFile = fileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "Proto file chosen: {0}", protoFile);
            setProtoFile(protoFile);
        });

        log.log(Level.FINER, "Setting Model File listener.");
        EventStreams.eventsOf(modelFileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing model file chooser.");
            fileChooser.setTitle(bundle.getString("modelFileChooser"));
            File modelFile = fileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "Model file chosen: {0}", modelFile);
            setModelFile(modelFile);
        });

        log.log(Level.FINER, "Setting Style Layer Add listener.");
        EventStreams.eventsOf(styleLayerAdd, ActionEvent.ACTION).subscribe(
                actionEvent -> styleLayers.add(new NeuralLayer("newLayer", false)));

        log.log(Level.FINER, "Setting Style Layer Remove listener.");
        EventStreams.eventsOf(styleLayerRemove, ActionEvent.ACTION).subscribe(
                actionEvent -> styleLayers.removeAll(styleLayersTable.getSelectionModel().getSelectedItems()));

        log.log(Level.FINER, "Setting Content Layer Add listener.");
        EventStreams.eventsOf(contentLayerAdd, ActionEvent.ACTION).subscribe(
                actionEvent -> contentLayers.add(new NeuralLayer("newLayer", false)));

        log.log(Level.FINER, "Setting Content Layer Remove listener.");
        EventStreams.eventsOf(contentLayerRemove, ActionEvent.ACTION).subscribe(
                actionEvent -> contentLayers.removeAll(contentLayersTable.getSelectionModel().getSelectedItems()));
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
        EventStreams.changesOf(printIterField.textProperty())
                .subscribe(numberChange -> neuralStyle.setIterationsPrint(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep save slider and text field synced and the slider updates the style
        saveIterField.textProperty().bindBidirectional(saveIterSlider.valueProperty(), intConverter);
        EventStreams.changesOf(saveIterField.textProperty())
                .subscribe(numberChange -> neuralStyle.setIterationsSave(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep max slider and text field synced and the slider updates the style
        maxIterField.textProperty().bindBidirectional(maxIterSlider.valueProperty(), intConverter);
        EventStreams.changesOf(maxIterField.textProperty())
                .subscribe(numberChange -> neuralStyle.setIterations(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep seed slider and text field synced and the slider updates the style
        seedField.textProperty().bindBidirectional(seedSlider.valueProperty(), intConverter);
        EventStreams.changesOf(seedField.textProperty())
                .subscribe(numberChange -> neuralStyle.setSeed(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep output size slider and text field synced and the slider updates the style
        outputSizeField.textProperty().bindBidirectional(outputSizeSlider.valueProperty(), intConverter);
        EventStreams.changesOf(outputSizeField.textProperty())
                .subscribe(numberChange -> neuralStyle.setOutputSize(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep style size slider and text field synced and the slider updates the style
        styleSizeField.textProperty().bindBidirectional(styleSizeSlider.valueProperty(), doubleConverter);
        EventStreams.changesOf(styleSizeField.textProperty())
                .subscribe(numberChange -> neuralStyle.setStyleSize(
                        doubleConverter.fromString(numberChange.getNewValue()).doubleValue()));

        // keep output weight slider and text field synced and the slider updates the style
        contentWeightField.textProperty().bindBidirectional(contentWeightSlider.valueProperty(), intConverter);
        EventStreams.changesOf(contentWeightField.textProperty())
                .subscribe(numberChange -> neuralStyle.setContentWeight(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep style weight slider and text field synced and the slider updates the style
        styleWeightField.textProperty().bindBidirectional(styleWeightSlider.valueProperty(), intConverter);
        EventStreams.changesOf(styleWeightField.textProperty())
                .subscribe(numberChange -> neuralStyle.setStyleWeight(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

        // keep TV weight slider and text field synced and the slider updates the style
        tvWeightField.textProperty().bindBidirectional(tvWeightSlider.valueProperty(), doubleConverter);
        EventStreams.changesOf(tvWeightField.textProperty())
                .subscribe(numberChange -> neuralStyle.setTvWeight(
                        doubleConverter.fromString(numberChange.getNewValue()).doubleValue()));

        // init choicebox updates the style
        EventStreams.changesOf(initChoice.valueProperty())
                .subscribe(stringChange -> neuralStyle.setInit(stringChange.getNewValue()));

        // pooling choicebox updates the style
        EventStreams.changesOf(poolingChoice.valueProperty())
                .subscribe(stringChange -> neuralStyle.setPooling(stringChange.getNewValue()));

        // original colors checkbox updates the style
        EventStreams.changesOf(originalColors.selectedProperty())
                .subscribe(booleanChange ->neuralStyle.setOriginalColors(booleanChange.getNewValue()));

        // normalize gradients checkbox updates the style
        EventStreams.changesOf(normalizeGradients.selectedProperty())
                .subscribe(booleanChange -> neuralStyle.setNormalizeGradients(booleanChange.getNewValue()));

        // keep gpu slider and text field synced and the slider updates the style
        gpuField.textProperty().bindBidirectional(gpuSlider.valueProperty(), intConverter);
        EventStreams.changesOf(gpuField.textProperty()).subscribe(numberChange ->  {
                int device = intConverter.fromString(numberChange.getNewValue()).intValue();
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
        EventStreams.changesOf(learningRateField.textProperty())
                .subscribe(numberChange -> neuralStyle.setLearningRate(
                        intConverter.fromString(numberChange.getNewValue()).intValue()));

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
                        statusLabel.setText(bundle.getString("neuralServiceStatusScheduled"));
                        startButton.setDisable(true);
                        stopButton.setDisable(false);
                        progress.setProgress(0);
                        break;
                    case READY:
                        log.log(Level.FINER, "Neural service: Ready.");
                        statusLabel.setText(bundle.getString("neuralServiceStatusReady"));
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        break;
                    case RUNNING:
                        log.log(Level.FINER, "Neural service: Running.");
                        statusLabel.setText(bundle.getString("neuralServiceStatusRunning"));
                        startButton.setDisable(true);
                        stopButton.setDisable(false);
                        break;
                    case SUCCEEDED:
                        log.log(Level.FINER, "Neural service: Succeeded.");
                        statusLabel.setText(bundle.getString("neuralServiceStatusFinished"));
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        progress.setProgress(100);
                        imageOutputTimer.stop();
                        break;
                    case CANCELLED:
                        log.log(Level.FINER, "Neural service: Cancelled.");
                        statusLabel.setText(bundle.getString("neuralServiceStatusCancelled"));
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        imageOutputTimer.stop();
                        break;
                    case FAILED:
                        log.log(Level.FINER, "Neural service: Failed.");
                        statusLabel.setText(bundle.getString("neuralServiceStatusFailed"));
                        startButton.setDisable(false);
                        stopButton.setDisable(true);
                        imageOutputTimer.stop();
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

    private void setupOutputImageListeners() {
        imageView.fitWidthProperty().bind(imageViewSizer.widthProperty());
        imageView.fitHeightProperty().bind(imageViewSizer.heightProperty());

        log.log(Level.FINER, "Setting image timer.");
        imageOutputTimer = FxTimer.createPeriodic(Duration.ofMillis(250), () -> {
            log.log(Level.FINER, "Timer: checking service");
            if (neuralService == null || !neuralService.isRunning())
                return;
            NeuralStyle neuralStyle = neuralService.getNeuralStyle();
            if (neuralStyle == null)
                return;

            log.log(Level.FINER, "Timer: checking images");
            // Check for generated image iterations to show
            File[] images = FileUtils.getTempOutputImageIterations();
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

    private void setupStyleImageTable() {
        log.log(Level.FINER, "Setting style image table list.");
        styleImageTable.setItems(styleImages);
        styleImageTable.setFixedCellSize(NeuralImage.THUMBNAIL_SIZE);

        log.log(Level.FINER, "Setting style image table selection listener.");
        EventStreams.changesOf(styleImages).subscribe(change -> {
            log.log(Level.FINE, "styleImages changed");

            List<NeuralImage> selectedNeuralImages = new ArrayList<>();
            for (NeuralImage neuralImage : styleImages)
                if (neuralImage.isSelected())
                    selectedNeuralImages.add(neuralImage);

            File[] neuralFiles = new File[selectedNeuralImages.size()];
            double[] neuralFilesWeights = new double[selectedNeuralImages.size()];
            for (int i = 0; i < selectedNeuralImages.size(); i++) {
                NeuralImage neuralImage = selectedNeuralImages.get(i);
                neuralFiles[i] = neuralImage.getImageFile();
                neuralFilesWeights[i] = neuralImage.getWeight();
            }
            neuralStyle.setStyleImages(neuralFiles);
            neuralStyle.setStyleWeights(neuralFilesWeights);

            toggleStartButton();
        });

        log.log(Level.FINER, "Setting style image table column factories.");
        styleImageTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        styleImageTableSelected.setCellFactory(CheckBoxTableCell.forTableColumn(styleImageTableSelected));

        styleImageTableName.setCellValueFactory(new PropertyValueFactory<>("name"));

        styleImageTableWeight.setCellValueFactory(new PropertyValueFactory<>("weight"));
        styleImageTableWeight.setCellFactory(
                TextFieldTableCell.forTableColumn(new StringConverter<Double>() {
                    @Override
                    public String toString(Double object) {
                        return String.valueOf(object.doubleValue());
                    }

                    @Override
                    public Double fromString(String string) {
                        try {
                            return Double.parseDouble(string);
                        } catch (Exception e) {
                            return 1.0;
                        }
                    }
                }));

        styleImageTableImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        styleImageTableImage.setCellFactory(new Callback<TableColumn<NeuralImage, Image>, TableCell<NeuralImage, Image>>() {
            @Override
            public TableCell<NeuralImage, Image> call(TableColumn<NeuralImage, Image> param) {
                return new TableCell<NeuralImage, Image>() {
                    ImageView imageView;
                    {
                        imageView = new ImageView();
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(NeuralImage.THUMBNAIL_SIZE);
                        imageView.setFitWidth(NeuralImage.THUMBNAIL_SIZE);
                        imageView.setImage(new WritableImage(NeuralImage.THUMBNAIL_SIZE, NeuralImage.THUMBNAIL_SIZE));
                        setGraphic(imageView);
                    }

                    @Override
                    public void updateItem(Image image, boolean empty) {
                        super.updateItem(image, empty);
                        if (empty || image == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            imageView.setImage(image);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });
    }

    private void setupContentImageTable() {
        log.log(Level.FINER, "Setting content image table list.");
        contentImageTable.setItems(contentImages);
        contentImageTable.setFixedCellSize(NeuralImage.THUMBNAIL_SIZE);

        log.log(Level.FINER, "Setting content image table selection listener.");
        EventStreams.changesOf(contentImageTable.getSelectionModel().selectedItemProperty())
                .subscribe(neuralImageChange -> {
                    NeuralImage newSelection = neuralImageChange.getNewValue();
                    log.log(Level.FINE, "Content image changed: " + newSelection);
                    if (newSelection == null)
                        neuralStyle.setContentImage(null);
                    else
                        neuralStyle.setContentImage(newSelection.getImageFile());
                    toggleStartButton();
                });

        log.log(Level.FINER, "Setting content image table column factories.");
        contentImageTableName.setCellValueFactory(new PropertyValueFactory<>("name"));

        contentImageTableImage.setCellValueFactory(new PropertyValueFactory<>("image"));
        contentImageTableImage.setCellFactory(new Callback<TableColumn<NeuralImage, Image>, TableCell<NeuralImage, Image>>() {
            @Override
            public TableCell<NeuralImage, Image> call(TableColumn<NeuralImage, Image> param) {
                return new TableCell<NeuralImage, Image>() {
                    ImageView imageView;
                    {
                        imageView = new ImageView();
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(NeuralImage.THUMBNAIL_SIZE);
                        imageView.setFitWidth(NeuralImage.THUMBNAIL_SIZE);
                        imageView.setImage(new WritableImage(NeuralImage.THUMBNAIL_SIZE, NeuralImage.THUMBNAIL_SIZE));
                        setGraphic(imageView);
                    }

                    @Override
                    public void updateItem(Image image, boolean empty) {
                        super.updateItem(image, empty);
                        if (empty || image == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            imageView.setImage(image);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });
    }

    private void setupStyleLayersTable() {
        log.log(Level.FINER, "Setting style layer table list.");
        styleLayersTable.setItems(styleLayers);
        styleLayersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        log.log(Level.FINER, "Setting style layer table selection listener.");
        EventStreams.changesOf(styleLayers).subscribe(change -> {
            log.log(Level.FINE, "styleLayers changed");

            List<NeuralLayer> selectedStyleLayers = styleLayers.stream()
                    .filter(NeuralLayer::isSelected)
                    .collect(Collectors.toList());

            String[] newStyleLayers = new String[selectedStyleLayers.size()];
            for (int i = 0; i < selectedStyleLayers.size(); i++)
                newStyleLayers[i] = selectedStyleLayers.get(i).getName();
            neuralStyle.setStyleLayers(newStyleLayers);

            toggleStartButton();
        });

        log.log(Level.FINER, "Setting style layer table column factories.");
        styleLayersTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        styleLayersTableSelected.setCellFactory(CheckBoxTableCell.forTableColumn(styleLayersTableSelected));

        styleLayersTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        styleLayersTableName.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void setupContentLayersTable() {
        log.log(Level.FINER, "Setting content layer table list.");
        contentLayersTable.setItems(contentLayers);
        contentLayersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        log.log(Level.FINER, "Setting content layer table selection listener.");
        EventStreams.changesOf(contentLayers).subscribe(change -> {
            log.log(Level.FINE, "contentLayers changed");

            List<NeuralLayer> selectedContentLayers = contentLayers.stream()
                    .filter(NeuralLayer::isSelected)
                    .collect(Collectors.toList());

            String[] newContentLayers = new String[selectedContentLayers.size()];
            for (int i = 0; i < selectedContentLayers.size(); i++)
                newContentLayers[i] = selectedContentLayers.get(i).getName();
            neuralStyle.setContentLayers(newContentLayers);

            toggleStartButton();
        });

        log.log(Level.FINER, "Setting content layer table column factories.");
        contentLayersTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        contentLayersTableSelected.setCellFactory(CheckBoxTableCell.forTableColumn(contentLayersTableSelected));

        contentLayersTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        contentLayersTableName.setCellFactory(TextFieldTableCell.forTableColumn());
    }
}
