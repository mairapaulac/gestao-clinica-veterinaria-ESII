package br.edu.clinica.clinicaveterinaria.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carregar fonte Poppins se disponível
        loadPoppinsFont();
        
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/br/edu/clinica/clinicaveterinaria/login-view.fxml")));
        Scene scene = new Scene(root, 1280, 720);

        SceneManager.setPrimaryStage(stage);
        SceneManager.setPrimaryScene(scene);

        stage.setTitle("PetManager - Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
    
    private void loadPoppinsFont() {
        int fontsLoaded = 0;
        try {
            // Tentar carregar as variantes da fonte Poppins
            java.io.InputStream regularStream = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/fonts/Poppins-Regular.ttf");
            java.io.InputStream boldStream = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/fonts/Poppins-Bold.ttf");
            java.io.InputStream semiBoldStream = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/fonts/Poppins-SemiBold.ttf");
            java.io.InputStream mediumStream = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/fonts/Poppins-Medium.ttf");
            
            if (regularStream != null) {
                Font font = Font.loadFont(regularStream, 12);
                regularStream.close();
                if (font != null) fontsLoaded++;
            }
            if (boldStream != null) {
                Font font = Font.loadFont(boldStream, 12);
                boldStream.close();
                if (font != null) fontsLoaded++;
            }
            if (semiBoldStream != null) {
                Font font = Font.loadFont(semiBoldStream, 12);
                semiBoldStream.close();
                if (font != null) fontsLoaded++;
            }
            if (mediumStream != null) {
                Font font = Font.loadFont(mediumStream, 12);
                mediumStream.close();
                if (font != null) fontsLoaded++;
            }
            
            if (fontsLoaded > 0) {
                System.out.println("✓ Fonte Poppins carregada com sucesso! (" + fontsLoaded + " variantes carregadas)");
            } else {
                System.out.println("⚠ Aviso: Arquivos de fonte Poppins não encontrados. Usando fonte do sistema.");
            }
        } catch (Exception e) {
            System.err.println("⚠ Erro ao carregar fonte Poppins: " + e.getMessage());
            e.printStackTrace();
        }
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
