package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.controller.MedicamentosController.Medicamento;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class CadastrarMedicamentoController {

    @FXML private Label lblTitle;
    @FXML private TextField txtNome;
    @FXML private TextField txtFabricante;
    @FXML private TextField txtQuantidade;
    @FXML private DatePicker dpValidade;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private Medicamento medicamentoToEdit;
    private ObservableList<Medicamento> existingMedicamentos;
    private Medicamento newMedicamento = null;

    @FXML
    private void initialize() {
        btnSalvar.setOnAction(event -> salvarMedicamento());
        btnCancelar.setOnAction(event -> cancelar());
    }

    public void setMedicamentoData(Medicamento medicamento, ObservableList<Medicamento> medicamentos) {
        this.existingMedicamentos = medicamentos;
        this.medicamentoToEdit = medicamento;

        if (medicamento != null) {
            lblTitle.setText("Editar Medicamento");
            btnSalvar.setText("Salvar");
            txtNome.setText(medicamento.nome());
            txtFabricante.setText(medicamento.fabricante());
            txtQuantidade.setText(String.valueOf(medicamento.quantidade()));
            dpValidade.setValue(medicamento.dataValidade());
        } else {
            lblTitle.setText("Cadastrar Novo Medicamento");
            btnSalvar.setText("Cadastrar");
        }
    }

    public Medicamento getNewMedicamento() {
        return newMedicamento;
    }

    private void salvarMedicamento() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            showAlert("Erro de Validação", "O nome do medicamento não pode estar em branco.");
            return;
        }

        for (Medicamento m : existingMedicamentos) {
            if (m.nome().equalsIgnoreCase(nome) && (medicamentoToEdit == null || !m.equals(medicamentoToEdit))) {
                showAlert("Erro", "Um medicamento com este nome já existe.");
                return;
            }
        }

        int quantidade;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText());
            if (quantidade < 0) {
                showAlert("Erro de Validação", "A quantidade não pode ser negativa.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Erro de Validação", "A quantidade deve ser um número válido.");
            return;
        }

        LocalDate dataValidade;
        try {
            dataValidade = dpValidade.getConverter().fromString(dpValidade.getEditor().getText());
        } catch (Exception e) {
            showAlert("Erro de Validação", "O formato da data de validade é inválido. Use dd/mm/aaaa.");
            return;
        }
        
        if (dataValidade == null) {
            showAlert("Erro de Validação", "A data de validade não pode estar em branco.");
            return;
        }

        String fabricante = txtFabricante.getText();
        newMedicamento = new Medicamento(nome, fabricante, quantidade, dataValidade);
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
