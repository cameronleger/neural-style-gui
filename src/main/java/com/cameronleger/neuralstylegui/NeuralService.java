package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.Image;
import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class NeuralService extends Service {
    private NeuralStyle neuralStyle;

    public NeuralStyle getNeuralStyle() {
        return neuralStyle;
    }

    public void setNeuralStyle(NeuralStyle neuralStyle) {
        this.neuralStyle = neuralStyle;
    }

    @Override
    protected Task<Image> createTask() {
        if (neuralStyle == null || !neuralStyle.checkArguments())
            return null;

        final NeuralStyle neuralStyleForTask = neuralStyle;
        return new Task<Image>() {
            @Override protected Image call() throws InterruptedException {
                updateMessage("Starting neural-style.");
                updateProgress(0, 10);
                for (int i = 0; i < 10; i++) {
                    if (isCancelled())
                        break;
                    Thread.sleep(300);
                    updateProgress(i + 1, 10);
                }
                return neuralStyleForTask.outputImage;
            }

            @Override protected void succeeded() {
                super.succeeded();
                updateMessage("Success!");
            }

            @Override protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
            }

            @Override protected void failed() {
                super.failed();
                updateMessage("Failed!");
            }
        };
    }
}
