package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.HistoricoItem;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DetalhesHistoricoController implements Initializable {

    @FXML private Label lblTitulo;
    @FXML private Label lblDataHora;
    @FXML private Label lblTipo;
    @FXML private Label lblVeterinario;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtTratamento;
    @FXML private TextArea txtDiagnosticoConsulta;
    @FXML private VBox vboxDiagnostico;
    @FXML private VBox vboxTratamento;
    @FXML private VBox vboxDiagnosticoConsulta;
    @FXML private Button btnFechar;

    private HistoricoItem item;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnFechar.setOnAction(event -> fechar());
    }

    public void setHistoricoItem(HistoricoItem item) {
        this.item = item;
        carregarDados();
    }

    private void carregarDados() {
        if (item == null) {
            return;
        }

        // Título
        lblTitulo.setText("Detalhes da " + item.getTipo());

        // Data e Hora
        if (item.getData() != null) {
            lblDataHora.setText(item.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else {
            lblDataHora.setText("-");
        }

        // Tipo
        lblTipo.setText(item.getTipo());

        // Veterinário
        if (item.getVeterinario() != null && item.getVeterinario().getNome() != null) {
            lblVeterinario.setText(item.getVeterinario().getNome());
        } else {
            lblVeterinario.setText("-");
        }

        // Se for CONSULTA, mostra diagnóstico
        if ("CONSULTA".equals(item.getTipo())) {
            vboxDiagnostico.setVisible(true);
            vboxDiagnostico.setManaged(true);
            vboxTratamento.setVisible(false);
            vboxTratamento.setManaged(false);
            vboxDiagnosticoConsulta.setVisible(false);
            vboxDiagnosticoConsulta.setManaged(false);

            if (item.getDiagnostico() != null && !item.getDiagnostico().trim().isEmpty()) {
                txtDiagnostico.setText(item.getDiagnostico());
            } else {
                txtDiagnostico.setText("Nenhum diagnóstico registrado.");
            }
        } 
        // Se for TRATAMENTO, mostra tratamento e diagnóstico da consulta relacionada
        else if ("TRATAMENTO".equals(item.getTipo())) {
            vboxDiagnostico.setVisible(false);
            vboxDiagnostico.setManaged(false);
            vboxTratamento.setVisible(true);
            vboxTratamento.setManaged(true);

            // Tratamento
            if (item.getDescricao() != null && !item.getDescricao().trim().isEmpty()) {
                txtTratamento.setText(item.getDescricao());
            } else {
                txtTratamento.setText("Nenhum tratamento registrado.");
            }

            // Diagnóstico da consulta relacionada
            if (item.getDiagnostico() != null && !item.getDiagnostico().trim().isEmpty()) {
                vboxDiagnosticoConsulta.setVisible(true);
                vboxDiagnosticoConsulta.setManaged(true);
                txtDiagnosticoConsulta.setText(item.getDiagnostico());
            } else {
                vboxDiagnosticoConsulta.setVisible(false);
                vboxDiagnosticoConsulta.setManaged(false);
            }
        }
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}

