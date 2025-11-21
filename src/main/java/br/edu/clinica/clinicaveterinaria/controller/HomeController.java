package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.view.SceneManager;
import br.edu.clinica.clinicaveterinaria.view.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.TilePane;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML private Button btnInicio;
    @FXML private Button btnPacientes;
    @FXML private Button btnAgendamentos;
    @FXML private Button btnEstoque;
    @FXML private Button btnFuncionarios;
    @FXML private Button btnRelatorios;
    @FXML private Button btnFinanceiro;
    @FXML private Button btnSair;

    @FXML private ImageView logoImageView;
    @FXML private ImageView doctorImageView;

    @FXML private VBox mainContent;
    @FXML private VBox contentArea;
    @FXML private TilePane cardsGrid;

    @FXML private Label headerTitle;
    @FXML private Label doctorName;
    @FXML private Label doctorRole;

    // Cards
    @FXML private VBox cardPacientes;
    @FXML private VBox cardAgendamentos;
    @FXML private VBox cardEstoque;
    @FXML private VBox cardFuncionarios;
    @FXML private VBox cardRelatorios;
    @FXML private VBox cardFinanceiro;

    private Button activeButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try (InputStream is = getClass().getResourceAsStream("/br/edu/clinica/clinicaveterinaria/images/logo.png")) {
            if (is != null) {
                Image logoImage = new Image(is);
                logoImageView.setImage(logoImage);
                doctorImageView.setImage(logoImage);
            } else {
                System.err.println("Logo not found or path is incorrect.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        setActive(btnInicio);
        atualizarInfoUsuario();
        configurarPermissoes();
    }

    private void atualizarInfoUsuario() {
        String nome = SessionManager.getNomeUsuario();
        String tipo = SessionManager.getTipoUsuarioString();
        
        if (doctorName != null) {
            doctorName.setText(nome);
        }
        if (doctorRole != null) {
            doctorRole.setText(tipo);
        }
    }

    private void configurarPermissoes() {
        boolean isAdmin = SessionManager.isAdministrador();
        boolean isFuncionario = SessionManager.isFuncionario();
        
        btnFuncionarios.setVisible(isAdmin);
        btnFuncionarios.setManaged(isAdmin);
        
        // Faturamento e Pagamento apenas para funcionários
        btnFinanceiro.setVisible(isFuncionario);
        btnFinanceiro.setManaged(isFuncionario);
        cardFinanceiro.setVisible(isFuncionario);
        cardFinanceiro.setManaged(isFuncionario);
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        Object source = event.getSource();
        if (source == cardPacientes) {
            btnPacientes.fire();
        } else if (source == cardAgendamentos) {
            btnAgendamentos.fire();
        } else if (source == cardEstoque) {
            btnEstoque.fire();
        } else if (source == cardFuncionarios) {
            btnFuncionarios.fire();
        } else if (source == cardRelatorios) {
            btnRelatorios.fire();
        } else if (source == cardFinanceiro) {
            btnFinanceiro.fire();
        }
    }

    @FXML
    private void handleMenuClick(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        if (clickedButton == activeButton) return;

        setActive(clickedButton);

        if (clickedButton == btnInicio) {
            contentArea.getChildren().setAll(cardsGrid);
            setHeaderTitle("Clínica Veterinária");
        } else {
            String fxmlFile = "";
            String title = "";
            if (clickedButton == btnPacientes) {
                fxmlFile = "pacientes-view.fxml";
                title = "Pacientes";
            } else if (clickedButton == btnAgendamentos) {
                fxmlFile = "agendamentos-view.fxml";
                title = "Agendamentos";
            } else if (clickedButton == btnEstoque) {
                fxmlFile = "medicamentos-view.fxml";
                title = "Controle de Estoque";
            } else if (clickedButton == btnFuncionarios) {
                fxmlFile = "funcionarios-view.fxml";
                title = "Gestão de Funcionários";
            } else if (clickedButton == btnRelatorios) {
                fxmlFile = "relatorios-view.fxml";
                title = "Relatórios";
            } else if (clickedButton == btnFinanceiro) {
                fxmlFile = "faturamento-view.fxml";
                title = "Faturamento e Pagamento";
            }
            loadPage(fxmlFile);
            setHeaderTitle(title);
        }
    }

    private void loadPage(String fxmlFile) {
        if (fxmlFile == null || fxmlFile.isEmpty()) return;
        try {
            Parent page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/br/edu/clinica/clinicaveterinaria/" + fxmlFile)));
            contentArea.getChildren().setAll(page);
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            e.printStackTrace();
        }
    }

    private void setActive(Button button) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }
        activeButton = button;
        activeButton.getStyleClass().add("active");
    }

    private void setHeaderTitle(String title) {
        headerTitle.setText(title);
    }

    @FXML
    private void sair() {
        try {
            SessionManager.logout();
            SceneManager.switchScene("/br/edu/clinica/clinicaveterinaria/login-view.fxml", "PetManager - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
