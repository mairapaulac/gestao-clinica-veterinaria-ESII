package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.Medicamento;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class DetalhesMedicamentoController {

    @FXML private Label lblNome;
    @FXML private Label lblFabricante;
    @FXML private Label lblPrincipioAtivo;
    @FXML private Label lblNumeroLote;
    @FXML private Label lblQuantidade;
    @FXML private Label lblDataValidade;
    @FXML private Label lblDataEntrada;
    @FXML private Button btnFechar;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        btnFechar.setOnAction(event -> fecharJanela());
    }

    public void setMedicamento(Medicamento medicamento) {
        if (medicamento != null) {
            lblNome.setText(medicamento.getNome());
            lblFabricante.setText(medicamento.getFabricante());
            lblPrincipioAtivo.setText(medicamento.getPrincipioAtivo() != null ? medicamento.getPrincipioAtivo() : "-");
            lblNumeroLote.setText(medicamento.getNumeroLote() != null ? medicamento.getNumeroLote() : "-");
            lblQuantidade.setText(String.valueOf(medicamento.getQuantidade()));
            
            if (medicamento.getDataValidade() != null) {
                lblDataValidade.setText(medicamento.getDataValidade().format(DATE_FORMATTER));
            } else {
                lblDataValidade.setText("-");
            }

            if (medicamento.getDataEntrada() != null) {
                lblDataEntrada.setText(medicamento.getDataEntrada().format(DATE_FORMATTER));
            } else {
                lblDataEntrada.setText("-");
            }
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}
