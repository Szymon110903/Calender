package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        DatabaseDao dao = new DatabaseDao();
        dao.connect();

        LoginScene login = new LoginScene(dao);
        Scene loginScene = login.create(primaryStage); // login scena tworzy przekierowanie

        primaryStage.setTitle("Logowanie");
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
