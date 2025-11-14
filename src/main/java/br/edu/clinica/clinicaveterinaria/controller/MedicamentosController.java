package br.edu.clinica.clinicaveterinaria.controller;

import javafx.beans.property.SimpleIntegerProperty;
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
import java.util.ResourceBundle;

public class MedicamentosController implements Initializable {

    @FXML private Button btnAdicionar;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Medicamento> tabelaMedicamentos;
    @FXML private TableColumn<Medicamento, String> colNome;
    @FXML private TableColumn<Medicamento, String> colFabricante;
    @FXML private TableColumn<Medicamento, Number> colQuantidade;
    @FXML private TableColumn<Medicamento, String> colValidade;

    private final ObservableList<Medicamento> listaMedicamentos = FXCollections.observableArrayList();
    private FilteredList<Medicamento> filteredData;

    public record Medicamento(String nome, String fabricante, int quantidade, LocalDate dataValidade) {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        carregarDadosExemplo();
        configurarBusca();
        configurarContextMenu();
        btnAdicionar.setOnAction(event -> showMedicamentoDialog(null));
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().nome()));
        colFabricante.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().fabricante()));
        colQuantidade.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().quantidade()));
        colValidade.setCellValueFactory(cellData -> {
            SimpleStringProperty property = new SimpleStringProperty();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            property.setValue(formatter.format(cellData.getValue().dataValidade()));
            return property;
        });
    }

    private void carregarDadosExemplo() {
        listaMedicamentos.add(new Medicamento("Dipirona", "Medley", 50, LocalDate.of(2026, 10, 5)));
        listaMedicamentos.add(new Medicamento("Amoxicilina", "EMS", 30, LocalDate.of(2025, 8, 20)));
        listaMedicamentos.add(new Medicamento("Ivermectina", "Pfizer", 100, LocalDate.of(2027, 1, 15)));
        listaMedicamentos.add(new Medicamento("Prednisona", "Aché", 25, LocalDate.of(2025, 5, 30)));

        filteredData = new FilteredList<>(listaMedicamentos, p -> true);
        tabelaMedicamentos.setItems(filteredData);
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(medicamento -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (medicamento.nome().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else return medicamento.fabricante().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem("Editar");
        MenuItem excluirItem = new MenuItem("Excluir");

        editarItem.setOnAction(event -> handleEditar(tabelaMedicamentos.getSelectionModel().getSelectedItem()));
        excluirItem.setOnAction(event -> handleExcluir(tabelaMedicamentos.getSelectionModel().getSelectedItem()));

        contextMenu.getItems().addAll(editarItem, excluirItem);

        tabelaMedicamentos.setRowFactory(tv -> {
            TableRow<Medicamento> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void handleEditar(Medicamento medicamento) {
        if (medicamento != null) {
            showMedicamentoDialog(medicamento);
        }
    }

    private void showMedicamentoDialog(Medicamento medicamento) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-medicamento-view.fxml"));
            Scene scene = new Scene(loader.load());

            CadastrarMedicamentoController controller = loader.getController();
            controller.setMedicamentoData(medicamento, listaMedicamentos);

            Stage stage = new Stage();
            stage.setTitle(medicamento == null ? "Cadastrar Novo Medicamento" : "Editar Medicamento");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();

            Medicamento newMedicamento = controller.getNewMedicamento();
            if (newMedicamento != null) {
                if (medicamento == null) {
                    listaMedicamentos.add(newMedicamento);
                } else {
                    int index = listaMedicamentos.indexOf(medicamento);
                    if (index != -1) {
                        listaMedicamentos.set(index, newMedicamento);
                    }
                }
                tabelaMedicamentos.refresh();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleExcluir(Medicamento medicamento) {
        if (medicamento != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir o medicamento: " + medicamento.nome() + "?");
            alert.setContentText("Esta ação não pode ser desfeita.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    listaMedicamentos.remove(medicamento);
                    tabelaMedicamentos.refresh();
                }
            });
        }
    }
}
