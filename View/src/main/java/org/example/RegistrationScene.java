package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegistrationScene {

    private final DatabaseDao dao;

    public RegistrationScene(DatabaseDao dao) {
        this.dao = dao;
    }

    public Scene create(Stage stage) {
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        Button registerButton = new Button("Zarejestruj się");
        Button backButton = new Button("Wróć");
        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red");

        VBox layout = new VBox(10,
                new Label("Login:"), usernameField,
                new Label("Hasło:"), passwordField,
                new Label("Powtórz hasło:"), confirmPasswordField,
                registerButton,
                backButton,
                messageLabel
        );
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        registerButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("Login i hasło nie mogą być puste.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Hasła nie są takie same.");
                return;
            }

            try {
                boolean success = dao.register(username, password);
                if (success) {
                    messageLabel.setStyle("-fx-text-fill: green");
                    messageLabel.setText("Rejestracja udana! Możesz się zalogować.");
                } else {
                    messageLabel.setStyle("-fx-text-fill: red");
                    messageLabel.setText("Użytkownik już istnieje.");
                }
            } catch (Exception ex) {
                messageLabel.setText("Błąd rejestracji: " + ex.getMessage());
                ex.printStackTrace(); // do logów
            }
        });

        backButton.setOnAction(e -> {
            LoginScene loginScene = new LoginScene(dao);
            stage.setScene(loginScene.create(stage));
        });

        return new Scene(layout, 300, 300);
    }
}
