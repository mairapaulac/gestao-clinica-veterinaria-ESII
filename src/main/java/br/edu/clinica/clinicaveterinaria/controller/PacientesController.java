package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.PacienteDAO;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PacientesController implements Initializable {

    @FXML private Button btnCadastrar;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Paciente> tabelaPacientes;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colEspecie;
    @FXML private TableColumn<Paciente, String> colRaca;
    @FXML private TableColumn<Paciente, String> colNascimento;
    @FXML private TableColumn<Paciente, String> colTutor;

    private final ObservableList<Paciente> listaPacientes = FXCollections.observableArrayList();
    private FilteredList<Paciente> filteredData;
    private PacienteDAO pacienteDAO = new PacienteDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        carregarDadosDoBanco();
        configurarBusca();
        configurarContextMenu();
        btnCadastrar.setOnAction(event -> handleCadastrarPaciente());
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colEspecie.setCellValueFactory(cellData -> {
            String especie = cellData.getValue().getEspecie();
            return new SimpleStringProperty(especie != null ? especie : "");
        });
        colRaca.setCellValueFactory(cellData -> {
            String raca = cellData.getValue().getRaca();
            return new SimpleStringProperty(raca != null ? raca : "");
        });
        colTutor.setCellValueFactory(cellData -> {
            Proprietario prop = cellData.getValue().getProprietario();
            return new SimpleStringProperty(prop != null && prop.getNome() != null ? prop.getNome() : "");
        });
        colNascimento.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            LocalDate data = cellData.getValue().getDataNascimento();
            if (data != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                property.setValue(formatter.format(data));
            } else {
                property.setValue("");
            }
            return property;
        });
    }

    private void carregarDadosDoBanco() {
        try {
            listaPacientes.setAll(pacienteDAO.listarTodos());
            
            filteredData = new FilteredList<>(listaPacientes, p -> true);
            tabelaPacientes.setItems(filteredData);
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro ao Carregar Dados");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao carregar pacientes do banco de dados: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(paciente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (paciente.getNome() != null && paciente.getNome().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getEspecie() != null && paciente.getEspecie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getRaca() != null && paciente.getRaca().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getProprietario() != null && paciente.getProprietario().getNome() != null 
                        && paciente.getProprietario().getNome().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem verDetalhesItem = new MenuItem("Ver detalhes");
        MenuItem editarItem = new MenuItem("Editar");
        MenuItem historicoItem = new MenuItem("Histórico");
        MenuItem excluirItem = new MenuItem("Excluir");

        verDetalhesItem.setOnAction(event -> handleVerDetalhes(tabelaPacientes.getSelectionModel().getSelectedItem()));
        editarItem.setOnAction(event -> handleEditar(tabelaPacientes.getSelectionModel().getSelectedItem()));
        historicoItem.setOnAction(event -> handleHistorico(tabelaPacientes.getSelectionModel().getSelectedItem()));
        excluirItem.setOnAction(event -> handleExcluir(tabelaPacientes.getSelectionModel().getSelectedItem()));

        contextMenu.getItems().addAll(verDetalhesItem, editarItem, historicoItem, excluirItem);

        tabelaPacientes.setRowFactory(tv -> {
            TableRow<Paciente> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    tabelaPacientes.getSelectionModel().select(row.getIndex());
                }
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    @FXML
    private void handleCadastrarPaciente() {
        showPacienteDialog(null);
    }

    private void handleVerDetalhes(Paciente paciente) {
        if (paciente != null) {
            try {
                Paciente pacienteCompleto = pacienteDAO.buscarPorId(paciente.getId());
                if (pacienteCompleto == null) {
                    pacienteCompleto = paciente;
                }
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/detalhes-paciente-view.fxml"));
                Scene scene = new Scene(loader.load());

                DetalhesPacienteController controller = loader.getController();
                controller.setPaciente(pacienteCompleto);

                Stage stage = new Stage();
                stage.setTitle("Detalhes do Paciente: " + pacienteCompleto.getNome());
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(scene);
                stage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao abrir detalhes do paciente: " + e.getMessage());
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao buscar dados do paciente: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void handleEditar(Paciente paciente) {
        if (paciente != null) {
            showPacienteDialog(paciente);
        }
    }

    private void showPacienteDialog(Paciente paciente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-paciente-view.fxml"));
            Scene scene = new Scene(loader.load());

            CadastrarPacienteController controller = loader.getController();
            controller.setPacienteData(paciente, listaPacientes);

            Stage stage = new Stage();
            stage.setTitle(paciente == null ? "Cadastrar Novo Paciente" : "Editar Paciente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Paciente newPaciente = controller.getNewPaciente();
            if (newPaciente != null) {
                carregarDadosDoBanco();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHistorico(Paciente paciente) {
        if (paciente != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/historico-paciente-view.fxml"));
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Histórico do Paciente: " + paciente.getNome());
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(btnCadastrar.getScene().getWindow());
                dialogStage.setScene(new Scene(loader.load()));

                HistoricoPacienteController controller = loader.getController();
                controller.setPaciente(paciente);

                dialogStage.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao abrir histórico do paciente: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    private void handleExcluir(Paciente paciente) {
        if (paciente != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir o paciente: " + paciente.getNome() + "?");
            alert.setContentText("Esta ação não pode ser desfeita.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        pacienteDAO.deletarPaciente(paciente.getId());
                        listaPacientes.remove(paciente);
                        tabelaPacientes.refresh();
                        
                        Alert sucesso = new Alert(Alert.AlertType.INFORMATION);
                        sucesso.setTitle("Sucesso");
                        sucesso.setHeaderText(null);
                        sucesso.setContentText("Paciente excluído com sucesso!");
                        sucesso.showAndWait();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert erro = new Alert(Alert.AlertType.ERROR);
                        erro.setTitle("Erro ao Excluir");
                        erro.setHeaderText(null);
                        erro.setContentText("Erro ao excluir paciente: " + e.getMessage());
                        erro.showAndWait();
                    }
                }
            });
        }
    }
}