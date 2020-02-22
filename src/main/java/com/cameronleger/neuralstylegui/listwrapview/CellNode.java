package com.cameronleger.neuralstylegui.listwrapview;

import javafx.scene.Node;

public class CellNode<T> {

    private final Node node;
    private final Cellable<T> cell;

    public CellNode(Node node, Cellable<T> cell) {
        this.node = node;
        this.cell = cell;
    }

    public Node getNode() {
        return node;
    }

    public Cellable<T> getCell() {
        return cell;
    }

}
