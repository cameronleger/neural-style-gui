package com.cameronleger.neuralstylegui.listwrapview;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.util.Callback;

import java.util.ArrayList;

class DataModel<T> implements ListChangeListener<T> {

    private final ArrayList<Entry<T>> itemNodes = new ArrayList<>();

    private final ObservableList<Node> flowNodes;

    private Callback<Void, CellNode<T>> cellFactory;

    private SimpleObjectProperty<T> selectedItem;

    public DataModel() {
        flowNodes = FXCollections.observableArrayList();
        cellFactory = null;
        selectedItem = new SimpleObjectProperty<>();
    }

    public void onChanged(Change<? extends T> c) {
        while (c.next()) {
            for (T item : c.getRemoved()) {
                removeItemsNode(item);
            }
            ObservableList<? extends T> list = c.getList();
            for (final T item : c.getAddedSubList()) {
                int index = list.indexOf(item);
                addItemsNode(index, item);
            }
        }
    }

    private void removeItemsNode(T item) {
        Entry<T> cellNode = null;

        for (Entry<T> entry : itemNodes) {
            if (entry.it.equals(item)) {
                cellNode = entry;
                break;
            }
        }

        if (cellNode != null) {
            itemNodes.remove(cellNode);
            flowNodes.remove(cellNode.cellNode.getNode());
        }
    }

    private void addItemsNode(int index, final T item) {
        CellNode<T> cellNode;

        cellNode = cellFactory.call(null);
        cellNode.getNode().setOnMouseClicked(e -> cellNode.getCell().actionItem(item, e.getButton()));

        updateItem(item, false, cellNode);
        itemNodes.add(new Entry<T>(item, cellNode));
        flowNodes.add(index, cellNode.getNode());
    }

    private void updateItem(T item, boolean empty, CellNode<T> node) {
        node.getCell().updateItem(item, empty);
    }

    public void setCellFactory(Callback<Void, CellNode<T>> cellFactory) {
        this.cellFactory = cellFactory;
    }

    public ObservableList<Node> getFlowNodes() {
        return flowNodes;
    }

    public SimpleObjectProperty<T> selectedItemProperty() {
        return selectedItem;
    }

    private static class Entry<T> {
        final T it;
        final CellNode<T> cellNode;

        public Entry(T it, CellNode<T> cellNode) {
            this.it = it;
            this.cellNode = cellNode;
        }
    }

}
