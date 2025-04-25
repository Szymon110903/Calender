package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginScene {

    private final DatabaseDao dao;

    public LoginScene(DatabaseDao dao) {
        this.dao = dao;
    }

    public Scene create(Stage stage) {
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Zaloguj się");
        Button registerButton = new Button("Załóż konto");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red");

        VBox layout = new VBox(10,
                new Label("Login:"), usernameField,
                new Label("Hasło:"), passwordField,
                new HBox(10, loginButton, registerButton),
                errorLabel
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            try {
                int userId = dao.login(username, password);
                if (userId != -1) {
                    TaskBoard taskBoard = new TaskBoard();
                    CalendarView calendarView = new CalendarView(userId, dao, taskBoard);

                    BorderPane root = new BorderPane();
                    root.setTop(calendarView);
                    root.setCenter(taskBoard);
                    root.setStyle("-fx-background-color: #f0f0f0;");
                    root.setPadding(new Insets(10));

                    Scene mainScene = new Scene(root);
                    stage.setScene(mainScene);
                    stage.setMaximized(true);
                } else {
                    dao.read();
                    errorLabel.setText("Niepoprawny login lub hasło.");
                }
            } catch (Exception ex) {
                errorLabel.setText("Błąd logowania");
                ex.printStackTrace(); // Możesz usunąć w wersji produkcyjnej
            }
        });

        registerButton.setOnAction(e -> {
            RegistrationScene registrationScene = new RegistrationScene(dao);
            stage.setScene(registrationScene.create(stage));
        });

        return new Scene(layout, 300, 250);
    }
}
