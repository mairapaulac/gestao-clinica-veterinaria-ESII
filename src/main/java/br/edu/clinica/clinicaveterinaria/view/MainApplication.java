package br.edu.clinica.clinicaveterinaria.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        loadPoppinsFont();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/br/edu/clinica/clinicaveterinaria/login-view.fxml")));
        Scene scene = new Scene(root, 1280, 720);

        SceneManager.setPrimaryStage(stage);
        SceneManager.setPrimaryScene(scene);

        stage.setTitle("PetManager - Login");
        stage.setScene(scene);
        stage.setMaximized(true);
        setStageIcon(stage);
        stage.show();
    }
    
    private void loadPoppinsFont() {
        java.util.logging.Logger fontboxLogger = java.util.logging.Logger.getLogger("org.apache.fontbox");
        fontboxLogger.setLevel(java.util.logging.Level.SEVERE);
        
        int fontsLoaded = 0;
        String[] fontFiles = {
            "Poppins-Regular.ttf",
            "Poppins-Bold.ttf",
            "Poppins-SemiBold.ttf",
            "Poppins-Medium.ttf"
        };
        
        for (String fontFile : fontFiles) {
            try (java.io.InputStream fontStream = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/fonts/" + fontFile)) {
                if (fontStream != null) {
                    if (fontStream.available() > 0) {
                        Font font = Font.loadFont(fontStream, 12);
                        if (font != null) {
                            fontsLoaded++;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("⚠ Não foi possível carregar " + fontFile + ": " + e.getMessage());
            }
        }
        
        if (fontsLoaded > 0) {
            System.out.println("✓ Fonte Poppins carregada com sucesso! (" + fontsLoaded + " variantes carregadas)");
        } else {
            System.out.println("⚠ Aviso: Arquivos de fonte Poppins não encontrados ou inválidos. Usando fonte do sistema.");
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

    /**
     * Define o ícone da aplicação em uma janela (Stage)
     * @param stage A janela onde o ícone será definido
     */
    public static void setStageIcon(Stage stage) {
        if (stage == null) {
            return;
        }
        
        // Tenta carregar o ícone .ico primeiro
        boolean icoLoaded = false;
        try {
            java.net.URL iconUrl = MainApplication.class.getResource("/br/edu/clinica/clinicaveterinaria/images/logo-icon.ico");
            if (iconUrl != null) {
                Image icon = new Image(iconUrl.toExternalForm());
                // Verifica se a imagem foi carregada com sucesso
                if (!icon.isError()) {
                    stage.getIcons().clear();
                    stage.getIcons().add(icon);
                    icoLoaded = true;
                }
            }
        } catch (Exception e) {
            // Silenciosamente tenta o fallback
        }
        
        // Se o .ico não funcionou, usa o logo.png como fallback
        if (!icoLoaded) {
            try {
                java.net.URL iconUrl = MainApplication.class.getResource("/br/edu/clinica/clinicaveterinaria/images/logo.png");
                if (iconUrl != null) {
                    Image icon = new Image(iconUrl.toExternalForm());
                    if (!icon.isError()) {
                        stage.getIcons().clear();
                        stage.getIcons().add(icon);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao carregar ícone da aplicação: " + e.getMessage());
            }
        }
    }
}
