package org.example;

import javafx.scene.Node;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;

public class TaskBoard extends HBox {
    private VBox todoColumn = new VBox();
    private VBox inProgressColumn = new VBox();
    private VBox doneColumn = new VBox();

    private DatabaseDao database;
    private int userId;
    private String currentDate;

    public void setContext(DatabaseDao database, int userId, String date) {
        this.database = database;
        this.userId = userId;
        this.currentDate = date;
    }

    public TaskBoard() {
        VBox todoBox = createColumnWithHeader("To Do", todoColumn);
        VBox inProgressBox = createColumnWithHeader("In Progress", inProgressColumn);
        VBox doneBox = createColumnWithHeader("Done", doneColumn);

        this.getChildren().addAll(todoBox, inProgressBox, doneBox);
        this.setSpacing(10);
        this.setStyle("-fx-padding: 20px;");

        HBox.setHgrow(todoBox, Priority.ALWAYS);
        HBox.setHgrow(inProgressBox, Priority.ALWAYS);
        HBox.setHgrow(doneBox, Priority.ALWAYS);
    }

    private VBox createColumnWithHeader(String title, VBox column) {
        Label header = new Label(title);
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        header.setMaxWidth(Double.MAX_VALUE);
        header.setStyle(header.getStyle() + " -fx-alignment: center; -fx-text-alignment: center;");

        column.setSpacing(5);
        column.setStyle("-fx-background-color: #f4f4f4; -fx-border-color: gray; -fx-padding: 10px;");

        column.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        column.setOnDragDropped(e -> {
            String content = e.getDragboard().getString();

            Task task = new Task(content);
            TaskCard card = new TaskCard(task);

            column.getChildren().add(card);

            if (database != null && currentDate != null) {
                database.updateTaskStatus(content, currentDate, userId, title);
            }

            e.setDropCompleted(true);
            e.consume();
        });

        VBox wrapper = new VBox();
        wrapper.getChildren().addAll(header, column);
        wrapper.setSpacing(5);
        VBox.setVgrow(column, Priority.ALWAYS);
        return wrapper;
    }

    public void clearColumns() {
        todoColumn.getChildren().clear();
        inProgressColumn.getChildren().clear();
        doneColumn.getChildren().clear();
    }

    public void addTaskToColumn(String column, Task task) {
        TaskCard taskCard = new TaskCard(task);
        switch (column) {
            case "To Do":
                todoColumn.getChildren().add(taskCard);
                break;
            case "In Progress":
                inProgressColumn.getChildren().add(taskCard);
                break;
            case "Done":
                doneColumn.getChildren().add(taskCard);
                break;
        }
    }
}
