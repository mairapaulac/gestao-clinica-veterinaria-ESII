package br.edu.clinica.clinicaveterinaria.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Button btnInicio;
    @FXML private ImageView logoImageView;
    @FXML private Button btnPacientes;
    @FXML private Button btnAgendamentos;
    @FXML private Button btnEstoque;
    @FXML private Button btnFuncionarios;
    @FXML private Button btnRelatorios;
    @FXML private Button btnFinanceiro;
    @FXML private Button btnSair;


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
    private void handleMenuClick() {
        // lógica de navegação (ex: trocar telas)
    }

    @FXML
    private void sair() {
        System.exit(0);
    }

}
