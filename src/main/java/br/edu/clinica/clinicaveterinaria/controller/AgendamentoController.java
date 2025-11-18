package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AgendamentoController implements Initializable {

    public static class Agendamento {
        private final Paciente paciente;
        private final Veterinario veterinario;
        private final LocalDate data;
        private final LocalTime hora;

        public Agendamento(Paciente p, Veterinario v, LocalDate d, LocalTime h) {
            this.paciente = p; this.veterinario = v; this.data = d; this.hora = h;
        }
        public Paciente getPaciente() { return paciente; }
        public Veterinario getVeterinario() { return veterinario; }
        public LocalDate getData() { return data; }
        public LocalTime getHora() { return hora; }
    }

    private static final List<Proprietario> proprietariosDB = new ArrayList<>();
    private static final List<Paciente> pacientesDB = new ArrayList<>();
    private static final List<Veterinario> veterinariosDB = new ArrayList<>();
    public static final ObservableList<Agendamento> agendamentosDB = FXCollections.observableArrayList();
    private static boolean dbInicializado = false;

    @FXML private ComboBox<Paciente> comboBuscarPaciente;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<Veterinario> comboVeterinarios;
    @FXML private ComboBox<LocalTime> comboHorarios;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Paciente pacienteSelecionado;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarBancoDeDadosEmMemoria();
        configurarControles();
        carregarVeterinarios();
        datePicker.setValue(LocalDate.now());
    }

    private void configurarControles() {
        configurarFormatadores();
        configurarSelecaoPaciente();
        datePicker.valueProperty().addListener((obs, old, val) -> carregarHorarios());
        comboVeterinarios.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> carregarHorarios());
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
    }

    private void configurarSelecaoPaciente() {
        comboBuscarPaciente.setEditable(false);
        comboBuscarPaciente.setPromptText("Clique para selecionar um paciente");

        comboBuscarPaciente.setOnShowing(event -> {
            try {
                comboBuscarPaciente.hide(); // Explicitly hide the dropdown
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/clinica/clinicaveterinaria/selecionar-paciente-view.fxml"));
                Parent root = loader.load();

                SelecionarPacienteController controller = loader.getController();
                controller.setPacientes(pacientesDB);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Selecionar Paciente");
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initOwner((Stage) btnConfirmar.getScene().getWindow());
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                dialogStage.showAndWait();

                Paciente selecionado = controller.getSelectedPaciente();
                if (selecionado != null) {
                    this.pacienteSelecionado = selecionado;
                    comboBuscarPaciente.setValue(selecionado);
                }
                event.consume(); // Prevent the empty dropdown from showing
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
        if (validarDisponibilidade(datePicker.getValue(), comboHorarios.getValue(), comboVeterinarios.getValue())) {
            agendamentosDB.add(new Agendamento(pacienteSelecionado, comboVeterinarios.getValue(), datePicker.getValue(), comboHorarios.getValue()));
            mostrarAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Agendamento confirmado!");
            Stage stage = (Stage) btnConfirmar.getScene().getWindow();
            stage.close();
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Horário Indisponível", "O horário selecionado não está disponível.");
            carregarHorarios();
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
        comboVeterinarios.setItems(FXCollections.observableArrayList(veterinariosDB));
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

        List<LocalTime> horarios = new ArrayList<>();
        for (int h = 9; h < 18; h++) {
            horarios.add(LocalTime.of(h, 0));
            horarios.add(LocalTime.of(h, 30));
        }
        agendamentosDB.stream()
            .filter(a -> a.getData().equals(data) && a.getVeterinario().equals(vet))
            .forEach(a -> horarios.remove(a.getHora()));
        comboHorarios.setItems(FXCollections.observableArrayList(horarios));
    }

    private boolean validarDisponibilidade(LocalDate d, LocalTime h, Veterinario v) {
        return agendamentosDB.stream().noneMatch(a -> a.getData().equals(d) && a.getHora().equals(h) && a.getVeterinario().equals(v));
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static void inicializarBancoDeDadosEmMemoria() {
        if (dbInicializado) return;
        Proprietario p1 = new Proprietario(); p1.setId(1); p1.setNome("Carlos Pereira"); p1.setTelefone("11987654321");
        Proprietario p2 = new Proprietario(); p2.setId(2); p2.setNome("Ana Julia"); p2.setTelefone("21912345678");
        proprietariosDB.addAll(List.of(p1, p2));
        Paciente pac1 = new Paciente(); pac1.setId(1); pac1.setNome("Rex"); pac1.setEspecie("Cachorro"); pac1.setRaca("Labrador"); pac1.setProprietario(p1);
        Paciente pac2 = new Paciente(); pac2.setId(2); pac2.setNome("Miau"); pac2.setEspecie("Gato"); pac2.setRaca("Siamês"); pac2.setProprietario(p2);
        pacientesDB.addAll(List.of(pac1, pac2));
        veterinariosDB.add(new Veterinario(1, "Dr. João Silva", "CRMV-SP 12345", "11999998888", "Clínico Geral"));
        veterinariosDB.add(new Veterinario(2, "Dra. Maria Souza", "CRMV-SP 54321", "11977776666", "Cirurgiã"));
        dbInicializado = true;
    }
}
