package com.cameronleger.neuralstylegui;

import com.cameronleger.neuralstyle.Image;
import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NeuralService extends Service {
    private static final Logger log = Logger.getLogger(NeuralService.class.getName());
    private static final Pattern iterationPattern = Pattern.compile("Iteration (\\d+) / (\\d+)");
    private NeuralStyle neuralStyle;

    NeuralStyle getNeuralStyle() {
        return neuralStyle;
    }

    void setNeuralStyle(NeuralStyle neuralStyle) {
        this.neuralStyle = neuralStyle;
    }

    void addLogHandler(Handler handler) {
        log.addHandler(handler);
    }

    private static int parseIterationProgress(String logLine) {
        int progress = -1;
        if (logLine == null)
            return progress;
        Matcher matcher = iterationPattern.matcher(logLine);
        if (matcher.matches())
            progress = Integer.parseInt(matcher.group(1));
        return progress;
    }

    private static int parseIterationTotal(String logLine) {
        int total = -1;
        if (logLine == null)
            return total;
        Matcher matcher = iterationPattern.matcher(logLine);
        if (matcher.matches())
            total = Integer.parseInt(matcher.group(2));
        return total;
    }

    @Override
    protected Task<File> createTask() {
        log.log(Level.FINE, "Getting neural style for task.");
        final NeuralStyle neuralStyleForTask = getNeuralStyle();

        log.log(Level.FINE, "Checking that style is valid.");
        if (neuralStyleForTask == null)
            return null;
        log.log(Level.FINE, "Checking that style can be run with arguments.");
        if (!neuralStyleForTask.checkArguments())
            return null;

        log.log(Level.FINE, "Generating run command.");
        final String[] buildCommand = neuralStyleForTask.buildCommand();
        for (String buildCommandPart : buildCommand)
            log.log(Level.FINE, buildCommandPart);

        return new Task<File>() {
            @Override protected File call() throws InterruptedException {
                updateMessage("Starting neural-style.");
                log.log(Level.FINE, "Starting neural-style process.");

                int exitCode = -1;
                String line;
                ProcessBuilder builder = new ProcessBuilder(buildCommand);
                builder.directory(NeuralStyle.getNeuralStylePath());
                builder.redirectErrorStream(true);

                try {
                    Process p = builder.start();
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    log.log(Level.FINE, "Gathering input.");
                    try {
                        while ((line = input.readLine()) != null) {
                            // Log progress
                            log.log(Level.INFO, line);

                            // Check for an iteration progress update
                            int progress = parseIterationProgress(line.trim());
                            if (progress != -1) {
                                int total = parseIterationTotal(line.trim());
                                if (total <= 0)
                                    total = neuralStyleForTask.getIterations();
                                updateProgress(progress, total);
                            }

                            // Kill the task if stopped by user
                            if (isCancelled()) {
                                p.destroy();
                                return null;
                            }
                        }
                        input.close();
                    } catch (IOException e) {
                        log.log(Level.SEVERE, e.toString(), e);
                    }

                    exitCode = p.waitFor();
                    log.log(Level.FINE, String.format("Neural-style process exit code: %s", exitCode));
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.toString(), e);
                }

                if (exitCode != 1)
                    throw new RuntimeException("Exit Code: " + String.valueOf(exitCode));
                return neuralStyleForTask.getOutputImage();
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
