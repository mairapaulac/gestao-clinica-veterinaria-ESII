package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.FuncionarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import br.edu.clinica.clinicaveterinaria.view.SessionManager;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableColumn<Funcionario, String> colNome;
    @FXML private TableColumn<Funcionario, String> colCargo;
    @FXML private TableColumn<Funcionario, String> colLogin;
    @FXML private TableColumn<Funcionario, Boolean> colGerente;

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private final ObservableList<Funcionario> listaFuncionarios = FXCollections.observableArrayList();
    private FilteredList<Funcionario> filteredData;

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
        btnAdicionar.setOnAction(event -> showFuncionarioDialog(null));
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colGerente.setCellValueFactory(cellData -> {
            Funcionario func = cellData.getValue();
            return new SimpleBooleanProperty(func != null && func.isGerente());
        });

        colGerente.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Sim" : "Não");
                }
            }
        });
    }

    private void carregarDadosDoBanco() {
        try {
            listaFuncionarios.setAll(funcionarioDAO.listarTodos());
            filteredData = new FilteredList<>(listaFuncionarios, p -> true);
            tabelaFuncionarios.setItems(filteredData);
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro de Banco de Dados", "Não foi possível carregar os funcionários do banco de dados.");
            e.printStackTrace();
        }
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(funcionario -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return (funcionario.getNome() != null && funcionario.getNome().toLowerCase().contains(lowerCaseFilter)) ||
                       (funcionario.getCargo() != null && funcionario.getCargo().toLowerCase().contains(lowerCaseFilter)) ||
                       (funcionario.getLogin() != null && funcionario.getLogin().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem("Editar");
        MenuItem excluirItem = new MenuItem("Excluir");

        editarItem.setOnAction(event -> handleEditar(tabelaFuncionarios.getSelectionModel().getSelectedItem()));
        excluirItem.setOnAction(event -> handleExcluir(tabelaFuncionarios.getSelectionModel().getSelectedItem()));

        contextMenu.getItems().addAll(editarItem, excluirItem);

        tabelaFuncionarios.setRowFactory(tv -> {
            TableRow<Funcionario> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void showFuncionarioDialog(Funcionario funcionario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-funcionario-view.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(funcionario == null ? "Cadastrar Novo Funcionário" : "Editar Funcionário");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAdicionar.getScene().getWindow());
            dialogStage.setScene(new Scene(loader.load()));

            CadastrarFuncionarioController controller = loader.getController();
            controller.setFuncionarioData(funcionario, listaFuncionarios);

            dialogStage.showAndWait();

            Funcionario result = controller.getNewFuncionario();
            if (result != null) {
                carregarDadosDoBanco();
            }
        } catch (IOException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro de Aplicação", "Falha ao abrir a tela de cadastro de funcionário.");
        }
    }

    private void handleEditar(Funcionario funcionario) {
        if (funcionario != null) {
            showFuncionarioDialog(funcionario);
        }
    }

    private void handleExcluir(Funcionario funcionario) {
        if (funcionario != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir o funcionário: " + funcionario.getNome() + "?");
            alert.setContentText("Esta ação não pode ser desfeita.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        funcionarioDAO.deletarFuncionario(funcionario.getId());
                        listaFuncionarios.remove(funcionario);
                        tabelaFuncionarios.refresh();
                        
                        Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                        sucesso.setTitle("Sucesso");
                        sucesso.setHeaderText(null);
                        sucesso.setContentText("Funcionário excluído com sucesso!");
                        sucesso.showAndWait();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        MainApplication.showErrorAlert("Erro ao Excluir", "Erro ao excluir funcionário: " + e.getMessage());
                    }
                }
            });
        }
    }
}

