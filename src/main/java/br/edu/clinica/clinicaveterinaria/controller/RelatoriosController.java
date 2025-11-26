package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.*;
import br.edu.clinica.clinicaveterinaria.model.*;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class RelatoriosController implements Initializable {

    @FXML private ComboBox<String> comboTipoRelatorio;
    @FXML private VBox vboxFiltros;
    @FXML private DatePicker dpDataInicial;
    @FXML private DatePicker dpDataFinal;
    @FXML private Label lblFiltro1;
    @FXML private Label lblFiltro2;
    @FXML private Label lblFiltro3;
    @FXML private ComboBox<Veterinario> comboVeterinario;
    @FXML private ComboBox<Paciente> comboPaciente;
    @FXML private ComboBox<String> comboStatus;
    @FXML private Button btnGerar;
    @FXML private Button btnLimpar;
    @FXML private VBox vboxResultados;
    @FXML private TableView<Object> tabelaRelatorio;
    @FXML private Button btnExportarPDF;
    @FXML private Button btnExportarExcel;
    @FXML private VBox vboxEmptyState;

    private ConsultaDAO consultaDAO = new ConsultaDAO();
    private TratamentoDAO tratamentoDAO = new TratamentoDAO();
    private PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private PacienteDAO pacienteDAO = new PacienteDAO();

    private ObservableList<Object> dadosRelatorio = FXCollections.observableArrayList();
    private String tipoRelatorioAtual = "";
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Suprimir warnings do PDFBox relacionados a fontes do sistema
        configurarLoggersPDFBox();
        configurarTipoRelatorio();
        configurarEventos();
        carregarFiltros();
    }
    
    private void configurarLoggersPDFBox() {
        // Configurar loggers do PDFBox para suprimir warnings de fontes corrompidas
        java.util.logging.Logger pdfboxLogger = java.util.logging.Logger.getLogger("org.apache.pdfbox");
        java.util.logging.Logger fontboxLogger = java.util.logging.Logger.getLogger("org.apache.fontbox");
        
        // Definir nível SEVERE para suprimir warnings (apenas erros críticos serão exibidos)
        pdfboxLogger.setLevel(java.util.logging.Level.SEVERE);
        fontboxLogger.setLevel(java.util.logging.Level.SEVERE);
    }

    private void configurarTipoRelatorio() {
        ObservableList<String> tipos = FXCollections.observableArrayList(
            "Consultas",
            "Tratamentos",
            "Faturamento",
            "Estoque"
        );
        comboTipoRelatorio.setItems(tipos);
    }

    private void configurarEventos() {
        comboTipoRelatorio.setOnAction(event -> aoSelecionarTipoRelatorio());
        btnGerar.setOnAction(event -> gerarRelatorio());
        btnLimpar.setOnAction(event -> limparFiltros());
        btnExportarPDF.setOnAction(event -> exportarPDF());
        btnExportarExcel.setOnAction(event -> exportarExcel());
    }

    private void carregarFiltros() {
        try {
            // Carregar veterinários
            List<Veterinario> veterinarios = veterinarioDAO.listarTodos();
            comboVeterinario.setItems(FXCollections.observableArrayList(veterinarios));
            comboVeterinario.setCellFactory(param -> new ListCell<Veterinario>() {
                @Override
                protected void updateItem(Veterinario item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome());
                    }
                }
            });
            comboVeterinario.setButtonCell(new ListCell<Veterinario>() {
                @Override
                protected void updateItem(Veterinario item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome());
                    }
                }
            });

            // Carregar pacientes
            List<Paciente> pacientes = pacienteDAO.listarTodos();
            comboPaciente.setItems(FXCollections.observableArrayList(pacientes));
            comboPaciente.setCellFactory(param -> new ListCell<Paciente>() {
                @Override
                protected void updateItem(Paciente item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome());
                    }
                }
            });
            comboPaciente.setButtonCell(new ListCell<Paciente>() {
                @Override
                protected void updateItem(Paciente item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome());
                    }
                }
            });
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar filtros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void aoSelecionarTipoRelatorio() {
        String tipo = comboTipoRelatorio.getSelectionModel().getSelectedItem();
        if (tipo == null) {
            vboxFiltros.setVisible(false);
            vboxFiltros.setManaged(false);
            return;
        }

        vboxFiltros.setVisible(true);
        vboxFiltros.setManaged(true);

        // Resetar todos os filtros
        lblFiltro1.setVisible(false);
        lblFiltro1.setManaged(false);
        comboVeterinario.setVisible(false);
        comboVeterinario.setManaged(false);
        lblFiltro2.setVisible(false);
        lblFiltro2.setManaged(false);
        comboPaciente.setVisible(false);
        comboPaciente.setManaged(false);
        lblFiltro3.setVisible(false);
        lblFiltro3.setManaged(false);
        comboStatus.setVisible(false);
        comboStatus.setManaged(false);

        // Configurar filtros específicos por tipo
        switch (tipo) {
            case "Consultas":
            case "Tratamentos":
                // Removidos filtros de veterinário e paciente - o PDF será gerado agrupado por veterinário
                // Apenas filtros de data são necessários
                break;
            case "Faturamento":
                lblFiltro3.setText("Status:");
                lblFiltro3.setVisible(true);
                lblFiltro3.setManaged(true);
                comboStatus.setVisible(true);
                comboStatus.setManaged(true);
                ObservableList<String> status = FXCollections.observableArrayList("Todos", "Pagos", "Pendentes");
                comboStatus.setItems(status);
                break;
            case "Estoque":
                // Estoque não precisa de filtros adicionais além de data
                break;
        }
    }

    private void gerarRelatorio() {
        String tipo = comboTipoRelatorio.getSelectionModel().getSelectedItem();
        if (tipo == null) {
            MainApplication.showErrorAlert("Erro", "Selecione um tipo de relatório.");
            return;
        }

        try {
            tipoRelatorioAtual = tipo;
            dadosRelatorio.clear();
            tabelaRelatorio.getColumns().clear();

            LocalDate dataInicial = dpDataInicial.getValue();
            LocalDate dataFinal = dpDataFinal.getValue();

            switch (tipo) {
                case "Consultas":
                    gerarRelatorioConsultas(dataInicial, dataFinal);
                    break;
                case "Tratamentos":
                    gerarRelatorioTratamentos(dataInicial, dataFinal);
                    break;
                case "Faturamento":
                    gerarRelatorioFaturamento(dataInicial, dataFinal);
                    break;
                case "Estoque":
                    gerarRelatorioEstoque();
                    break;
            }

            if (dadosRelatorio.isEmpty()) {
                vboxResultados.setVisible(true);
                vboxResultados.setManaged(true);
                tabelaRelatorio.setVisible(false);
                vboxEmptyState.setVisible(true);
                vboxEmptyState.setManaged(true);
                MainApplication.showErrorAlert("Sem Dados", "Nenhum dado encontrado para os critérios informados.");
            } else {
                vboxResultados.setVisible(true);
                vboxResultados.setManaged(true);
                tabelaRelatorio.setVisible(true);
                tabelaRelatorio.setItems(dadosRelatorio);
                vboxEmptyState.setVisible(false);
                vboxEmptyState.setManaged(false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro", "Erro ao gerar relatório: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro", "Erro ao gerar relatório. Tente novamente mais tarde.");
        }
    }

    private void gerarRelatorioConsultas(LocalDate dataInicial, LocalDate dataFinal) throws SQLException {
        List<Consulta> consultas = consultaDAO.listarTodas();

        // Aplicar apenas filtros de data (inclusivo nas datas limites)
        if (dataInicial != null) {
            consultas.removeIf(c -> c.getDataConsulta() != null && 
                c.getDataConsulta().toLocalDate().isBefore(dataInicial));
        }
        if (dataFinal != null) {
            consultas.removeIf(c -> c.getDataConsulta() != null && 
                c.getDataConsulta().toLocalDate().isAfter(dataFinal));
        }

        dadosRelatorio.addAll(consultas);

        // Configurar colunas
        TableColumn<Consulta, String> colData = new TableColumn<>("Data/Hora");
        colData.setCellValueFactory(cellData -> {
            Consulta c = cellData.getValue();
            if (c.getDataConsulta() != null) {
                return new SimpleStringProperty(c.getDataConsulta().format(dateTimeFormatter));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Consulta, String> colPaciente = new TableColumn<>("Paciente");
        colPaciente.setCellValueFactory(cellData -> {
            Consulta c = cellData.getValue();
            if (c.getPaciente() != null) {
                return new SimpleStringProperty(c.getPaciente().getNome());
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Consulta, String> colVeterinario = new TableColumn<>("Veterinário");
        colVeterinario.setCellValueFactory(cellData -> {
            Consulta c = cellData.getValue();
            if (c.getVeterinario() != null) {
                return new SimpleStringProperty(c.getVeterinario().getNome());
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Consulta, String> colDiagnostico = new TableColumn<>("Diagnóstico");
        colDiagnostico.setCellValueFactory(cellData -> {
            Consulta c = cellData.getValue();
            return new SimpleStringProperty(c.getDiagnostico() != null ? c.getDiagnostico() : "");
        });

        @SuppressWarnings("unchecked")
        ObservableList<TableColumn<?, ?>> colunas = (ObservableList<TableColumn<?, ?>>) (ObservableList<?>) tabelaRelatorio.getColumns();
        colunas.addAll(colData, colPaciente, colVeterinario, colDiagnostico);
    }

    private void gerarRelatorioTratamentos(LocalDate dataInicial, LocalDate dataFinal) throws SQLException {
        List<Tratamento> tratamentos = tratamentoDAO.listarTodos();

        // Aplicar apenas filtros de data (inclusivo nas datas limites)
        if (dataInicial != null || dataFinal != null) {
            tratamentos.removeIf(t -> {
                if (t.getConsulta() == null || t.getConsulta().getDataConsulta() == null) {
                    return true;
                }
                LocalDate dataConsulta = t.getConsulta().getDataConsulta().toLocalDate();
                if (dataInicial != null && dataConsulta.isBefore(dataInicial)) {
                    return true;
                }
                if (dataFinal != null && dataConsulta.isAfter(dataFinal)) {
                    return true;
                }
                return false;
            });
        }

        dadosRelatorio.addAll(tratamentos);

        // Configurar colunas
        TableColumn<Tratamento, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cellData -> {
            Tratamento t = cellData.getValue();
            if (t.getConsulta() != null && t.getConsulta().getDataConsulta() != null) {
                return new SimpleStringProperty(t.getConsulta().getDataConsulta().format(dateFormatter));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Tratamento, String> colPaciente = new TableColumn<>("Paciente");
        colPaciente.setCellValueFactory(cellData -> {
            Tratamento t = cellData.getValue();
            if (t.getConsulta() != null && t.getConsulta().getPaciente() != null) {
                return new SimpleStringProperty(t.getConsulta().getPaciente().getNome());
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Tratamento, String> colVeterinario = new TableColumn<>("Veterinário");
        colVeterinario.setCellValueFactory(cellData -> {
            Tratamento t = cellData.getValue();
            if (t.getConsulta() != null && t.getConsulta().getVeterinario() != null) {
                return new SimpleStringProperty(t.getConsulta().getVeterinario().getNome());
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Tratamento, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(cellData -> {
            Tratamento t = cellData.getValue();
            return new SimpleStringProperty(t.getDescricao() != null ? t.getDescricao() : "");
        });

        @SuppressWarnings({"unchecked", "rawtypes"})
        ObservableList<TableColumn> colunas = (ObservableList<TableColumn>) (ObservableList) tabelaRelatorio.getColumns();
        colunas.addAll(colData, colPaciente, colVeterinario, colDescricao);
    }

    private void gerarRelatorioFaturamento(LocalDate dataInicial, LocalDate dataFinal) throws SQLException {
        String statusSelecionado = comboStatus.getSelectionModel().getSelectedItem();
        
        // Se for "Pendentes", mostrar consultas pendentes
        if (statusSelecionado != null && statusSelecionado.equals("Pendentes")) {
            List<Consulta> consultasPendentes = pagamentoDAO.listarConsultasPendentes();
            
            // Aplicar filtros de data nas consultas pendentes
            if (dataInicial != null) {
                consultasPendentes.removeIf(c -> c.getDataConsulta() != null && 
                    c.getDataConsulta().toLocalDate().isBefore(dataInicial));
            }
            if (dataFinal != null) {
                consultasPendentes.removeIf(c -> c.getDataConsulta() != null && 
                    c.getDataConsulta().toLocalDate().isAfter(dataFinal));
            }
            
            dadosRelatorio.addAll(consultasPendentes);
            
            // Configurar colunas para consultas pendentes
            TableColumn<Consulta, String> colData = new TableColumn<>("Data");
            colData.setCellValueFactory(cellData -> {
                Consulta c = cellData.getValue();
                if (c.getDataConsulta() != null) {
                    return new SimpleStringProperty(c.getDataConsulta().format(dateFormatter));
                }
                return new SimpleStringProperty("");
            });

            TableColumn<Consulta, String> colPaciente = new TableColumn<>("Paciente");
            colPaciente.setCellValueFactory(cellData -> {
                Consulta c = cellData.getValue();
                if (c.getPaciente() != null) {
                    return new SimpleStringProperty(c.getPaciente().getNome());
                }
                return new SimpleStringProperty("");
            });

            TableColumn<Consulta, String> colVeterinario = new TableColumn<>("Veterinário");
            colVeterinario.setCellValueFactory(cellData -> {
                Consulta c = cellData.getValue();
                if (c.getVeterinario() != null) {
                    return new SimpleStringProperty(c.getVeterinario().getNome());
                }
                return new SimpleStringProperty("");
            });

            TableColumn<Consulta, String> colStatus = new TableColumn<>("Status");
            colStatus.setCellValueFactory(cellData -> new SimpleStringProperty("Pendente"));

            @SuppressWarnings({"unchecked", "rawtypes"})
            ObservableList<TableColumn> colunas = (ObservableList<TableColumn>) (ObservableList) tabelaRelatorio.getColumns();
            colunas.addAll(colData, colPaciente, colVeterinario, colStatus);
            return;
        }

        // Para "Todos" ou "Pagos", mostrar pagamentos
        List<Pagamento> pagamentos = pagamentoDAO.listarTodos();
        
        if (pagamentos != null && !pagamentos.isEmpty()) {
            // Aplicar filtros de data (inclusivo nas datas limites)
            if (dataInicial != null) {
                pagamentos.removeIf(p -> p.getDataPagamento() != null && 
                    p.getDataPagamento().toLocalDate().isBefore(dataInicial));
            }
            if (dataFinal != null) {
                pagamentos.removeIf(p -> p.getDataPagamento() != null && 
                    p.getDataPagamento().toLocalDate().isAfter(dataFinal));
            }

            dadosRelatorio.addAll(pagamentos);

            // Configurar colunas
            TableColumn<Pagamento, String> colData = new TableColumn<>("Data Pagamento");
            colData.setCellValueFactory(cellData -> {
                Pagamento p = cellData.getValue();
                if (p.getDataPagamento() != null) {
                    return new SimpleStringProperty(p.getDataPagamento().format(dateTimeFormatter));
                }
                return new SimpleStringProperty("");
            });

            TableColumn<Pagamento, String> colPaciente = new TableColumn<>("Paciente");
            colPaciente.setCellValueFactory(cellData -> {
                Pagamento p = cellData.getValue();
                if (p.getConsulta() != null && p.getConsulta().getPaciente() != null) {
                    return new SimpleStringProperty(p.getConsulta().getPaciente().getNome());
                }
                return new SimpleStringProperty("");
            });

            TableColumn<Pagamento, String> colValor = new TableColumn<>("Valor");
            colValor.setCellValueFactory(cellData -> {
                Pagamento p = cellData.getValue();
                return new SimpleStringProperty(currencyFormat.format(p.getValorTotal()));
            });

            TableColumn<Pagamento, String> colMetodo = new TableColumn<>("Método");
            colMetodo.setCellValueFactory(cellData -> {
                Pagamento p = cellData.getValue();
                return new SimpleStringProperty(p.getMetodoPagamento() != null ? p.getMetodoPagamento() : "");
            });

            TableColumn<Pagamento, String> colFuncionario = new TableColumn<>("Funcionário");
            colFuncionario.setCellValueFactory(cellData -> {
                Pagamento p = cellData.getValue();
                if (p.getFuncionario() != null) {
                    return new SimpleStringProperty(p.getFuncionario().getNome());
                }
                return new SimpleStringProperty("");
            });

            @SuppressWarnings({"unchecked", "rawtypes"})
            ObservableList<TableColumn> colunas = (ObservableList<TableColumn>) (ObservableList) tabelaRelatorio.getColumns();
            colunas.addAll(colData, colPaciente, colValor, colMetodo, colFuncionario);
        }
    }

    private void gerarRelatorioEstoque() throws SQLException {
        List<Medicamento> medicamentos = medicamentoDAO.listarTodos();
        dadosRelatorio.addAll(medicamentos);

        // Configurar colunas
        TableColumn<Medicamento, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(cellData -> {
            Medicamento m = cellData.getValue();
            return new SimpleStringProperty(m.getNome() != null ? m.getNome() : "");
        });

        TableColumn<Medicamento, String> colFabricante = new TableColumn<>("Fabricante");
        colFabricante.setCellValueFactory(cellData -> {
            Medicamento m = cellData.getValue();
            return new SimpleStringProperty(m.getFabricante() != null ? m.getFabricante() : "");
        });

        TableColumn<Medicamento, String> colQuantidade = new TableColumn<>("Quantidade Disponível");
        colQuantidade.setCellValueFactory(cellData -> {
            Medicamento m = cellData.getValue();
            return new SimpleStringProperty(String.valueOf(m.getQuantidade()));
        });

        @SuppressWarnings({"unchecked", "rawtypes"})
        ObservableList<TableColumn> colunas = (ObservableList<TableColumn>) (ObservableList) tabelaRelatorio.getColumns();
        colunas.addAll(colNome, colFabricante, colQuantidade);
    }

    private void limparFiltros() {
        comboTipoRelatorio.getSelectionModel().clearSelection();
        dpDataInicial.setValue(null);
        dpDataFinal.setValue(null);
        comboVeterinario.getSelectionModel().clearSelection();
        comboPaciente.getSelectionModel().clearSelection();
        comboStatus.getSelectionModel().clearSelection();
        vboxFiltros.setVisible(false);
        vboxFiltros.setManaged(false);
        vboxResultados.setVisible(false);
        vboxResultados.setManaged(false);
        dadosRelatorio.clear();
        tabelaRelatorio.getColumns().clear();
    }

    private void exportarPDF() {
        exportar("PDF");
    }

    private void exportarExcel() {
        exportar("Excel");
    }

    private void exportar(String formato) {
        if (dadosRelatorio.isEmpty()) {
            MainApplication.showErrorAlert("Erro", "Não há dados para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório");
        String extensao = formato.equals("PDF") ? ".pdf" : ".csv";
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(formato + " Files", "*" + extensao)
        );
        fileChooser.setInitialFileName("relatorio_" + tipoRelatorioAtual.toLowerCase() + extensao);

        Stage stage = (Stage) btnExportarPDF.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                if (formato.equals("Excel")) {
                    exportarParaCSV(file);
                } else {
                    exportarParaPDF(file);
                }
                MainApplication.showSuccessAlert("Sucesso", "Relatório exportado com sucesso!");
            } catch (IOException e) {
                e.printStackTrace();
                MainApplication.showErrorAlert("Erro", "Erro ao exportar relatório: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                MainApplication.showErrorAlert("Erro", "Erro ao exportar relatório: " + e.getMessage());
            }
        }
    }

    private void exportarParaCSV(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Escrever cabeçalho baseado no tipo de relatório
            switch (tipoRelatorioAtual) {
                case "Consultas":
                    writer.append("Data/Hora,Paciente,Veterinário,Diagnóstico\n");
                    break;
                case "Tratamentos":
                    writer.append("Data,Paciente,Veterinário,Descrição\n");
                    break;
                case "Faturamento":
                    if (comboStatus.getSelectionModel().getSelectedItem() != null && 
                        comboStatus.getSelectionModel().getSelectedItem().equals("Pendentes")) {
                        writer.append("Data,Paciente,Veterinário,Status\n");
                    } else {
                        writer.append("Data Pagamento,Paciente,Valor,Método,Funcionário\n");
                    }
                    break;
                case "Estoque":
                    writer.append("Nome,Fabricante,Quantidade Disponível\n");
                    break;
            }

            // Método auxiliar para escapar campos CSV
            java.util.function.Function<String, String> escapeCSV = (text) -> {
                if (text == null) return "";
                // Se contém vírgula, aspas ou quebra de linha, envolver em aspas e duplicar aspas internas
                if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
                    return "\"" + text.replace("\"", "\"\"") + "\"";
                }
                return text;
            };

            // Escrever dados
            for (Object item : dadosRelatorio) {
                if (item instanceof Consulta) {
                    Consulta c = (Consulta) item;
                    writer.append(escapeCSV.apply(c.getDataConsulta() != null ? 
                        c.getDataConsulta().format(dateTimeFormatter) : "")).append(",");
                    writer.append(escapeCSV.apply(c.getPaciente() != null ? c.getPaciente().getNome() : "")).append(",");
                    writer.append(escapeCSV.apply(c.getVeterinario() != null ? c.getVeterinario().getNome() : "")).append(",");
                    writer.append(escapeCSV.apply(c.getDiagnostico() != null ? c.getDiagnostico() : ""));
                } else if (item instanceof Tratamento) {
                    Tratamento t = (Tratamento) item;
                    writer.append(escapeCSV.apply(t.getConsulta() != null && t.getConsulta().getDataConsulta() != null ? 
                        t.getConsulta().getDataConsulta().format(dateFormatter) : "")).append(",");
                    writer.append(escapeCSV.apply(t.getConsulta() != null && t.getConsulta().getPaciente() != null ? 
                        t.getConsulta().getPaciente().getNome() : "")).append(",");
                    writer.append(escapeCSV.apply(t.getConsulta() != null && t.getConsulta().getVeterinario() != null ? 
                        t.getConsulta().getVeterinario().getNome() : "")).append(",");
                    writer.append(escapeCSV.apply(t.getDescricao() != null ? t.getDescricao() : ""));
                } else if (item instanceof Pagamento) {
                    Pagamento p = (Pagamento) item;
                    writer.append(escapeCSV.apply(p.getDataPagamento() != null ? 
                        p.getDataPagamento().format(dateTimeFormatter) : "")).append(",");
                    writer.append(escapeCSV.apply(p.getConsulta() != null && p.getConsulta().getPaciente() != null ? 
                        p.getConsulta().getPaciente().getNome() : "")).append(",");
                    writer.append(escapeCSV.apply(currencyFormat.format(p.getValorTotal()))).append(",");
                    writer.append(escapeCSV.apply(p.getMetodoPagamento() != null ? p.getMetodoPagamento() : "")).append(",");
                    writer.append(escapeCSV.apply(p.getFuncionario() != null ? p.getFuncionario().getNome() : ""));
                } else if (item instanceof Medicamento) {
                    Medicamento m = (Medicamento) item;
                    writer.append(escapeCSV.apply(m.getNome() != null ? m.getNome() : "")).append(",");
                    writer.append(escapeCSV.apply(m.getFabricante() != null ? m.getFabricante() : "")).append(",");
                    writer.append(escapeCSV.apply(String.valueOf(m.getQuantidade())));
                }
                writer.append("\n");
            }
        }
    }

    private void exportarParaPDF(File file) throws Exception {
        // Para Consultas e Tratamentos, usar formato hierárquico
        if (tipoRelatorioAtual.equals("Consultas") || tipoRelatorioAtual.equals("Tratamentos")) {
            exportarParaPDFHierarquico(file);
            return;
        }
        
        // Para outros tipos, usar formato de tabela tradicional
        exportarParaPDFTabela(file);
    }
    
    private void exportarParaPDFTabela(File file) throws Exception {
        org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
        org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
        document.addPage(page);

        org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);

        float margin = 50;
        float yPosition = 750;
        float tableWidth = 500;
        float tableLeft = margin;
        float lineHeight = 15;

        // Título
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Relatório de " + tipoRelatorioAtual);
        contentStream.endText();
        yPosition -= 30;

        // Informações do relatório
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(margin, yPosition);
        String dataGeracao = "Data de geração: " + LocalDate.now().format(dateFormatter);
        contentStream.showText(dataGeracao);
        contentStream.endText();
        yPosition -= 20;

        if (dpDataInicial.getValue() != null || dpDataFinal.getValue() != null) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(margin, yPosition);
            String periodo = "Período: ";
            if (dpDataInicial.getValue() != null) {
                periodo += dpDataInicial.getValue().format(dateFormatter);
            }
            periodo += " até ";
            if (dpDataFinal.getValue() != null) {
                periodo += dpDataFinal.getValue().format(dateFormatter);
            }
            contentStream.showText(periodo);
            contentStream.endText();
            yPosition -= 20;
        }

        yPosition -= 10;

        // Cabeçalho da tabela
        String[] headers = getHeaders();
        float colWidth = tableWidth / headers.length;
        float headerY = yPosition;

        // Desenhar retângulo do cabeçalho
        contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
        contentStream.addRect(tableLeft, headerY - lineHeight, tableWidth, lineHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(0f, 0f, 0f);

        // Texto do cabeçalho
        float xPos = tableLeft + 5;
        for (int i = 0; i < headers.length; i++) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
            contentStream.newLineAtOffset(xPos, headerY - 10);
            String headerText = headers[i].length() > 20 ? headers[i].substring(0, 20) : headers[i];
            contentStream.showText(headerText);
            contentStream.endText();
            xPos += colWidth;
        }

        // Linha do cabeçalho
        contentStream.setLineWidth(1f);
        contentStream.moveTo(tableLeft, headerY - lineHeight);
        contentStream.lineTo(tableLeft + tableWidth, headerY - lineHeight);
        contentStream.stroke();

        float currentY = headerY - lineHeight;

        // Dados
        for (Object item : dadosRelatorio) {
            if (currentY < 100) {
                // Nova página
                contentStream.close();
                page = new org.apache.pdfbox.pdmodel.PDPage();
                document.addPage(page);
                contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
                currentY = 750;
                
                // Redesenhar cabeçalho
                contentStream.setNonStrokingColor(0.9f, 0.9f, 0.9f);
                contentStream.addRect(tableLeft, currentY - lineHeight, tableWidth, lineHeight);
                contentStream.fill();
                contentStream.setNonStrokingColor(0f, 0f, 0f);
                
                xPos = tableLeft + 5;
                for (int i = 0; i < headers.length; i++) {
                    contentStream.beginText();
                    contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
                    contentStream.newLineAtOffset(xPos, currentY - 10);
                    String headerText = headers[i].length() > 20 ? headers[i].substring(0, 20) : headers[i];
                    contentStream.showText(headerText);
                    contentStream.endText();
                    xPos += colWidth;
                }
                
                contentStream.moveTo(tableLeft, currentY - lineHeight);
                contentStream.lineTo(tableLeft + tableWidth, currentY - lineHeight);
                contentStream.stroke();
                
                currentY -= lineHeight;
            }

            String[] rowData = getRowData(item);
            xPos = tableLeft + 5;
            for (int i = 0; i < rowData.length && i < headers.length; i++) {
                String cellText = rowData[i];
                if (cellText.length() > 25) {
                    cellText = cellText.substring(0, 22) + "...";
                }
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
                contentStream.newLineAtOffset(xPos, currentY - 10);
                contentStream.showText(cellText);
                contentStream.endText();
                xPos += colWidth;
            }

            // Linha separadora
            contentStream.moveTo(tableLeft, currentY - lineHeight);
            contentStream.lineTo(tableLeft + tableWidth, currentY - lineHeight);
            contentStream.stroke();

            currentY -= lineHeight;
        }

        // Rodapé
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 8);
        contentStream.newLineAtOffset(margin, 50);
        contentStream.showText("Total de registros: " + dadosRelatorio.size());
        contentStream.endText();

        contentStream.close();
        document.save(file);
        document.close();
    }
    
    private void exportarParaPDFHierarquico(File file) throws Exception {
        org.apache.pdfbox.pdmodel.PDDocument document = new org.apache.pdfbox.pdmodel.PDDocument();
        org.apache.pdfbox.pdmodel.PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
        document.addPage(page);

        org.apache.pdfbox.pdmodel.PDPageContentStream contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);

        float margin = 50;
        float yPosition = 750;
        float pageWidth = 500;
        float sectionSpacing = 20;
        
        // Título
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 18);
        contentStream.newLineAtOffset(margin, yPosition);
        contentStream.showText("Relatório de " + tipoRelatorioAtual);
        contentStream.endText();
        yPosition -= 30;

        // Informações do relatório
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(margin, yPosition);
        String dataGeracao = "Data de geração: " + LocalDate.now().format(dateFormatter);
        contentStream.showText(dataGeracao);
        contentStream.endText();
        yPosition -= 20;

        if (dpDataInicial.getValue() != null || dpDataFinal.getValue() != null) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(margin, yPosition);
            String periodo = "Período: ";
            if (dpDataInicial.getValue() != null) {
                periodo += dpDataInicial.getValue().format(dateFormatter);
            }
            periodo += " até ";
            if (dpDataFinal.getValue() != null) {
                periodo += dpDataFinal.getValue().format(dateFormatter);
            }
            contentStream.showText(periodo);
            contentStream.endText();
            yPosition -= 20;
        }

        yPosition -= 20;
        
        // Agrupar dados por veterinário e depois por paciente
        java.util.Map<Veterinario, java.util.Map<Paciente, java.util.List<Object>>> dadosAgrupados = new java.util.LinkedHashMap<>();
        
        for (Object item : dadosRelatorio) {
            Veterinario vet = null;
            Paciente pac = null;
            
            if (item instanceof Consulta) {
                Consulta c = (Consulta) item;
                vet = c.getVeterinario();
                pac = c.getPaciente();
            } else if (item instanceof Tratamento) {
                Tratamento t = (Tratamento) item;
                if (t.getConsulta() != null) {
                    vet = t.getConsulta().getVeterinario();
                    pac = t.getConsulta().getPaciente();
                }
            }
            
            if (vet != null && pac != null) {
                dadosAgrupados.computeIfAbsent(vet, k -> new java.util.LinkedHashMap<>())
                    .computeIfAbsent(pac, k -> new java.util.ArrayList<>())
                    .add(item);
            }
        }
        
        // Escrever dados agrupados
        for (java.util.Map.Entry<Veterinario, java.util.Map<Paciente, java.util.List<Object>>> entryVet : dadosAgrupados.entrySet()) {
            Veterinario veterinario = entryVet.getKey();
            
            // Verificar se precisa de nova página (deixar espaço para cabeçalho do veterinário)
            if (yPosition < 180) {
                contentStream.close();
                page = new org.apache.pdfbox.pdmodel.PDPage();
                document.addPage(page);
                contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
                yPosition = 750;
            }
            
            // Título do Veterinário - Desenhar fundo azul primeiro
            float alturaCabecalho = 28;
            float yBaseRetangulo = yPosition - alturaCabecalho;
            
            // Desenhar retângulo azul
            contentStream.setNonStrokingColor(0.2f, 0.4f, 0.8f);
            contentStream.addRect(margin, yBaseRetangulo, pageWidth, alturaCabecalho);
            contentStream.fill();
            
            // Escrever texto em branco sobre o fundo azul (centralizado verticalmente)
            contentStream.setNonStrokingColor(1f, 1f, 1f);
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 14);
            // Centralizar verticalmente: yBase + (altura/2) - ajuste para alinhamento do texto
            float yTexto = yBaseRetangulo + (alturaCabecalho / 2) - 5;
            contentStream.newLineAtOffset(margin + 5, yTexto);
            String nomeVet = veterinario.getNome() != null ? veterinario.getNome() : "Veterinário Desconhecido";
            if (veterinario.getCRMV() != null) {
                nomeVet += " - CRMV: " + veterinario.getCRMV();
            }
            // Truncar se muito longo para caber na página
            if (nomeVet.length() > 75) {
                nomeVet = nomeVet.substring(0, 72) + "...";
            }
            contentStream.showText(nomeVet);
            contentStream.endText();
            
            // Restaurar cor preta para o resto do conteúdo
            contentStream.setNonStrokingColor(0f, 0f, 0f);
            yPosition = yBaseRetangulo - 15; // Espaço após o cabeçalho
            
            // Para cada paciente deste veterinário
            for (java.util.Map.Entry<Paciente, java.util.List<Object>> entryPac : entryVet.getValue().entrySet()) {
                Paciente paciente = entryPac.getKey();
                java.util.List<Object> itens = entryPac.getValue();
                
                // Verificar se precisa de nova página
                if (yPosition < 200) {
                    contentStream.close();
                    page = new org.apache.pdfbox.pdmodel.PDPage();
                    document.addPage(page);
                    contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
                    yPosition = 750;
                }
                
                // Subtítulo do Paciente - com linha separadora
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(margin + 10, yPosition);
                String nomePac = paciente.getNome() != null ? paciente.getNome() : "Paciente Desconhecido";
                if (paciente.getEspecie() != null) {
                    nomePac += " (" + paciente.getEspecie();
                    if (paciente.getRaca() != null) {
                        nomePac += " - " + paciente.getRaca();
                    }
                    nomePac += ")";
                }
                contentStream.showText(nomePac);
                contentStream.endText();
                yPosition -= 18;
                
                // Linha separadora abaixo do nome do paciente
                contentStream.setLineWidth(0.5f);
                contentStream.setStrokingColor(0.7f, 0.7f, 0.7f);
                contentStream.moveTo(margin + 10, yPosition);
                contentStream.lineTo(margin + pageWidth - 10, yPosition);
                contentStream.stroke();
                contentStream.setStrokingColor(0f, 0f, 0f);
                yPosition -= 10;
                
                // Listar consultas/tratamentos deste paciente
                for (Object item : itens) {
                    if (yPosition < 100) {
                        contentStream.close();
                        page = new org.apache.pdfbox.pdmodel.PDPage();
                        document.addPage(page);
                        contentStream = new org.apache.pdfbox.pdmodel.PDPageContentStream(document, page);
                        yPosition = 750;
                    }
                    
                    if (item instanceof Consulta) {
                        Consulta c = (Consulta) item;
                        float alturaUsada = escreverConsultaNoPDF(contentStream, c, margin + 20, yPosition, pageWidth - 40);
                        yPosition -= alturaUsada;
                    } else if (item instanceof Tratamento) {
                        Tratamento t = (Tratamento) item;
                        float alturaUsada = escreverTratamentoNoPDF(contentStream, t, margin + 20, yPosition, pageWidth - 40);
                        yPosition -= alturaUsada;
                    }
                }
                
                yPosition -= sectionSpacing;
            }
            
            yPosition -= sectionSpacing;
        }

        // Rodapé
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 8);
        contentStream.newLineAtOffset(margin, 50);
        contentStream.showText("Total de registros: " + dadosRelatorio.size());
        contentStream.endText();

        contentStream.close();
        document.save(file);
        document.close();
    }
    
    private float escreverConsultaNoPDF(org.apache.pdfbox.pdmodel.PDPageContentStream contentStream, 
                                       Consulta consulta, float x, float y, float width) throws Exception {
        float currentY = y;
        float yInicial = y;
        
        // Data/Hora
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x, currentY);
        contentStream.showText("Data/Hora: ");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(x + 85, currentY);
        String dataHora = consulta.getDataConsulta() != null ? 
            consulta.getDataConsulta().format(dateTimeFormatter) : "Não informado";
        contentStream.showText(dataHora);
        contentStream.endText();
        currentY -= 18;
        
        // Diagnóstico
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x, currentY);
        contentStream.showText("Diagnóstico: ");
        contentStream.endText();
        currentY -= 18;
        
        String diagnostico = consulta.getDiagnostico() != null && !consulta.getDiagnostico().isEmpty() ? 
            consulta.getDiagnostico() : "Nenhum diagnóstico registrado.";
        
        // Quebrar diagnóstico em múltiplas linhas se necessário
        String[] linhas = quebrarTexto(diagnostico, 70);
        for (String linha : linhas) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
            contentStream.newLineAtOffset(x + 10, currentY);
            contentStream.showText(linha);
            contentStream.endText();
            currentY -= 13;
        }
        
        // Buscar tratamentos relacionados
        try {
            List<Tratamento> tratamentos = tratamentoDAO.listarPorConsulta(consulta.getId());
            if (!tratamentos.isEmpty()) {
                currentY -= 8;
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(x, currentY);
                contentStream.showText("Tratamentos Prescritos: ");
                contentStream.endText();
                currentY -= 18;
                
                for (Tratamento t : tratamentos) {
                    String descTratamento = t.getDescricao() != null ? t.getDescricao() : "Sem descrição";
                    String[] linhasTrat = quebrarTexto(descTratamento, 70);
                    for (String linha : linhasTrat) {
                        contentStream.beginText();
                        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
                        contentStream.newLineAtOffset(x + 10, currentY);
                        contentStream.showText("• " + linha);
                        contentStream.endText();
                        currentY -= 13;
                    }
                }
            }
        } catch (SQLException e) {
            // Ignorar erro ao buscar tratamentos
        }
        
        float alturaUsada = yInicial - currentY + 15;
        
        // Desenhar fundo cinza claro para destacar a consulta (após calcular altura)
        contentStream.setNonStrokingColor(0.95f, 0.95f, 0.95f);
        contentStream.addRect(x - 5, currentY, width + 10, alturaUsada);
        contentStream.fill();
        contentStream.setNonStrokingColor(0f, 0f, 0f);
        
        // Redesenhar todo o conteúdo sobre o fundo
        currentY = y;
        // Data/Hora
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x, currentY);
        contentStream.showText("Data/Hora: ");
        contentStream.endText();
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(x + 85, currentY);
        contentStream.showText(dataHora);
        contentStream.endText();
        currentY -= 18;
        
        // Diagnóstico
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x, currentY);
        contentStream.showText("Diagnóstico: ");
        contentStream.endText();
        currentY -= 18;
        
        for (String linha : linhas) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
            contentStream.newLineAtOffset(x + 10, currentY);
            contentStream.showText(linha);
            contentStream.endText();
            currentY -= 13;
        }
        
        // Tratamentos
        try {
            List<Tratamento> tratamentos = tratamentoDAO.listarPorConsulta(consulta.getId());
            if (!tratamentos.isEmpty()) {
                currentY -= 8;
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
                contentStream.newLineAtOffset(x, currentY);
                contentStream.showText("Tratamentos Prescritos: ");
                contentStream.endText();
                currentY -= 18;
                
                for (Tratamento t : tratamentos) {
                    String descTratamento = t.getDescricao() != null ? t.getDescricao() : "Sem descrição";
                    String[] linhasTrat = quebrarTexto(descTratamento, 70);
                    for (String linha : linhasTrat) {
                        contentStream.beginText();
                        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
                        contentStream.newLineAtOffset(x + 10, currentY);
                        contentStream.showText("• " + linha);
                        contentStream.endText();
                        currentY -= 13;
                    }
                }
            }
        } catch (SQLException e) {
            // Ignorar erro ao buscar tratamentos
        }
        
        return alturaUsada; // Retornar altura usada
    }
    
    private float escreverTratamentoNoPDF(org.apache.pdfbox.pdmodel.PDPageContentStream contentStream, 
                                         Tratamento tratamento, float x, float y, float width) throws Exception {
        float currentY = y;
        float yInicial = y;
        
        if (tratamento.getConsulta() == null) {
            return 20; // Altura mínima
        }
        
        Consulta consulta = tratamento.getConsulta();
        
        // Data da Consulta
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x, currentY);
        contentStream.showText("Data da Consulta: ");
        contentStream.endText();
        
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(x + 120, currentY);
        String data = consulta.getDataConsulta() != null ? 
            consulta.getDataConsulta().format(dateFormatter) : "Não informado";
        contentStream.showText(data);
        contentStream.endText();
        currentY -= 15;
        
        // Diagnóstico da Consulta
        if (consulta.getDiagnostico() != null && !consulta.getDiagnostico().isEmpty()) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
            contentStream.newLineAtOffset(x, currentY);
            contentStream.showText("Diagnóstico da Consulta: ");
            contentStream.endText();
            currentY -= 15;
            
            String[] linhasDiag = quebrarTexto(consulta.getDiagnostico(), 70);
            for (String linha : linhasDiag) {
                contentStream.beginText();
                contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
                contentStream.newLineAtOffset(x + 10, currentY);
                contentStream.showText(linha);
                contentStream.endText();
                currentY -= 12;
            }
        }
        
        // Descrição do Tratamento
        contentStream.beginText();
        contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD, 10);
        contentStream.newLineAtOffset(x, currentY);
        contentStream.showText("Tratamento: ");
        contentStream.endText();
        currentY -= 15;
        
        String descricao = tratamento.getDescricao() != null ? tratamento.getDescricao() : "Sem descrição";
        String[] linhas = quebrarTexto(descricao, 70);
        for (String linha : linhas) {
            contentStream.beginText();
            contentStream.setFont(org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA, 9);
            contentStream.newLineAtOffset(x + 10, currentY);
            contentStream.showText(linha);
            contentStream.endText();
            currentY -= 12;
        }
        
        return yInicial - currentY + 10; // Retornar altura usada
    }
    
    private String[] quebrarTexto(String texto, int maxLength) {
        if (texto == null || texto.isEmpty()) {
            return new String[]{" "};
        }
        
        java.util.List<String> linhas = new java.util.ArrayList<>();
        String[] palavras = texto.split(" ");
        StringBuilder linhaAtual = new StringBuilder();
        
        for (String palavra : palavras) {
            if (linhaAtual.length() + palavra.length() + 1 <= maxLength) {
                if (linhaAtual.length() > 0) {
                    linhaAtual.append(" ");
                }
                linhaAtual.append(palavra);
            } else {
                if (linhaAtual.length() > 0) {
                    linhas.add(linhaAtual.toString());
                    linhaAtual = new StringBuilder();
                }
                // Se a palavra é muito longa, quebrar ela
                if (palavra.length() > maxLength) {
                    int inicio = 0;
                    while (inicio < palavra.length()) {
                        int fim = Math.min(inicio + maxLength, palavra.length());
                        linhas.add(palavra.substring(inicio, fim));
                        inicio = fim;
                    }
                } else {
                    linhaAtual.append(palavra);
                }
            }
        }
        
        if (linhaAtual.length() > 0) {
            linhas.add(linhaAtual.toString());
        }
        
        return linhas.toArray(new String[0]);
    }

    private String[] getHeaders() {
        switch (tipoRelatorioAtual) {
            case "Consultas":
                return new String[]{"Data/Hora", "Paciente", "Veterinário", "Diagnóstico"};
            case "Tratamentos":
                return new String[]{"Data", "Paciente", "Veterinário", "Descrição"};
            case "Faturamento":
                if (comboStatus.getSelectionModel().getSelectedItem() != null && 
                    comboStatus.getSelectionModel().getSelectedItem().equals("Pendentes")) {
                    return new String[]{"Data", "Paciente", "Veterinário", "Status"};
                } else {
                    return new String[]{"Data Pagamento", "Paciente", "Valor", "Método", "Funcionário"};
                }
            case "Estoque":
                return new String[]{"Nome", "Fabricante", "Quantidade"};
            default:
                return new String[]{};
        }
    }

    private String[] getRowData(Object item) {
        if (item instanceof Consulta) {
            Consulta c = (Consulta) item;
            return new String[]{
                c.getDataConsulta() != null ? c.getDataConsulta().format(dateTimeFormatter) : "",
                c.getPaciente() != null ? c.getPaciente().getNome() : "",
                c.getVeterinario() != null ? c.getVeterinario().getNome() : "",
                c.getDiagnostico() != null ? c.getDiagnostico() : ""
            };
        } else if (item instanceof Tratamento) {
            Tratamento t = (Tratamento) item;
            return new String[]{
                t.getConsulta() != null && t.getConsulta().getDataConsulta() != null ? 
                    t.getConsulta().getDataConsulta().format(dateFormatter) : "",
                t.getConsulta() != null && t.getConsulta().getPaciente() != null ? 
                    t.getConsulta().getPaciente().getNome() : "",
                t.getConsulta() != null && t.getConsulta().getVeterinario() != null ? 
                    t.getConsulta().getVeterinario().getNome() : "",
                t.getDescricao() != null ? t.getDescricao() : ""
            };
        } else if (item instanceof Pagamento) {
            Pagamento p = (Pagamento) item;
            return new String[]{
                p.getDataPagamento() != null ? p.getDataPagamento().format(dateTimeFormatter) : "",
                p.getConsulta() != null && p.getConsulta().getPaciente() != null ? 
                    p.getConsulta().getPaciente().getNome() : "",
                currencyFormat.format(p.getValorTotal()),
                p.getMetodoPagamento() != null ? p.getMetodoPagamento() : "",
                p.getFuncionario() != null ? p.getFuncionario().getNome() : ""
            };
        } else if (item instanceof Medicamento) {
            Medicamento m = (Medicamento) item;
            return new String[]{
                m.getNome() != null ? m.getNome() : "",
                m.getFabricante() != null ? m.getFabricante() : "",
                String.valueOf(m.getQuantidade())
            };
        }
        return new String[]{};
    }
}

