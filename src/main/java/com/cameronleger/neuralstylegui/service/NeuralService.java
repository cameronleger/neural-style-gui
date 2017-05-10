package com.cameronleger.neuralstylegui.service;

import com.cameronleger.neuralstyle.NeuralStyle;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NeuralService extends Service<Integer> {
    private static final Logger log = Logger.getLogger(NeuralService.class.getName());
    private static final Pattern iterationPattern = Pattern.compile("Iteration (\\d+) / (\\d+)");
    private NeuralStyle neuralStyle;

    private static class Iteration {
        private int progress;
        private int total;

        Iteration(int progress, int total) {
            this.progress = progress;
            this.total = total;
        }

        int getProgress() {
            return progress;
        }

        void setProgress(int progress) {
            this.progress = progress;
        }

        int getTotal() {
            return total;
        }

        void setTotal(int total) {
            this.total = total;
        }
    }

    public NeuralStyle getNeuralStyle() {
        return neuralStyle;
    }

    public void setNeuralStyle(NeuralStyle neuralStyle) {
        this.neuralStyle = neuralStyle;
    }

    public void addLogHandler(Handler handler) {
        log.addHandler(handler);
    }

    private static Iteration parseIteration(String logLine) {
        if (logLine == null)
            return null;
        Matcher matcher = iterationPattern.matcher(logLine);
        if (!matcher.matches())
            return null;

        Iteration i = new Iteration(-1, -1);
        i.setProgress(Integer.parseInt(matcher.group(1)));
        i.setTotal(Integer.parseInt(matcher.group(2)));
        return i;
    }

    @Override
    protected Task<Integer> createTask() {
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

        return new Task<Integer>() {
            @Override protected Integer call() throws InterruptedException {
                updateMessage("Starting neural-style.");
                log.log(Level.FINE, "Starting neural-style process.");

                int exitCode = -1;
                String line;
                ProcessBuilder builder = new ProcessBuilder(buildCommand);
                builder.directory(neuralStyleForTask.getNeuralStylePath());
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
                            Iteration i = parseIteration(line.trim());
                            if (i != null) {
                                int progress = i.getProgress();
                                if (progress != -1) {
                                    int total = i.getTotal();
                                    if (total <= 0)
                                        total = neuralStyleForTask.getIterations();
                                    updateProgress(progress, total);
                                }
                            }

                            // Kill the task if stopped by user
                            if (isCancelled()) {
                                input.close();
                                p.destroy();
                                return null;
                            }
                        }
                        input.close();
                    } catch (IOException e) {
                        log.log(Level.SEVERE, e.toString(), e);
                        input.close();
                    }

                    exitCode = p.waitFor();
                    log.log(Level.FINE, String.format("Neural-style process exit code: %s", exitCode));
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.toString(), e);
                }

                if (exitCode != 0)
                    throw new RuntimeException("Exit Code: " + String.valueOf(exitCode));
                return exitCode;
            }
        };
    }
}
