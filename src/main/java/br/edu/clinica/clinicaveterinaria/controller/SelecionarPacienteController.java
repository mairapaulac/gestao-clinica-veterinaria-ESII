package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SelecionarPacienteController implements Initializable {

    @FXML private TextField txtBuscar;
    @FXML private TableView<Paciente> tabelaPacientes;
    @FXML private TableColumn<Paciente, String> colNome;
    @FXML private TableColumn<Paciente, String> colEspecie;
    @FXML private TableColumn<Paciente, String> colRaca;
    @FXML private TableColumn<Paciente, String> colTutor;
    @FXML private Button btnSelecionar;
    @FXML private Button btnNovoPaciente;

    private List<Paciente> masterPacienteList;
    private final ObservableList<Paciente> localPacienteList = FXCollections.observableArrayList();
    private final FilteredList<Paciente> filteredData = new FilteredList<>(localPacienteList, p -> true);
    private Paciente selectedPaciente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tabelaPacientes.setItems(filteredData);
        configurarColunas();
        configurarBusca();
        
        tabelaPacientes.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                if (tabelaPacientes.getSelectionModel().getSelectedItem() != null) {
                    handleSelecionar();
                }
            }
        });
    }

    public void setPacientes(List<Paciente> masterList) {
        this.masterPacienteList = masterList;
        this.localPacienteList.setAll(masterList);
    }

    public Paciente getSelectedPaciente() {
        return selectedPaciente;
    }

    @FXML
    private void handleSelecionar() {
        this.selectedPaciente = tabelaPacientes.getSelectionModel().getSelectedItem();
        if (this.selectedPaciente != null) {
            Stage stage = (Stage) btnSelecionar.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleNovoPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-paciente-view.fxml"));
            Parent root = loader.load();

            CadastrarPacienteController controller = loader.getController();
            controller.setPacienteData(null, masterPacienteList);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastrar Novo Paciente");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner((Stage) btnNovoPaciente.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            MainApplication.setStageIcon(dialogStage);
            
            dialogStage.showAndWait();

            Paciente newPaciente = controller.getNewPaciente();
            if (newPaciente != null) {
                this.masterPacienteList.add(newPaciente);
                this.localPacienteList.add(newPaciente);
                tabelaPacientes.getSelectionModel().select(newPaciente);
                tabelaPacientes.scrollTo(newPaciente);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colEspecie.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspecie()));
        colRaca.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRaca()));
        colTutor.setCellValueFactory(cellData -> {
            Proprietario prop = cellData.getValue().getProprietario();
            return new SimpleStringProperty(prop != null && prop.getNome() != null ? prop.getNome() : "");
        });
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
}
