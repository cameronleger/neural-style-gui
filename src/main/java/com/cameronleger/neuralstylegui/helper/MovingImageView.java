package com.cameronleger.neuralstylegui.helper;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovingImageView {
    private static final Logger log = Logger.getLogger(MovingImageView.class.getName());
    private ImageView imageView;
    private String imageFilePath;
    private double width, height;
    private double scale = 1;

    public MovingImageView(ImageView imageView) {
        this.imageView = imageView;
        setupListeners();
    }

    private void setupListeners() {
        ObjectProperty<Point2D> mouseDown = new SimpleObjectProperty<>();

        EventStreams.eventsOf(imageView, MouseEvent.MOUSE_PRESSED).subscribe(e -> {
            Point2D mousePress = imageViewToImage(new Point2D(e.getX(), e.getY()));
            mouseDown.set(mousePress);
        });

        EventStreams.eventsOf(imageView, MouseEvent.MOUSE_DRAGGED).subscribe(e -> {
            Point2D dragPoint = imageViewToImage(new Point2D(e.getX(), e.getY()));
            shift(dragPoint.subtract(mouseDown.get()));
            mouseDown.set(imageViewToImage(new Point2D(e.getX(), e.getY())));
        });

        EventStream<ScrollEvent> scrollEvents = EventStreams.eventsOf(imageView, ScrollEvent.SCROLL);
        EventStream<ScrollEvent> scrollEventsUp = scrollEvents.filter(scrollEvent -> scrollEvent.getDeltaY() < 0);
        EventStream<ScrollEvent> scrollEventsDown = scrollEvents.filter(scrollEvent -> scrollEvent.getDeltaY() > 0);
        scrollEventsUp.subscribe(scrollEvent -> scale = Math.min(scale + 0.25, 3));
        scrollEventsDown.subscribe(scrollEvent -> scale = Math.max(scale - 0.25, 0.25));
        EventStreams.merge(scrollEventsUp, scrollEventsDown).subscribe(scrollEvent -> scaleImageViewport(scale));

        EventStreams.eventsOf(imageView, MouseEvent.MOUSE_CLICKED)
                .filter(mouseEvent -> mouseEvent.getClickCount() == 2)
                .subscribe(mouseEvent -> fitToView());
    }

    public void setImage(File imageFile) {
        if (imageFile == null || imageFile.getAbsolutePath().equals(imageFilePath))
            return;

        Image image;
        try {
            image = new Image(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            log.log(Level.SEVERE, e.toString(), e);
            return;
        }
        imageFilePath = imageFile.getAbsolutePath();
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
