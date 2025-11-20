package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.FuncionarioDAO;
import br.edu.clinica.clinicaveterinaria.dao.VeterinarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import br.edu.clinica.clinicaveterinaria.view.SceneManager;
import br.edu.clinica.clinicaveterinaria.view.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private ImageView logoImageView;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();

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
            MainApplication.showErrorAlert("Erro de Carregamento", "Não foi possível carregar a imagem do logo.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String login = emailField.getText().trim();
        String senha = passwordField.getText();

        if (login.isEmpty() || senha.isEmpty()) {
            mostrarAlerta("Erro de Validação", "Por favor, preencha todos os campos.");
            return;
        }

        try {
            boolean loginFuncionario = funcionarioDAO.verificar_login_funcionario(login, senha);
            
            if (loginFuncionario) {
                Funcionario funcionario = funcionarioDAO.buscarPorLogin(login);
                
                if (funcionario != null) {
                    SessionManager.setFuncionarioLogado(funcionario);
                    try {
                        SceneManager.switchScene("/br/edu/clinica/clinicaveterinaria/home-screen-view.fxml", "PetManager - Main");
                    } catch (IOException e) {
                        MainApplication.showErrorAlert("Erro de Carregamento", "Não foi possível carregar a tela principal.");
                        e.printStackTrace();
                    }
                    return;
                }
            }

            Veterinario veterinario = veterinarioDAO.buscarPorEmail(login);
            
            if (veterinario != null && veterinario.getSenha() != null && veterinario.getSenha().equals(senha)) {
                SessionManager.setVeterinarioLogado(veterinario);
                try {
                    SceneManager.switchScene("/br/edu/clinica/clinicaveterinaria/home-screen-view.fxml", "PetManager - Main");
                } catch (IOException e) {
                    MainApplication.showErrorAlert("Erro de Carregamento", "Não foi possível carregar a tela principal.");
                    e.printStackTrace();
                }
                return;
            }

            mostrarAlerta("Credenciais Inválidas", "Credenciais inválidas. Tente novamente.");
            passwordField.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro de Banco de Dados", "Erro ao verificar credenciais. Tente novamente.");
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}