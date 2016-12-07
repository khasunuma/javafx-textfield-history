package jp.coppermine.tools.javafx.history;

import static java.util.stream.Collectors.toList;
import static javafx.geometry.Orientation.VERTICAL;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class HistoryView {
    
    /**
     * Representation of {@link ListView} that consists to {@code HistoryView}.
     */
    private final ListView<String> history;
    
    /**
     * Maximum numbers of items contains {@code HistoryView}.
     */
    private final long count;
    
    /**
     * Representation of {@link TextField} that is attached with {@code HistoryView}
     */
    private TextField textField;
    
    /**
     * Representation of keyword supplier.
     * 
     * @see Supplier
     */
    private Supplier<List<String>> keywordSupplier;
    
    public HistoryView() {
        history = new ListView<>();
        history.setOpacity(1.0);
        history.setOrientation(VERTICAL);
        history.setOnMousePressed(this::onMousePressedAction);
        history.setItems(FXCollections.observableArrayList());
        history.setVisible(false);
        count = Long.getLong("jp.coppermine.tools.javafx.history.items", 10);
    }
    
    /**
     * Attach a history view on a text field.
     * 
     * @param parent the parent pane of the text field, not null
     * @param textField the text field that is attached a history view, not null
     * @param keywordSupplier keyword supplier, not null
     * @return an instance of {@code HistoryView}, never null
     */
    public HistoryView attach(Pane parent, TextField textField, Supplier<List<String>> keywordSupplier) {
        history.setLayoutX(textField.getLayoutX());
        history.setLayoutY(textField.getLayoutY() + textField.getPrefHeight());
        history.setPrefWidth(textField.getPrefWidth());
        parent.getChildren().add(history);
        
        this.textField = textField;
        textField.setOnKeyReleased(this::onTextFieldKeyReleasedAction);
        textField.setOnMouseClicked(this::onTextFieldMouseClickedAction);
        textField.textProperty().addListener(
                (observable, oldValue, newValue) -> history.setItems(
                        FXCollections.observableList(getKeywords().stream()
                                .filter(s -> s.startsWith(newValue))
                                .limit(count)
                                .collect(toList()))
                        )
                );
        
        this.keywordSupplier = keywordSupplier;
        
        return this;
    }
    
    /**
     * Attach a history view on a text field.
     * <p>
     * It's same as call {@code attach(getParentPane(textField), textField, keywordSupplier)}.
     * 
     * @param textField the text field that is attached a history view, not null
     * @param keywordSupplier keyword supplier, not null
     * @return an instance of {@code HistoryView}, never null
     */
    public HistoryView attach(TextField textField, Supplier<List<String>> keywordSupplier) {
        return attach(getParentPane(textField), textField, keywordSupplier);
    }
    
    /**
     * Obtains the parent pane of the node.
     * 
     * @param node the target, not null
     * @return the parent pane of the node, never null
     */
    public Pane getParentPane(Node node) {
        while (node.getParent() instanceof Pane) {
            return (Pane) node.getParent();
        }
        return getParentPane(node);
    }
    
    /**
     * Obtains keywords in the history.
     * 
     * @return list of keywords in the history, never null
     */
    private List<String> getKeywords() {
        return keywordSupplier.get().stream()
                                .distinct()
                                .collect(toList());
    }

    /**
     * Mouse pressed action on {@code HistoryView}.
     * 
     * @param event a mouse event
     */
    private void onMousePressedAction(MouseEvent event) {
        Optional.ofNullable(history.getSelectionModel().getSelectedItem())
                    .ifPresent(textField::setText);
        history.setVisible(false);
    } 
    
    /**
     * Mouse clicked action on {@code TextField}.
     * 
     * @param event a mouse event
     */
    private void onTextFieldMouseClickedAction(MouseEvent event) {
        IntStream.of(event.getClickCount())
                    .limit(1)
                    .filter(i -> i >= 2)
                    .forEach(i -> showItems(!history.isVisible()));
    }
    
    /**
     * Key released action on {@code TextField}.
     * 
     * @param event a key event
     */
    private void onTextFieldKeyReleasedAction(KeyEvent event) {
        int items = history.getItems().size();
        int index = history.getSelectionModel().getSelectedIndex();
        
        switch (event.getCode()) {
        case DOWN:
            showItems(true);
            IntStream.of(items)
                        .filter(i -> i > 0)
                        .map(i -> (index + 1) % i)
                        .findFirst()
                        .ifPresent(history.getSelectionModel()::select);
            break;
        case UP:
            showItems(true);
            IntStream.of(items)
                        .filter(i -> i > 0)
                        .map(i -> (i + index - 1) % i)
                        .findFirst()
                        .ifPresent(history.getSelectionModel()::select);
            break;
        case ENTER:
            IntStream.of(items)
                        .filter(i -> i > 0)
                        .findFirst()
                        .ifPresent(
                                i -> Optional.ofNullable(history.getSelectionModel().getSelectedItem())
                                                .ifPresent(textField::setText));
            // fall through
        case ESCAPE:
            history.setVisible(false);
            break;
        default:
            showItems(!textField.getText().isEmpty());
            break;
        }
    }
    
    /**
     * Switch to show items of {@code HistoryView}.
     * 
     * @param visible {@code true} if it's visible, otherwise {@code false}
     */
    private void showItems(boolean visible) {
        if (visible) {
            history.setItems(
                    FXCollections.observableList(getKeywords().stream()
                            .filter(s -> !s.trim().isEmpty())
                            .filter(textField.getText()::startsWith)
                            .limit(count)
                            .collect(toList())));
            history.setPrefHeight((history.getItems().size() * 20.0 + 2.0));
            history.setVisible(!history.getItems().isEmpty());
        } else {
            history.setVisible(false);
        }
    }
}
