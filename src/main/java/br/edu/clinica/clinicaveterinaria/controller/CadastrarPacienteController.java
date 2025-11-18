package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

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
    private List<Paciente> existingPacientes;
    private Paciente newPaciente = null;

    @FXML
    private void initialize() {
        btnSalvar.setOnAction(event -> salvarPaciente());
        btnCancelar.setOnAction(event -> cancelar());
    }

    public void setPacienteData(Paciente paciente, List<Paciente> pacientes) {
        this.existingPacientes = pacientes;
        this.pacienteToEdit = paciente;

        if (paciente != null) {
            txtNome.setText(paciente.getNome());
            txtEspecie.setText(paciente.getEspecie());
            txtRaca.setText(paciente.getRaca());
            dpNascimento.setValue(paciente.getDataNascimento());
            txtTutor.setText(paciente.getProprietario().getNome());
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

        if (existingPacientes != null) {
            for (Paciente p : existingPacientes) {
                if (p.getNome().equalsIgnoreCase(nome) && (pacienteToEdit == null || !p.equals(pacienteToEdit))) {
                    showAlert("Erro", "Um paciente com este nome já existe.");
                    return;
                }
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

        Proprietario proprietario = new Proprietario();
        proprietario.setNome(tutor);

        newPaciente = new Paciente();
        newPaciente.setNome(nome);
        newPaciente.setEspecie(especie);
        newPaciente.setRaca(raca);
        newPaciente.setDataNascimento(dataNascimento);
        newPaciente.setProprietario(proprietario);
        
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

