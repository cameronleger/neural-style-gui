package com.cameronleger.neuralstylegui.listwrapview;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.FlowPane;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class ListWrapView<T> extends FlowPane {

    private static final Logger log = Logger.getLogger(ListWrapView.class.getName());

    private DataModel<T> dataModel;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    public ListWrapView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/listWrapView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        dataModel = new DataModel<>();
        Bindings.bindContentBidirectional(getChildren(), dataModel.getFlowNodes());
    }

    @FXML
    void initialize() {

    }

    public void setItems(ObservableList<T> items) {
        items.addListener(dataModel);
    }

    public void setCellFactory(Callback<Void, CellNode<T>> cellFactory) {
        assert Platform.isFxApplicationThread();
        dataModel.setCellFactory(cellFactory);
    }

    public SimpleObjectProperty<T> selectedItemProperty() {
        return dataModel.selectedItemProperty();
    }

}
