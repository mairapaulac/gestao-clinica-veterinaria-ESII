package br.edu.clinica.clinicaveterinaria.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
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
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(MainApplication::handleException);
        launch(args);
    }

    private static void handleException(Thread t, Throwable e) {
        e.printStackTrace();

        Platform.runLater(() -> {
            if (e instanceof SQLException) {
                showErrorAlert("Erro de Banco de Dados", "Ocorreu um erro ao acessar o banco de dados. Verifique a conexão e tente novamente.");
            } else {
                showErrorAlert("Erro Inesperado", "Ocorreu um erro inesperado. O aplicativo pode precisar ser reiniciado.");
            }
        });
    }

    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
