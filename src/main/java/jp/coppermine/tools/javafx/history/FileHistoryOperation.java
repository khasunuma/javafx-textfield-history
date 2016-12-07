package jp.coppermine.tools.javafx.history;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Platform;

public interface FileHistoryOperation extends HistoryOperation {
    
    /**
     * Obtains a path of directory.
     * <p>
     * In the default, this value is '${HOME}/.javafx-history-sample'
     * 
     * @return a path of directory contains a history file, never null
     */
    default Path getDirectory() {
        return Paths.get(System.getProperty("user.home"), 
                System.getProperty("jp.coppermine.tools.javafx.history.dir", ".javafx-history-sample"));
    }
    
    /**
     * Obtain a path of a history file.
     * <p>
     * In the default, this value is '${HOME}/.javafx-history-sample/history.txt'
     * 
     * @return a path of a history file, never null
     */
    default Path getPath() {
        return getDirectory().resolve(System.getProperty("jp.coppermine.tools.javafx.history.file", "history.txt"));
    }
    
    @Override
    default void initializeHistory() {
        Platform.runLater(() -> {
            try {
                if (Files.notExists(getDirectory())) {
                    Files.createDirectories(getDirectory());
                }
                if (Files.notExists(getPath())) {
                    Files.createFile(getPath());
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    @Override
    default boolean isValid() {
        return Files.exists(getPath());
    }
    
}
