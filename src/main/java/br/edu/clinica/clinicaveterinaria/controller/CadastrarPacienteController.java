package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.controller.PacientesController.Paciente;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CadastrarPacienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtRaca;
    @FXML private DatePicker dpNascimento;
    @FXML private TextField txtTutor;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;
    @FXML private Label lblTitle;

    private Paciente pacienteToEdit;
    private ObservableList<Paciente> existingPacientes;
    private Paciente newPaciente = null;

    @FXML
    private void initialize() {
        btnSalvar.setOnAction(event -> salvarPaciente());
        btnCancelar.setOnAction(event -> cancelar());
    }

    public void setPacienteData(Paciente paciente, ObservableList<Paciente> pacientes) {
        this.existingPacientes = pacientes;
        this.pacienteToEdit = paciente;

        if (paciente != null) {
            txtNome.setText(paciente.nome());
            txtEspecie.setText(paciente.especie());
            txtRaca.setText(paciente.raca());
            dpNascimento.setValue(paciente.dataNascimento());
            txtTutor.setText(paciente.tutor());
            btnSalvar.setText("Salvar");
            lblTitle.setText("Editando Paciente");
        } else {
            btnSalvar.setText("Cadastrar");
            lblTitle.setText("Cadastrar Novo Paciente");
        }
    }

    public Paciente getNewPaciente() {
        return newPaciente;
    }

    private void salvarPaciente() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            showAlert("Erro de Validação", "O nome do paciente não pode estar em branco.");
            return;
        }

        for (Paciente p : existingPacientes) {
            if (p.nome().equalsIgnoreCase(nome) && (pacienteToEdit == null || !p.equals(pacienteToEdit))) {
                showAlert("Erro", "Um paciente com este nome já existe.");
                return;
            }
        }

        String especie = txtEspecie.getText();
        String raca = txtRaca.getText();

        // Validação da Data
        LocalDate dataNascimento = dpNascimento.getValue();
        String dataTexto = dpNascimento.getEditor().getText();

        if (dataTexto == null || dataTexto.trim().isEmpty()) {
             showAlert("Erro de Validação", "A data de nascimento não pode estar em branco.");
             return;
        }

        try {
            // Tenta forçar a conversão do texto para garantir que é válido
            dpNascimento.getConverter().fromString(dataTexto);
        } catch (Exception e) {
            showAlert("Erro de Validação", "O formato da data é inválido. Use dd/mm/aaaa.");
            return;
        }
        
        dataNascimento = dpNascimento.getValue(); // Pega o valor após a conversão bem-sucedida

        if (dataNascimento.isAfter(LocalDate.now())) {
            showAlert("Erro de Validação", "A data de nascimento não pode ser uma data futura.");
            return;
        }
        String tutor = txtTutor.getText();

        newPaciente = new Paciente(nome, especie, raca, dataNascimento, tutor);
        fecharJanela();
    }

    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

