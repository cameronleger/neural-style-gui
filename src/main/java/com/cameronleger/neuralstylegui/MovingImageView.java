package com.cameronleger.neuralstylegui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MovingImageView {
    private ImageView imageView;
    private double width, height;
    private double[] scales = new double[] {0.25, 0.5, 1, 1.5, 2, 4};
    private int scale = 2;

    public MovingImageView(ImageView imageView) {
        this.imageView = imageView;

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
                scale = Math.min(scale + 1, scales.length - 1);
            else
                scale = Math.max(scale - 1, 0);

            double newWidth = width * scales[scale];
            double newHeight = height * scales[scale];

            double offsetX = (width - newWidth) / 2;
            double offsetY = (height - newHeight) / 2;

            imageView.setViewport(new Rectangle2D(offsetX, offsetY, newWidth, newHeight));
        });

        imageView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                reset();
            }
        });
    }

    public void setImage(Image image) {
        imageView.setImage(image);
        if (width != image.getWidth() || height != image.getHeight()) {
            width = image.getWidth();
            height = image.getHeight();
            reset();
        }
    }

    public void reset() {
        imageView.setViewport(new Rectangle2D(0, 0, width, height));
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
