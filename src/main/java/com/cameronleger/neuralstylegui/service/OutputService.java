package com.cameronleger.neuralstylegui.service;

import com.cameronleger.neuralstyle.FileUtils;
import com.cameronleger.neuralstyle.NeuralStyleWrapper;
import com.cameronleger.neuralstylegui.model.NeuralQueueFile;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;

public class OutputService extends Service<List<NeuralQueueFile>> {
    private static final Logger log = Logger.getLogger(OutputService.class.getName());

    @Override
    protected Task<List<NeuralQueueFile>> createTask() {
        return new Task<>() {

            private Path primaryPath = null;
            private WatchService watcher = null;
            private WatchKey primaryKey = null;

            @Override protected List<NeuralQueueFile> call() throws InterruptedException {
                updateMessage("Starting OutputService.");
                updateProgress(0, 2);

                final String path = NeuralStyleWrapper.getWorkingFolder().getAbsolutePath();

                log.log(Level.FINER, "Starting to getTempOutputs.");
                List<NeuralQueueFile> currentOutputFiles = Arrays.stream(FileUtils.getTempOutputFiles(path))
                        .map(f -> new NeuralQueueFile(f, NeuralQueueFile.ChangeType.NEW))
                        .collect(Collectors.toList());

                log.log(Level.FINER, "Finished getTempOutputs.");
                updateValue(currentOutputFiles);

                try {
                    this.watcher = FileSystems.getDefault().newWatchService();
                    this.primaryPath = Paths.get(path);
                    this.primaryKey = primaryPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Unable to create a WatchService + Key for: " + path, e);
                    updateMessage("Finished OutputService.");
                    updateProgress(2, 2);
                    return null;
                }

                log.log(Level.FINE, "Monitoring for Changes: " + path);
                for (;;) {
                    if (isCancelled()) {
                        log.log(Level.FINE, "Cancelled Monitoring: " + path);
                        this.primaryKey.cancel();
                        break;
                    }

                    WatchKey key;

                    try {
                        key = watcher.take();
                    } catch (InterruptedException e) {
                        log.log(Level.INFO, "Interrupted, cancelling");
                        cancel();
                        continue;
                    }

                    Set<String> updateFilePaths = new HashSet<>();
                    List<NeuralQueueFile> updatedOutputFiles = new ArrayList<>();

                    List<WatchEvent<?>> watchEvents = key.pollEvents();
                    for (WatchEvent<?> event : watchEvents) {
                        WatchEvent.Kind<?> kind = event.kind();
                        NeuralQueueFile.ChangeType changeType = NeuralQueueFile.ChangeType.UNKNOWN;
                        if (kind == ENTRY_CREATE)
                            changeType = NeuralQueueFile.ChangeType.NEW;
                        else if (kind == ENTRY_MODIFY)
                            changeType = NeuralQueueFile.ChangeType.MODIFIED;
                        else if (kind == ENTRY_DELETE)
                            changeType = NeuralQueueFile.ChangeType.DELETED;

                        if (changeType == NeuralQueueFile.ChangeType.UNKNOWN) {
                            log.log(Level.WARNING, "Unexpected WatchEvent.Kind: " + kind.name());
                            continue;
                        }

                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> e = (WatchEvent<Path>) event;

                        Path eventPath = primaryPath.resolve(e.context());
                        File file = eventPath.toFile();
                        String absolutePath = file.getAbsolutePath();
                        if (!updateFilePaths.contains(absolutePath)) {
                            updateFilePaths.add(absolutePath);
                            updatedOutputFiles.add(new NeuralQueueFile(absolutePath, changeType));
                        }
                    }

                    Thread.sleep(100);

                    if (!updatedOutputFiles.isEmpty()) {
                        updateMessage("Polled OutputService.");
                        updateProgress(1, 2);
                        updateValue(updatedOutputFiles);
                    }

                    boolean valid = this.primaryKey.reset();
                    if (!valid) {
                        cancel();
                    }
                }

                updateMessage("Finished OutputService.");
                updateProgress(2, 2);
                return null;
            }
        };
    }
}
