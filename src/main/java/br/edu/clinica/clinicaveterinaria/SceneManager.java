package br.edu.clinica.clinicaveterinaria;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxmlFileName, String title, int width, int height) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlFileName)));
        Scene scene = new Scene(root, width, height);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void switchScene(String fxmlFileName, String title) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(fxmlFileName)));
        Scene scene = new Scene(root);
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
