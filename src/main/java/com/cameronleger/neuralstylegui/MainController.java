package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyleV2;
import com.cameronleger.neuralstyle.NeuralStyleV3;
import com.cameronleger.neuralstylegui.component.*;
import com.cameronleger.neuralstylegui.helper.AsyncImageProperty;
import com.cameronleger.neuralstylegui.helper.MovingImageView;
import com.cameronleger.neuralstylegui.helper.TextAreaLogHandler;
import com.cameronleger.neuralstylegui.listwrapview.CellNode;
import com.cameronleger.neuralstylegui.listwrapview.Cellable;
import com.cameronleger.neuralstylegui.listwrapview.ListWrapView;
import com.cameronleger.neuralstylegui.model.NeuralImage;
import com.cameronleger.neuralstylegui.model.NeuralQueue;
import com.cameronleger.neuralstyle.NeuralStyleWrapper;
import com.cameronleger.neuralstylegui.model.properties.NeuralBoolean;
import com.cameronleger.neuralstylegui.model.properties.NeuralDouble;
import com.cameronleger.neuralstylegui.model.properties.NeuralInt;
import com.cameronleger.neuralstylegui.service.NeuralService;
import com.cameronleger.neuralstylegui.service.NvidiaService;
import com.cameronleger.neuralstylegui.service.OutputService;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class MainController {
    private static final Logger log = Logger.getLogger(MainController.class.getName());

    private Stage stage;

    private OutputService imageOutputService = new OutputService();
    private NvidiaService nvidiaService = new NvidiaService();
    private NeuralService neuralService = new NeuralService();

    private NeuralStyleWrapper neuralStyle = new NeuralStyleWrapper();

    private Timer imageOutputTimer;
    private Timer nvidiaTimer;

    private ObservableList<NeuralImage> styleImages;
    private ObservableList<NeuralImage> contentImages;
    private ObservableList<NeuralBoolean> gpuIndices;
    private ObservableList<NeuralBoolean> styleLayers;
    private ObservableList<NeuralBoolean> contentLayers;
    private final TreeItem<NeuralQueue.NeuralQueueItem> outputRoot = new TreeItem<>(createQueueItem(null));

    private final KeyCombination spaceBar = new KeyCodeCombination(KeyCode.SPACE);

    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

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
    private TextField outputName;
    @FXML
    private Button styleFolderButton;
    @FXML
    private Button contentFolderButton;
    @FXML
    private Button outputImageButton;
    @FXML
    private CheckBox styleMultipleSelect;

    @FXML
    private ListWrapView<NeuralImage> styleImageGrid;
    @FXML
    private ListWrapView<NeuralImage> contentImageGrid;

    @FXML
    private Button styleLayerAdd;
    @FXML
    private Button styleLayerRemove;
    @FXML
    private TableView<NeuralBoolean> styleLayersTable;
    @FXML
    private TableColumn<NeuralBoolean, Boolean> styleLayersTableSelected;
    @FXML
    private TableColumn<NeuralBoolean, String> styleLayersTableName;

    @FXML
    private Button contentLayerAdd;
    @FXML
    private Button contentLayerRemove;
    @FXML
    private TableView<NeuralBoolean> contentLayersTable;
    @FXML
    private TableColumn<NeuralBoolean, Boolean> contentLayersTableSelected;
    @FXML
    private TableColumn<NeuralBoolean, String> contentLayersTableName;

    @FXML
    private ProgressBar vramBar;

    @FXML
    private NumberView chainLength;
    @FXML
    private NumberView maxIter;
    @FXML
    private NumberView printIter;
    @FXML
    private NumberView saveIter;
    @FXML
    private NumberView sizeOutput;
    @FXML
    private NumberView sizeStyle;
    @FXML
    private NumberView seed;
    @FXML
    private NumberView weightContent;
    @FXML
    private NumberView weightStyle;
    @FXML
    private ChoiceView init;
    @FXML
    private FileView initImage;
    @FXML
    private ImageView initImageView;
    private AsyncImageProperty initImageViewLoader;
    @FXML
    private CheckboxView originalColors;
    @FXML
    private CheckboxView normalizeGradients;
    @FXML
    private NumberView tvWeight;
    @FXML
    private ChoiceView pooling;
    @FXML
    private CheckboxView cpu;
    @FXML
    private TextView multiGpuStrategy;
    @FXML
    private ChoiceView backend;
    @FXML
    private CheckboxView autotune;
    @FXML
    private ChoiceView optimizer;
    @FXML
    private NumberView nCorrection;
    @FXML
    private NumberView learningRate;
    @FXML
    private FileView thFile;
    @FXML
    private DirectoryView neuralStyleFolder;
    @FXML
    private FileView protoFile;
    @FXML
    private FileView modelFile;
    @FXML
    private DirectoryView outputFolder;
    @FXML
    private DirectoryView workingFolder;

    @FXML
    private TableView<NeuralBoolean> gpuTable;
    @FXML
    private TableColumn<NeuralBoolean, Boolean> gpuTableSelected;
    @FXML
    private TableColumn<NeuralBoolean, String> gpuTableIndex;

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

    @FXML
    public void initialize() {
        log.log(Level.FINER, "Checking that all FXML items were injected.");
        checkInjections();

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

        setupStyleImageGrid();
        setupContentImageGrid();
        setupGpuIndexTable();
        setupStyleLayersTable();
        setupContentLayersTable();
        setupOutputTreeTable();

        log.log(Level.FINER, "Setting neural service log handler.");
        neuralService.addLogHandler(new TextAreaLogHandler(logTextArea));

        log.log(Level.FINER, "Loading last used style.");
        NeuralStyleV3 loadedNeuralStyle = FileUtils.loadStyle(FileUtils.getLastUsedOutputStyle());
        if (loadedNeuralStyle != null)
            loadStyle(loadedNeuralStyle);

        log.log(Level.FINER, "Starting output timer.");
        imageOutputTimer.restart();
    }

    void setStage(Stage stage) {
        this.stage = stage;

        log.log(Level.FINER, "Setting keyboard shortcuts.");
        final KeyCombination enter = new KeyCodeCombination(KeyCode.ENTER);
        final KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlC = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlL = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlO = new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        final KeyCombination ctrlShiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
        stage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (enter.match(event)) {
                // TODO: Suppress
            } else if (ctrlS.match(event)) {
                tabs.getSelectionModel().select(inputTab);
                styleImageGrid.requestFocus();
            } else if (ctrlC.match(event)) {
                tabs.getSelectionModel().select(inputTab);
                contentImageGrid.requestFocus();
            } else if (ctrlL.match(event)) {
                tabs.getSelectionModel().select(layersTab);
                styleLayersTable.requestFocus();
            } else if (ctrlO.match(event)) {
                tabs.getSelectionModel().select(outputTab);
            } else if (ctrlEnter.match(event)) {
                showTooltipNextTo(queueButton, resources.getString("queueButtonHit"));
                queueStyle();
            } else if (ctrlShiftEnter.match(event)) {
                startService();
            }
        });
    }

    private void queueStyle() {
        log.log(Level.FINE, "Queueing neural style.");
        NeuralStyleV3 style = neuralStyle.getNeuralStyle();
        style.generateUniqueName();
        for (NeuralStyleV3 ns : style.getQueueItems())
            FileUtils.saveOutputStyle(ns);
        FileUtils.saveLastUsedOutputStyle(style);
    }

    private void startService() {
        if (!neuralService.isRunning()) {
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

    private void setStyleFolder(File styleFolder) {
        if (!styleFolder.getAbsolutePath().equals(styleFolderPath.getText())) {
            log.log(Level.FINE, "New styleFolder: " + styleFolder);
            styleFolderPath.setText(styleFolder.getAbsolutePath());
            if (FileUtils.checkFolderExists(styleFolder))
                DirectoryView.directoryChooser.setInitialDirectory(styleFolder);
            styleImages.setAll(FileUtils.getImages(styleFolder));
        }
    }

    private void setContentFolder(File contentFolder) {
        if (!contentFolder.getAbsolutePath().equals(contentFolderPath.getText())) {
            log.log(Level.FINE, "New contentFolder: " + contentFolder);
            contentFolderPath.setText(contentFolder.getAbsolutePath());
            if (FileUtils.checkFolderExists(contentFolder))
                DirectoryView.directoryChooser.setInitialDirectory(contentFolder);
            contentImages.setAll(FileUtils.getImages(contentFolder));
        }
    }

    private void setDefaultNeuralBooleans() {
        gpuIndices.setAll(
                new NeuralBoolean("0", true),
                new NeuralBoolean("1", false),
                new NeuralBoolean("2", false),
                new NeuralBoolean("3", false),
                new NeuralBoolean("4", false),
                new NeuralBoolean("5", false),
                new NeuralBoolean("6", false),
                new NeuralBoolean("7", false),
                new NeuralBoolean("8", false),
                new NeuralBoolean("9", false)
        );
        styleLayers.setAll(
                new NeuralBoolean("relu1_1", true),
                new NeuralBoolean("relu1_2", false),
                new NeuralBoolean("relu2_1", true),
                new NeuralBoolean("relu2_2", false),
                new NeuralBoolean("relu3_1", true),
                new NeuralBoolean("relu3_2", false),
                new NeuralBoolean("relu3_3", false),
                new NeuralBoolean("relu3_4", false),
                new NeuralBoolean("relu4_1", true),
                new NeuralBoolean("relu4_2", false),
                new NeuralBoolean("relu4_3", false),
                new NeuralBoolean("relu4_4", false),
                new NeuralBoolean("relu5_1", true),
                new NeuralBoolean("relu5_2", false),
                new NeuralBoolean("relu5_3", false),
                new NeuralBoolean("relu5_4", false),
                new NeuralBoolean("relu6", false),
                new NeuralBoolean("relu7", false)
        );
        contentLayers.setAll(
                new NeuralBoolean("relu1_1", false),
                new NeuralBoolean("relu1_2", false),
                new NeuralBoolean("relu2_1", false),
                new NeuralBoolean("relu2_2", false),
                new NeuralBoolean("relu3_1", false),
                new NeuralBoolean("relu3_2", false),
                new NeuralBoolean("relu3_3", false),
                new NeuralBoolean("relu3_4", false),
                new NeuralBoolean("relu4_1", false),
                new NeuralBoolean("relu4_2", true),
                new NeuralBoolean("relu4_3", false),
                new NeuralBoolean("relu4_4", false),
                new NeuralBoolean("relu5_1", false),
                new NeuralBoolean("relu5_2", false),
                new NeuralBoolean("relu5_3", false),
                new NeuralBoolean("relu5_4", false),
                new NeuralBoolean("relu6", false),
                new NeuralBoolean("relu7", false)
        );
    }

    private void updateLayers(String[] layers) {
        List<NeuralBoolean> newStyleLayers = new ArrayList<>();
        List<NeuralBoolean> newContentLayers = new ArrayList<>();
        for (String layer : layers) {
            newStyleLayers.add(new NeuralBoolean(layer, false));
            newContentLayers.add(new NeuralBoolean(layer, false));
        }
        styleLayers.setAll(newStyleLayers);
        contentLayers.setAll(newContentLayers);
    }

    private void updateNeuralBooleans(String[] selectedNames, ObservableList<NeuralBoolean> existingNames) {
        Set<String> names = new HashSet<>(Arrays.asList(selectedNames));

        // ensure deselected
        for (NeuralBoolean namedSelection : existingNames) {
            if (!names.contains(namedSelection.getName()) && namedSelection.getValue())
                namedSelection.setValue(false);
        }

        if (names.size() > 0) {
            // select
            for (String selectedName : names) {
                boolean existed = false;
                for (NeuralBoolean namedSelection : existingNames) {
                    if (namedSelection.getName().equalsIgnoreCase(selectedName)) {
                        if (!namedSelection.getValue())
                            namedSelection.setValue(true);
                        existed = true;
                        break;
                    }
                }

                // create new for selection if necessary
                if (!existed)
                    existingNames.add(new NeuralBoolean(selectedName, true));
            }
        }
    }

    private List<NeuralImage> updateStyleImageSelections(File[] selectedImages, double[] weights,
                                                         ObservableList<NeuralImage> existingImages) {
        Set<String> names = Arrays.stream(selectedImages).map(File::getName).collect(Collectors.toSet());
        List<NeuralImage> selectedNeuralImages = new ArrayList<>();

        // ensure deselected
        for (NeuralImage image : existingImages) {
            if (!names.contains(image.getName()) && image.isSelected())
                image.setSelected(false);
        }

        if (names.size() > 0) {
            // select
            for (int i = 0; i < selectedImages.length; i++) {
                File selectedImage = selectedImages[i];
                double weight;
                try {
                    weight = weights[i];
                } catch (Exception e) {
                    weight = 1.0;
                }
                boolean existed = false;
                for (NeuralImage neuralImage : existingImages) {
                    if (neuralImage.getName().equalsIgnoreCase(selectedImage.getName())) {
                        if (!neuralImage.isSelected())
                            neuralImage.setSelected(true);
                        if (neuralImage.getWeight() != weight)
                            neuralImage.setWeight(weight);
                        selectedNeuralImages.add(neuralImage);
                        existed = true;
                        break;
                    }
                }

                // create new for selection if necessary
                if (!existed) {
                    NeuralImage neuralImage = new NeuralImage(selectedImage);
                    neuralImage.setSelected(true);
                    neuralImage.setWeight(weight);
                    existingImages.add(neuralImage);
                    selectedNeuralImages.add(neuralImage);
                }
            }

            if (selectedNeuralImages.size() == 1)
                styleImageGrid.selectedItemProperty().set(selectedNeuralImages.get(0));
        }
        return selectedNeuralImages;
    }

    private void updateContentImageSelections(File selectedImage, ObservableList<NeuralImage> existingImages) {
        // ensure deselected
        for (NeuralImage image : existingImages) {
            if (!selectedImage.getName().equals(image.getName()) && image.isSelected())
                image.setSelected(false);
        }

        if (selectedImage != null) {
            NeuralImage selectedNeuralImage = null;
            boolean existed = false;
            for (NeuralImage neuralImage : existingImages) {
                if (neuralImage.getName().equalsIgnoreCase(selectedImage.getName())) {
                    selectedNeuralImage = neuralImage;
                    existed = true;
                    break;
                }
            }

            // create new for selection if necessary
            if (!existed) {
                NeuralImage neuralImage = new NeuralImage(selectedImage);
                selectedNeuralImage = neuralImage;
                existingImages.add(neuralImage);
            }

            // select the new image in the table
            contentImageGrid.selectedItemProperty().set(selectedNeuralImage);
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
                                .equalsIgnoreCase(resources.getString("neuralQueueItemInProgress")))
                        .collect(Collectors.toList());
        if (!inProgressItems.isEmpty())
            return inProgressItems.get(0);

        List<TreeItem<NeuralQueue.NeuralQueueItem>> allItems =
                outputTreeTable.getRoot().getChildren().stream()
                        .filter(queueItem -> !queueItem.getValue().getStatus().getValue()
                                .equalsIgnoreCase(resources.getString("neuralQueueItemFailed")))
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
                        showTooltipNextTo(tooltipRegion, resources.getString("outputImageNullIterations"));
                    else if (tooltipRegion != null && inProgressImages.length <= 0)
                        showTooltipNextTo(tooltipRegion, resources.getString("outputImageNoIterations"));
                    return null;
                }
            } else {
                log.log(Level.FINER, "Output Image: no output selection nor latest image");
                if (tooltipRegion != null)
                    showTooltipNextTo(tooltipRegion, resources.getString("outputImageNullIterations"));
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
                        showTooltipNextTo(tooltipRegion, resources.getString("outputImageNullIterations"));
                    else if (tooltipRegion != null && outputChildren.isEmpty())
                        showTooltipNextTo(tooltipRegion, resources.getString("outputImageNoIterations"));
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
                    showTooltipNextTo(tooltipRegion, resources.getString("outputImageNullIterations"));
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

    private void loadStyle(NeuralStyleV3 loadedNeuralStyle) {
        neuralStyle.loadNeuralStyle(loadedNeuralStyle);

        // Retrieve these before paths because that will change them
        File[] selectedStyleImages = neuralStyle.getStyleImages();
        double[] selectedStyleWeights = neuralStyle.getStyleWeights();
        File contentImage = neuralStyle.getContentImage();
        String[] selectedGpuIndices = neuralStyle.getGpu();
        String[] selectedStyleLayers = neuralStyle.getStyleLayers();
        String[] selectedContentLayers = neuralStyle.getContentLayers();

        if (selectedStyleImages != null)
            styleMultipleSelect.setSelected(selectedStyleImages.length != 1);

        // Set selected layers after updating layers from paths
        updateNeuralBooleans(selectedGpuIndices, this.gpuIndices);
        updateNeuralBooleans(selectedStyleLayers, this.styleLayers);
        updateNeuralBooleans(selectedContentLayers, this.contentLayers);

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
                    NeuralStyleV3 loadedStyle = FileUtils.loadStyle(file);
                    if (loadedStyle == null)
                        showTooltipNextTo(loadStyleButton, resources.getString("loadStyleFailed"));
                    else {
                        loadStyle(loadedStyle);
                        showTooltipNextTo(loadStyleButton, resources.getString("loadStyleSuccess"));
                    }
                });
                break;

            case NeuralQueue.QUEUED_IMAGE:
                queueItem.setActionCallback(() -> {
                    neuralStyle.getInit().setValue("image");
                    neuralStyle.getInitImage().setValue(file.getAbsolutePath());
                    showTooltipNextTo(initImage, resources.getString("outputTreeTableInitTooltip"));
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

    private void openImageInTab(File imageFile) {
        Tab imageTab = new Tab(imageFile.getName());
        FullImageView imagePreview = new FullImageView(
                imageFile,
                e -> updateStyleImageSelections(new File[]{imageFile}, new double[]{1.0}, styleImages),
                e -> updateContentImageSelections(imageFile, contentImages),
                e -> neuralStyle.getInitImage().setValue(imageFile.getAbsolutePath()),
                resources
        );
        imageTab.setContent(imagePreview);
        tabs.getTabs().add(imageTab);
        tabs.getSelectionModel().selectLast();
    }

    private void checkInjections() {
        assert maxIter != null : "fx:id=\"maxIter\" was not injected.";
        assert saveStyleButton != null : "fx:id=\"saveStyleButton\" was not injected.";
        assert loadStyleButton != null : "fx:id=\"loadStyleButton\" was not injected.";
        assert tabs != null : "fx:id=\"tabs\" was not injected.";
        assert inputTab != null : "fx:id=\"inputTab\" was not injected.";
        assert outputTab != null : "fx:id=\"outputTab\" was not injected.";
        assert layersTab != null : "fx:id=\"layersTab\" was not injected.";
        assert styleFolderPath != null : "fx:id=\"styleFolderPath\" was not injected.";
        assert contentFolderPath != null : "fx:id=\"contentFolderPath\" was not injected.";
        assert outputName != null : "fx:id=\"outputName\" was not injected.";
        assert styleFolderButton != null : "fx:id=\"styleFolderButton\" was not injected.";
        assert contentFolderButton != null : "fx:id=\"contentFolderButton\" was not injected.";
        assert outputImageButton != null : "fx:id=\"outputImageButton\" was not injected.";
        assert styleMultipleSelect != null : "fx:id=\"styleMultipleSelect\" was not injected.";
        assert styleImageGrid != null : "fx:id=\"styleImageGrid\" was not injected.";
        assert contentImageGrid != null : "fx:id=\"contentImageGrid\" was not injected.";
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
        assert gpuTable != null : "fx:id=\"gpuTable\" was not injected.";
        assert gpuTableSelected != null : "fx:id=\"gpuTableSelected\" was not injected.";
        assert gpuTableIndex != null : "fx:id=\"gpuTableIndex\" was not injected.";
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
                new Observable[] {gpuIndex.valueProperty()});
        styleLayers = FXCollections.observableArrayList(neuralLayer ->
                new Observable[] {neuralLayer.valueProperty(), neuralLayer.nameProperty()});
        contentLayers = FXCollections.observableArrayList(neuralLayer ->
                new Observable[] {neuralLayer.valueProperty(), neuralLayer.nameProperty()});

        setDefaultNeuralBooleans();
    }

    private void setupButtonListeners() {
        log.log(Level.FINER, "Setting Style Save listener.");
        EventStreams.eventsOf(saveStyleButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing save style file chooser.");
            FileView.fileChooser.setTitle(resources.getString("saveStyleChooser"));
            File styleFile = FileView.fileChooser.showSaveDialog(stage);
            log.log(Level.FINE, "Style file chosen: {0}", styleFile);
            if (styleFile != null) {
                FileView.fileChooser.setInitialDirectory(styleFile.getParentFile());
                File savedStyle = FileUtils.saveOutputStyle(neuralStyle.getNeuralStyle(), styleFile);
                if (savedStyle == null)
                    showTooltipNextTo(saveStyleButton, resources.getString("saveStyleFailed"));
                else
                    showTooltipNextTo(saveStyleButton, resources.getString("saveStyleSuccess"));
            }
        });

        log.log(Level.FINER, "Setting Style Load listener.");
        EventStreams.eventsOf(loadStyleButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing save style file chooser.");
            FileView.fileChooser.setTitle(resources.getString("loadStyleChooser"));
            File styleFile = FileView.fileChooser.showOpenDialog(stage);
            log.log(Level.FINE, "Style file chosen: {0}", styleFile);
            if (styleFile != null) {
                FileView.fileChooser.setInitialDirectory(styleFile.getParentFile());
                NeuralStyleV3 loadedStyle = FileUtils.loadStyle(styleFile);
                if (loadedStyle == null)
                    showTooltipNextTo(loadStyleButton, resources.getString("loadStyleFailed"));
                else {
                    loadStyle(loadedStyle);
                    showTooltipNextTo(loadStyleButton, resources.getString("loadStyleSuccess"));
                }
            }
        });

        log.log(Level.FINER, "Setting Style Folder listener.");
        EventStreams.eventsOf(styleFolderButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing style folder chooser.");
            DirectoryView.directoryChooser.setTitle(resources.getString("styleFolderChooser"));
            File styleFolder = DirectoryView.directoryChooser.showDialog(stage);
            log.log(Level.FINE, "Style folder chosen: {0}", styleFolder);
            if (styleFolder != null) {
                setStyleFolder(styleFolder);
            }
        });

        log.log(Level.FINER, "Setting Content Folder listener.");
        EventStreams.eventsOf(contentFolderButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing content folder chooser.");
            DirectoryView.directoryChooser.setTitle(resources.getString("contentFolderChooser"));
            File contentFolder = DirectoryView.directoryChooser.showDialog(stage);
            log.log(Level.FINE, "Content folder chosen: {0}", contentFolder);
            if (contentFolder != null) {
                setContentFolder(contentFolder);
            }
        });

        log.log(Level.FINER, "Setting Output Image listener.");
        EventStreams.eventsOf(outputImageButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINE, "Output Image button hit, checking images.");

            // Check for generated image iterations to show
            File outputFolder = new File(neuralStyle.getOutputFolder().getValue());

            if (outputFolder == null) {
                showTooltipNextTo(outputImageButton, resources.getString("outputImageNoOutputFolder"));
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
                    showTooltipNextTo(outputImageButton, resources.getString("outputImageNoSavedImage"));
                } else {
                    showTooltipNextTo(outputImageButton,
                            resources.getString("outputImageSavedImage") + "\n" + savedFiles[0].getName());
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
            NeuralStyleV3 style = neuralStyle.getNeuralStyle();
            if (style.checkArguments()) {
                style.generateUniqueName();
                String[] command = style.buildCommand();
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
                showTooltipNextTo(commandButton, resources.getString("commandButtonInvalid"));
            }
        });

        log.log(Level.FINER, "Setting Fit View listener.");
        EventStreams.eventsOf(imageViewModeFit, ActionEvent.ACTION).subscribe(actionEvent ->
                outputImageView.fitToView());

        log.log(Level.FINER, "Setting Actual Size listener.");
        EventStreams.eventsOf(imageViewModeActual, ActionEvent.ACTION).subscribe(actionEvent ->
                outputImageView.scaleImageViewport(1));

        log.log(Level.FINER, "Setting Style Layer Add listener.");
        EventStreams.eventsOf(styleLayerAdd, ActionEvent.ACTION).subscribe(
                actionEvent -> styleLayers.add(new NeuralBoolean("newLayer", false)));

        log.log(Level.FINER, "Setting Style Layer Remove listener.");
        EventStreams.eventsOf(styleLayerRemove, ActionEvent.ACTION).subscribe(
                actionEvent -> styleLayers.removeAll(styleLayersTable.getSelectionModel().getSelectedItems()));

        log.log(Level.FINER, "Setting Content Layer Add listener.");
        EventStreams.eventsOf(contentLayerAdd, ActionEvent.ACTION).subscribe(
                actionEvent -> contentLayers.add(new NeuralBoolean("newLayer", false)));

        log.log(Level.FINER, "Setting Content Layer Remove listener.");
        EventStreams.eventsOf(contentLayerRemove, ActionEvent.ACTION).subscribe(
                actionEvent -> contentLayers.removeAll(contentLayersTable.getSelectionModel().getSelectedItems()));
    }

    private void setupFieldListeners() {
        // useful to keep sliders synced to text fields
        StringConverter<Number> intConverter = NeuralInt.INT_CONVERTER;
        StringConverter<Number> doubleConverter = NeuralDouble.DOUBLE_CONVERTER;

        chainLength.linkToInt(neuralStyle.getChainLength());
        maxIter.linkToInt(neuralStyle.getIterations());
        printIter.linkToInt(neuralStyle.getIterationsPrint());
        saveIter.linkToInt(neuralStyle.getIterationsSave());
        sizeOutput.linkToInt(neuralStyle.getOutputSize());
        sizeStyle.linkToDouble(neuralStyle.getStyleSize());
        seed.linkToInt(neuralStyle.getSeed());
        weightContent.linkToInt(neuralStyle.getContentWeight());
        weightStyle.linkToInt(neuralStyle.getStyleWeight());
        init.link(neuralStyle.getInit());

        initImage.link(neuralStyle.getInitImage());
        EventStreams.valuesOf(neuralStyle.getInit().valueProperty()).subscribe(init -> {
            boolean notInitImage = !"image".equalsIgnoreCase(init);
            initImage.setDisable(notInitImage);
            if (notInitImage) neuralStyle.getInitImage().setValue("");
        });

        initImageViewLoader = new AsyncImageProperty(350, 350);
        initImageView.imageProperty().bind(initImageViewLoader);
        EventStreams.valuesOf(neuralStyle.getInitImage().valueProperty()).subscribe(newInitImagePath -> {
            if (newInitImagePath == null || newInitImagePath.isEmpty()) {
                initImageView.setVisible(false);
            } else {
                initImageView.setVisible(true);
                initImageViewLoader.imageFileProperty().set(new File(newInitImagePath));
            }
        });

        originalColors.link(neuralStyle.getOriginalColors());
        normalizeGradients.link(neuralStyle.getNormalizeGradients());
        tvWeight.linkToDouble(neuralStyle.getTvWeight());
        pooling.link(neuralStyle.getPooling());

        cpu.link(neuralStyle.getCpu());
        multiGpuStrategy.link(neuralStyle.getMultiGpuStrategy());
        EventStreams.valuesOf(neuralStyle.getCpu().valueProperty()).subscribe(useCpu -> {
            gpuTable.setDisable(useCpu);
            multiGpuStrategy.setDisable(useCpu);
        });

        backend.link(neuralStyle.getBackend());
        autotune.link(neuralStyle.getAutotune());
        EventStreams.valuesOf(neuralStyle.getBackend().valueProperty()).subscribe(backend -> {
            if ("cudnn".equalsIgnoreCase(backend)) {
                autotune.setDisable(false);
            } else {
                autotune.setDisable(true);
                neuralStyle.getAutotune().setValue(false);
            }
        });

        optimizer.link(neuralStyle.getOptimizer());
        nCorrection.linkToDouble(neuralStyle.getnCorrection());
        learningRate.linkToDouble(neuralStyle.getLearningRate());

        EventStreams.valuesOf(neuralStyle.getOptimizer().valueProperty()).subscribe(optimizer -> {
            if ("adam".equalsIgnoreCase(optimizer)) {
                nCorrection.setDisable(true);
                learningRate.setDisable(false);
                neuralStyle.getnCorrection().reset();
            } else {
                nCorrection.setDisable(false);
                learningRate.setDisable(true);
                neuralStyle.getLearningRate().reset();
            }
        });

        thFile.link(neuralStyle.getThPath());
        neuralStyleFolder.link(neuralStyle.getNeuralStylePath());
        protoFile.link(neuralStyle.getProtoFile());
        modelFile.link(neuralStyle.getModelFile());
        outputFolder.link(neuralStyle.getOutputFolder());
        workingFolder.link(NeuralStyleWrapper.workingFolder);

        neuralStyle.getProtoFile().valueProperty().addListener((observable, oldValue, value) -> {
            if (value != null && !value.isEmpty()) {
                File pFile = new File(value);
                File parentFile = pFile.getParentFile();
                if (FileUtils.checkFolderExists(parentFile))
                    FileView.fileChooser.setInitialDirectory(parentFile);

                String[] newLayers = FileUtils.parseLoadcaffeProto(pFile);

                if (newLayers == null) {
                    showTooltipNextTo(protoFile, resources.getString("protoFileInvalid"));
                    updateLayers(new String[]{});
                } else if (newLayers.length <= 0) {
                    showTooltipNextTo(protoFile, resources.getString("protoFileNoLayers"));
                    updateLayers(new String[]{});
                } else {
                    showTooltipNextTo(protoFile, resources.getString("protoFileNewLayers"));
                    updateLayers(newLayers);
                }
            } else {
                setDefaultNeuralBooleans();
            }
        });

    }

    private void setupServiceListeners() {
        // handle each Worker.State
        log.log(Level.FINER, "Setting state listener.");
        EventStreams.valuesOf(neuralService.stateProperty()).subscribe(state -> {
            switch (state) {
                case SCHEDULED:
                    log.log(Level.FINER, "Neural service: Scheduled.");
                    statusLabel.setText(resources.getString("neuralServiceStatusScheduled"));
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                    progress.setProgress(0);
                    break;
                case READY:
                    log.log(Level.FINER, "Neural service: Ready.");
                    statusLabel.setText(resources.getString("neuralServiceStatusReady"));
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                    break;
                case RUNNING:
                    log.log(Level.FINER, "Neural service: Running.");
                    statusLabel.setText(resources.getString("neuralServiceStatusRunning"));
                    startButton.setDisable(true);
                    stopButton.setDisable(false);
                    break;
                case SUCCEEDED:
                    log.log(Level.FINER, "Neural service: Succeeded.");
                    statusLabel.setText(resources.getString("neuralServiceStatusFinished"));
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                    progress.setProgress(100);
                    break;
                case CANCELLED:
                    log.log(Level.FINER, "Neural service: Cancelled.");
                    statusLabel.setText(resources.getString("neuralServiceStatusCancelled"));
                    startButton.setDisable(false);
                    stopButton.setDisable(true);
                    break;
                case FAILED:
                    log.log(Level.FINER, "Neural service: Failed.");
                    statusLabel.setText(resources.getString("neuralServiceStatusFailed"));
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

    private void setupStyleImageGrid() {
        log.log(Level.FINER, "Setting style image grid.");
        styleImageGrid.setItems(styleImages);

        log.log(Level.FINER, "Setting style image grid multi-selection listener.");
        EventStreams.changesOf(styleMultipleSelect.selectedProperty()).subscribe(selectedChange -> {
            if (!selectedChange.getNewValue()) {
                for (NeuralImage neuralImage : styleImages)
                    if (neuralImage.isSelected()) neuralImage.setSelected(false);
            }
        });

        log.log(Level.FINER, "Setting style image grid selection listener.");
        EventStreams.changesOf(styleImageGrid.selectedItemProperty())
                .subscribe(neuralImageChange -> {
                    if (!styleMultipleSelect.isSelected()) {
                        NeuralImage oldNeuralImage = neuralImageChange.getOldValue();
                        if (oldNeuralImage != null)
                            oldNeuralImage.setSelected(false);
                    }
                    NeuralImage newNeuralImage = neuralImageChange.getNewValue();
                    if (newNeuralImage != null)
                        newNeuralImage.setSelected(true);
                });

        log.log(Level.FINER, "Setting style image grid selection listener.");
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

        log.log(Level.FINER, "Setting style image grid column factory.");
        styleImageGrid.setCellFactory(v -> {
            final NeuralImageCell neuralImageCell = new NeuralImageCell(styleMultipleSelect.selectedProperty());
            Cellable<NeuralImage> cell = new Cellable<NeuralImage>() {
                @Override
                public void updateItem(NeuralImage item, boolean empty) {
                    neuralImageCell.setNeuralImage(item);
                }

                @Override
                public void actionItem(NeuralImage item, MouseButton mouseButton) {
                    switch (mouseButton) {
                        case PRIMARY:
                            styleImageGrid.selectedItemProperty().set(item);
                            break;
                        case MIDDLE:
                            openImageInTab(item.getImageFile());
                            break;
                        case SECONDARY:
                            neuralStyle.getInitImage().setValue(item.getImageFile().getAbsolutePath());
                            break;
                    }
                }
            };
            return new CellNode<>(neuralImageCell, cell);
        });
    }

    private void setupContentImageGrid() {
        log.log(Level.FINER, "Setting content image grid.");
        contentImageGrid.setItems(contentImages);

        log.log(Level.FINER, "Setting content image grid selection listener.");
        EventStreams.changesOf(contentImageGrid.selectedItemProperty())
                .subscribe(neuralImageChange -> {
                    log.log(Level.FINE, "contentImage changed");

                    NeuralImage oldNeuralImage = neuralImageChange.getOldValue();
                    if (oldNeuralImage != null)
                        oldNeuralImage.setSelected(false);

                    NeuralImage newNeuralImage = neuralImageChange.getNewValue();
                    if (newNeuralImage != null) {
                        newNeuralImage.setSelected(true);
                        neuralStyle.setContentImage(newNeuralImage.getImageFile());
                    } else {
                        neuralStyle.setContentImage(null);
                    }

                    toggleStyleButtons();
                });

        log.log(Level.FINER, "Setting content image grid column factory.");
        contentImageGrid.setCellFactory(v -> {
            final NeuralImageCell neuralImageCell = new NeuralImageCell(null);
            Cellable<NeuralImage> cell = new Cellable<NeuralImage>() {
                @Override
                public void updateItem(NeuralImage item, boolean empty) {
                    neuralImageCell.setNeuralImage(item);
                }

                @Override
                public void actionItem(NeuralImage item, MouseButton mouseButton) {
                    switch (mouseButton) {
                        case PRIMARY:
                            contentImageGrid.selectedItemProperty().set(item);
                            break;
                        case MIDDLE:
                            openImageInTab(item.getImageFile());
                            break;
                        case SECONDARY:
                            neuralStyle.getInitImage().setValue(item.getImageFile().getAbsolutePath());
                            break;
                    }
                }
            };
            return new CellNode<>(neuralImageCell, cell);
        });
    }

    private void setupGpuIndexTable() {
        log.log(Level.FINER, "Setting GPU index table list.");
        gpuTable.setItems(gpuIndices);
        gpuTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        log.log(Level.FINER, "Setting GPU index table selection listener.");
        EventStreams.changesOf(gpuIndices).subscribe(change -> {
            log.log(Level.FINE, "gpuIndices changed");

            List<NeuralBoolean> selectedGpuIndices = gpuIndices.stream()
                    .filter(NeuralBoolean::getValue)
                    .collect(Collectors.toList());

            String[] newGpuIndices = new String[selectedGpuIndices.size()];
            for (int i = 0; i < selectedGpuIndices.size(); i++)
                newGpuIndices[i] = selectedGpuIndices.get(i).getName();
            neuralStyle.setGpu(newGpuIndices);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting GPU index table shortcut listener");
        EventStreams.eventsOf(gpuTable, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            ObservableList<NeuralBoolean> selectedGpuIndices =
                    gpuTable.getSelectionModel().getSelectedItems();
            for (NeuralBoolean gpuIndex : selectedGpuIndices)
                gpuIndex.setValue(!gpuIndex.getValue());
        });

        log.log(Level.FINER, "Setting GPU index table column factories.");
        gpuTableSelected.setCellValueFactory(new PropertyValueFactory<>("value"));
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

            List<NeuralBoolean> selectedStyleLayers = styleLayers.stream()
                    .filter(NeuralBoolean::getValue)
                    .collect(Collectors.toList());

            String[] newStyleLayers = new String[selectedStyleLayers.size()];
            for (int i = 0; i < selectedStyleLayers.size(); i++)
                newStyleLayers[i] = selectedStyleLayers.get(i).getName();
            neuralStyle.setStyleLayers(newStyleLayers);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting style layer table shortcut listener");
        EventStreams.eventsOf(styleLayersTable, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            ObservableList<NeuralBoolean> selectedStyleLayers =
                    styleLayersTable.getSelectionModel().getSelectedItems();
            for (NeuralBoolean neuralLayer : selectedStyleLayers)
                neuralLayer.setValue(!neuralLayer.getValue());
        });

        log.log(Level.FINER, "Setting style layer table column factories.");
        styleLayersTableSelected.setCellValueFactory(new PropertyValueFactory<>("value"));
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

            List<NeuralBoolean> selectedContentLayers = contentLayers.stream()
                    .filter(NeuralBoolean::getValue)
                    .collect(Collectors.toList());

            String[] newContentLayers = new String[selectedContentLayers.size()];
            for (int i = 0; i < selectedContentLayers.size(); i++)
                newContentLayers[i] = selectedContentLayers.get(i).getName();
            neuralStyle.setContentLayers(newContentLayers);

            toggleStyleButtons();
        });

        log.log(Level.FINER, "Setting style layer table shortcut listener");
        EventStreams.eventsOf(contentLayersTable, KeyEvent.KEY_RELEASED).filter(spaceBar::match).subscribe(keyEvent -> {
            ObservableList<NeuralBoolean> selectedStyleLayers =
                    contentLayersTable.getSelectionModel().getSelectedItems();
            for (NeuralBoolean neuralLayer : selectedStyleLayers)
                neuralLayer.setValue(!neuralLayer.getValue());
        });

        log.log(Level.FINER, "Setting content layer table column factories.");
        contentLayersTableSelected.setCellValueFactory(new PropertyValueFactory<>("value"));
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
                new Callback<>() {
                    @Override
                    public TreeTableCell<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem>
                    call(TreeTableColumn<NeuralQueue.NeuralQueueItem, NeuralQueue.NeuralQueueItem> param) {
                        return new TreeTableCell<>() {
                            Button button;
                            Subscription subscribe;

                            {
                                button = new Button();
                                setText(null);
                                setGraphic(null);
                            }

                            @Override
                            public void updateItem(NeuralQueue.NeuralQueueItem queueItem, boolean empty) {
                                super.updateItem(queueItem, empty);
                                if (subscribe != null) {
                                    subscribe.unsubscribe();
                                    subscribe = null;
                                }
                                if (empty || queueItem == null) {
                                    setText(null);
                                    setGraphic(null);
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
                new Callback<>() {
                    @Override
                    public TreeTableCell<NeuralQueue.NeuralQueueItem, String>
                    call(TreeTableColumn<NeuralQueue.NeuralQueueItem, String> param) {
                        return new TreeTableCell<>() {
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
                                            status.equalsIgnoreCase(resources.getString("neuralQueueItemQueued"))) {
                                        final ContextMenu cellMenu = new ContextMenu();

                                        final MenuItem cancelMenuItem =
                                                new MenuItem(resources.getString("neuralQueueItemCancel"));
                                        cancelMenuItem.setOnAction(event ->
                                                queueItem.changeStatus(NeuralStyleV2.CANCELLED));

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
