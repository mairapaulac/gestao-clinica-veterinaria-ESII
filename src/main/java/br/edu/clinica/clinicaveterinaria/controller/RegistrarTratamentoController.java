package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.ConsultaDAO;
import br.edu.clinica.clinicaveterinaria.dao.ConnectionFactory;
import br.edu.clinica.clinicaveterinaria.dao.MedicamentoDAO;
import br.edu.clinica.clinicaveterinaria.dao.TratamentoDAO;
import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Medicamento;
import br.edu.clinica.clinicaveterinaria.model.Tratamento;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistrarTratamentoController implements Initializable {

    @FXML private Label lblPaciente;
    @FXML private Label lblDataHora;
    @FXML private Label lblVeterinario;
    @FXML private TableView<Consulta> tabelaHistorico;
    @FXML private TableColumn<Consulta, String> colDataHistorico;
    @FXML private TableColumn<Consulta, String> colDiagnosticoHistorico;
    @FXML private TableColumn<Consulta, String> colTratamentoHistorico;
    @FXML private TextArea txtDiagnostico;
    @FXML private TextArea txtProcedimentos;
    @FXML private ComboBox<Medicamento> comboMedicamento;
    @FXML private Spinner<Integer> spinnerQuantidade;
    @FXML private Label lblEstoqueDisponivel;
    @FXML private Button btnAdicionarMedicamento;
    @FXML private TableView<MedicamentoPrescrito> tabelaMedicamentos;
    @FXML private TableColumn<MedicamentoPrescrito, String> colMedicamento;
    @FXML private TableColumn<MedicamentoPrescrito, Integer> colQuantidade;
    @FXML private TableColumn<MedicamentoPrescrito, String> colAcoes;
    @FXML private Button btnCancelar;
    @FXML private Button btnSalvar;

    private Consulta consulta;
    private ConsultaDAO consultaDAO = new ConsultaDAO();
    private TratamentoDAO tratamentoDAO = new TratamentoDAO();
    private MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
    private ObservableList<Medicamento> medicamentosDisponiveis = FXCollections.observableArrayList();
    private ObservableList<MedicamentoPrescrito> medicamentosPrescritos = FXCollections.observableArrayList();
    private ObservableList<Consulta> historicoPaciente = FXCollections.observableArrayList();

    public static class MedicamentoPrescrito {
        private int idEstoque;
        private String nome;
        private int quantidade;
        private int estoqueDisponivel;

        public MedicamentoPrescrito(int idEstoque, String nome, int quantidade, int estoqueDisponivel) {
            this.idEstoque = idEstoque;
            this.nome = nome;
            this.quantidade = quantidade;
            this.estoqueDisponivel = estoqueDisponivel;
        }

        public int getIdEstoque() { return idEstoque; }
        public String getNome() { return nome; }
        public int getQuantidade() { return quantidade; }
        public int getEstoqueDisponivel() { return estoqueDisponivel; }
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
        carregarDadosConsulta();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        configurarSpinner();
        configurarEventos();
        carregarMedicamentosDisponiveis();
    }

    private void configurarColunas() {
        colDataHistorico.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataConsulta() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataConsulta()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });
        colDiagnosticoHistorico.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDiagnostico() != null ? 
                cellData.getValue().getDiagnostico() : ""));
        colTratamentoHistorico.setCellValueFactory(cellData -> {
            try {
                List<Tratamento> tratamentos = tratamentoDAO.listarPorConsulta(cellData.getValue().getId());
                if (tratamentos != null && !tratamentos.isEmpty()) {
                    return new SimpleStringProperty(tratamentos.get(0).getDescricao());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return new SimpleStringProperty("");
        });

        colMedicamento.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colAcoes.setCellValueFactory(cellData -> new SimpleStringProperty("Remover"));
        colAcoes.setCellFactory(column -> new TableCell<MedicamentoPrescrito, String>() {
            private final Button btnRemover = new Button("Remover");
            {
                btnRemover.getStyleClass().add("btn-remove");
                btnRemover.setOnAction(event -> {
                    MedicamentoPrescrito item = getTableView().getItems().get(getIndex());
                    medicamentosPrescritos.remove(item);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnRemover);
                }
            }
        });
    }

    private void configurarSpinner() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1);
        spinnerQuantidade.setValueFactory(valueFactory);
    }

    private void configurarEventos() {
        comboMedicamento.setOnAction(event -> atualizarEstoqueDisponivel());
        btnAdicionarMedicamento.setOnAction(event -> adicionarMedicamento());
        btnSalvar.setOnAction(event -> salvarTratamento());
        btnCancelar.setOnAction(event -> cancelar());
    }

    private void carregarDadosConsulta() {
        if (consulta == null) return;

        try {
            Consulta consultaCompleta = consultaDAO.buscarPorId(consulta.getId());
            if (consultaCompleta != null) {
                if (consultaCompleta.getPaciente() != null) {
                    lblPaciente.setText(consultaCompleta.getPaciente().getNome());
                }
                if (consultaCompleta.getDataConsulta() != null) {
                    lblDataHora.setText(consultaCompleta.getDataConsulta()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
                if (consultaCompleta.getVeterinario() != null) {
                    lblVeterinario.setText(consultaCompleta.getVeterinario().getNome());
                }
                if (consultaCompleta.getDiagnostico() != null) {
                    txtDiagnostico.setText(consultaCompleta.getDiagnostico());
                }

                carregarHistoricoPaciente(consultaCompleta.getPaciente().getId());
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar dados da consulta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarHistoricoPaciente(int idPaciente) {
        try {
            List<Consulta> consultas = consultaDAO.listarPorPaciente(idPaciente);
            
            historicoPaciente.clear();
            historicoPaciente.addAll(consultas);
            tabelaHistorico.setItems(historicoPaciente);
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar histórico do paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarMedicamentosDisponiveis() {
        try {
            medicamentosDisponiveis.setAll(medicamentoDAO.listarMedicamentosDisponiveis());
            comboMedicamento.setItems(medicamentosDisponiveis);
            comboMedicamento.setCellFactory(param -> new ListCell<Medicamento>() {
                @Override
                protected void updateItem(Medicamento item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome() + " (Estoque: " + item.getQuantidade() + ")");
                    }
                }
            });
            comboMedicamento.setButtonCell(new ListCell<Medicamento>() {
                @Override
                protected void updateItem(Medicamento item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome() + " (Estoque: " + item.getQuantidade() + ")");
                    }
                }
            });
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar medicamentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void atualizarEstoqueDisponivel() {
        Medicamento medicamento = comboMedicamento.getValue();
        if (medicamento != null) {
            try {
                int estoque = medicamentoDAO.obterEstoqueDisponivel(medicamento.getId());
                lblEstoqueDisponivel.setText("Estoque disponível: " + estoque);
            } catch (SQLException e) {
                lblEstoqueDisponivel.setText("Erro ao obter estoque");
                e.printStackTrace();
            }
        } else {
            lblEstoqueDisponivel.setText("");
        }
    }

    private void adicionarMedicamento() {
        Medicamento medicamento = comboMedicamento.getValue();
        if (medicamento == null) {
            MainApplication.showErrorAlert("Validação", "Selecione um medicamento.");
            return;
        }

        int quantidade = spinnerQuantidade.getValue();
        if (quantidade <= 0) {
            MainApplication.showErrorAlert("Validação", "A quantidade deve ser maior que zero.");
            return;
        }

        try {
            List<Medicamento> lotes = medicamentoDAO.listarLotesDisponiveisPorMedicamento(medicamento.getId());
            if (lotes.isEmpty()) {
                MainApplication.showErrorAlert("Estoque", "Não há lotes disponíveis para este medicamento.");
                return;
            }

            // Tenta encontrar um lote com estoque suficiente considerando o que já foi prescrito
            Medicamento loteSelecionado = null;
            int estoqueDisponivel = 0;
            final int quantidadeFinal = quantidade; // Variável final para usar na lambda
            
            for (Medicamento lote : lotes) {
                final Medicamento loteAtual = lote; // Variável final para usar na lambda
                int estoqueLote = lote.getQuantidade();
                // Verifica se este lote já foi usado na lista atual
                int quantidadeUsadaDesteLote = medicamentosPrescritos.stream()
                        .filter(p -> p.getIdEstoque() == loteAtual.getId())
                        .mapToInt(MedicamentoPrescrito::getQuantidade)
                        .sum();
                
                int estoqueRealDisponivel = estoqueLote - quantidadeUsadaDesteLote;
                
                if (estoqueRealDisponivel >= quantidadeFinal) {
                    loteSelecionado = lote;
                    estoqueDisponivel = estoqueRealDisponivel;
                    break;
                }
            }

            // Se não encontrou lote suficiente, usa o primeiro disponível e avisa
            if (loteSelecionado == null) {
                Medicamento primeiroLote = lotes.get(0);
                final int idPrimeiroLote = primeiroLote.getId();
                int quantidadeUsadaDesteLote = medicamentosPrescritos.stream()
                        .filter(p -> p.getIdEstoque() == idPrimeiroLote)
                        .mapToInt(MedicamentoPrescrito::getQuantidade)
                        .sum();
                estoqueDisponivel = primeiroLote.getQuantidade() - quantidadeUsadaDesteLote;
                loteSelecionado = primeiroLote;
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Estoque Insuficiente");
                alert.setHeaderText("Estoque insuficiente para " + medicamento.getNome());
                alert.setContentText("Estoque disponível no lote: " + estoqueDisponivel + 
                        "\nQuantidade solicitada: " + quantidade + 
                        "\n\nDeseja registrar mesmo assim?");
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return;
                }
            }

            MedicamentoPrescrito prescrito = new MedicamentoPrescrito(
                    loteSelecionado.getId(), 
                    medicamento.getNome(), 
                    quantidade, 
                    estoqueDisponivel
            );
            medicamentosPrescritos.add(prescrito);
            tabelaMedicamentos.setItems(medicamentosPrescritos);

            comboMedicamento.getSelectionModel().clearSelection();
            spinnerQuantidade.getValueFactory().setValue(1);
            lblEstoqueDisponivel.setText("");
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao adicionar medicamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvarTratamento() {
        if (txtDiagnostico.getText().trim().isEmpty() || txtProcedimentos.getText().trim().isEmpty()) {
            MainApplication.showErrorAlert("Validação", "Preencha todos os campos obrigatórios antes de salvar.");
            return;
        }

        // Valida estoque antes de salvar
        if (!validarEstoqueAntesDeSalvar()) {
            return;
        }

        try {
            Connection conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try {
                consulta.setDiagnostico(txtDiagnostico.getText().trim());
                consultaDAO.atualizarConsulta(consulta);

                String descricao = "Diagnóstico: " + txtDiagnostico.getText().trim() + 
                                 "\nProcedimentos: " + txtProcedimentos.getText().trim();
                Tratamento tratamento = new Tratamento(descricao, consulta);
                int idTratamento = tratamentoDAO.inserirTratamentoERetornarId(tratamento);

                for (MedicamentoPrescrito prescrito : medicamentosPrescritos) {
                    tratamentoDAO.inserirUsoMedicamento(idTratamento, prescrito.getIdEstoque(), prescrito.getQuantidade());
                }

                conn.commit();
                MainApplication.showSuccessAlert("Sucesso", "Tratamento registrado com sucesso!");
                fecharJanela();
            } catch (SQLException e) {
                conn.rollback();
                String mensagem = e.getMessage();
                if (mensagem != null && mensagem.contains("Estoque insuficiente")) {
                    MainApplication.showErrorAlert("Erro de Estoque", mensagem);
                } else {
                    MainApplication.showErrorAlert("Erro", "Erro ao registrar tratamento: " + mensagem);
                }
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao registrar tratamento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarEstoqueAntesDeSalvar() {
        try {
            // Agrupa medicamentos por lote para validar estoque total
            java.util.Map<Integer, Integer> quantidadePorLote = new java.util.HashMap<>();
            
            for (MedicamentoPrescrito prescrito : medicamentosPrescritos) {
                int idLote = prescrito.getIdEstoque();
                quantidadePorLote.put(idLote, 
                    quantidadePorLote.getOrDefault(idLote, 0) + prescrito.getQuantidade());
            }

            // Valida cada lote
            for (java.util.Map.Entry<Integer, Integer> entry : quantidadePorLote.entrySet()) {
                int idLote = entry.getKey();
                int quantidadeTotal = entry.getValue();
                
                // Busca informações do lote
                String sql = "SELECT em.quantidade_inicial, " +
                            "(em.quantidade_inicial - COALESCE(SUM(tm.quantidade_utilizada), 0)) AS quantidade_disponivel " +
                            "FROM estoque_medicamento em " +
                            "LEFT JOIN tratamento_medicamento tm ON em.id = tm.id_estoque_medicamento " +
                            "WHERE em.id = ? " +
                            "GROUP BY em.id, em.quantidade_inicial";
                
                try (Connection conn = ConnectionFactory.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setInt(1, idLote);
                    ResultSet rs = pstmt.executeQuery();
                    
                    if (rs.next()) {
                        int estoqueDisponivel = rs.getInt("quantidade_disponivel");
                        
                        if (quantidadeTotal > estoqueDisponivel) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Estoque Insuficiente");
                            alert.setHeaderText("Estoque insuficiente no lote selecionado");
                            alert.setContentText("Estoque disponível: " + estoqueDisponivel + 
                                    "\nQuantidade total solicitada: " + quantidadeTotal + 
                                    "\n\nDeseja continuar mesmo assim?");
                            
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isEmpty() || result.get() != ButtonType.OK) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao validar estoque: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void cancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Cancelamento");
        alert.setHeaderText("Deseja realmente cancelar o registro do tratamento?");
        alert.setContentText("Todas as informações não salvas serão perdidas.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            fecharJanela();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}

