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
        // Load the new FXML root
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlFileName)));

        // Preserve fullscreen state
        boolean isFullScreen = primaryStage.isFullScreen();

        // Set the new root on the existing scene
        primaryScene.setRoot(root);
        primaryStage.setTitle(title);

        // Re-apply fullscreen state
        primaryStage.setFullScreen(isFullScreen);
    }
}
