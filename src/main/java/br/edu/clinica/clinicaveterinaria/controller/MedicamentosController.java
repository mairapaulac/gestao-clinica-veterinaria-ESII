package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.MedicamentoDAO;
import br.edu.clinica.clinicaveterinaria.model.Medicamento;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MedicamentosController implements Initializable {

    @FXML private Button btnAdicionar;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Medicamento> tabelaMedicamentos;
    @FXML private TableColumn<Medicamento, String> colNome;
    @FXML private TableColumn<Medicamento, String> colFabricante;
    @FXML private TableColumn<Medicamento, Integer> colQuantidade;
    @FXML private TableColumn<Medicamento, LocalDate> colValidade;

    private final MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
    private final ObservableList<Medicamento> listaMedicamentos = FXCollections.observableArrayList();
    private FilteredList<Medicamento> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColunas();
        carregarMedicamentosDoBanco();
        configurarBusca();
        configurarContextMenu();
        btnAdicionar.setOnAction(event -> showMedicamentoDialog(null));
    }

    private void configurarColunas() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colFabricante.setCellValueFactory(new PropertyValueFactory<>("fabricante"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colValidade.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        colValidade.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
    }

    private void carregarMedicamentosDoBanco() {
        try {
            listaMedicamentos.setAll(medicamentoDAO.listarTodos());
            filteredData = new FilteredList<>(listaMedicamentos, p -> true);
            tabelaMedicamentos.setItems(filteredData);
        } catch (SQLException e) {
            MainApplication.showErrorAlert("Erro de Banco de Dados", "Não foi possível carregar os medicamentos do banco de dados.");
            e.printStackTrace();
        }
    }

    private void configurarBusca() {
        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(medicamento -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return medicamento.getNome().toLowerCase().contains(lowerCaseFilter) ||
                       medicamento.getFabricante().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem verMaisItem = new MenuItem("Ver mais");
        MenuItem editarItem = new MenuItem("Editar");
        MenuItem excluirItem = new MenuItem("Excluir");

        verMaisItem.setOnAction(event -> handleVerMais(tabelaMedicamentos.getSelectionModel().getSelectedItem()));
        editarItem.setOnAction(event -> handleEditar(tabelaMedicamentos.getSelectionModel().getSelectedItem()));
        excluirItem.setOnAction(event -> handleExcluir(tabelaMedicamentos.getSelectionModel().getSelectedItem()));

        contextMenu.getItems().addAll(verMaisItem, new SeparatorMenuItem(), editarItem, excluirItem);

        tabelaMedicamentos.setRowFactory(tv -> {
            TableRow<Medicamento> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    private void handleVerMais(Medicamento medicamento) {
        if (medicamento != null) {
            try {
                Medicamento fullMedicamento = medicamentoDAO.buscarPorId(medicamento.getId());
                if (fullMedicamento != null) {
                    FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/br/edu/clinica/clinicaveterinaria/detalhes-medicamento-view.fxml"));
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Detalhes do Medicamento");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(btnAdicionar.getScene().getWindow());
                    dialogStage.setScene(new Scene(loader.load()));

                    DetalhesMedicamentoController controller = loader.getController();
                    controller.setMedicamento(fullMedicamento);

                    dialogStage.showAndWait();
                } else {
                    MainApplication.showErrorAlert("Erro", "Não foi possível encontrar os detalhes completos do medicamento selecionado.");
                }
            } catch (SQLException e) {
                MainApplication.showErrorAlert("Erro de Banco de Dados", "Falha ao buscar os detalhes do medicamento.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                MainApplication.showErrorAlert("Erro de Aplicação", "Falha ao abrir a tela de detalhes do medicamento.");
            }
        }
    }

    private void showMedicamentoDialog(Medicamento medicamento) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("/br/edu/clinica/clinicaveterinaria/cadastrar-medicamento-view.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(medicamento == null ? "Cadastrar Novo Medicamento" : "Editar Medicamento");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(btnAdicionar.getScene().getWindow());
            dialogStage.setScene(new Scene(loader.load()));

            CadastrarMedicamentoController controller = loader.getController();
            controller.setMedicamentoData(medicamento, listaMedicamentos);

            dialogStage.showAndWait();

            Medicamento result = controller.getNewMedicamento();
            if (result != null) {
                try {
                    if (medicamento == null) {
                        medicamentoDAO.inserir(result);
                        listaMedicamentos.add(result);
                    } else {
                        result.setId(medicamento.getId());
                        medicamentoDAO.atualizar(result);
                        int index = listaMedicamentos.indexOf(medicamento);
                        if (index != -1) {
                            listaMedicamentos.set(index, result);
                        }
                    }
                    tabelaMedicamentos.refresh();
                } catch (SQLException e) {
                    MainApplication.showErrorAlert("Erro de Banco de Dados", "Falha ao salvar o medicamento.");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro de Aplicação", "Falha ao abrir a tela de cadastro de medicamento.");
        }
    }


    private void handleEditar(Medicamento medicamento) {
        if (medicamento != null) {
            try {
                Medicamento fullMedicamento = medicamentoDAO.buscarPorId(medicamento.getId());
                if (fullMedicamento != null) {
                    showMedicamentoDialog(fullMedicamento);
                } else {
                    MainApplication.showErrorAlert("Erro", "Não foi possível encontrar os detalhes completos do medicamento selecionado.");
                }
            } catch (SQLException e) {
                MainApplication.showErrorAlert("Erro de Banco de Dados", "Falha ao buscar os detalhes do medicamento.");
                e.printStackTrace();
            }
        }
    }

    private void handleExcluir(Medicamento medicamento) {
        if (medicamento != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmação de Exclusão");
            alert.setHeaderText("Tem certeza que deseja excluir o medicamento: " + medicamento.getNome() + "?");
            alert.setContentText("Esta ação não pode ser desfeita.");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                try {
                    medicamentoDAO.excluir(medicamento.getId());
                    
                    // Recarregar dados do banco para garantir consistência
                    carregarMedicamentosDoBanco();
                    
                    MainApplication.showSuccessAlert("Sucesso", "Medicamento excluído com sucesso!");
                } catch (SQLException e) {
                    MainApplication.showErrorAlert("Erro de Banco de Dados", "Falha ao excluir o medicamento: " + e.getMessage());
                    e.printStackTrace();
                }
                }
            });
        }
    }
}
