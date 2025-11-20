package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.Medicamento;
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
    @FXML private TextField txtPrincipioAtivo;
    @FXML private TextField txtNumeroLote;
    @FXML private TextField txtQuantidade;
    @FXML private DatePicker dpValidade;
    @FXML private DatePicker dpEntrada;
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
            txtNome.setText(medicamento.getNome());
            txtFabricante.setText(medicamento.getFabricante());
            txtPrincipioAtivo.setText(medicamento.getPrincipioAtivo());
            txtNumeroLote.setText(medicamento.getNumeroLote());
            txtQuantidade.setText(String.valueOf(medicamento.getQuantidade()));
            dpValidade.setValue(medicamento.getDataValidade());
            dpEntrada.setValue(medicamento.getDataEntrada());
        } else {
            lblTitle.setText("Cadastrar Novo Medicamento");
            btnSalvar.setText("Cadastrar");
            dpEntrada.setValue(LocalDate.now());
        }
    }

    public Medicamento getNewMedicamento() {
        return newMedicamento;
    }

    private void salvarMedicamento() {
        String nome = txtNome.getText().trim();
        String fabricante = txtFabricante.getText().trim();
        String principioAtivo = txtPrincipioAtivo.getText().trim();
        String numeroLote = txtNumeroLote.getText().trim();

        if (nome.isEmpty() || fabricante.isEmpty() || numeroLote.isEmpty()) {
            showAlert("Erro de Validação", "Nome, Fabricante e Número do Lote não podem estar em branco.");
            return;
        }

        if (medicamentoToEdit == null) {
            for (Medicamento m : existingMedicamentos) {
                if (m.getNome().equalsIgnoreCase(nome)) {
                    showAlert("Erro", "Um medicamento com este nome já existe.");
                    return;
                }
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

        LocalDate dataValidade = dpValidade.getValue();
        LocalDate dataEntrada = dpEntrada.getValue();

        if (dataValidade == null || dataEntrada == null) {
            showAlert("Erro de Validação", "As datas de validade e entrada não podem estar em branco.");
            return;
        }

        newMedicamento = new Medicamento(nome, fabricante, principioAtivo, numeroLote, quantidade, dataValidade, dataEntrada);
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
