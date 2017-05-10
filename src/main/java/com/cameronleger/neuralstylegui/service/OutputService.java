package com.cameronleger.neuralstylegui.service;

import com.cameronleger.neuralstyle.FileUtils;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputService extends Service<Map<String, Set<String>>> {
    private static final Logger log = Logger.getLogger(OutputService.class.getName());

    @Override
    protected Task<Map<String, Set<String>>> createTask() {
        return new Task<Map<String, Set<String>>>() {
            @Override protected Map<String, Set<String>> call() throws InterruptedException {
                updateMessage("Starting OutputService.");
                updateProgress(0, 1);

                log.log(Level.FINER, "Starting to getTempOutputs.");
                Map<String, Set<String>> results = FileUtils.getTempOutputs();
                log.log(Level.FINER, "Finished getTempOutputs.");

                updateMessage("Finished OutputService.");
                updateProgress(1, 1);

                return results;
            }
        };
    }
}
