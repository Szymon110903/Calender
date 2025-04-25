package org.example;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class CalendarView extends VBox {
    private DatePicker datePicker;
    private Text selectedDateText;
    private TaskBoard taskBoard;
    private TextField taskInput;
    private Button addTaskButton;
    private int userid;
    private DatabaseDao database;

    public CalendarView(int userid, DatabaseDao dao, TaskBoard taskBoard) {
        this.taskBoard = taskBoard;
        datePicker = new DatePicker();
        selectedDateText = new Text("Wybierz datę");
        taskInput = new TextField();
        taskInput.setPromptText("Wpisz nowe zadanie");
        addTaskButton = new Button("Dodaj zadanie");
        this.database = dao;
        this.userid = userid;

        addTaskButton.setOnAction(e -> {
            String taskText = taskInput.getText().trim();
            String selectedColumn = "To Do";
            if (!taskText.isEmpty()) {
                database.addTask(userid, taskText, datePicker.getValue().toString(), selectedColumn);

                taskBoard.addTaskToColumn(selectedColumn, new Task(taskText));
                taskInput.clear();
            }
        });

        datePicker.setOnAction(e -> {
            selectedDateText.setText("Wybrano: " + datePicker.getValue());
            updateTasksForSelectedDate(datePicker.getValue().toString());
            taskBoard.setVisible(true);
        });

        taskBoard.setVisible(false);

        this.getChildren().addAll(datePicker, selectedDateText, taskInput, addTaskButton);
        this.setSpacing(10);
        this.setStyle("-fx-padding: 20px;");
    }

    private void updateTasksForSelectedDate(String selectedDate) {
        database.ensureDayExists(userid, selectedDate);

        taskBoard.clearColumns();

        List<Task> toDoTasks = database.getTasksForDateAndColumn(selectedDate,userid, "To Do");
        List<Task> inProgressTasks = database.getTasksForDateAndColumn(selectedDate,userid, "In Progress");
        List<Task> doneTasks = database.getTasksForDateAndColumn(selectedDate,userid, "Done");

        for (Task task : toDoTasks) {
            taskBoard.addTaskToColumn("To Do", task);
        }
        for (Task task : inProgressTasks) {
            taskBoard.addTaskToColumn("In Progress", task);
        }
        for (Task task : doneTasks) {
            taskBoard.addTaskToColumn("Done", task);
        }
        taskBoard.setContext(database, userid, selectedDate);

        taskBoard.setVisible(true);
    }

    }
