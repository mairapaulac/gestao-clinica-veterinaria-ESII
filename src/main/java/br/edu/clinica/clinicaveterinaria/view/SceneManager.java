package br.edu.clinica.clinicaveterinaria.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;
    private static Scene primaryScene;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void setPrimaryScene(Scene scene) {
        primaryScene = scene;
    }

    public static void switchScene(String fxmlFileName, String title) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlFileName)));

        boolean isFullScreen = primaryStage.isFullScreen();

        primaryScene.setRoot(root);
        primaryStage.setTitle(title);

        primaryStage.setFullScreen(isFullScreen);
    }
}
