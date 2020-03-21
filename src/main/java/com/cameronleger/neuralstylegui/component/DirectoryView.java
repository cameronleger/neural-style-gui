package com.cameronleger.neuralstylegui.component;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstylegui.model.properties.NeuralString;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import org.reactfx.EventStreams;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryView extends HBox {

    private static final Logger log = Logger.getLogger(DirectoryView.class.getName());

    public static DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button fileButton;

    @FXML
    private TextField filePathText;

    @FXML
    private Button clearButton;

    @FXML
    private Button resetButton;

    private NeuralString property;

    private String title = "";
    private String tooltip = "";

    public DirectoryView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fileView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        assert fileButton != null : "fx:id=\"fileButton\" was not injected: check your FXML file 'fileView.fxml'.";
        assert filePathText != null : "fx:id=\"filePathText\" was not injected: check your FXML file 'fileView.fxml'.";
        assert clearButton != null : "fx:id=\"clearButton\" was not injected: check your FXML file 'fileView.fxml'.";
        assert resetButton != null : "fx:id=\"resetButton\" was not injected: check your FXML file 'fileView.fxml'.";
        clearButton.setTooltip(new Tooltip("Clear"));
        clearButton.setOnAction(e -> this.property.setValue(""));
        resetButton.setTooltip(new Tooltip("Reset to Default"));
        resetButton.setOnAction(e -> this.property.reset());
        EventStreams.eventsOf(fileButton, ActionEvent.ACTION).subscribe(actionEvent -> {
            log.log(Level.FINER, "Showing directory chooser.");
            directoryChooser.setTitle(title);
            if (!filePathText.getText().isEmpty())
                directoryChooser.setInitialDirectory(new File(filePathText.getText()));
            File chosenFolder = directoryChooser.showDialog(null);
            log.log(Level.FINE, "directory chosen: {0}", chosenFolder);
            if (chosenFolder != null) {
                filePathText.setText(chosenFolder.getAbsolutePath());
                File parentFile = chosenFolder.getParentFile();
                if (FileUtils.checkFolderExists(parentFile))
                    directoryChooser.setInitialDirectory(parentFile);
            }
        });
    }

    public void link(NeuralString property) {
        this.property = property;
        fileButton.setText(property.getPrettyName());
        filePathText.textProperty().bindBidirectional(property.valueProperty());
    }

    public String getTooltip() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
        Tooltip tt = new Tooltip(tooltip);
        tt.setWrapText(true);
        tt.setMaxWidth(300);
        fileButton.setTooltip(tt);
        filePathText.setTooltip(tt);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        filePathText.setPromptText(title);
    }
}
