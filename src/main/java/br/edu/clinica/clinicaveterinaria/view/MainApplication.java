package br.edu.clinica.clinicaveterinaria.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/br/edu/clinica/clinicaveterinaria/login-view.fxml")));
        Scene scene = new Scene(root, 1280, 720);

        SceneManager.setPrimaryStage(stage);
        SceneManager.setPrimaryScene(scene);

        stage.setTitle("PetManager - Login");
        stage.setScene(scene);
        stage.setMaximized(true); // Start maximized instead of forced full-screen
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
