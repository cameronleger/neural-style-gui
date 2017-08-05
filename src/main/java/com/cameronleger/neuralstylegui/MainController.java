package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyle;
import com.cameronleger.neuralstylegui.helper.MovingImageView;
import com.cameronleger.neuralstylegui.helper.NeuralImageCell;
import com.cameronleger.neuralstylegui.helper.TextAreaLogHandler;
import com.cameronleger.neuralstylegui.model.NeuralImage;
import com.cameronleger.neuralstylegui.model.NamedSelection;
import com.cameronleger.neuralstylegui.model.NeuralQueue;
import com.cameronleger.neuralstylegui.service.NeuralService;
import com.cameronleger.neuralstylegui.service.NvidiaService;
import com.cameronleger.neuralstylegui.service.OutputService;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.io.FilenameUtils;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MainController implements Initializable {
    private static final Logger log = Logger.getLogger(MainController.class.getName());

    private Stage stage;
    private ResourceBundle bundle;

    private OutputService imageOutputService = new OutputService();
    private NvidiaService nvidiaService = new NvidiaService();
    private NeuralService neuralService = new NeuralService();

    private NeuralStyle neuralStyle = new NeuralStyle();

    private Timer imageOutputTimer;
    private Timer nvidiaTimer;

    private ObservableList<NeuralImage> styleImages;
    private ObservableList<NeuralImage> contentImages;
    private ObservableList<NamedSelection> gpuIndices;
    private ObservableList<NamedSelection> styleLayers;
    private ObservableList<NamedSelection> contentLayers;
    private final TreeItem<NeuralQueue.NeuralQueueItem> outputRoot = new TreeItem<>(createQueueItem(null));

    private final KeyCombination spaceBar = new KeyCodeCombination(KeyCode.SPACE);

    @FXML
    private Button thPathButton;
    @FXML
    private TextField thPath;

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
    private Tab inputTab;
    @FXML
    private Tab layersTab;
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
    private CheckBox styleMultipleSelect;

    @FXML
    private ListView<NeuralImage> styleImageList;
    @FXML
    private ListView<NeuralImage> contentImageList;

    @FXML
    private Button styleLayerAdd;
    @FXML
    private Button styleLayerRemove;
    @FXML
    private TableView<NamedSelection> styleLayersTable;
    @FXML
    private TableColumn<NamedSelection, Boolean> styleLayersTableSelected;
    @FXML
    private TableColumn<NamedSelection, String> styleLayersTableName;

    @FXML
    private Button contentLayerAdd;
    @FXML
    private Button contentLayerRemove;
    @FXML
    private TableView<NamedSelection> contentLayersTable;
    @FXML
    private TableColumn<NamedSelection, Boolean> contentLayersTableSelected;
    @FXML
    private TableColumn<NamedSelection, String> contentLayersTableName;

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
    private Button initImageButton;
    @FXML
    private TextField initImagePath;
    @FXML
    private ChoiceBox<String> poolingChoice;
    @FXML
    private CheckBox originalColors;
    @FXML
    private CheckBox normalizeGradients;

    @FXML
    private CheckBox cpuMode;
    @FXML
    private TableView<NamedSelection> gpuTable;
    @FXML
    private TableColumn<NamedSelection, Boolean> gpuTableSelected;
    @FXML
    private TableColumn<NamedSelection, String> gpuTableIndex;
    @FXML
    private TextField multiGpuSplit;
    @FXML
    private ChoiceBox<String> optimizerChoice;
    @FXML
    private ChoiceBox<String> backendChoice;
    @FXML
    private Slider nCorrectionSlider;
    @FXML
    private TextField nCorrectionField;
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
    private Slider chainLengthSlider;
    @FXML
    private TextField chainLengthField;
    @FXML
    private Slider chainIterationRatioSlider;
    @FXML
    private TextField chainIterationRatioField;
    @FXML
    private Slider chainSizeRatioSlider;
    @FXML
    private TextField chainSizeRatioField;

    @FXML
    private Button queueButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button commandButton;
    @FXML
    private Button imageViewModeFit;
    @FXML
    private Button imageViewModeActual;

    @FXML
    private TreeTableView<NeuralQueue.NeuralQueueItem> outputTreeTable;
    @FXML
    private TreeTableColumn<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem> outputTreeTableButton;
    @FXML
    private TreeTableColumn<NeuralQueue.NeuralQueueItem, String> outputTreeTableName;
    @FXML
    private TreeTableColumn<NeuralQueue.NeuralQueueItem, String> outputTreeTableStatus;

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
        NeuralQueue.setBundle(resources);
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

        setupStyleImageList();
        setupContentImageList();
        setupGpuIndexTable();
        setupStyleLayersTable();
        setupContentLayersTable();
        setupOutputTreeTable();

        log.log(Level.FINER, "Setting neural service log handler.");
        neuralService.addLogHandler(new TextAreaLogHandler(logTextArea));

        log.log(Level.FINER, "Loading last used style.");
        NeuralStyle loadedNeuralStyle = FileUtils.loadStyle(FileUtils.getLastUsedOutputStyle());
        if (loadedNeuralStyle != null)
            loadStyle(loadedNeuralStyle);

        log.log(Level.FINER, "Starting output timer.");
        imageOutputTimer.restart();
    }

    void setStage(Stage stage) {
        this.stage = stage;

        log.log(Level.FINER, "Setting keyboard shortcuts.");
        final KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlShiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (ctrlS.match(event)) {
                tabs.getSelectionModel().select(inputTab);
                styleImageList.requestFocus();
            } else if (ctrlC.match(event)) {
                tabs.getSelectionModel().select(inputTab);
                contentImageList.requestFocus();
            } else if (ctrlL.match(event)) {
                tabs.getSelectionModel().select(layersTab);
                styleLayersTable.requestFocus();
            } else if (ctrlO.match(event)) {
                tabs.getSelectionModel().select(outputTab);
            } else if (ctrlEnter.match(event)) {
                queueStyle();
            } else if (ctrlShiftEnter.match(event)) {
                startService();
            }
        });
    }

    private void queueStyle() {
        log.log(Level.FINE, "Queueing neural style.");
        neuralStyle.generateUniqueName();
        for (NeuralStyle ns : neuralStyle.getQueueItems())
            FileUtils.saveOutputStyle(ns);
        FileUtils.saveLastUsedOutputStyle(neuralStyle);
    }

    private void startService() {
        if (!neuralService.isRunning() && neuralStyle != null) {
            log.log(Level.FINE, "Starting neural service.");
            logTextArea.clear();
            neuralService.reset();
            neuralService.start();
            tabs.getSelectionModel().select(outputTab);
        }
    }

    void stopService() {
        if (neuralService.isRunning()) {
            log.log(Level.FINE, "Cancelling neural service.");
            neuralService.cancel();
        }
    }

    private void toggleStyleButtons() {
        queueButton.setDisable(!neuralStyle.checkArguments());
        startButton.setDisable(neuralService.isRunning());
        commandButton.setDisable(!neuralStyle.checkArguments());
    }

    private void updateImageView() {
        File imageFile = getOutputImage(null);
        if (imageFile != null && stage.isShowing())
            outputImageView.setImage(imageFile);
    }

    private void setThPath(File newThPath) {
        if (newThPath == null)
            thPath.setText("");
        else
            thPath.setText(newThPath.getAbsolutePath());
        neuralStyle.setThPath(newThPath);
    }

    private void setNeuralPath(File neuralStylePath) {
        if (neuralStylePath == null)
            neuralPath.setText("");
        else
            neuralPath.setText(neuralStylePath.getAbsolutePath());
        neuralStyle.setNeuralStylePath(neuralStylePath);
    }

    private void setInitImageFile(File initImageFile) {
        neuralStyle.setInitImage(initImageFile);
        if (initImageFile != null) {
            initImagePath.setText(initImageFile.getAbsolutePath());
            File parentFile = initImageFile.getParentFile();
            if (FileUtils.checkFolderExists(parentFile))
                fileChooser.setInitialDirectory(parentFile);
        } else {
            initImagePath.setText("");
        }
    }

    private void setModelFile(File modelFile) {
        neuralStyle.setModelFile(modelFile);
        if (modelFile != null) {
            modelFilePath.setText(modelFile.getAbsolutePath());
            File parentFile = modelFile.getParentFile();
            if (FileUtils.checkFolderExists(parentFile))
                fileChooser.setInitialDirectory(parentFile);
        } else {
            modelFilePath.setText("");
        }
    }

    private void setProtoFile(File protoFile) {
        neuralStyle.setProtoFile(protoFile);
        if (protoFile != null) {
            protoFilePath.setText(protoFile.getAbsolutePath());
            File parentFile = protoFile.getParentFile();
            if (FileUtils.checkFolderExists(parentFile))
                fileChooser.setInitialDirectory(parentFile);

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
            setDefaultNamedSelections();
        }
    }

    private void setStyleFolder(File styleFolder) {
        styleFolderPath.setText(styleFolder.getAbsolutePath());
        if (FileUtils.checkFolderExists(styleFolder))
            directoryChooser.setInitialDirectory(styleFolder);
        styleImages.setAll(FileUtils.getImages(styleFolder));
    }

    private void setContentFolder(File contentFolder) {
        contentFolderPath.setText(contentFolder.getAbsolutePath());
        if (FileUtils.checkFolderExists(contentFolder))
            directoryChooser.setInitialDirectory(contentFolder);
        contentImages.setAll(FileUtils.getImages(contentFolder));
    }

    private void setOutputFolder(File outputFolder) {
        neuralStyle.setOutputFolder(outputFolder);
        if (FileUtils.checkFolderExists(outputFolder)) {
            outputPath.setText(outputFolder.getAbsolutePath());
            directoryChooser.setInitialDirectory(outputFolder);
        } else {
            outputPath.setText("");
        }
    }

    private void setDefaultNamedSelections() {
        gpuIndices.setAll(
                new NamedSelection("0", true),
                new NamedSelection("1", false),
                new NamedSelection("2", false),
                new NamedSelection("3", false),
                new NamedSelection("4", false),
                new NamedSelection("5", false),
                new NamedSelection("6", false),
                new NamedSelection("7", false),
                new NamedSelection("8", false),
                new NamedSelection("9", false)
        );
        styleLayers.setAll(
                new NamedSelection("relu1_1", true),
                new NamedSelection("relu1_2", false),
                new NamedSelection("relu2_1", true),
                new NamedSelection("relu2_2", false),
                new NamedSelection("relu3_1", true),
                new NamedSelection("relu3_2", false),
                new NamedSelection("relu3_3", false),
                new NamedSelection("relu3_4", false),
                new NamedSelection("relu4_1", true),
                new NamedSelection("relu4_2", false),
                new NamedSelection("relu4_3", false),
                new NamedSelection("relu4_4", false),
                new NamedSelection("relu5_1", true),
                new NamedSelection("relu5_2", false),
                new NamedSelection("relu5_3", false),
                new NamedSelection("relu5_4", false),
                new NamedSelection("relu6", false),
                new NamedSelection("relu7", false)
        );
        contentLayers.setAll(
                new NamedSelection("relu1_1", false),
                new NamedSelection("relu1_2", false),
                new NamedSelection("relu2_1", false),
                new NamedSelection("relu2_2", false),
                new NamedSelection("relu3_1", false),
                new NamedSelection("relu3_2", false),
                new NamedSelection("relu3_3", false),
                new NamedSelection("relu3_4", false),
                new NamedSelection("relu4_1", false),
                new NamedSelection("relu4_2", true),
                new NamedSelection("relu4_3", false),
                new NamedSelection("relu4_4", false),
                new NamedSelection("relu5_1", false),
                new NamedSelection("relu5_2", false),
                new NamedSelection("relu5_3", false),
                new NamedSelection("relu5_4", false),
                new NamedSelection("relu6", false),
                new NamedSelection("relu7", false)
        );
    }

    private void updateLayers(String[] layers) {
        List<NamedSelection> newStyleLayers = new ArrayList<>();
        List<NamedSelection> newContentLayers = new ArrayList<>();
        for (String layer : layers) {
            newStyleLayers.add(new NamedSelection(layer, false));
            newContentLayers.add(new NamedSelection(layer, false));
        }
        styleLayers.setAll(newStyleLayers);
        contentLayers.setAll(newContentLayers);
    }

    private void updateNamedSelections(String[] selectedNames, ObservableList<NamedSelection> existingNames) {
        // ensure all NamedSelections are deselected
        List<NamedSelection> newSelectedNamed = existingNames.stream()
                .map(namedSelection -> new NamedSelection(namedSelection.getName(), false))
                .collect(Collectors.toList());

        if (selectedNames != null && selectedNames.length > 0) {
            // select NamedSelections
            for (String selectedName : selectedNames) {
                boolean existed = false;
                for (NamedSelection namedSelection : newSelectedNamed) {
                    if (namedSelection.getName().equalsIgnoreCase(selectedName)) {
                        namedSelection.setSelected(true);
                        existed = true;
                        break;
                    }
                }

                // create new name for selection if necessary
                if (!existed) {
                    newSelectedNamed.add(new NamedSelection(selectedName, true));
                }
            }

            existingNames.setAll(newSelectedNamed);
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

            if (selectedNeuralImages.size() == 1)
                styleImageList.getSelectionModel().select(selectedNeuralImages.get(0));
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
            contentImageList.getSelectionModel().select(selectedNeuralImage);
        }
    }

    private void updateNeuralOutputs(Map<String, Set<String>> updatedOutputs) {
        if (updatedOutputs == null || updatedOutputs.isEmpty()) {
            outputRoot.getChildren().clear();
            return;
        }

        // remove any outputs that are no longer there
        outputRoot.getChildren().removeAll(outputRoot.getChildren().stream()
                        .filter(existingOutput -> !updatedOutputs.containsKey(
                                existingOutput.getValue().getFile().getAbsolutePath()))
                        .collect(Collectors.toList()));

        // remove any output images that are no longer there
        for (TreeItem<NeuralQueue.NeuralQueueItem> existingOutput : outputRoot.getChildren()) {
            Set<String> updatedOutputImages = updatedOutputs.get(existingOutput.getValue().getFile().getAbsolutePath());
            existingOutput.getChildren().removeAll(
                    existingOutput.getChildren().stream()
                    .filter(existingOutputImage -> !updatedOutputImages.contains(
                            existingOutputImage.getValue().getFile().getAbsolutePath()))
                    .collect(Collectors.toList()));
        }

        // update any outputs that are still there
        for (String updatedOutput : updatedOutputs.keySet()) {
            for (TreeItem<NeuralQueue.NeuralQueueItem> existingOutput : outputRoot.getChildren()) {
                NeuralQueue.NeuralQueueItem queueItem = existingOutput.getValue();
                if (queueItem.getFile().getAbsolutePath().equals(updatedOutput)) {
                    queueItem.setFile(new File(updatedOutput));
                    break;
                }
            }
        }

        // add any new outputs
        List<TreeItem<NeuralQueue.NeuralQueueItem>> newOutputs = new ArrayList<>();
        for (String updatedOutput : updatedOutputs.keySet()) {
            boolean exists = false;
            for (TreeItem<NeuralQueue.NeuralQueueItem> existingOutput : outputRoot.getChildren()) {
                if (existingOutput.getValue().getFile().getAbsolutePath().equals(updatedOutput)) {
                    exists = true;
                    break;
                }
            }
            if (!exists)
                newOutputs.add(new TreeItem<>(createQueueItem(new File(updatedOutput))));
        }
        outputRoot.getChildren().addAll(newOutputs);

        // add any new output images
        for (String updatedOutput : updatedOutputs.keySet()) {
            for (TreeItem<NeuralQueue.NeuralQueueItem> existingOutput : outputRoot.getChildren()) {
                if (existingOutput.getValue().getFile().getAbsolutePath().equals(updatedOutput)) {
                    // found matching style to add this to
                    List<TreeItem<NeuralQueue.NeuralQueueItem>> newOutputImages = new ArrayList<>();
                    for (String updatedOutputImage : updatedOutputs.get(updatedOutput)) {
                        boolean exists = false;
                        for (TreeItem<NeuralQueue.NeuralQueueItem> existingOutputImage : existingOutput.getChildren()) {
                            if (existingOutputImage.getValue().getFile().getAbsolutePath().equals(updatedOutputImage)) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists)
                            newOutputImages.add(new TreeItem<>(createQueueItem(new File(updatedOutputImage))));
                    }
                    existingOutput.getChildren().addAll(newOutputImages);
                    break;
                }
            }
        }
    }

    private TreeItem<NeuralQueue.NeuralQueueItem> getMostRecentOutput() {
        List<TreeItem<NeuralQueue.NeuralQueueItem>> inProgressItems =
                outputTreeTable.getRoot().getChildren().stream()
                        .filter(queueItem -> queueItem.getValue().getStatus().getValue()
                                .equalsIgnoreCase(bundle.getString("neuralQueueItemInProgress")))
                        .collect(Collectors.toList());
        if (!inProgressItems.isEmpty())
            return inProgressItems.get(0);

        List<TreeItem<NeuralQueue.NeuralQueueItem>> allItems =
                outputTreeTable.getRoot().getChildren().stream()
                        .filter(queueItem -> !queueItem.getValue().getStatus().getValue()
                                .equalsIgnoreCase(bundle.getString("neuralQueueItemFailed")))
                        .collect(Collectors.toList());
        if (!allItems.isEmpty())
            return allItems.get(allItems.size() - 1);

        return null;
    }

    private File getOutputImage(Region tooltipRegion) {
        TreeItem<NeuralQueue.NeuralQueueItem> outputSelection = outputTreeTable.getSelectionModel().getSelectedItem();
        if (outputSelection == null) {
            log.log(Level.FINER, "Output Image: no output selection, checking for latest current output");
            TreeItem<NeuralQueue.NeuralQueueItem> mostRecentOutput = getMostRecentOutput();

            if (mostRecentOutput != null) {
                File mostRecentStyle = mostRecentOutput.getValue().getFile();
                File[] inProgressImages = FileUtils.getTempOutputImageIterations(mostRecentStyle);
                if (inProgressImages != null && inProgressImages.length > 0)
                    return inProgressImages[inProgressImages.length - 1];
                else {
                    log.log(Level.FINER, "Output Image: no output selection nor latest image");
                    if (tooltipRegion != null && inProgressImages == null)
                        showTooltipNextTo(tooltipRegion, bundle.getString("outputImageNullIterations"));
                    else if (tooltipRegion != null && inProgressImages.length <= 0)
                        showTooltipNextTo(tooltipRegion, bundle.getString("outputImageNoIterations"));
                    return null;
                }
            } else {
                log.log(Level.FINER, "Output Image: no output selection nor latest image");
                if (tooltipRegion != null)
                    showTooltipNextTo(tooltipRegion, bundle.getString("outputImageNullIterations"));
                return null;
            }
        } else {
            NeuralQueue.NeuralQueueItem output = outputSelection.getValue();
            if (FilenameUtils.isExtension(output.getFile().getAbsolutePath(), "json")) {
                log.log(Level.FINER, "Output Image: output selection is style, using latest child");
                ObservableList<TreeItem<NeuralQueue.NeuralQueueItem>> outputChildren = outputSelection.getChildren();
                if (outputChildren != null && !outputChildren.isEmpty())
                    return outputChildren.get(outputChildren.size() - 1).getValue().getFile();
                else {
                    log.log(Level.FINER, "Output Image: output selection but no latest image");
                    if (tooltipRegion != null && outputChildren == null)
                        showTooltipNextTo(tooltipRegion, bundle.getString("outputImageNullIterations"));
                    else if (tooltipRegion != null && outputChildren.isEmpty())
                        showTooltipNextTo(tooltipRegion, bundle.getString("outputImageNoIterations"));
                    return null;
                }
            } else {
                log.log(Level.FINER, "Output Image: output selection is image");
                return output.getFile();
            }
        }
    }

    private File getOutputStyle(Region tooltipRegion) {
        TreeItem<NeuralQueue.NeuralQueueItem> outputSelection = outputTreeTable.getSelectionModel().getSelectedItem();
        if (outputSelection == null) {
            log.log(Level.FINER, "Output Style: no output selection, checking for latest current output");
            TreeItem<NeuralQueue.NeuralQueueItem> mostRecentOutput = getMostRecentOutput();

            if (mostRecentOutput != null)
                return mostRecentOutput.getValue().getFile();
            else {
                log.log(Level.FINER, "Output Style: no output selection nor latest image");
                if (tooltipRegion != null)
                    showTooltipNextTo(tooltipRegion, bundle.getString("outputImageNullIterations"));
                return null;
            }
        } else {
            NeuralQueue.NeuralQueueItem output = outputSelection.getValue();
            if (FilenameUtils.isExtension(output.getFile().getAbsolutePath(), "json")) {
                log.log(Level.FINER, "Output Style: output selection is style, using selection");
                return output.getFile();
            } else {
                log.log(Level.FINER, "Output Style: output selection is image, using parent style");
                return outputSelection.getParent().getValue().getFile();
            }
        }
    }

    private void loadStyle(NeuralStyle loadedNeuralStyle) {
        neuralStyle = loadedNeuralStyle;

        // Reset the Queued status
        neuralStyle.setQueueStatus(NeuralStyle.QUEUED);

        // Retrieve these before paths because that will change them
        File[] selectedStyleImages = neuralStyle.getStyleImages();
        double[] selectedStyleWeights = neuralStyle.getStyleWeights();
        File contentImage = neuralStyle.getContentImage();
        String[] selectedGpuIndices = neuralStyle.getGpu();
        String[] selectedStyleLayers = neuralStyle.getStyleLayers();
        String[] selectedContentLayers = neuralStyle.getContentLayers();

        if (selectedStyleImages != null)
            styleMultipleSelect.setSelected(selectedStyleImages.length != 1);

        // Set paths
        setThPath(neuralStyle.getThPath());
        setNeuralPath(neuralStyle.getNeuralStylePath());
        setProtoFile(neuralStyle.getProtoFile());
        setModelFile(neuralStyle.getModelFile());
        setOutputFolder(neuralStyle.getOutputFolder());
        setInitImageFile(neuralStyle.getInitImage());

        // Set selected layers after updating layers from paths
        updateNamedSelections(selectedGpuIndices, this.gpuIndices);
        updateNamedSelections(selectedStyleLayers, this.styleLayers);
        updateNamedSelections(selectedContentLayers, this.contentLayers);

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
        cpuMode.setSelected(neuralStyle.isCpu());
        multiGpuSplit.setText(neuralStyle.getMultiGpuStrategy());
        backendChoice.setValue(neuralStyle.getBackend());
        optimizerChoice.setValue(neuralStyle.getOptimizer());
        nCorrectionSlider.setValue(neuralStyle.getNCorrection());
        learningRateSlider.setValue(neuralStyle.getLearningRate());
        autotune.setSelected(neuralStyle.isAutotune());
        chainLengthSlider.setValue(neuralStyle.getChainLength());
        chainIterationRatioSlider.setValue(neuralStyle.getChainIterationRatio());
        chainSizeRatioSlider.setValue(neuralStyle.getChainSizeRatio());

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

    private NeuralQueue.NeuralQueueItem createQueueItem(File file) {
        NeuralQueue.NeuralQueueItem queueItem = NeuralQueue.createQueueItem(file);
        switch (queueItem.getType()) {

            case NeuralQueue.QUEUED_STYLE:
                queueItem.setActionCallback(() -> {
                    NeuralStyle loadedStyle = FileUtils.loadStyle(file);
                    if (loadedStyle == null)
                        showTooltipNextTo(loadStyleButton, bundle.getString("loadStyleFailed"));
                    else {
                        loadStyle(loadedStyle);
                        showTooltipNextTo(loadStyleButton, bundle.getString("loadStyleSuccess"));
                    }
                });
                break;

            case NeuralQueue.QUEUED_IMAGE:
                queueItem.setActionCallback(() -> {
                    initChoice.setValue("image");
                    setInitImageFile(file);
                    showTooltipNextTo(initImagePath, bundle.getString("outputTreeTableInitTooltip"));
                });
                break;

            default:
                break;
        }
        return queueItem;
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
        assert thPathButton != null : "fx:id=\"thButton\" was not injected.";
        assert thPath != null : "fx:id=\"thPath\" was not injected.";
        assert neuralPathButton != null : "fx:id=\"neuralPathButton\" was not injected.";
        assert neuralPath != null : "fx:id=\"neuralPath\" was not injected.";
        assert saveStyleButton != null : "fx:id=\"saveStyleButton\" was not injected.";
        assert loadStyleButton != null : "fx:id=\"loadStyleButton\" was not injected.";
        assert tabs != null : "fx:id=\"tabs\" was not injected.";
        assert inputTab != null : "fx:id=\"inputTab\" was not injected.";
        assert outputTab != null : "fx:id=\"outputTab\" was not injected.";
        assert layersTab != null : "fx:id=\"layersTab\" was not injected.";
        assert styleFolderPath != null : "fx:id=\"styleFolderPath\" was not injected.";
        assert contentFolderPath != null : "fx:id=\"contentFolderPath\" was not injected.";
        assert outputPath != null : "fx:id=\"outputPath\" was not injected.";
        assert outputName != null : "fx:id=\"outputName\" was not injected.";
        assert styleFolderButton != null : "fx:id=\"styleFolderButton\" was not injected.";
        assert contentFolderButton != null : "fx:id=\"contentFolderButton\" was not injected.";
        assert outputFolderButton != null : "fx:id=\"outputFolderButton\" was not injected.";
        assert outputImageButton != null : "fx:id=\"outputImageButton\" was not injected.";
        assert styleMultipleSelect != null : "fx:id=\"styleMultipleSelect\" was not injected.";
        assert styleImageList != null : "fx:id=\"styleImageList\" was not injected.";
        assert contentImageList != null : "fx:id=\"contentImageList\" was not injected.";
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
        assert initImageButton != null : "fx:id=\"initImageButton\" was not injected.";
        assert initImagePath != null : "fx:id=\"initImagePath\" was not injected.";
        assert poolingChoice != null : "fx:id=\"poolingChoice\" was not injected.";
        assert originalColors != null : "fx:id=\"originalColors\" was not injected.";
        assert normalizeGradients != null : "fx:id=\"normalizeGradients\" was not injected.";
        assert cpuMode != null : "fx:id=\"cpuMode\" was not injected.";
        assert gpuTable != null : "fx:id=\"gpuTable\" was not injected.";
        assert gpuTableSelected != null : "fx:id=\"gpuTableSelected\" was not injected.";
        assert gpuTableIndex != null : "fx:id=\"gpuTableIndex\" was not injected.";
        assert multiGpuSplit != null : "fx:id=\"multiGpuSplit\" was not injected.";
        assert backendChoice != null : "fx:id=\"backendChoice\" was not injected.";
        assert optimizerChoice != null : "fx:id=\"optimizerChoice\" was not injected.";
        assert nCorrectionSlider != null : "fx:id=\"nCorrectionSlider\" was not injected.";
        assert nCorrectionField != null : "fx:id=\"nCorrectionField\" was not injected.";
        assert learningRateSlider != null : "fx:id=\"learningRateSlider\" was not injected.";
        assert learningRateField != null : "fx:id=\"learningRateField\" was not injected.";
        assert autotune != null : "fx:id=\"autotune\" was not injected.";
        assert chainLengthSlider != null : "fx:id=\"chainLengthSlider\" was not injected.";
        assert chainLengthField != null : "fx:id=\"chainLengthField\" was not injected.";
        assert chainIterationRatioSlider != null : "fx:id=\"chainIterationRatioSlider\" was not injected.";
        assert chainIterationRatioField != null : "fx:id=\"chainIterationRatioField\" was not injected.";
        assert chainSizeRatioSlider != null : "fx:id=\"chainSizeRatioSlider\" was not injected.";
        assert chainSizeRatioField != null : "fx:id=\"chainSizeRatioField\" was not injected.";
        assert protoFileButton != null : "fx:id=\"protoFileButton\" was not injected.";
        assert protoFilePath != null : "fx:id=\"protoFilePath\" was not injected.";
        assert modelFileButton != null : "fx:id=\"modelFileButton\" was not injected.";
        assert modelFilePath != null : "fx:id=\"modelFilePath\" was not injected.";
        assert queueButton != null : "fx:id=\"queueButton\" was not injected.";
        assert startButton != null : "fx:id=\"startButton\" was not injected.";
        assert stopButton != null : "fx:id=\"stopButton\" was not injected.";
        assert commandButton != null : "fx:id=\"commandButton\" was not injected.";
        assert imageViewModeFit != null : "fx:id=\"imageViewModeFit\" was not injected.";
        assert imageViewModeActual != null : "fx:id=\"imageViewModeActual\" was not injected.";
        assert outputTreeTable != null : "fx:id=\"outputTreeTable\" was not injected.";
        assert outputTreeTableButton != null : "fx:id=\"outputTreeTableButton\" was not injected.";
        assert outputTreeTableName != null : "fx:id=\"outputTreeTableName\" was not injected.";
        assert outputTreeTableStatus != null : "fx:id=\"outputTreeTableStatus\" was not injected.";
        assert imageView != null : "fx:id=\"imageView\" was not injected.";
        assert imageViewSizer != null : "fx:id=\"imageViewSizer\" was not injected.";
        assert statusLabel != null : "fx:id=\"statusLabel\" was not injected.";
        assert progress != null : "fx:id=\"progress\" was not injected.";
        assert logTextArea != null : "fx:id=\"logTextArea\" was not injected.";
        log.log(Level.FINER, "All FXML items were injected.");
    }

    private void setupObservableLists() {
        styleImages = FXCollections.observableArrayList(neuralImage ->
                new Observable[] {neuralImage.selectedProperty(), neuralImage.weightProperty(),
                        styleMultipleSelect.selectedProperty()});
        contentImages = FXCollections.observableArrayList(neuralImage ->
                new Observable[] {neuralImage.selectedProperty()});
        gpuIndices = FXCollections.observableArrayList(gpuIndex ->
                new Observable[] {gpuIndex.selectedProperty()});
        styleLayers = FXCollections.observableArrayList(neuralLayer ->
                new Observable[] {neuralLayer.selectedProperty(), neuralLayer.nameProperty()});
        contentLayers = FXCollections.observableArrayList(neuralLayer ->
                new Observable[] {neuralLayer.selectedProperty(), neuralLayer.nameProperty()});

        setDefaultNamedSelections();
    }

    private void setupButtonListeners() {
        log.log(Level.FINER, "Setting TH listener.");
        EventStreams.eventsOf(thPathButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing th file chooser.");
            fileChooser.setTitle(bundle.getString("thPathChooser"));
            File thPath = fileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "th file chosen: {0}", thPath);
            setThPath(thPath);
        });

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
                toggleStyleButtons();
            }
        });

        log.log(Level.FINER, "Setting Output Image listener.");
        EventStreams.eventsOf(outputImageButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Output Image button hit, checking images.");

            // Check for generated image iterations to show
            File outputFolder = neuralStyle.getOutputFolder();

            if (outputFolder == null) {
                showTooltipNextTo(outputImageButton, bundle.getString("outputImageNoOutputFolder"));
            } else {
                File imageFile = getOutputImage(outputImageButton);
                if (imageFile == null)
                    return;
                File styleFile = getOutputStyle(outputImageButton);
                if (styleFile == null)
                    return;
                String possibleName = outputName.getText();

                File[] savedFiles = FileUtils.saveTempOutputsTo(imageFile, styleFile, outputFolder, possibleName);
                if (savedFiles == null || savedFiles.length <= 0) {
                    showTooltipNextTo(outputImageButton, bundle.getString("outputImageNoSavedImage"));
                } else {
                    showTooltipNextTo(outputImageButton,
                            bundle.getString("outputImageSavedImage") + "\n" + savedFiles[0].getName());
                }
            }
        });

        log.log(Level.FINER, "Setting Queue listener.");
        EventStreams.eventsOf(queueButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Queue button hit.");
            outputImageView.fitToView();
            queueStyle();
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

        log.log(Level.FINER, "Setting Command listener.");
        EventStreams.eventsOf(commandButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Command button hit.");
            if (neuralStyle.checkArguments()) {
                neuralStyle.generateUniqueName();
                String[] command = neuralStyle.buildCommand();
                StringBuilder builder = new StringBuilder();
                for (String commandPart : command) {
                    if (commandPart.contains(" "))
                        commandPart = '"' + commandPart + '"';
                    builder.append(commandPart);
                    builder.append(' ');
                }

                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(builder.toString());
                clipboard.setContent(content);
            } else {
                showTooltipNextTo(commandButton, bundle.getString("commandButtonInvalid"));
            }
        });

        log.log(Level.FINER, "Setting Fit View listener.");
        EventStreams.eventsOf(imageViewModeFit, ActionEvent.ACTION).subscribe(actionEvent ->
                outputImageView.fitToView());

        log.log(Level.FINER, "Setting Actual Size listener.");
        EventStreams.eventsOf(imageViewModeActual, ActionEvent.ACTION).subscribe(actionEvent ->
                outputImageView.scaleImageViewport(1));

        log.log(Level.FINER, "Setting Init Image listener.");
        EventStreams.eventsOf(initImageButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing init image file chooser.");
            fileChooser.setTitle(bundle.getString("initImageChooser"));
            File initImageFile = fileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "init image file chosen: {0}", initImageFile);
            setInitImageFile(initImageFile);
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
                actionEvent -> styleLayers.add(new NamedSelection("newLayer", false)));

        log.log(Level.FINER, "Setting Style Layer Remove listener.");
        EventStreams.eventsOf(styleLayerRemove, ActionEvent.ACTION).subscribe(
                actionEvent -> styleLayers.removeAll(styleLayersTable.getSelectionModel().getSelectedItems()));

        log.log(Level.FINER, "Setting Content Layer Add listener.");
        EventStreams.eventsOf(contentLayerAdd, ActionEvent.ACTION).subscribe(
                actionEvent -> contentLayers.add(new NamedSelection("newLayer", false)));

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
        EventStreams.valuesOf(printIterField.textProperty()).subscribe(numberChange ->
                neuralStyle.setIterationsPrint(intConverter.fromString(numberChange).intValue()));

        // keep save slider and text field synced and the slider updates the style
        saveIterField.textProperty().bindBidirectional(saveIterSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(saveIterField.textProperty()).subscribe(numberChange ->
                neuralStyle.setIterationsSave(intConverter.fromString(numberChange).intValue()));

        // keep max slider and text field synced and the slider updates the style
        maxIterField.textProperty().bindBidirectional(maxIterSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(maxIterField.textProperty()).subscribe(numberChange ->
                neuralStyle.setIterations(intConverter.fromString(numberChange).intValue()));

        // keep seed slider and text field synced and the slider updates the style
        seedField.textProperty().bindBidirectional(seedSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(seedField.textProperty()).subscribe(numberChange ->
                neuralStyle.setSeed(intConverter.fromString(numberChange).intValue()));

        // keep output size slider and text field synced and the slider updates the style
        outputSizeField.textProperty().bindBidirectional(outputSizeSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(outputSizeField.textProperty()).subscribe(numberChange ->
                neuralStyle.setOutputSize(intConverter.fromString(numberChange).intValue()));

        // keep style size slider and text field synced and the slider updates the style
        styleSizeField.textProperty().bindBidirectional(styleSizeSlider.valueProperty(), doubleConverter);
        EventStreams.valuesOf(styleSizeField.textProperty()).subscribe(numberChange ->
                neuralStyle.setStyleSize(doubleConverter.fromString(numberChange).doubleValue()));

        // keep output weight slider and text field synced and the slider updates the style
        contentWeightField.textProperty().bindBidirectional(contentWeightSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(contentWeightField.textProperty()).subscribe(numberChange ->
                neuralStyle.setContentWeight(intConverter.fromString(numberChange).intValue()));

        // keep style weight slider and text field synced and the slider updates the style
        styleWeightField.textProperty().bindBidirectional(styleWeightSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(styleWeightField.textProperty()).subscribe(numberChange ->
                neuralStyle.setStyleWeight(intConverter.fromString(numberChange).intValue()));

        // keep TV weight slider and text field synced and the slider updates the style
        tvWeightField.textProperty().bindBidirectional(tvWeightSlider.valueProperty(), doubleConverter);
        EventStreams.valuesOf(tvWeightField.textProperty()).subscribe(numberChange ->
                neuralStyle.setTvWeight(doubleConverter.fromString(numberChange).doubleValue()));

        // init choicebox updates the style and toggles init path
        EventStreams.valuesOf(initChoice.valueProperty()).subscribe(init -> {
            neuralStyle.setInit(init);
            boolean notInitImage = !init.equalsIgnoreCase("image");
            initImageButton.setDisable(notInitImage);
            initImagePath.setDisable(notInitImage);
        });

        // pooling choicebox updates the style
        EventStreams.valuesOf(poolingChoice.valueProperty()).subscribe(stringChange ->
                neuralStyle.setPooling(stringChange));

        // original colors checkbox updates the style
        EventStreams.valuesOf(originalColors.selectedProperty()).subscribe(booleanChange ->
                neuralStyle.setOriginalColors(booleanChange));

        // normalize gradients checkbox updates the style
        EventStreams.valuesOf(normalizeGradients.selectedProperty()).subscribe(booleanChange ->
                neuralStyle.setNormalizeGradients(booleanChange));

        // CPU checkbox updates the style and toggles GPU
        EventStreams.valuesOf(cpuMode.selectedProperty()).subscribe(useCpu -> {
            neuralStyle.setCpu(useCpu);
            gpuTable.setDisable(useCpu);
            multiGpuSplit.setDisable(useCpu);
        });

        // Multi-GPU updates the style
        EventStreams.valuesOf(multiGpuSplit.textProperty()).subscribe(stringChange ->
                neuralStyle.setMultiGpuStrategy(stringChange));

        // backend choicebox updates the style and toggles autotune
        EventStreams.valuesOf(backendChoice.valueProperty()).subscribe(backend -> {
            neuralStyle.setBackend(backend);
            if (backend.equalsIgnoreCase("cudnn")) {
                autotune.setDisable(false);
            } else {
                autotune.setDisable(true);
                autotune.setSelected(false);
            }
        });

        // optimizer choicebox updates the style and toggles learning rate
        EventStreams.valuesOf(optimizerChoice.valueProperty()).subscribe(optimizer -> {
            neuralStyle.setOptimizer(optimizer);
            if (optimizer.equalsIgnoreCase("adam")) {
                nCorrectionSlider.setDisable(true);
                nCorrectionField.setDisable(true);
                nCorrectionField.setText("-1");
                learningRateSlider.setDisable(false);
                learningRateField.setDisable(false);
            } else {
                nCorrectionSlider.setDisable(false);
                nCorrectionField.setDisable(false);
                learningRateSlider.setDisable(true);
                learningRateField.setDisable(true);
                learningRateField.setText("10");
            }
        });

        // keep nCorrection slider and text field synced and the slider updates the style
        nCorrectionField.textProperty().bindBidirectional(nCorrectionSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(nCorrectionField.textProperty()).subscribe(numberChange ->
                neuralStyle.setNCorrection(intConverter.fromString(numberChange).intValue()));

        // keep learning rate slider and text field synced and the slider updates the style
        learningRateField.textProperty().bindBidirectional(learningRateSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(learningRateField.textProperty()).subscribe(numberChange ->
                neuralStyle.setLearningRate(intConverter.fromString(numberChange).intValue()));

        // autotune checkbox updates the style
        EventStreams.valuesOf(autotune.selectedProperty())
                .subscribe(booleanChange -> neuralStyle.setAutotune(booleanChange));

        // keep chain length slider and text field synced and the slider updates the style, some values toggle chain fields
        chainLengthField.textProperty().bindBidirectional(chainLengthSlider.valueProperty(), intConverter);
        EventStreams.valuesOf(chainLengthField.textProperty()).subscribe(numberChange -> {
            int chainLength = intConverter.fromString(numberChange).intValue();
            neuralStyle.setChainLength(chainLength);
            if (chainLength <= 1) {
                chainIterationRatioField.setDisable(true);
                chainIterationRatioSlider.setDisable(true);
                chainSizeRatioField.setDisable(true);
                chainSizeRatioSlider.setDisable(true);
            } else {
                chainIterationRatioField.setDisable(false);
                chainIterationRatioSlider.setDisable(false);
                chainSizeRatioField.setDisable(false);
                chainSizeRatioSlider.setDisable(false);
            }
        });

        // keep chain iteration ratio slider and text field synced and the slider updates the style
        chainIterationRatioField.textProperty().bindBidirectional(chainIterationRatioSlider.valueProperty(), doubleConverter);
        EventStreams.valuesOf(chainIterationRatioField.textProperty()).subscribe(numberChange ->
                neuralStyle.setChainIterationRatio(doubleConverter.fromString(numberChange).doubleValue()));

        // keep chain size ratio slider and text field synced and the slider updates the style
        chainSizeRatioField.textProperty().bindBidirectional(chainSizeRatioSlider.valueProperty(), doubleConverter);
        EventStreams.valuesOf(chainSizeRatioField.textProperty()).subscribe(numberChange ->
                neuralStyle.setChainSizeRatio(doubleConverter.fromString(numberChange).doubleValue()));
    }

    private void setupServiceListeners() {
        // handle each Worker.State
        log.log(Level.FINER, "Setting state listener.");
        EventStreams.valuesOf(neuralService.stateProperty()).subscribe(state -> {
            switch (state) {
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
                    break;
                case CANCELLED:
                    log.log(Level.FINER, "Neural service: Cancelled.");
                    statusLabel.setText(bundle.getString("neuralServiceStatusCancelled"));
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                    break;
                case FAILED:
                    log.log(Level.FINER, "Neural service: Failed.");
                    statusLabel.setText(bundle.getString("neuralServiceStatusFailed"));
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                    break;
            }
        });

        log.log(Level.FINER, "Setting Image Output Service listener.");
        EventStreams.nonNullValuesOf(imageOutputService.valueProperty()).subscribe(valueProperty -> {
            log.log(Level.FINER, "Received updated Image Outputs from Service.");
            Map<String, Set<String>> results = imageOutputService.getValue();
            updateNeuralOutputs(results);
            updateImageView();
        });

        log.log(Level.FINER, "Setting progress listener.");
        EventStreams.nonNullValuesOf(neuralService.progressProperty())
                .subscribe(value -> progress.setProgress(value.doubleValue()));

        log.log(Level.FINER, "Setting running listener.");
        final ColorAdjust highlighted = new ColorAdjust(0, 0, 0.3, 0);
        EventStreams.nonNullValuesOf(neuralService.runningProperty())
                .subscribe(running -> {
                   if (running)
                       statusLabel.setEffect(highlighted);
                   else
                       statusLabel.setEffect(null);
                });
    }

    private void setupOutputImageListeners() {
        imageView.fitWidthProperty().bind(imageViewSizer.widthProperty());
        imageView.fitHeightProperty().bind(imageViewSizer.heightProperty());

        log.log(Level.FINER, "Setting image timer.");
        imageOutputTimer = FxTimer.createPeriodic(Duration.ofMillis(250), () -> {
            log.log(Level.FINER, "Timer: checking service");

            if (imageOutputService != null && !imageOutputService.isRunning()) {
                imageOutputService.reset();
                imageOutputService.start();
            }
        });
    }

    private void setupNvidiaListener() {
        log.log(Level.FINER, "Setting nvidia ram listener.");
        EventStreams.nonNullValuesOf(nvidiaService.progressProperty())
                .filter(vramUsage -> vramUsage.doubleValue() > 0)
                .subscribe(vramUsage -> vramBar.setProgress(vramUsage.doubleValue()));

        log.log(Level.FINER, "Setting nvidia timer.");
        nvidiaTimer = FxTimer.createPeriodic(Duration.ofMillis(1000), () -> {
            log.log(Level.FINER, "Timer: checking service");
            if (nvidiaService == null || nvidiaService.isRunning())
                return;

            log.log(Level.FINER, "Timer: starting service");
            nvidiaService.restart();
            nvidiaTimer.restart();
        });
        nvidiaTimer.restart();
    }

    private void setupStyleImageList() {
        log.log(Level.FINER, "Setting style image list.");
        styleImageList.setItems(styleImages);
        styleImageList.setFixedCellSize(NeuralImage.THUMBNAIL_SIZE);

        log.log(Level.FINER, "Setting style image list selection mode listener.");
        EventStreams.valuesOf(styleMultipleSelect.selectedProperty()).subscribe(booleanChange -> {
            if (booleanChange)
                styleImageList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            else {
                for (NeuralImage neuralImage : styleImages)
                    neuralImage.setSelected(false);
                styleImageList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            }
        });

        log.log(Level.FINER, "Setting style image list selection listener.");
        EventStreams.changesOf(styleImageList.getSelectionModel().selectedItemProperty())
                .subscribe(neuralImageChange -> {
                    if (!styleMultipleSelect.isSelected()) {
                        NeuralImage oldNeuralImage = neuralImageChange.getOldValue();
                        if (oldNeuralImage != null)
                            oldNeuralImage.setSelected(false);
                        NeuralImage newNeuralImage = neuralImageChange.getNewValue();
                        if (newNeuralImage != null)
                            newNeuralImage.setSelected(true);
                    }
                });

        log.log(Level.FINER, "Setting style image list selection listener.");
        EventStreams.changesOf(styleImages).subscribe(change -> {
            log.log(Level.FINE, "styleImages changed");

            List<NeuralImage> selectedNeuralImages = styleImages.stream()
                    .filter(NeuralImage::isSelected).collect(Collectors.toList());

            File[] neuralFiles = new File[selectedNeuralImages.size()];
            double[] neuralFilesWeights = new double[selectedNeuralImages.size()];
            for (int i = 0; i < selectedNeuralImages.size(); i++) {
                NeuralImage neuralImage = selectedNeuralImages.get(i);
                neuralFiles[i] = neuralImage.getImageFile();
                neuralFilesWeights[i] = neuralImage.getWeight();
            }
            neuralStyle.setStyleImages(neuralFiles);
            neuralStyle.setStyleWeights(neuralFilesWeights);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting style image list shortcut listener");
        EventStreams.eventsOf(styleImageList, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            if (styleMultipleSelect.isSelected()) {
                ObservableList<NeuralImage> selectedStyleImages =
                        styleImageList.getSelectionModel().getSelectedItems();
                for (NeuralImage neuralImage : selectedStyleImages)
                    neuralImage.setSelected(!neuralImage.isSelected());
            }
        });

        log.log(Level.FINER, "Setting style image list column factory.");
        styleImageList.setCellFactory(new Callback<ListView<NeuralImage>, ListCell<NeuralImage>>() {
            @Override
            public ListCell<NeuralImage> call(ListView<NeuralImage> param) {
                return new ListCell<NeuralImage>() {
                    NeuralImageCell neuralImageCell = new NeuralImageCell(true);

                    @Override
                    public void updateItem(NeuralImage neuralImage, boolean empty) {
                        super.updateItem(neuralImage, empty);
                        neuralImageCell.setEditable(styleMultipleSelect.isSelected());

                        neuralImageCell.setNeuralImage(neuralImage);

                        if (empty || neuralImage == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            neuralImageCell.setNeuralImage(neuralImage);
                            setText(null);
                            setGraphic(neuralImageCell.getCellLayout());
                        }
                    }
                };
            }
        });
    }

    private void setupContentImageList() {
        log.log(Level.FINER, "Setting content image list.");
        contentImageList.setItems(contentImages);
        contentImageList.setFixedCellSize(NeuralImage.THUMBNAIL_SIZE);

        log.log(Level.FINER, "Setting content image list selection listener.");
        EventStreams.valuesOf(contentImageList.getSelectionModel().selectedItemProperty())
                .subscribe(newSelection -> {
                    log.log(Level.FINE, "Content image changed: " + newSelection);
                    if (newSelection == null)
                        neuralStyle.setContentImage(null);
                    else
                        neuralStyle.setContentImage(newSelection.getImageFile());
                    toggleStyleButtons();
                });

        log.log(Level.FINER, "Setting content image list column factory.");
        contentImageList.setCellFactory(new Callback<ListView<NeuralImage>, ListCell<NeuralImage>>() {
            @Override
            public ListCell<NeuralImage> call(ListView<NeuralImage> param) {
                return new ListCell<NeuralImage>() {
                    NeuralImageCell neuralImageCell = new NeuralImageCell(false);

                    @Override
                    public void updateItem(NeuralImage neuralImage, boolean empty) {
                        super.updateItem(neuralImage, empty);

                        neuralImageCell.setNeuralImage(neuralImage);

                        if (empty || neuralImage == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            neuralImageCell.setNeuralImage(neuralImage);
                            setText(null);
                            setGraphic(neuralImageCell.getCellLayout());
                        }
                    }
                };
            }
        });
    }

    private void setupGpuIndexTable() {
        log.log(Level.FINER, "Setting GPU index table list.");
        gpuTable.setItems(gpuIndices);
        gpuTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        log.log(Level.FINER, "Setting GPU index table selection listener.");
        EventStreams.changesOf(gpuIndices).subscribe(change -> {
            log.log(Level.FINE, "gpuIndices changed");

            List<NamedSelection> selectedGpuIndices = gpuIndices.stream()
                    .filter(NamedSelection::isSelected)
                    .collect(Collectors.toList());

            String[] newGpuIndices = new String[selectedGpuIndices.size()];
            for (int i = 0; i < selectedGpuIndices.size(); i++)
                newGpuIndices[i] = selectedGpuIndices.get(i).getName();
            neuralStyle.setGpu(newGpuIndices);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting GPU index table shortcut listener");
        EventStreams.eventsOf(gpuTable, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            ObservableList<NamedSelection> selectedGpuIndices =
                    gpuTable.getSelectionModel().getSelectedItems();
            for (NamedSelection gpuIndex : selectedGpuIndices)
                gpuIndex.setSelected(!gpuIndex.isSelected());
        });

        log.log(Level.FINER, "Setting GPU index table column factories.");
        gpuTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        gpuTableSelected.setCellFactory(CheckBoxTableCell.forTableColumn(gpuTableSelected));

        gpuTableIndex.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void setupStyleLayersTable() {
        log.log(Level.FINER, "Setting style layer table list.");
        styleLayersTable.setItems(styleLayers);
        styleLayersTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        log.log(Level.FINER, "Setting style layer table selection listener.");
        EventStreams.changesOf(styleLayers).subscribe(change -> {
            log.log(Level.FINE, "styleLayers changed");

            List<NamedSelection> selectedStyleLayers = styleLayers.stream()
                    .filter(NamedSelection::isSelected)
                    .collect(Collectors.toList());

            String[] newStyleLayers = new String[selectedStyleLayers.size()];
            for (int i = 0; i < selectedStyleLayers.size(); i++)
                newStyleLayers[i] = selectedStyleLayers.get(i).getName();
            neuralStyle.setStyleLayers(newStyleLayers);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting style layer table shortcut listener");
        EventStreams.eventsOf(styleLayersTable, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            ObservableList<NamedSelection> selectedStyleLayers =
                    styleLayersTable.getSelectionModel().getSelectedItems();
            for (NamedSelection neuralLayer : selectedStyleLayers)
                neuralLayer.setSelected(!neuralLayer.isSelected());
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

            List<NamedSelection> selectedContentLayers = contentLayers.stream()
                    .filter(NamedSelection::isSelected)
                    .collect(Collectors.toList());

            String[] newContentLayers = new String[selectedContentLayers.size()];
            for (int i = 0; i < selectedContentLayers.size(); i++)
                newContentLayers[i] = selectedContentLayers.get(i).getName();
            neuralStyle.setContentLayers(newContentLayers);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting style layer table shortcut listener");
        EventStreams.eventsOf(contentLayersTable, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            ObservableList<NamedSelection> selectedStyleLayers =
                    contentLayersTable.getSelectionModel().getSelectedItems();
            for (NamedSelection neuralLayer : selectedStyleLayers)
                neuralLayer.setSelected(!neuralLayer.isSelected());
        });

        log.log(Level.FINER, "Setting content layer table column factories.");
        contentLayersTableSelected.setCellValueFactory(new PropertyValueFactory<>("selected"));
        contentLayersTableSelected.setCellFactory(CheckBoxTableCell.forTableColumn(contentLayersTableSelected));

        contentLayersTableName.setCellValueFactory(new PropertyValueFactory<>("name"));
        contentLayersTableName.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    private void setupOutputTreeTable() {
        log.log(Level.FINER, "Setting output tree table list.");
        outputTreeTable.setRoot(outputRoot);

        log.log(Level.FINER, "Setting output tree table selection listener.");
        EventStreams.changesOf(outputTreeTable.getSelectionModel().selectedItemProperty())
                .subscribe(neuralOutputChange -> updateImageView());

        log.log(Level.FINER, "Setting output tree table column factories.");
        outputTreeTableButton.setCellValueFactory(param ->
                new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
        outputTreeTableButton.setCellFactory(
                new Callback<TreeTableColumn<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem>,
                        TreeTableCell<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem>>() {
                    @Override
                    public TreeTableCell<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem>
                    call(TreeTableColumn<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem> param) {
                        return new TreeTableCell<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem>() {
                            Button button;
                            Subscription subscribe;
                            {
                                button = new Button();
                                setText(null);
                                setGraphic(button);
                            }

                            @Override
                            public void updateItem(NeuralQueue.NeuralQueueItem queueItem, boolean empty) {
                                super.updateItem(queueItem, empty);
                                if (empty || queueItem == null) {
                                    setText(null);
                                    setGraphic(null);
                                    if (subscribe != null) {
                                        subscribe.unsubscribe();
                                        subscribe = null;
                                    }
                                } else {
                                    button.setText(queueItem.getActionText());
                                    subscribe = EventStreams.eventsOf(button, ActionEvent.ACTION)
                                            .subscribe(actionEvent -> queueItem.doAction());
                                    setText(null);
                                    setGraphic(button);
                                }
                            }
                        };
                    }
                });

        outputTreeTableName.setCellValueFactory(param -> param.getValue().getValue().getName());

        outputTreeTableStatus.setCellValueFactory(param -> param.getValue().getValue().getStatus());
        outputTreeTableStatus.setCellFactory(
                new Callback<TreeTableColumn<NeuralQueue.NeuralQueueItem, String>,
                        TreeTableCell<NeuralQueue.NeuralQueueItem, String>>() {
                    @Override
                    public TreeTableCell<NeuralQueue.NeuralQueueItem, String>
                    call(TreeTableColumn<NeuralQueue.NeuralQueueItem, String> param) {
                        return new TreeTableCell<NeuralQueue.NeuralQueueItem, String>() {
                            @Override
                            public void updateItem(String queueStatus, boolean empty) {
                                super.updateItem(queueStatus, empty);
                                if (empty || queueStatus == null) {
                                    setText(null);
                                    setGraphic(null);
                                    setContextMenu(null);
                                } else {
                                    NeuralQueue.NeuralQueueItem queueItem = this.getTreeTableRow().getItem();
                                    String status = queueItem.getStatus().getValue();
                                    setText(status);
                                    setGraphic(null);

                                    if (queueItem.getType() == NeuralQueue.QUEUED_STYLE &&
                                            status.equalsIgnoreCase(bundle.getString("neuralQueueItemQueued"))) {
                                        final ContextMenu cellMenu = new ContextMenu();

                                        final MenuItem cancelMenuItem =
                                                new MenuItem(bundle.getString("neuralQueueItemCancel"));
                                        cancelMenuItem.setOnAction(event ->
                                                queueItem.changeStatus(NeuralStyle.CANCELLED));

                                        cellMenu.getItems().addAll(cancelMenuItem);
                                        setContextMenu(cellMenu);
                                    } else {
                                        setContextMenu(null);
                                    }
                                }
                            }
                        };
                    }
                });
    }
}
