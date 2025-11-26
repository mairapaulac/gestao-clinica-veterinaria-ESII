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
        SessionManager.TipoUsuario tipoUsuario = SessionManager.getTipoUsuario();
        
        if (tipoUsuario == null) {
            return;
        }
        
        switch (tipoUsuario) {
            case VETERINARIO:
                configurarPermissoesVeterinario();
                break;
            case FUNCIONARIO:
                configurarPermissoesFuncionario();
                break;
            case ADMINISTRADOR:
                configurarPermissoesAdministrador();
                break;
        }
    }
    
    private void configurarPermissoesVeterinario() {
        // Veterinário: apenas Início, Pacientes e Agendamentos
        
        // Sidebar - ocultar opções sem acesso
        btnEstoque.setVisible(false);
        btnEstoque.setManaged(false);
        btnFuncionarios.setVisible(false);
        btnFuncionarios.setManaged(false);
        btnFinanceiro.setVisible(false);
        btnFinanceiro.setManaged(false);
        btnRelatorios.setVisible(false);
        btnRelatorios.setManaged(false);
        
        // Cards - todos visíveis, mas alguns desabilitados
        // Cards com acesso
        habilitarCard(cardPacientes);
        habilitarCard(cardAgendamentos);
        
        // Cards sem acesso - desabilitar
        desabilitarCard(cardEstoque);
        desabilitarCard(cardFuncionarios);
        desabilitarCard(cardFinanceiro);
        desabilitarCard(cardRelatorios);
    }
    
    private void configurarPermissoesFuncionario() {
        // Funcionário não-admin: tudo exceto Relatórios
        
        // Sidebar - ocultar opções sem acesso
        btnRelatorios.setVisible(false);
        btnRelatorios.setManaged(false);
        
        // Cards - todos visíveis, mas alguns desabilitados
        // Cards com acesso
        habilitarCard(cardPacientes);
        habilitarCard(cardAgendamentos);
        habilitarCard(cardEstoque);
        habilitarCard(cardFuncionarios);
        habilitarCard(cardFinanceiro);
        
        // Cards sem acesso - desabilitar
        desabilitarCard(cardRelatorios);
    }
    
    private void configurarPermissoesAdministrador() {
        // Administrador: tudo
        
        // Sidebar - todas visíveis
        btnEstoque.setVisible(true);
        btnEstoque.setManaged(true);
        btnFuncionarios.setVisible(true);
        btnFuncionarios.setManaged(true);
        btnFinanceiro.setVisible(true);
        btnFinanceiro.setManaged(true);
        btnRelatorios.setVisible(true);
        btnRelatorios.setManaged(true);
        
        // Cards - todos habilitados
        habilitarCard(cardPacientes);
        habilitarCard(cardAgendamentos);
        habilitarCard(cardEstoque);
        habilitarCard(cardFuncionarios);
        habilitarCard(cardFinanceiro);
        habilitarCard(cardRelatorios);
    }
    
    private void desabilitarCard(VBox card) {
        if (card == null) return;
        
        // Remover evento de clique
        card.setOnMouseClicked(null);
        
        // Adicionar estilo de desabilitado
        card.getStyleClass().add("card-disabled");
        card.getStyleClass().remove("card");
        
        // Desabilitar cursor pointer
        card.setCursor(javafx.scene.Cursor.DEFAULT);
    }
    
    private void habilitarCard(VBox card) {
        if (card == null) return;
        
        // Restaurar evento de clique
        card.setOnMouseClicked(this::handleCardClick);
        
        // Remover estilo de desabilitado e adicionar estilo normal
        card.getStyleClass().remove("card-disabled");
        if (!card.getStyleClass().contains("card")) {
            card.getStyleClass().add("card");
        }
        
        // Restaurar cursor pointer
        card.setCursor(javafx.scene.Cursor.HAND);
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        Object source = event.getSource();
        
        // Verificar se o card está desabilitado
        if (source instanceof VBox) {
            VBox card = (VBox) source;
            if (card.getStyleClass().contains("card-disabled")) {
                return; // Não fazer nada se o card estiver desabilitado
            }
        }
        
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
