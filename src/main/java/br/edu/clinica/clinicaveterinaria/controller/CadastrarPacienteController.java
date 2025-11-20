package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.PacienteDAO;
import br.edu.clinica.clinicaveterinaria.dao.ProprietarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CadastrarPacienteController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEspecie;
    @FXML private TextField txtRaca;
    @FXML private DatePicker dpNascimento;
    @FXML private TextField txtCpfTutor;
    @FXML private TextField txtNomeTutor;
    @FXML private TextField txtTelefoneTutor;
    @FXML private TextField txtEmailTutor;
    @FXML private TextField txtRua;
    @FXML private TextField txtNumero;
    @FXML private TextField txtBairro;
    @FXML private TextField txtCidade;
    @FXML private TextField txtEstado;
    @FXML private TextField txtCep;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;
    @FXML private Label lblTitle;

    private Paciente pacienteToEdit;
    private List<Paciente> existingPacientes;
    private Paciente newPaciente = null;
    private PacienteDAO pacienteDAO = new PacienteDAO();
    private ProprietarioDAO proprietarioDAO = new ProprietarioDAO();

    @FXML
    private void initialize() {
        btnSalvar.setOnAction(event -> salvarPaciente());
        btnCancelar.setOnAction(event -> cancelar());
    }

    public void setPacienteData(Paciente paciente, List<Paciente> pacientes) {
        this.existingPacientes = pacientes;
        this.pacienteToEdit = paciente;

        if (paciente != null) {
            txtNome.setText(paciente.getNome());
            txtEspecie.setText(paciente.getEspecie() != null ? paciente.getEspecie() : "");
            txtRaca.setText(paciente.getRaca() != null ? paciente.getRaca() : "");
            dpNascimento.setValue(paciente.getDataNascimento());
            
            Proprietario prop = paciente.getProprietario();
            txtCpfTutor.setText(prop.getCpf());
            txtNomeTutor.setText(prop.getNome());
            txtTelefoneTutor.setText(prop.getTelefone() != null ? prop.getTelefone() : "");
            txtEmailTutor.setText(prop.getEmail() != null ? prop.getEmail() : "");
            txtRua.setText(prop.getRua() != null ? prop.getRua() : "");
            txtNumero.setText(prop.getNumero() != null ? prop.getNumero() : "");
            txtBairro.setText(prop.getBairro() != null ? prop.getBairro() : "");
            txtCidade.setText(prop.getCidade() != null ? prop.getCidade() : "");
            txtEstado.setText(prop.getEstado() != null ? prop.getEstado() : "");
            txtCep.setText(prop.getCep() != null ? prop.getCep() : "");
            
            txtCpfTutor.setEditable(false);
            btnSalvar.setText("Salvar");
            lblTitle.setText("Editando Paciente");
        } else {
            btnSalvar.setText("Cadastrar");
            lblTitle.setText("Cadastrar Novo Paciente");
        }
    }

    public Paciente getNewPaciente() {
        return newPaciente;
    }

    private void salvarPaciente() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            showAlert("Erro de Validação", "O nome do paciente não pode estar em branco.");
            return;
        }

        String cpfTutor = txtCpfTutor.getText().trim().replaceAll("[^0-9]", "");
        if (cpfTutor.isEmpty()) {
            showAlert("Erro de Validação", "O CPF do tutor é obrigatório.");
            return;
        }

        String nomeTutor = txtNomeTutor.getText().trim();
        if (nomeTutor.isEmpty()) {
            showAlert("Erro de Validação", "O nome do tutor é obrigatório.");
            return;
        }

        String telefoneTutor = txtTelefoneTutor.getText().trim();
        if (telefoneTutor.isEmpty()) {
            showAlert("Erro de Validação", "O telefone do tutor é obrigatório.");
            return;
        }

        String emailTutor = txtEmailTutor.getText().trim();
        if (emailTutor.isEmpty()) {
            showAlert("Erro de Validação", "O e-mail do tutor é obrigatório.");
            return;
        }

        String rua = txtRua.getText().trim();
        if (rua.isEmpty()) {
            showAlert("Erro de Validação", "A rua é obrigatória.");
            return;
        }

        String numero = txtNumero.getText().trim();
        if (numero.isEmpty()) {
            showAlert("Erro de Validação", "O número é obrigatório.");
            return;
        }

        String bairro = txtBairro.getText().trim();
        if (bairro.isEmpty()) {
            showAlert("Erro de Validação", "O bairro é obrigatório.");
            return;
        }

        String cidade = txtCidade.getText().trim();
        if (cidade.isEmpty()) {
            showAlert("Erro de Validação", "A cidade é obrigatória.");
            return;
        }

        String estado = txtEstado.getText().trim();
        if (estado.isEmpty()) {
            showAlert("Erro de Validação", "O estado é obrigatório.");
            return;
        }

        String cep = txtCep.getText().trim();
        if (cep.isEmpty()) {
            showAlert("Erro de Validação", "O CEP é obrigatório.");
            return;
        }

        String especie = txtEspecie.getText().trim();
        String raca = txtRaca.getText().trim();

        LocalDate dataNascimento = dpNascimento.getValue();
        String dataTexto = dpNascimento.getEditor().getText();

        if (dataTexto == null || dataTexto.trim().isEmpty()) {
             showAlert("Erro de Validação", "A data de nascimento não pode estar em branco.");
             return;
        }

        try {
            dpNascimento.getConverter().fromString(dataTexto);
        } catch (Exception e) {
            showAlert("Erro de Validação", "O formato da data é inválido. Use dd/mm/aaaa.");
            return;
        }
        
        dataNascimento = dpNascimento.getValue();

        if (dataNascimento.isAfter(LocalDate.now())) {
            showAlert("Erro de Validação", "A data de nascimento não pode ser uma data futura.");
            return;
        }

        try {
            Proprietario proprietario = proprietarioDAO.buscarPorCpf(cpfTutor);
            
            if (proprietario == null) {
                proprietario = new Proprietario();
                proprietario.setCpf(cpfTutor);
                proprietario.setNome(nomeTutor);
                proprietario.setTelefone(telefoneTutor);
                proprietario.setEmail(emailTutor);
                proprietario.setRua(rua);
                proprietario.setNumero(numero);
                proprietario.setBairro(bairro);
                proprietario.setCidade(cidade);
                proprietario.setEstado(estado);
                proprietario.setCep(cep);
                
                proprietarioDAO.inserirProprietario(proprietario);
            } else {
                if (pacienteToEdit != null) {
                    proprietario.setNome(nomeTutor);
                    proprietario.setTelefone(telefoneTutor);
                    proprietario.setEmail(emailTutor);
                    proprietario.setRua(rua);
                    proprietario.setNumero(numero);
                    proprietario.setBairro(bairro);
                    proprietario.setCidade(cidade);
                    proprietario.setEstado(estado);
                    proprietario.setCep(cep);
                    
                    proprietarioDAO.atualizarProprietario(proprietario);
                }
            }

            if (pacienteToEdit == null) {
                newPaciente = new Paciente();
                newPaciente.setNome(nome);
                newPaciente.setEspecie(especie);
                newPaciente.setRaca(raca);
                newPaciente.setDataNascimento(dataNascimento);
                newPaciente.setProprietario(proprietario);
                
                pacienteDAO.inserirPaciente(newPaciente);
                showAlert("Sucesso", "Paciente cadastrado com sucesso!");
            } else {
                pacienteToEdit.setNome(nome);
                pacienteToEdit.setEspecie(especie);
                pacienteToEdit.setRaca(raca);
                pacienteToEdit.setDataNascimento(dataNascimento);
                pacienteToEdit.setProprietario(proprietario);
                
                pacienteDAO.atualizarPaciente(pacienteToEdit);
                newPaciente = pacienteToEdit;
                showAlert("Sucesso", "Paciente atualizado com sucesso!");
            }
            
            fecharJanela();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro no Banco de Dados", "Erro ao salvar paciente: " + e.getMessage());
        }
    }

    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert.AlertType type = title.equals("Sucesso") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR;
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

