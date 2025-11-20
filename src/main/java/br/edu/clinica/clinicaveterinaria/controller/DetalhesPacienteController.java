package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DetalhesPacienteController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private Label lblNome;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaca;
    @FXML private Label lblDataNascimento;
    @FXML private Label lblCpf;
    @FXML private Label lblNomeTutor;
    @FXML private Label lblTelefone;
    @FXML private Label lblEmail;
    @FXML private Label lblRua;
    @FXML private Label lblNumero;
    @FXML private Label lblBairro;
    @FXML private Label lblCidade;
    @FXML private Label lblEstado;
    @FXML private Label lblCep;
    @FXML private Button btnFechar;

    private Paciente paciente;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnFechar.setOnAction(event -> fecharJanela());
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        carregarDados();
    }

    private void carregarDados() {
        if (paciente == null) {
            return;
        }

        lblNome.setText(paciente.getNome() != null ? paciente.getNome() : "-");
        lblEspecie.setText(paciente.getEspecie() != null ? paciente.getEspecie() : "-");
        lblRaca.setText(paciente.getRaca() != null ? paciente.getRaca() : "-");
        
        if (paciente.getDataNascimento() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            lblDataNascimento.setText(formatter.format(paciente.getDataNascimento()));
        } else {
            lblDataNascimento.setText("-");
        }

        Proprietario prop = paciente.getProprietario();
        if (prop != null) {
            lblCpf.setText(prop.getCpf() != null ? formatarCpf(prop.getCpf()) : "-");
            lblNomeTutor.setText(prop.getNome() != null ? prop.getNome() : "-");
            lblTelefone.setText(prop.getTelefone() != null ? prop.getTelefone() : "-");
            lblEmail.setText(prop.getEmail() != null ? prop.getEmail() : "-");
            lblRua.setText(prop.getRua() != null ? prop.getRua() : "-");
            lblNumero.setText(prop.getNumero() != null ? prop.getNumero() : "-");
            lblBairro.setText(prop.getBairro() != null ? prop.getBairro() : "-");
            lblCidade.setText(prop.getCidade() != null ? prop.getCidade() : "-");
            lblEstado.setText(prop.getEstado() != null ? prop.getEstado() : "-");
            lblCep.setText(prop.getCep() != null ? prop.getCep() : "-");
        } else {
            lblCpf.setText("-");
            lblNomeTutor.setText("-");
            lblTelefone.setText("-");
            lblEmail.setText("-");
            lblRua.setText("-");
            lblNumero.setText("-");
            lblBairro.setText("-");
            lblCidade.setText("-");
            lblEstado.setText("-");
            lblCep.setText("-");
        }
    }

    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}

