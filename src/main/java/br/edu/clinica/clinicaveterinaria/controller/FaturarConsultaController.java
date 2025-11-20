package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.PagamentoDAO;
import br.edu.clinica.clinicaveterinaria.dao.TratamentoDAO;
import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Pagamento;
import br.edu.clinica.clinicaveterinaria.model.Tratamento;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import br.edu.clinica.clinicaveterinaria.view.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class FaturarConsultaController implements Initializable {

    @FXML private Label lblPaciente;
    @FXML private Label lblDataHora;
    @FXML private Label lblVeterinario;
    @FXML private Label lblDiagnostico;
    @FXML private TextField txtValorConsulta;
    @FXML private TextField txtValorTratamentos;
    @FXML private TextField txtValorMedicamentos;
    @FXML private Label lblValorTotal;
    @FXML private ComboBox<String> comboMetodoPagamento;
    
    @FXML private Button btnCancelar;
    @FXML private Button btnGerarFatura;
    @FXML private Button btnConfirmarPagamento;

    private PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private TratamentoDAO tratamentoDAO = new TratamentoDAO();
    private Consulta consulta;
    private boolean faturaGerada = false;
    
    private static final float VALOR_BASE_CONSULTA = 100.00f;
    private static final float VALOR_BASE_TRATAMENTO = 50.00f;
    private static final float VALOR_BASE_MEDICAMENTO = 20.00f;
    
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
        carregarDadosConsulta();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarMetodosPagamento();
        configurarEventos();
    }

    private void configurarMetodosPagamento() {
        comboMetodoPagamento.getItems().addAll(
            "Dinheiro",
            "Cartão de Débito",
            "Cartão de Crédito",
            "Transferência Bancária",
            "PIX"
        );
    }

    private void configurarEventos() {
        btnCancelar.setOnAction(event -> fecharJanela());
        btnGerarFatura.setOnAction(event -> gerarFatura());
        btnConfirmarPagamento.setOnAction(event -> confirmarPagamento());
        
        txtValorConsulta.textProperty().addListener((observable, oldValue, newValue) -> calcularValorTotal());
    }

    private void carregarDadosConsulta() {
        if (consulta == null) return;

        // Verificar se já foi faturada
        try {
            List<Pagamento> pagamentos = pagamentoDAO.listarPorConsulta(consulta.getId());
            if (!pagamentos.isEmpty()) {
                MainApplication.showErrorAlert("Fatura Já Gerada", 
                    "Esta consulta já foi faturada anteriormente.");
                fecharJanela();
                return;
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao verificar fatura: " + e.getMessage());
            fecharJanela();
            return;
        }

        // Carregar informações da consulta
        lblPaciente.setText(consulta.getPaciente() != null ? consulta.getPaciente().getNome() : "-");
        if (consulta.getDataConsulta() != null) {
            lblDataHora.setText(consulta.getDataConsulta()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        } else {
            lblDataHora.setText("-");
        }
        lblVeterinario.setText(consulta.getVeterinario() != null ? consulta.getVeterinario().getNome() : "-");
        lblDiagnostico.setText(consulta.getDiagnostico() != null ? consulta.getDiagnostico() : "-");
        
        // Calcular valores
        calcularValores();
    }

    private void calcularValores() {
        try {
            // Valor da consulta (editável)
            txtValorConsulta.setText(decimalFormat.format(VALOR_BASE_CONSULTA));
            
            // Calcular valor dos tratamentos
            List<Tratamento> tratamentos = tratamentoDAO.listarPorConsulta(consulta.getId());
            float valorTratamentos = tratamentos.size() * VALOR_BASE_TRATAMENTO;
            txtValorTratamentos.setText(decimalFormat.format(valorTratamentos));
            
            // Calcular valor dos medicamentos (simplificado - baseado em quantidade de tratamentos)
            float valorMedicamentos = tratamentos.size() * VALOR_BASE_MEDICAMENTO;
            txtValorMedicamentos.setText(decimalFormat.format(valorMedicamentos));
            
            calcularValorTotal();
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao calcular valores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calcularValorTotal() {
        try {
            float valorConsulta = Float.parseFloat(txtValorConsulta.getText().replace(",", "."));
            float valorTratamentos = Float.parseFloat(txtValorTratamentos.getText().replace(",", "."));
            float valorMedicamentos = Float.parseFloat(txtValorMedicamentos.getText().replace(",", "."));
            
            float total = valorConsulta + valorTratamentos + valorMedicamentos;
            lblValorTotal.setText(currencyFormat.format(total));
        } catch (NumberFormatException e) {
            lblValorTotal.setText("R$ 0,00");
        }
    }

    private void gerarFatura() {
        if (consulta == null) {
            MainApplication.showErrorAlert("Erro", "Nenhuma consulta selecionada.");
            return;
        }
        
        try {
            float valorTotal = Float.parseFloat(txtValorConsulta.getText().replace(",", ".")) +
                              Float.parseFloat(txtValorTratamentos.getText().replace(",", ".")) +
                              Float.parseFloat(txtValorMedicamentos.getText().replace(",", "."));
            
            if (valorTotal <= 0) {
                MainApplication.showErrorAlert("Erro", "O valor total deve ser maior que zero.");
                return;
            }
            
            faturaGerada = true;
            btnGerarFatura.setVisible(false);
            btnGerarFatura.setManaged(false);
            btnConfirmarPagamento.setVisible(true);
            btnConfirmarPagamento.setManaged(true);
            
            MainApplication.showSuccessAlert("Fatura Gerada", 
                "Fatura gerada com sucesso! Valor total: " + currencyFormat.format(valorTotal));
        } catch (NumberFormatException e) {
            MainApplication.showErrorAlert("Erro", "Valores inválidos. Verifique os campos.");
        }
    }

    private void confirmarPagamento() {
        if (consulta == null) {
            MainApplication.showErrorAlert("Erro", "Nenhuma consulta selecionada.");
            return;
        }
        
        if (!faturaGerada) {
            MainApplication.showErrorAlert("Erro", "Gere a fatura antes de confirmar o pagamento.");
            return;
        }
        
        String metodoPagamento = comboMetodoPagamento.getSelectionModel().getSelectedItem();
        if (metodoPagamento == null || metodoPagamento.isEmpty()) {
            MainApplication.showErrorAlert("Erro", "Selecione a forma de pagamento.");
            return;
        }
        
        try {
            float valorTotal = Float.parseFloat(txtValorConsulta.getText().replace(",", ".")) +
                              Float.parseFloat(txtValorTratamentos.getText().replace(",", ".")) +
                              Float.parseFloat(txtValorMedicamentos.getText().replace(",", "."));
            
            if (SessionManager.getFuncionarioLogado() == null) {
                MainApplication.showErrorAlert("Erro", "Funcionário não logado.");
                return;
            }
            
            Pagamento pagamento = new Pagamento(
                metodoPagamento,
                valorTotal,
                java.time.LocalDateTime.now(),
                SessionManager.getFuncionarioLogado(),
                consulta
            );
            
            pagamentoDAO.inserirPagamento(pagamento);
            
            MainApplication.showSuccessAlert("Sucesso", "Pagamento registrado com sucesso!");
            
            fecharJanela();
        } catch (NumberFormatException e) {
            MainApplication.showErrorAlert("Erro", "Valores inválidos. Verifique os dados.");
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao registrar pagamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}

