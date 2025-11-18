package br.edu.clinica.clinicaveterinaria.controller;

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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        carregarDadosExemplo();
        configurarBusca();
        configurarContextMenu();
        btnCadastrar.setOnAction(event -> handleCadastrarPaciente());
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colEspecie.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspecie()));
        colRaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRaca()));
        colTutor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProprietario().getNome()));
        colNascimento.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            property.setValue(formatter.format(cellData.getValue().getDataNascimento()));
            return property;
        });
    }

    private void carregarDadosExemplo() {
        Proprietario p1 = new Proprietario(); p1.setId(1); p1.setNome("Carlos Silva");
        Proprietario p2 = new Proprietario(); p2.setId(2); p2.setNome("Ana Souza");
        Proprietario p3 = new Proprietario(); p3.setId(3); p3.setNome("João Pereira");
        Proprietario p4 = new Proprietario(); p4.setId(4); p4.setNome("Maria Oliveira");
        Proprietario p5 = new Proprietario(); p5.setId(5); p5.setNome("Pedro Santos");

        List<Paciente> pacientes = new ArrayList<>();
        pacientes.add(createPaciente(1, "Rex", "Cachorro", "Labrador", LocalDate.of(2020, 5, 10), p1));
        pacientes.add(createPaciente(2, "Mimi", "Gato", "Siamês", LocalDate.of(2018, 8, 22), p2));
        pacientes.add(createPaciente(3, "Bolinha", "Cachorro", "Poodle", LocalDate.of(2022, 1, 30), p3));
        pacientes.add(createPaciente(4, "Nemo", "Peixe", "Palhaço", LocalDate.of(2023, 3, 15), p4));
        pacientes.add(createPaciente(5, "Pé de Pano", "Cavalo", "Manga-larga", LocalDate.of(2015, 11, 5), p5));

        listaPacientes.addAll(pacientes);

        filteredData = new FilteredList<>(listaPacientes, p -> true);
        tabelaPacientes.setItems(filteredData);
    }

    private Paciente createPaciente(int id, String nome, String especie, String raca, LocalDate dataNascimento, Proprietario proprietario) {
        Paciente p = new Paciente();
        p.setId(id);
        p.setNome(nome);
        p.setEspecie(especie);
        p.setRaca(raca);
        p.setDataNascimento(dataNascimento);
        p.setProprietario(proprietario);
        return p;
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(paciente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (paciente.getNome().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getEspecie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getRaca().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.getProprietario().getNome().toLowerCase().contains(lowerCaseFilter)) {
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
            System.out.println("Ação: Ver detalhes do paciente " + paciente.getNome());
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
                if (paciente == null) {
                    listaPacientes.add(newPaciente);
                } else {
                    int index = listaPacientes.indexOf(paciente);
                    if (index != -1) {
                        listaPacientes.set(index, newPaciente);
                    }
                }
                tabelaPacientes.refresh();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleHistorico(Paciente paciente) {
        if (paciente != null) {
            System.out.println("Ação: Ver histórico do paciente " + paciente.getNome());
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
                    listaPacientes.remove(paciente);
                    tabelaPacientes.refresh();
                    System.out.println("Ação: Excluir o paciente " + paciente.getNome());
                }
            });
        }
    }
}