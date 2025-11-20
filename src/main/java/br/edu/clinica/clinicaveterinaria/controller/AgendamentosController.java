package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.ConsultaDAO;
import br.edu.clinica.clinicaveterinaria.model.Consulta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class AgendamentosController implements Initializable {

    @FXML private Button btnHoje;
    @FXML private Button btnMesAnterior;
    @FXML private Button btnMesProximo;
    @FXML private Button btnNovoAgendamento;
    @FXML private GridPane calendarioGrid;
    @FXML private Label lblMesAno;

    private YearMonth currentYearMonth;
    private LocalDate selectedDate;
    private VBox selectedCell = null;
    private ConsultaDAO consultaDAO = new ConsultaDAO();
    private ObservableList<Consulta> consultasList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        carregarConsultasDoBanco();
        gerarCalendario();
    }
    
    private void carregarConsultasDoBanco() {
        try {
            consultasList.setAll(consultaDAO.listarTodas());
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar consultas: " + e.getMessage());
        }
    }

    @FXML
    void onMesAnterior() {
        currentYearMonth = currentYearMonth.minusMonths(1);
        gerarCalendario();
    }

    @FXML
    void onMesProximo() {
        currentYearMonth = currentYearMonth.plusMonths(1);
        gerarCalendario();
    }

    @FXML
    void onHoje() {
        currentYearMonth = YearMonth.now();
        selectedDate = LocalDate.now();
        gerarCalendario();
    }

    @FXML
    void abrirModalNovoAgendamento() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/agendamento-view.fxml"));
            Parent root = loader.load();
            Stage modalStage = new Stage();
            modalStage.setTitle("Novo Agendamento");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner((Stage) btnNovoAgendamento.getScene().getWindow());
            modalStage.showAndWait();
            carregarConsultasDoBanco();
            gerarCalendario();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de agendamento.");
        }
    }

    @FXML
    void onDiaSelecionado(MouseEvent event) {
        if (selectedCell != null) {
            selectedCell.getStyleClass().remove("cell-selecionada");
        }
        selectedCell = (VBox) event.getSource();
        selectedCell.getStyleClass().add("cell-selecionada");
        selectedDate = (LocalDate) selectedCell.getProperties().get("date");

        boolean hasAppointments = consultasList.stream()
                .anyMatch(c -> c.getDataConsulta() != null && c.getDataConsulta().toLocalDate().equals(selectedDate));

        if (hasAppointments) {
            mostrarDetalhesAgendamento(selectedDate);
        }
    }

    private void mostrarDetalhesAgendamento(LocalDate data) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner((Stage) calendarioGrid.getScene().getWindow());
        modalStage.setTitle("Agendamentos para " + data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        TableView<Consulta> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Consulta, String> colHorario = new TableColumn<>("Horário");
        colHorario.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDataConsulta() != null) {
                return new SimpleStringProperty(cellData.getValue().getDataConsulta().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Consulta, String> colPaciente = new TableColumn<>("Paciente");
        colPaciente.setCellValueFactory(cellData -> {
            if (cellData.getValue().getPaciente() != null) {
                return new SimpleStringProperty(cellData.getValue().getPaciente().getNome());
            }
            return new SimpleStringProperty("");
        });

        TableColumn<Consulta, String> colVeterinario = new TableColumn<>("Veterinário(a)");
        colVeterinario.setCellValueFactory(cellData -> {
            if (cellData.getValue().getVeterinario() != null) {
                return new SimpleStringProperty(cellData.getValue().getVeterinario().getNome());
            }
            return new SimpleStringProperty("");
        });

        tableView.getColumns().addAll(colHorario, colPaciente, colVeterinario);

        FilteredList<Consulta> filteredConsultas = new FilteredList<>(consultasList);
        filteredConsultas.setPredicate(c -> c.getDataConsulta() != null && c.getDataConsulta().toLocalDate().equals(data));
        tableView.setItems(filteredConsultas);

        ContextMenu contextMenu = new ContextMenu();
        
        MenuItem registrarTratamentoItem = new MenuItem("Registrar Tratamento");
        registrarTratamentoItem.setOnAction(event -> {
            Consulta item = tableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
            abrirTelaRegistrarTratamento(item);
        });
        
        MenuItem deleteItem = new MenuItem("Excluir");
        deleteItem.setOnAction(event -> {
            Consulta item = tableView.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Você tem certeza que deseja excluir este agendamento?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Confirmar Exclusão");
            String nomePaciente = item.getPaciente() != null ? item.getPaciente().getNome() : "N/A";
            String hora = item.getDataConsulta() != null ? item.getDataConsulta().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
            alert.setHeaderText(String.format("Excluir agendamento de %s às %s?",
                nomePaciente, hora));

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        consultaDAO.deletarConsulta(item.getId());
                        carregarConsultasDoBanco();
                        gerarCalendario();
                        mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Agendamento excluído com sucesso!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao excluir agendamento: " + e.getMessage());
                    }
                }
            });
        });
        
        contextMenu.getItems().addAll(registrarTratamentoItem, deleteItem);

        tableView.setRowFactory(tv -> {
            TableRow<Consulta> row = new TableRow<>();
            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });
            return row;
        });

        Label labelConsultas = new Label("Consultas do dia:");
        labelConsultas.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        VBox layout = new VBox(10, labelConsultas, tableView);
        layout.setPadding(new Insets(15));
        VBox.setVgrow(tableView, Priority.ALWAYS);

        Scene scene = new Scene(layout, 600, 400);
        scene.getStylesheets().add(getClass().getResource("/br/edu/clinica/clinicaveterinaria/css/pacientes.css").toExternalForm());
        modalStage.setScene(scene);
        modalStage.showAndWait();
    }

    private void gerarCalendario() {
        calendarioGrid.getChildren().clear();
        Locale locale = new Locale("pt", "BR");
        String mes = currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, locale);
        lblMesAno.setText(mes.substring(0, 1).toUpperCase() + mes.substring(1) + " " + currentYearMonth.getYear());

        LocalDate primeiroDiaDoMes = currentYearMonth.atDay(1);
        int offset = primeiroDiaDoMes.getDayOfWeek().getValue() % 7;
        LocalDate diaIterator = primeiroDiaDoMes.minusDays(offset);

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                VBox cell = createCell(diaIterator);
                calendarioGrid.add(cell, col, row);
                diaIterator = diaIterator.plusDays(1);
            }
        }
    }

    private VBox createCell(LocalDate date) {
        VBox cell = new VBox(5);
        cell.getStyleClass().add("calendar-cell");
        cell.setAlignment(Pos.CENTER);
        cell.setOnMouseClicked(this::onDiaSelecionado);
        cell.getProperties().put("date", date);

        Label diaNumero = new Label(String.valueOf(date.getDayOfMonth()));
        diaNumero.getStyleClass().add("dia-numero");
        cell.getChildren().add(diaNumero);

        if (consultasList.stream().anyMatch(c -> c.getDataConsulta() != null && c.getDataConsulta().toLocalDate().equals(date))) {
            Circle indicador = new Circle(4);
            indicador.getStyleClass().add("indicador-agendamento");
            cell.getChildren().add(indicador);
        }
        if (!date.getMonth().equals(currentYearMonth.getMonth())) {
            cell.getStyleClass().add("outro-mes");
        }
        if (date.equals(selectedDate)) {
            cell.getStyleClass().add("cell-selecionada");
            selectedCell = cell;
        }
        return cell;
    }
    
    private void abrirTelaRegistrarTratamento(Consulta consulta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/registrar-tratamento-view.fxml"));
            Parent root = loader.load();
            
            RegistrarTratamentoController controller = loader.getController();
            controller.setConsulta(consulta);
            
            Stage modalStage = new Stage();
            modalStage.setTitle("Registrar Tratamento");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner((Stage) calendarioGrid.getScene().getWindow());
            modalStage.showAndWait();
            
            carregarConsultasDoBanco();
            gerarCalendario();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de registro de tratamento.");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
