package com.cameronleger.neuralstylegui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MovingImageView {
    private ImageView imageView;
    private double width, height;
    private double scale = 1;

    public MovingImageView(ImageView imageView) {
        this.imageView = imageView;
        setupListeners();
    }

    private void setupListeners() {
        ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

        imageView.setOnMousePressed(e -> {
            Point2D mousePress = imageViewToImage(new Point2D(e.getX(), e.getY()));
            mouseDown.set(mousePress);
        });

        imageView.setOnMouseDragged(e -> {
            Point2D dragPoint = imageViewToImage(new Point2D(e.getX(), e.getY()));
            shift(dragPoint.subtract(mouseDown.get()));
            mouseDown.set(imageViewToImage(new Point2D(e.getX(), e.getY())));
        });

        imageView.setOnScroll(e -> {
            if (e.getDeltaY() < 0)
                scale = Math.min(scale + 0.25, 3);
            else
                scale = Math.max(scale - 0.25, 0.25);
            scaleImageViewport(scale);
        });

        imageView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                fitToView();
            }
        });
    }

    public void setImage(Image image) {
        imageView.setImage(image);
        if (width != image.getWidth() || height != image.getHeight()) {
            width = image.getWidth();
            height = image.getHeight();
            fitToView();
        }
    }

    public void fitToView() {
        Bounds layoutBounds = imageView.getLayoutBounds();
        if (width > height)
            scale = width / layoutBounds.getWidth();
        else
            scale = height / layoutBounds.getHeight();
        scaleImageViewport(scale);
    }

    public void scaleImageViewport(double scale) {
        this.scale = scale;
        Bounds layoutBounds = imageView.getLayoutBounds();
        double layoutWidth = layoutBounds.getWidth();
        double layoutHeight = layoutBounds.getHeight();

        // center the image's x&y
        double newWidth = layoutWidth * scale;
        double newHeight = layoutHeight * scale;
        double offsetX = (this.width - newWidth) / 2;
        double offsetY = (this.height - newHeight) / 2;

        imageView.setViewport(new Rectangle2D(offsetX, offsetY,
                layoutWidth * scale, layoutHeight * scale));
    }

    private void shift(Point2D delta) {
        Rectangle2D viewport = imageView.getViewport();

        imageView.setViewport(new Rectangle2D(
                viewport.getMinX() - delta.getX(), viewport.getMinY() - delta.getY(),
                viewport.getWidth(), viewport.getHeight()));
    }

    private Point2D imageViewToImage(Point2D imageViewCoordinates) {
        double xProportion = imageViewCoordinates.getX() / imageView.getBoundsInLocal().getWidth();
        double yProportion = imageViewCoordinates.getY() / imageView.getBoundsInLocal().getHeight();

        Rectangle2D viewport = imageView.getViewport();
        return new Point2D(
                viewport.getMinX() + xProportion * viewport.getWidth(),
                viewport.getMinY() + yProportion * viewport.getHeight());
    }
}
