package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.FuncionarioDAO;
import br.edu.clinica.clinicaveterinaria.dao.VeterinarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import br.edu.clinica.clinicaveterinaria.model.UsuarioSistema;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import br.edu.clinica.clinicaveterinaria.util.DatabaseErrorHandler;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import br.edu.clinica.clinicaveterinaria.view.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FuncionariosController implements Initializable {

    @FXML private Button btnAdicionar;
    @FXML private TextField txtBuscar;
    @FXML private TableView<UsuarioSistema> tabelaUsuarios;
    @FXML private TableColumn<UsuarioSistema, String> colNome;
    @FXML private TableColumn<UsuarioSistema, String> colTipo;
    @FXML private TableColumn<UsuarioSistema, String> colCargo;
    @FXML private TableColumn<UsuarioSistema, String> colLogin;
    @FXML private TableColumn<UsuarioSistema, String> colGerente;

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private final ObservableList<UsuarioSistema> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<UsuarioSistema> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!SessionManager.isAdministrador()) {
            MainApplication.showErrorAlert("Acesso Negado", "Apenas administradores podem acessar esta funcionalidade.");
            return;
        }

        configurarColunas();
        carregarDadosDoBanco();
        configurarBusca();
        configurarContextMenu();
        btnAdicionar.setOnAction(event -> showUsuarioDialog(null));
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        
        colTipo.setCellValueFactory(cellData -> {
            String tipo = cellData.getValue().getTipo();
            return new SimpleStringProperty(tipo.equals("FUNCIONARIO") ? "Funcionário" : "Veterinário");
        });
        
        colCargo.setCellValueFactory(cellData -> {
            String cargo = cellData.getValue().getCargo();
            return new SimpleStringProperty(cargo != null ? cargo : "-");
        });
        
        colLogin.setCellValueFactory(cellData -> {
            String login = cellData.getValue().getLogin();
            return new SimpleStringProperty(login != null ? login : "-");
        });
        
        colGerente.setCellValueFactory(cellData -> {
            UsuarioSistema usuario = cellData.getValue();
            if (usuario.getTipo().equals("FUNCIONARIO")) {
                return new SimpleStringProperty(usuario.isGerente() ? "Sim" : "Não");
            }
            return new SimpleStringProperty("-");
        });
    }

    private void carregarDadosDoBanco() {
        try {
            listaUsuarios.clear();
            
            // Carregar funcionários
            for (Funcionario funcionario : funcionarioDAO.listarTodos()) {
                listaUsuarios.add(new UsuarioSistema(funcionario));
            }
            
            // Carregar veterinários
            for (Veterinario veterinario : veterinarioDAO.listarTodos()) {
                listaUsuarios.add(new UsuarioSistema(veterinario));
            }
            
            filteredData = new FilteredList<>(listaUsuarios, p -> true);
            tabelaUsuarios.setItems(filteredData);
        } catch (SQLException e) {
            e.printStackTrace();
            String mensagem = DatabaseErrorHandler.getFriendlyMessage(e, "carregar usuários");
            MainApplication.showErrorAlert("Erro de Banco de Dados", mensagem);
        }
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(usuario -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (usuario.getNome() != null && usuario.getNome().toLowerCase().contains(lowerCaseFilter)) ||
                       (usuario.getCargo() != null && usuario.getCargo().toLowerCase().contains(lowerCaseFilter)) ||
                       (usuario.getLogin() != null && usuario.getLogin().toLowerCase().contains(lowerCaseFilter)) ||
                       (usuario.getTipo() != null && usuario.getTipo().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem("Editar");
        MenuItem excluirItem = new MenuItem("Excluir");

        editarItem.setOnAction(event -> handleEditar(tabelaUsuarios.getSelectionModel().getSelectedItem()));
        excluirItem.setOnAction(event -> handleExcluir(tabelaUsuarios.getSelectionModel().getSelectedItem()));

        contextMenu.getItems().addAll(editarItem, excluirItem);

        tabelaUsuarios.setRowFactory(tv -> {
            TableRow<UsuarioSistema> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void showUsuarioDialog(UsuarioSistema usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-funcionario-view.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(usuario == null ? "Cadastrar Novo Usuário" : "Editar Usuário");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAdicionar.getScene().getWindow());
            dialogStage.setScene(new Scene(loader.load()));
            MainApplication.setStageIcon(dialogStage);

            CadastrarFuncionarioController controller = loader.getController();
            controller.setUsuarioData(usuario, listaUsuarios);

            dialogStage.showAndWait();

            UsuarioSistema result = controller.getNewUsuario();
            if (result != null) {
                carregarDadosDoBanco();
            }
        } catch (IOException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro de Aplicação", "Falha ao abrir a tela de cadastro de usuário.");
        }
    }

    private void handleEditar(UsuarioSistema usuario) {
        if (usuario != null) {
            showUsuarioDialog(usuario);
        }
    }

    private void handleExcluir(UsuarioSistema usuario) {
        if (usuario != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir " + usuario.getNome() + "?");
            alert.setContentText("Esta ação não pode ser desfeita.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        if (usuario.getTipo().equals("FUNCIONARIO")) {
                            funcionarioDAO.deletarFuncionario(usuario.getId());
                        } else {
                            veterinarioDAO.deletarVeterinario(usuario.getId());
                        }
                        
                        // Recarregar dados do banco para garantir consistência
                        carregarDadosDoBanco();
                        
                        MainApplication.showSuccessAlert("Sucesso", "Usuário excluído com sucesso!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        String mensagem = DatabaseErrorHandler.getFriendlyMessage(e, "excluir usuário");
                        String titulo = DatabaseErrorHandler.getErrorTitle("excluir usuário");
                        MainApplication.showErrorAlert(titulo, mensagem);
                    }
                }
            });
        }
    }
}
