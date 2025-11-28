package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.ConsultaDAO;
import br.edu.clinica.clinicaveterinaria.dao.PacienteDAO;
import br.edu.clinica.clinicaveterinaria.dao.TratamentoDAO;
import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.HistoricoItem;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Tratamento;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class HistoricoPacienteController implements Initializable {

    @FXML private Label lblNomePaciente;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaca;
    @FXML private Label lblTutor;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> comboFiltroTipo;
    @FXML private TableView<HistoricoItem> tabelaHistorico;
    @FXML private TableColumn<HistoricoItem, String> colData;
    @FXML private TableColumn<HistoricoItem, String> colTipo;
    @FXML private TableColumn<HistoricoItem, String> colVeterinario;
    @FXML private TableColumn<HistoricoItem, String> colDescricao;
    @FXML private TableColumn<HistoricoItem, String> colAcoes;
    @FXML private Button btnFechar;
    @FXML private Button btnCadastrarPaciente;
    @FXML private VBox vboxEmptyState;

    private Paciente paciente;
    private PacienteDAO pacienteDAO = new PacienteDAO();
    private ConsultaDAO consultaDAO = new ConsultaDAO();
    private TratamentoDAO tratamentoDAO = new TratamentoDAO();
    private ObservableList<HistoricoItem> listaHistorico = FXCollections.observableArrayList();
    private FilteredList<HistoricoItem> filteredData;

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        carregarDadosPaciente();
        carregarHistorico();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        configurarFiltros();
        configurarBusca();
        configurarEventos();
    }

    private void configurarColunas() {
        colData.setCellValueFactory(cellData -> {
            if (cellData.getValue().getData() != null) {
                return new SimpleStringProperty(cellData.getValue().getData()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo()));
        colTipo.setCellFactory(column -> new TableCell<HistoricoItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                // Limpa completamente o estilo e texto quando vazio
                if (empty || item == null || item.isEmpty()) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    getStyleClass().clear();
                } else {
                    setText(item);
                    setAlignment(javafx.geometry.Pos.CENTER);
                    // Garantir que a célula use todo o espaço e centralize o conteúdo
                    setMaxWidth(Double.MAX_VALUE);
                    getStyleClass().clear();
                    getStyleClass().add("type-badge");
                    if ("CONSULTA".equals(item)) {
                        getStyleClass().add("type-consulta");
                    } else if ("TRATAMENTO".equals(item)) {
                        getStyleClass().add("type-tratamento");
                    }
                }
            }
        });

        colVeterinario.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVeterinario() != null) {
                return new SimpleStringProperty(cellData.getValue().getVeterinario().getNome());
            }
            return new SimpleStringProperty("");
        });

        colDescricao.setCellValueFactory(cellData -> {
            String descricao = cellData.getValue().getDescricao();
            if (descricao != null && descricao.length() > 100) {
                return new SimpleStringProperty(descricao.substring(0, 100) + "...");
            }
            return new SimpleStringProperty(descricao != null ? descricao : "");
        });

        colAcoes.setCellValueFactory(cellData -> new SimpleStringProperty("Ver Detalhes"));
        colAcoes.setCellFactory(column -> new TableCell<HistoricoItem, String>() {
            private final Button btnDetalhes = new Button("Ver Detalhes");
            {
                btnDetalhes.getStyleClass().add("btn-primary");
                btnDetalhes.setStyle("-fx-font-size: 11px; -fx-padding: 6px 12px;");
                btnDetalhes.setOnAction(event -> {
                    HistoricoItem item = getTableView().getItems().get(getIndex());
                    mostrarDetalhes(item);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDetalhes);
                }
            }
        });
    }

    private void configurarFiltros() {
        ObservableList<String> tipos = FXCollections.observableArrayList("Todos", "Consulta", "Tratamento");
        comboFiltroTipo.setItems(tipos);
        comboFiltroTipo.getSelectionModel().selectFirst();
        comboFiltroTipo.setOnAction(event -> aplicarFiltros());
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros());
    }

    private void configurarEventos() {
        btnFechar.setOnAction(event -> fecharJanela());
        btnCadastrarPaciente.setOnAction(event -> abrirCadastroPaciente());
    }

    private void carregarDadosPaciente() {
        if (paciente == null) {
            MainApplication.showErrorAlert("Erro", "Paciente não encontrado. Deseja cadastrá-lo agora?");
            btnCadastrarPaciente.setVisible(true);
            btnCadastrarPaciente.setManaged(true);
            return;
        }

        try {
            Paciente pacienteCompleto = pacienteDAO.buscarPorId(paciente.getId());
            if (pacienteCompleto == null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Paciente Não Encontrado");
                alert.setHeaderText("Paciente não encontrado.");
                alert.setContentText("Deseja cadastrá-lo agora?");

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        abrirCadastroPaciente();
                    } else {
                        fecharJanela();
                    }
                });
                return;
            }

            paciente = pacienteCompleto;
            lblNomePaciente.setText(paciente.getNome() != null ? paciente.getNome() : "-");
            lblEspecie.setText(paciente.getEspecie() != null ? paciente.getEspecie() : "-");
            lblRaca.setText(paciente.getRaca() != null ? paciente.getRaca() : "-");
            if (paciente.getProprietario() != null) {
                lblTutor.setText(paciente.getProprietario().getNome() != null ? paciente.getProprietario().getNome() : "-");
            } else {
                lblTutor.setText("-");
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar dados do paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarHistorico() {
        if (paciente == null) return;

        try {
            listaHistorico.clear();

            // Carrega consultas
            List<Consulta> consultas = consultaDAO.listarPorPaciente(paciente.getId());
            for (Consulta consulta : consultas) {
                listaHistorico.add(new HistoricoItem(consulta));
            }

            // Carrega tratamentos
            List<Tratamento> tratamentos = tratamentoDAO.listarPorPaciente(paciente.getId());
            for (Tratamento tratamento : tratamentos) {
                listaHistorico.add(new HistoricoItem(tratamento));
            }

            // Ordena por data (mais recente primeiro)
            listaHistorico.sort(Comparator.comparing(HistoricoItem::getData, Comparator.nullsLast(Comparator.reverseOrder())));

            filteredData = new FilteredList<>(listaHistorico, p -> true);
            tabelaHistorico.setItems(filteredData);

            aplicarFiltros();

            // Verifica se está vazio
            if (listaHistorico.isEmpty()) {
                tabelaHistorico.setVisible(false);
                vboxEmptyState.setVisible(true);
                vboxEmptyState.setManaged(true);
            } else {
                tabelaHistorico.setVisible(true);
                vboxEmptyState.setVisible(false);
                vboxEmptyState.setManaged(false);
            }
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao carregar histórico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void aplicarFiltros() {
        if (filteredData == null) return;

        String filtroTexto = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";
        String filtroTipo = comboFiltroTipo.getSelectionModel().getSelectedItem();

        filteredData.setPredicate(item -> {
            // Filtro por tipo
            if (filtroTipo != null && !filtroTipo.equals("Todos")) {
                if (filtroTipo.equals("Consulta") && !item.getTipo().equals("CONSULTA")) {
                    return false;
                }
                if (filtroTipo.equals("Tratamento") && !item.getTipo().equals("TRATAMENTO")) {
                    return false;
                }
            }

            // Filtro por texto
            if (filtroTexto.isEmpty()) {
                return true;
            }

            String descricao = item.getDescricao() != null ? item.getDescricao().toLowerCase() : "";
            String diagnostico = item.getDiagnostico() != null ? item.getDiagnostico().toLowerCase() : "";
            String veterinario = item.getVeterinario() != null && item.getVeterinario().getNome() != null 
                    ? item.getVeterinario().getNome().toLowerCase() : "";

            return descricao.contains(filtroTexto) || 
                   diagnostico.contains(filtroTexto) || 
                   veterinario.contains(filtroTexto);
        });
        
        // Força atualização das células para evitar células vazias com estilo
        tabelaHistorico.refresh();
    }

    private void mostrarDetalhes(HistoricoItem item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/detalhes-historico-view.fxml"));
            Scene scene = new Scene(loader.load());

            DetalhesHistoricoController controller = loader.getController();
            controller.setHistoricoItem(item);

            Stage stage = new Stage();
            stage.setTitle("Detalhes da " + item.getTipo());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnFechar.getScene().getWindow());
            stage.setScene(scene);
            MainApplication.setStageIcon(stage);
            stage.setMinWidth(750);
            stage.setMinHeight(650);
            stage.setWidth(850);
            stage.setHeight(750);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro", "Erro ao abrir detalhes: " + e.getMessage());
        }
    }

    private void abrirCadastroPaciente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-paciente-view.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cadastrar Novo Paciente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnFechar.getScene().getWindow());
            dialogStage.setScene(new Scene(loader.load()));
            MainApplication.setStageIcon(dialogStage);

            dialogStage.showAndWait();
            
            // Recarrega o paciente após cadastro
            if (paciente != null) {
                try {
                    Paciente pacienteAtualizado = pacienteDAO.buscarPorId(paciente.getId());
                    if (pacienteAtualizado != null) {
                        setPaciente(pacienteAtualizado);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            MainApplication.showErrorAlert("Erro", "Erro ao abrir tela de cadastro de paciente.");
            e.printStackTrace();
        }
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnFechar.getScene().getWindow();
        stage.close();
    }
}

