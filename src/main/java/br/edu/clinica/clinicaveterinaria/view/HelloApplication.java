package br.edu.clinica.clinicaveterinaria.view;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setPrimaryStage(stage);
        SceneManager.switchScene("/br/edu/clinica/clinicaveterinaria/home-screen-view.fxml", "PetManager - Home", 1280, 720);

        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}