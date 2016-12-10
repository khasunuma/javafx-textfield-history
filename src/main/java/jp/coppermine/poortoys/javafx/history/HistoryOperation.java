package jp.coppermine.poortoys.javafx.history;

import static java.util.stream.Collectors.toList;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import jp.coppermine.poortoys.history.Command;
import jp.coppermine.poortoys.history.History;

public interface HistoryOperation {

    /**
     * Obtains an instance of {@link History}.
     * 
     * @return an instance of {@code History}, never null
     */
    History getHistory();
    
    /**
     * Initializes the history.
     */
    default void initializeHistory() {
        
    }
    
    /**
     * Obtains if the history is valid.
     * 
     * @return {@code true} if the history is valid, otherwise {@code false}
     */
    default boolean isValid() {
        return true;
    }
    
    /**
     * Remaining of the history amount.
     * <p>
     * In the default, this value is 30 days.
     * 
     * @return Remaining explained {@link TemporalAmount}, never null
     */
    default TemporalAmount remaining() {
        return Duration.ofDays(Integer.getInteger("jp.coppermine.poortoys.javafx.history.remain.days", 30));
    }
    
    /**
     * loads keywords in the history.
     */
    default void loadKeywords() {
        Platform.runLater(() -> {
            getHistory().load();
            getHistory().shrink(LocalDateTime.now().minus(remaining()));
            getHistory().save();
        });
    }
    
    /**
     * Appends or updates a keyword in the history.
     * 
     * @param keyword an updating keyword, not null
     */
    default void updateKeywords(String keyword) {
        Platform.runLater(() -> {
            getHistory().append(Command.of(keyword));
            getHistory().save();
        });
    }
    
    /**
     * Obtains keywords in the history.
     * 
     * @return keywords, never null 
     */
    default List<String> getKeywords() {
        if (isValid()) {
            return getHistory().list().stream().map(e -> e.getCommand()).collect(toList());
        } else {
            return new ArrayList<>();
        }
    }
}
