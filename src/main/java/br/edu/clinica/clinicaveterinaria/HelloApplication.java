package br.edu.clinica.clinicaveterinaria;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setPrimaryStage(stage);
        SceneManager.switchScene("login-view.fxml", "PetManager - Login", 1280, 720);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}