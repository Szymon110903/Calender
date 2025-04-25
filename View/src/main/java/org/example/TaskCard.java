package org.example;

import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

public class TaskCard extends Label {
    private final Task task;

    public TaskCard(Task task) {
        super(task.toString());
        this.task = task;

        this.setWrapText(true);
        this.setMaxWidth(Double.MAX_VALUE);
        this.setPrefWidth(30);

        this.setStyle("""
            -fx-border-color: black;
            -fx-padding: 5px;
            -fx-background-color: #e0e0e0;
            -fx-background-radius: 5px;
        """);

        setupDrag();
    }

    private void setupDrag() {
        this.setOnDragDetected(e -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(task.toString());
            db.setContent(content);
            ((javafx.scene.layout.VBox) this.getParent()).getChildren().remove(this);
            e.consume();
        });
    }

    public Task getTask() {
        return task;
    }
}
