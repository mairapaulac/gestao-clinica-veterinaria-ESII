package br.edu.clinica.clinicaveterinaria.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import java.net.URL;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        carregarDadosExemplo();
        configurarBusca();
        configurarContextMenu();
        btnCadastrar.setOnAction(event -> handleCadastrarPaciente());
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nome()));
        colEspecie.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().especie()));
        colRaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().raca()));
        colTutor.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().tutor()));
        colNascimento.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            property.setValue(formatter.format(cellData.getValue().dataNascimento()));
            return property;
        });
    }

    private void carregarDadosExemplo() {
        listaPacientes.add(new Paciente("Rex", "Cachorro", "Labrador", LocalDate.of(2020, 5, 10), "Carlos Silva"));
        listaPacientes.add(new Paciente("Mimi", "Gato", "Siamês", LocalDate.of(2018, 8, 22), "Ana Souza"));
        listaPacientes.add(new Paciente("Bolinha", "Cachorro", "Poodle", LocalDate.of(2022, 1, 30), "João Pereira"));
        listaPacientes.add(new Paciente("Nemo", "Peixe", "Palhaço", LocalDate.of(2023, 3, 15), "Maria Oliveira"));
        listaPacientes.add(new Paciente("Pé de Pano", "Cavalo", "Manga-larga", LocalDate.of(2015, 11, 5), "Pedro Santos"));
        listaPacientes.add(new Paciente("Amora", "Cachorro", "Golden Retriever", LocalDate.of(2019, 7, 1), "Fernanda Costa"));
        listaPacientes.add(new Paciente("Fumaça", "Gato", "Persa", LocalDate.of(2021, 2, 14), "Rafael Lima"));
        listaPacientes.add(new Paciente("Thor", "Cachorro", "Pastor Alemão", LocalDate.of(2017, 9, 20), "Patrícia Almeida"));
        listaPacientes.add(new Paciente("Luna", "Gato", "Maine Coon", LocalDate.of(2022, 4, 5), "Gustavo Rocha"));
        listaPacientes.add(new Paciente("Pipoca", "Cachorro", "Chihuahua", LocalDate.of(2020, 11, 11), "Mariana Santos"));
        listaPacientes.add(new Paciente("Mel", "Cachorro", "Beagle", LocalDate.of(2018, 3, 25), "Daniel Oliveira"));
        listaPacientes.add(new Paciente("Garfield", "Gato", "Exótico", LocalDate.of(2016, 6, 8), "Carla Pereira"));
        listaPacientes.add(new Paciente("Buddy", "Cachorro", "Bulldog Francês", LocalDate.of(2021, 1, 1), "Ricardo Souza"));
        listaPacientes.add(new Paciente("Estrela", "Gato", "Sphynx", LocalDate.of(2023, 10, 3), "Juliana Costa"));
        listaPacientes.add(new Paciente("Max", "Cachorro", "Rottweiler", LocalDate.of(2019, 12, 18), "Felipe Martins"));

        filteredData = new FilteredList<>(listaPacientes, p -> true);
        tabelaPacientes.setItems(filteredData);
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(paciente -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (paciente.nome().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.especie().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.raca().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (paciente.tutor().toLowerCase().contains(lowerCaseFilter)) {
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
        System.out.println("Ação: Cadastrar novo paciente.");
    }

    private void handleVerDetalhes(Paciente paciente) {
        if (paciente != null) {
            System.out.println("Ação: Ver detalhes do paciente " + paciente.nome());
        }
    }

    private void handleEditar(Paciente paciente) {
        if (paciente != null) {
            System.out.println("Ação: Editar o paciente " + paciente.nome());
        }
    }

    private void handleHistorico(Paciente paciente) {
        if (paciente != null) {
            System.out.println("Ação: Ver histórico do paciente " + paciente.nome());
        }
    }

    private void handleExcluir(Paciente paciente) {
        if (paciente != null) {
            System.out.println("Ação: Excluir o paciente " + paciente.nome());
            listaPacientes.remove(paciente);
        }
    }

    public record Paciente(String nome, String especie, String raca, LocalDate dataNascimento, String tutor) {}
}