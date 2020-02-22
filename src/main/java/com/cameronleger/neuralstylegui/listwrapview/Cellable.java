package com.cameronleger.neuralstylegui.listwrapview;

import javafx.scene.input.MouseButton;

public interface Cellable<T> {
    void updateItem(T item, boolean empty);
    void actionItem(T item, MouseButton mouseButton);
}
