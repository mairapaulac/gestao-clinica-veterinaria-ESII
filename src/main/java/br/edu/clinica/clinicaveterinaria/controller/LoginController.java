package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.view.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private ImageView logoImageView;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try (InputStream is = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/images/logo.png")) {
            if (is != null) {
                Image logoImage = new Image(is);
                logoImageView.setImage(logoImage);
                logoImageView.setPreserveRatio(true);
            } else {
                System.err.println("Logo not found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.equals("") && password.equals("")) {
            System.out.println("Login successful!");
            try {
                SceneManager.switchScene("/br/edu/clinica/clinicaveterinaria/home-screen-view.fxml", "PetManager - Main");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("login falhou");
        }
    }
}