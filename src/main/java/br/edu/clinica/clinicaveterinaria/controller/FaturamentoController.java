package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.PagamentoDAO;
import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Pagamento;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class FaturamentoController implements Initializable {

    @FXML private ToggleButton togglePendentes;
    @FXML private ToggleButton togglePagos;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Consulta> tabelaConsultas;
    @FXML private TableColumn<Consulta, String> colData;
    @FXML private TableColumn<Consulta, String> colPaciente;
    @FXML private TableColumn<Consulta, String> colVeterinario;
    @FXML private TableColumn<Consulta, String> colDiagnostico;
    @FXML private TableColumn<Consulta, String> colAcoes;
    
    @FXML private TableView<Pagamento> tabelaPagamentos;
    @FXML private TableColumn<Pagamento, String> colDataPagamento;
    @FXML private TableColumn<Pagamento, String> colPacientePagamento;
    @FXML private TableColumn<Pagamento, String> colVeterinarioPagamento;
    @FXML private TableColumn<Pagamento, String> colValorPagamento;
    @FXML private TableColumn<Pagamento, String> colMetodoPagamento;
    @FXML private TableColumn<Pagamento, String> colFuncionarioPagamento;
    
    @FXML private VBox vboxPendentes;
    @FXML private VBox vboxPagos;
    @FXML private VBox vboxEmptyState;
    @FXML private Label lblTotalMes;
    @FXML private Label lblTotalMesLabel;

    private PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private ObservableList<Consulta> listaConsultas = FXCollections.observableArrayList();
    private FilteredList<Consulta> filteredData;
    private ObservableList<Pagamento> listaPagamentos = FXCollections.observableArrayList();
    private FilteredList<Pagamento> filteredDataPagos;
    
    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        configurarColunasPagos();
        configurarEventos();
        configurarBusca();
        configurarToggle();
        carregarConsultasPendentes();
    }

    private void configurarToggle() {
        ToggleGroup toggleGroup = new ToggleGroup();
        togglePendentes.setToggleGroup(toggleGroup);
        togglePagos.setToggleGroup(toggleGroup);
        
        togglePendentes.setOnAction(event -> {
            if (togglePendentes.isSelected()) {
                mostrarPendentes();
            }
        });
        
        togglePagos.setOnAction(event -> {
            if (togglePagos.isSelected()) {
                mostrarPagos();
            }
        });
    }

    private void mostrarPendentes() {
        vboxPendentes.setVisible(true);
        vboxPendentes.setManaged(true);
        vboxPagos.setVisible(false);
        vboxPagos.setManaged(false);
        lblTotalMes.setVisible(false);
        lblTotalMes.setManaged(false);
        lblTotalMesLabel.setVisible(false);
        lblTotalMesLabel.setManaged(false);
        carregarConsultasPendentes();
    }

    private void mostrarPagos() {
        vboxPendentes.setVisible(false);
        vboxPendentes.setManaged(false);
        vboxPagos.setVisible(true);
        vboxPagos.setManaged(true);
        lblTotalMes.setVisible(true);
        lblTotalMes.setManaged(true);
        lblTotalMesLabel.setVisible(true);
        lblTotalMesLabel.setManaged(true);
        carregarPagamentos();
    }

    private void configurarColunas() {
        colData.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataConsulta() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataConsulta()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        colPaciente.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPaciente() != null) {
                return new SimpleStringProperty(cellData.getValue().getPaciente().getNome());
            }
            return new SimpleStringProperty("");
        });

        colVeterinario.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVeterinario() != null) {
                return new SimpleStringProperty(cellData.getValue().getVeterinario().getNome());
            }
            return new SimpleStringProperty("");
        });

        colDiagnostico.setCellValueFactory(cellData -> {
            String diagnostico = cellData.getValue().getDiagnostico();
            if (diagnostico != null && diagnostico.length() > 50) {
                return new SimpleStringProperty(diagnostico.substring(0, 50) + "...");
            }
            return new SimpleStringProperty(diagnostico != null ? diagnostico : "");
        });

        colAcoes.setCellValueFactory(cellData -> new SimpleStringProperty("Faturar"));
        colAcoes.setCellFactory(column -> new TableCell<Consulta, String>() {
            private final Button btnFaturar = new Button("Faturar");
            {
                btnFaturar.getStyleClass().add("btn-primary");
                btnFaturar.setStyle("-fx-font-size: 11px; -fx-padding: 6px 12px;");
                btnFaturar.setOnAction(event -> {
                    Consulta consulta = getTableView().getItems().get(getIndex());
                    abrirTelaFaturar(consulta);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnFaturar);
                }
            }
        });
    }

    private void configurarColunasPagos() {
        colDataPagamento.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataPagamento() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataPagamento()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        colPacientePagamento.setCellValueFactory(cellData -> {
            if (cellData.getValue().getConsulta() != null && 
                cellData.getValue().getConsulta().getPaciente() != null) {
                return new SimpleStringProperty(cellData.getValue().getConsulta().getPaciente().getNome());
            }
            return new SimpleStringProperty("");
        });

        colVeterinarioPagamento.setCellValueFactory(cellData -> {
            if (cellData.getValue().getConsulta() != null && 
                cellData.getValue().getConsulta().getVeterinario() != null) {
                return new SimpleStringProperty(cellData.getValue().getConsulta().getVeterinario().getNome());
            }
            return new SimpleStringProperty("");
        });

        colValorPagamento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(currencyFormat.format(cellData.getValue().getValorTotal())));

        colMetodoPagamento.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMetodoPagamento() != null 
                ? cellData.getValue().getMetodoPagamento() : ""));

        colFuncionarioPagamento.setCellValueFactory(cellData -> {
            if (cellData.getValue().getFuncionario() != null) {
                return new SimpleStringProperty(cellData.getValue().getFuncionario().getNome());
            }
            return new SimpleStringProperty("");
        });
    }

    private void configurarEventos() {
        // Eventos já configurados nos botões toggle e na célula da tabela
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (togglePendentes.isSelected() && filteredData != null) {
                aplicarFiltroPendentes(newValue);
            } else if (togglePagos.isSelected() && filteredDataPagos != null) {
                aplicarFiltroPagos(newValue);
            }
        });
    }

    private void aplicarFiltroPendentes(String filtroTexto) {
        if (filteredData == null) return;
        
        String filtro = filtroTexto != null ? filtroTexto.toLowerCase() : "";
        filteredData.setPredicate(consulta -> {
            if (filtro.isEmpty()) {
                return true;
            }
            
            String paciente = consulta.getPaciente() != null && consulta.getPaciente().getNome() != null
                    ? consulta.getPaciente().getNome().toLowerCase() : "";
            String veterinario = consulta.getVeterinario() != null && consulta.getVeterinario().getNome() != null
                    ? consulta.getVeterinario().getNome().toLowerCase() : "";
            String diagnostico = consulta.getDiagnostico() != null
                    ? consulta.getDiagnostico().toLowerCase() : "";
            
            return paciente.contains(filtro) || veterinario.contains(filtro) || diagnostico.contains(filtro);
        });
    }

    private void aplicarFiltroPagos(String filtroTexto) {
        if (filteredDataPagos == null) return;
        
        String filtro = filtroTexto != null ? filtroTexto.toLowerCase() : "";
        filteredDataPagos.setPredicate(pagamento -> {
            if (filtro.isEmpty()) {
                return true;
            }
            
            String paciente = pagamento.getConsulta() != null && 
                pagamento.getConsulta().getPaciente() != null &&
                pagamento.getConsulta().getPaciente().getNome() != null
                    ? pagamento.getConsulta().getPaciente().getNome().toLowerCase() : "";
            String veterinario = pagamento.getConsulta() != null && 
                pagamento.getConsulta().getVeterinario() != null &&
                pagamento.getConsulta().getVeterinario().getNome() != null
                    ? pagamento.getConsulta().getVeterinario().getNome().toLowerCase() : "";
            
            return paciente.contains(filtro) || veterinario.contains(filtro);
        });
    }

    private void carregarConsultasPendentes() {
        try {
            List<Consulta> consultas = pagamentoDAO.listarConsultasPendentes();
            listaConsultas.setAll(consultas);
            
            filteredData = new FilteredList<>(listaConsultas, p -> true);
            tabelaConsultas.setItems(filteredData);
            
            if (consultas.isEmpty()) {
                tabelaConsultas.setVisible(false);
                vboxEmptyState.setVisible(true);
                vboxEmptyState.setManaged(true);
            } else {
                tabelaConsultas.setVisible(true);
                vboxEmptyState.setVisible(false);
                vboxEmptyState.setManaged(false);
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar consultas pendentes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarPagamentos() {
        try {
            List<Pagamento> pagamentos = pagamentoDAO.listarTodos();
            listaPagamentos.setAll(pagamentos);
            
            filteredDataPagos = new FilteredList<>(listaPagamentos, p -> true);
            tabelaPagamentos.setItems(filteredDataPagos);
            
            calcularTotalMes();
            
            if (pagamentos.isEmpty()) {
                tabelaPagamentos.setVisible(false);
                vboxEmptyState.setVisible(true);
                vboxEmptyState.setManaged(true);
            } else {
                tabelaPagamentos.setVisible(true);
                vboxEmptyState.setVisible(false);
                vboxEmptyState.setManaged(false);
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar pagamentos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void calcularTotalMes() {
        LocalDate hoje = LocalDate.now();
        float total = 0.0f;
        
        for (Pagamento pagamento : listaPagamentos) {
            if (pagamento.getDataPagamento() != null) {
                LocalDate dataPagamento = pagamento.getDataPagamento().toLocalDate();
                if (dataPagamento.getYear() == hoje.getYear() && 
                    dataPagamento.getMonth() == hoje.getMonth()) {
                    total += pagamento.getValorTotal();
                }
            }
        }
        
        lblTotalMes.setText(currencyFormat.format(total));
    }

    private void abrirTelaFaturar(Consulta consulta) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/faturar-consulta-view.fxml"));
            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Faturar Consulta");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(tabelaConsultas.getScene().getWindow());
            dialogStage.setScene(new javafx.scene.Scene(loader.load()));
            MainApplication.setStageIcon(dialogStage);

            FaturarConsultaController controller = loader.getController();
            controller.setConsulta(consulta);

            dialogStage.showAndWait();
            
            // Sempre recarregar ambas as listas após fechar a janela
            // para garantir que as mudanças sejam refletidas
            if (controller.foiPagamentoRealizado()) {
                // Se pagamento foi realizado, recarregar ambas as listas
                carregarConsultasPendentes();
                carregarPagamentos();
                
                // Mudar automaticamente para a aba de pagos para mostrar o pagamento recém-criado
                if (!togglePagos.isSelected()) {
                    togglePagos.setSelected(true);
                    mostrarPagos();
                } else {
                    // Se já estava na aba de pagos, apenas recarregar
                    mostrarPagos();
                }
            } else {
                // Se não houve pagamento, apenas recarregar a lista atual
                if (togglePendentes.isSelected()) {
                    carregarConsultasPendentes();
                } else if (togglePagos.isSelected()) {
                    carregarPagamentos();
                }
            }
        } catch (java.io.IOException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao abrir tela de faturamento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
