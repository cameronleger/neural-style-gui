package com.cameronleger.neuralstylegui;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class NvidiaService extends Service {
    private static final Logger log = Logger.getLogger(NvidiaService.class.getName());
    private static final Pattern totalPattern = Pattern.compile("Total\\s+:\\s+(\\d+)\\s+MiB");
    private static final Pattern usedPattern = Pattern.compile("Used\\s+:\\s+(\\d+)\\s+MiB");
    private int device = 0;

    public void setDevice(int device) {
        this.device = device;
    }

    private static int parseTotalMemory(String logLine) {
        int total = -1;
        if (logLine == null)
            return total;
        Matcher matcher = totalPattern.matcher(logLine);
        if (matcher.matches())
            total = Integer.parseInt(matcher.group(1));
        return total;
    }

    private static int parseUsedMemory(String logLine) {
        int used = -1;
        if (logLine == null)
            return used;
        Matcher matcher = usedPattern.matcher(logLine);
        if (matcher.matches())
            used = Integer.parseInt(matcher.group(1));
        return used;
    }

    @Override
    protected Task<Integer> createTask() {
        log.log(Level.FINER, "Generating run command.");
        final String[] buildCommand = new String[] {
                "nvidia-smi",
                "-i",
                String.valueOf(device),
                "-q",
                "-d",
                "MEMORY"
        };
        for (String buildCommandPart : buildCommand)
            log.log(Level.FINER, buildCommandPart);

        return new Task<Integer>() {
            @Override protected Integer call() throws InterruptedException {
                updateMessage("Starting nvidia-smi.");
                log.log(Level.FINER, "Starting nvidia-smi process.");

                int exitCode = -1;
                String line;
                ProcessBuilder builder = new ProcessBuilder(buildCommand);
                builder.redirectErrorStream(true);

                try {
                    Process p = builder.start();
                    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    int totalMemory = -1;
                    int usedMemory = -1;

                    log.log(Level.FINER, "Gathering input.");
                    try {
                        while ((line = input.readLine()) != null && (totalMemory == -1 || usedMemory == -1)) {
                            // Log progress
                            log.log(Level.FINER, line);

                            // Check for total or used memory
                            int potentialTotalMemory = parseTotalMemory(line.trim());
                            if (potentialTotalMemory != -1)
                                totalMemory = potentialTotalMemory;
                            int potentialUsedMemory = parseUsedMemory(line.trim());
                            if (potentialUsedMemory != -1)
                                usedMemory = potentialUsedMemory;

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

                    if (totalMemory != -1 && usedMemory != -1) {
                        updateProgress(usedMemory, totalMemory);
                    }

                    exitCode = p.waitFor();
                    log.log(Level.FINER, String.format("nvidia-smi process exit code: %s", exitCode));
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
