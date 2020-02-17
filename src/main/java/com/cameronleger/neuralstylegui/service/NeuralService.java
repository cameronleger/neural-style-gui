package com.cameronleger.neuralstylegui.service;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyleV2;
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

public class NeuralService extends Service<Integer> {
    private static final Logger log = Logger.getLogger(NeuralService.class.getName());
    private static final Pattern iterationPattern = Pattern.compile("Iteration (\\d+) / (\\d+)");

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

    private static class Workload {
        private NeuralStyleV2 neuralStyle;
        private File file;

        public NeuralStyleV2 getNeuralStyle() {
            return neuralStyle;
        }

        public void setNeuralStyle(NeuralStyleV2 neuralStyle) {
            this.neuralStyle = neuralStyle;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }
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

    private static Workload getNextNeuralStyle() {
        log.log(Level.FINE, "Looking for styles to run.");
        File[] neuralStyleFiles = FileUtils.getTempOutputStyles();
        if (neuralStyleFiles == null) {
            log.log(Level.FINE, "No styles found.");
            return null;
        }

        log.log(Level.FINE, "Checking that style is valid.");
        for (File neuralStyleFile : neuralStyleFiles) {
            NeuralStyleV2 possibleNeuralStyle = FileUtils.loadStyle(neuralStyleFile);

            log.log(Level.FINE, "Checking that style is valid.");
            if (possibleNeuralStyle == null) {
                log.log(Level.FINE, "Unable to load style from file.");
                continue;
            }

            if (possibleNeuralStyle.getQueueStatus() != NeuralStyleV2.QUEUED) {
                log.log(Level.FINE, "Style isn't queued.");
                continue;
            }

            Workload workload = new Workload();
            workload.setNeuralStyle(possibleNeuralStyle);
            workload.setFile(neuralStyleFile);

            if (!possibleNeuralStyle.checkArguments()) {
                log.log(Level.FINE, "Style has invalid arguments.");
                setNeuralStyleQueueStatus(workload, NeuralStyleV2.INVALID_ARGUMENTS);
                return null;
            }

            return workload;
        }

        return null;
    }

    private static void setNeuralStyleQueueStatus(Workload workload, int status) {
        try {
            workload.getNeuralStyle().setQueueStatus(status);
            FileUtils.saveOutputStyle(workload.getNeuralStyle(), workload.getFile());
        } catch (Exception e) {
            log.log(Level.SEVERE, e.toString(), e);
        }
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            private int runNeuralStyleCommand(NeuralStyleV2 neuralStyleForTask) {
                log.log(Level.FINE, "Generating run command.");
                final String[] buildCommand = neuralStyleForTask.buildCommand();
                for (String buildCommandPart : buildCommand)
                    log.log(Level.FINE, buildCommandPart);

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
                            if (isCancelled())
                                p.destroy();
                        }
                    } catch (IOException e) {
                        log.log(Level.SEVERE, e.toString(), e);
                    } finally {
                        input.close();
                    }

                    exitCode = p.waitFor();
                    log.log(Level.FINE, String.format("Neural-style process exit code: %s", exitCode));
                } catch (Exception e) {
                    log.log(Level.SEVERE, e.toString(), e);
                }
                return exitCode;
            }

            @Override protected Integer call() throws InterruptedException {
                log.log(Level.FINE, "Getting neural style for task.");
                Workload workload = getNextNeuralStyle();
                while (workload != null) {
                    updateMessage("Starting neural-style.");
                    log.log(Level.FINE, "Starting neural-style process.");
                    setNeuralStyleQueueStatus(workload, NeuralStyleV2.IN_PROGRESS);

                    int exitCode = runNeuralStyleCommand(workload.getNeuralStyle());

                    if (isCancelled())
                        setNeuralStyleQueueStatus(workload, NeuralStyleV2.CANCELLED);
                    else if (exitCode != 0)
                        setNeuralStyleQueueStatus(workload, NeuralStyleV2.FAILED);
                    else
                        setNeuralStyleQueueStatus(workload, NeuralStyleV2.FINISHED);

                    workload = getNextNeuralStyle();
                }

                log.log(Level.FINE, "No more work to do.");
                return 0;
            }
        };
    }
}
