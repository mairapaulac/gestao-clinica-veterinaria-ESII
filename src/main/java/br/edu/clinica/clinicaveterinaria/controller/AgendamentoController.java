package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.ConsultaDAO;
import br.edu.clinica.clinicaveterinaria.dao.PacienteDAO;
import br.edu.clinica.clinicaveterinaria.dao.VeterinarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AgendamentoController implements Initializable {

    private PacienteDAO pacienteDAO = new PacienteDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private ConsultaDAO consultaDAO = new ConsultaDAO();
    private ObservableList<Paciente> pacientesList = FXCollections.observableArrayList();
    private ObservableList<Veterinario> veterinariosList = FXCollections.observableArrayList();

    @FXML private ComboBox<Paciente> comboBuscarPaciente;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Veterinario> comboVeterinarios;
    @FXML private ComboBox<LocalTime> comboHorarios;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Paciente pacienteSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarControles();
        carregarDadosDoBanco();
        datePicker.setValue(LocalDate.now());
    }
    
    private void carregarDadosDoBanco() {
        try {
            // Carregar pacientes
            List<Paciente> pacientes = pacienteDAO.listarTodos();
            pacientesList.clear();
            pacientesList.addAll(pacientes);
            
            // Carregar veterinários
            List<Veterinario> veterinarios = veterinarioDAO.listarTodos();
            veterinariosList.clear();
            veterinariosList.addAll(veterinarios);
            
            carregarVeterinarios();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar dados do banco: " + e.getMessage());
        }
    }

    private void configurarControles() {
        configurarFormatadores();
        configurarSelecaoPaciente();
        datePicker.valueProperty().addListener((obs, old, val) -> carregarHorarios());
        comboVeterinarios.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> carregarHorarios());
        
        comboHorarios.setOnShowing(event -> {
            if (datePicker.getValue() != null && comboVeterinarios.getValue() != null) {
                carregarHorarios();
            } else {
                comboHorarios.hide();
                if (datePicker.getValue() == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione uma data primeiro.");
                } else if (comboVeterinarios.getValue() == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Atenção", "Selecione um veterinário primeiro.");
                }
            }
        });
    }

    private void configurarFormatadores() {
        comboBuscarPaciente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Paciente paciente) {
                if (paciente == null) {
                    return null;
                }
                return paciente.getNome() + " (Tutor: " + paciente.getProprietario().getNome() + ")";
            }

            @Override
            public Paciente fromString(String string) {
                return null; 
            }
        });
        
        comboHorarios.setConverter(new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime time) {
                if (time == null) {
                    return null;
                }
                return time.format(DateTimeFormatter.ofPattern("HH:mm"));
            }

            @Override
            public LocalTime fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalTime.parse(string, DateTimeFormatter.ofPattern("HH:mm"));
                } catch (Exception e) {
                    return null;
                }
            }
        });
    }

    private void configurarSelecaoPaciente() {
        comboBuscarPaciente.setEditable(false);
        comboBuscarPaciente.setPromptText("Clique para selecionar um paciente");

        comboBuscarPaciente.setOnShowing(event -> {
            try {
                comboBuscarPaciente.hide();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/selecionar-paciente-view.fxml"));
                Parent root = loader.load();

                SelecionarPacienteController controller = loader.getController();
                controller.setPacientes(new ArrayList<>(pacientesList));

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Selecionar Paciente");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initOwner((Stage) btnConfirmar.getScene().getWindow());
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                MainApplication.setStageIcon(dialogStage);

                dialogStage.showAndWait();

                Paciente selecionado = controller.getSelectedPaciente();
                if (selecionado != null) {
                    this.pacienteSelecionado = selecionado;
                    comboBuscarPaciente.setValue(selecionado);
                }
                // Garantir que o popup do ComboBox seja fechado
                comboBuscarPaciente.hide();
                event.consume();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a tela de seleção de paciente.");
            }
        });
    }

    @FXML
    void confirmarAgendamento() {
        this.pacienteSelecionado = comboBuscarPaciente.getValue();

        if (pacienteSelecionado == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Nenhum paciente selecionado.");
            return;
        }
        if (datePicker.getValue() == null || comboVeterinarios.getValue() == null || comboHorarios.getValue() == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Todos os campos devem ser preenchidos.");
            return;
        }
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Não é possível agendar para uma data passada.");
            return;
        }
        
        try {
            if (validarDisponibilidade(datePicker.getValue(), comboHorarios.getValue(), comboVeterinarios.getValue())) {
                LocalDateTime dataHora = LocalDateTime.of(datePicker.getValue(), comboHorarios.getValue());
                
                Consulta consulta = new Consulta();
                consulta.setDataConsulta(dataHora);
                consulta.setDiagnostico("");
                consulta.setPaciente(pacienteSelecionado);
                consulta.setVeterinario(comboVeterinarios.getValue());
                
                consultaDAO.inserirConsulta(consulta);
                
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Agendamento confirmado!");
                Stage stage = (Stage) btnConfirmar.getScene().getWindow();
                stage.close();
            } else {
                mostrarAlerta(Alert.AlertType.WARNING, "Horário Indisponível", "O horário selecionado não está disponível.");
                carregarHorarios();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar agendamento: " + e.getMessage());
        }
    }

    @FXML
    void cancelarAgendamento() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Você tem certeza que deseja cancelar?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Cancelar Agendamento");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                Stage stage = (Stage) btnCancelar.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void carregarVeterinarios() {
        comboVeterinarios.setItems(veterinariosList);
        comboVeterinarios.setConverter(new StringConverter<>() {
            public String toString(Veterinario v) { return v == null ? "" : v.getNome(); }
            public Veterinario fromString(String s) { return null; }
        });
    }

    private void carregarHorarios() {
        comboHorarios.getItems().clear();
        LocalDate data = datePicker.getValue();
        Veterinario vet = comboVeterinarios.getValue();
        if (data == null || vet == null) return;

        try {
            List<Consulta> consultasAgendadas = consultaDAO.listarPorDataEVeterinario(data, vet.getId());
            
            List<LocalTime> horarios = new ArrayList<>();
            for (int h = 9; h <= 17; h++) {
                horarios.add(LocalTime.of(h, 0));
            }
            
            for (Consulta consulta : consultasAgendadas) {
                if (consulta.getDataConsulta() != null) {
                    LocalTime horaConsulta = consulta.getDataConsulta().toLocalTime();
                    horarios.removeIf(horario -> {
                        int horaOcupada = horaConsulta.getHour();
                        int horaDisponivel = horario.getHour();
                        return horaOcupada == horaDisponivel;
                    });
                }
            }
            
            comboHorarios.setItems(FXCollections.observableArrayList(horarios));
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao carregar horários: " + e.getMessage());
        }
    }

    private boolean validarDisponibilidade(LocalDate d, LocalTime h, Veterinario v) {
        try {
            List<Consulta> consultas = consultaDAO.listarPorDataEVeterinario(d, v.getId());
            
            return consultas.stream().noneMatch(c -> {
                if (c.getDataConsulta() == null) return false;
                LocalDateTime consultaDateTime = c.getDataConsulta();
                return consultaDateTime.toLocalDate().equals(d) && 
                       consultaDateTime.toLocalTime().equals(h);
            });
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setDataPreSelecionada(LocalDate data) {
        if (data != null) {
            datePicker.setValue(data);
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
