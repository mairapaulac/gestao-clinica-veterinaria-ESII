package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.PacienteDAO;
import br.edu.clinica.clinicaveterinaria.dao.ProprietarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;
import br.edu.clinica.clinicaveterinaria.util.DatabaseErrorHandler;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ProgressIndicator;
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
    @FXML private ProgressIndicator cpfLoadingIndicator;
    @FXML private Label cpfStatusLabel;

    private Paciente pacienteToEdit;
    private Paciente newPaciente = null;
    private PacienteDAO pacienteDAO = new PacienteDAO();
    private ProprietarioDAO proprietarioDAO = new ProprietarioDAO();
    private Task<Proprietario> buscaProprietarioTask = null;

    @FXML
    private void initialize() {
        btnSalvar.setOnAction(event -> salvarPaciente());
        btnCancelar.setOnAction(event -> cancelar());
        
        // Listener para buscar proprietário automaticamente quando CPF for digitado
        txtCpfTutor.textProperty().addListener((observable, oldValue, newValue) -> {
            // Só busca se não estiver editando um paciente existente e se o CPF for diferente do anterior
            if (pacienteToEdit == null && newValue != null && !newValue.trim().isEmpty() 
                && (oldValue == null || !oldValue.trim().equals(newValue.trim()))) {
                buscarProprietarioPorCpf(newValue.trim());
            }
        });
    }

    public void setPacienteData(Paciente paciente, List<Paciente> pacientes) {
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
            
            // Desabilita busca automática ao editar
            resetarStatusCpf();
        } else {
            btnSalvar.setText("Cadastrar");
            lblTitle.setText("Cadastrar Novo Paciente");
            txtCpfTutor.setEditable(true);
        }
    }

    public Paciente getNewPaciente() {
        return newPaciente;
    }

    private void salvarPaciente() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O nome do paciente não pode estar em branco.");
            return;
        }

        String cpfTutor = txtCpfTutor.getText().trim().replaceAll("[^0-9]", "");
        if (cpfTutor.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O CPF do tutor é obrigatório.");
            return;
        }

        String nomeTutor = txtNomeTutor.getText().trim();
        if (nomeTutor.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O nome do tutor é obrigatório.");
            return;
        }

        String telefoneTutor = txtTelefoneTutor.getText().trim();
        if (telefoneTutor.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O telefone do tutor é obrigatório.");
            return;
        }

        String emailTutor = txtEmailTutor.getText().trim();
        if (emailTutor.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O e-mail do tutor é obrigatório.");
            return;
        }

        String rua = txtRua.getText().trim();
        if (rua.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "A rua é obrigatória.");
            return;
        }

        String numero = txtNumero.getText().trim();
        if (numero.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O número é obrigatório.");
            return;
        }

        String bairro = txtBairro.getText().trim();
        if (bairro.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O bairro é obrigatório.");
            return;
        }

        String cidade = txtCidade.getText().trim();
        if (cidade.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "A cidade é obrigatória.");
            return;
        }

        String estado = txtEstado.getText().trim();
        if (estado.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O estado é obrigatório.");
            return;
        }

        String cep = txtCep.getText().trim();
        if (cep.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O CEP é obrigatório.");
            return;
        }

        String especie = txtEspecie.getText().trim();
        String raca = txtRaca.getText().trim();

        LocalDate dataNascimento = dpNascimento.getValue();
        String dataTexto = dpNascimento.getEditor().getText();

        if (dataTexto == null || dataTexto.trim().isEmpty()) {
             MainApplication.showErrorAlert("Erro de Validação", "A data de nascimento não pode estar em branco.");
             return;
        }

        try {
            dpNascimento.getConverter().fromString(dataTexto);
        } catch (Exception e) {
            MainApplication.showErrorAlert("Erro de Validação", "O formato da data é inválido. Use dd/mm/aaaa.");
            return;
        }
        
        dataNascimento = dpNascimento.getValue();

        if (dataNascimento.isAfter(LocalDate.now())) {
            MainApplication.showErrorAlert("Erro de Validação", "A data de nascimento não pode ser uma data futura.");
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
                MainApplication.showSuccessAlert("Sucesso", "Paciente cadastrado com sucesso!");
            } else {
                pacienteToEdit.setNome(nome);
                pacienteToEdit.setEspecie(especie);
                pacienteToEdit.setRaca(raca);
                pacienteToEdit.setDataNascimento(dataNascimento);
                pacienteToEdit.setProprietario(proprietario);
                
                pacienteDAO.atualizarPaciente(pacienteToEdit);
                newPaciente = pacienteToEdit;
                MainApplication.showSuccessAlert("Sucesso", "Paciente atualizado com sucesso!");
            }
            
            fecharJanela();
            
        } catch (SQLException e) {
            e.printStackTrace();
            String mensagem = DatabaseErrorHandler.getFriendlyMessage(e, "salvar paciente");
            String titulo = DatabaseErrorHandler.getErrorTitle("salvar paciente");
            MainApplication.showErrorAlert(titulo, mensagem);
        }
    }

    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
    
    private void buscarProprietarioPorCpf(String cpfCompleto) {
        // Remove caracteres especiais do CPF
        String cpf = cpfCompleto.replaceAll("[^0-9]", "");
        
        // Só busca se tiver exatamente 11 dígitos (CPF completo)
        if (cpf.length() != 11) {
            resetarStatusCpf();
            return;
        }
        
        // Cancela busca anterior se estiver em andamento
        if (buscaProprietarioTask != null && buscaProprietarioTask.isRunning()) {
            buscaProprietarioTask.cancel();
        }
        
        // Mostra indicador de carregamento
        cpfLoadingIndicator.setVisible(true);
        cpfLoadingIndicator.setManaged(true);
        cpfStatusLabel.setVisible(false);
        cpfStatusLabel.setManaged(false);
        
        // Cria task assíncrona para buscar no banco
        buscaProprietarioTask = new Task<Proprietario>() {
            @Override
            protected Proprietario call() throws Exception {
                // Pequeno delay para evitar múltiplas buscas enquanto usuário digita
                Thread.sleep(300);
                
                if (isCancelled()) {
                    return null;
                }
                
                try {
                    return proprietarioDAO.buscarPorCpf(cpf);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        
        // Quando a busca terminar
        buscaProprietarioTask.setOnSucceeded(e -> {
            try {
                Proprietario proprietario = buscaProprietarioTask.getValue();
                Platform.runLater(() -> {
                    cpfLoadingIndicator.setVisible(false);
                    cpfLoadingIndicator.setManaged(false);
                    
                    if (proprietario != null) {
                        // Preenche automaticamente os campos do proprietário
                        preencherDadosProprietario(proprietario);
                        
                        // Mostra ícone de verificado
                        cpfStatusLabel.setText("✓");
                        cpfStatusLabel.setVisible(true);
                        cpfStatusLabel.setManaged(true);
                    } else {
                        // Limpa os campos se não encontrou
                        resetarStatusCpf();
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    resetarStatusCpf();
                    ex.printStackTrace();
                });
            }
        });
        
        // Trata erros
        buscaProprietarioTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                resetarStatusCpf();
                Throwable ex = buscaProprietarioTask.getException();
                if (ex != null && !(ex instanceof InterruptedException)) {
                    ex.printStackTrace();
                }
            });
        });
        
        // Cancela task se for cancelada
        buscaProprietarioTask.setOnCancelled(e -> {
            Platform.runLater(() -> {
                resetarStatusCpf();
            });
        });
        
        // Executa a task em uma thread separada
        Thread thread = new Thread(buscaProprietarioTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    private void preencherDadosProprietario(Proprietario proprietario) {
        if (proprietario == null) {
            return;
        }
        
        // Preenche os campos do proprietário
        txtNomeTutor.setText(proprietario.getNome() != null ? proprietario.getNome() : "");
        txtTelefoneTutor.setText(proprietario.getTelefone() != null ? proprietario.getTelefone() : "");
        txtEmailTutor.setText(proprietario.getEmail() != null ? proprietario.getEmail() : "");
        txtRua.setText(proprietario.getRua() != null ? proprietario.getRua() : "");
        txtNumero.setText(proprietario.getNumero() != null ? proprietario.getNumero() : "");
        txtBairro.setText(proprietario.getBairro() != null ? proprietario.getBairro() : "");
        txtCidade.setText(proprietario.getCidade() != null ? proprietario.getCidade() : "");
        txtEstado.setText(proprietario.getEstado() != null ? proprietario.getEstado() : "");
        txtCep.setText(proprietario.getCep() != null ? proprietario.getCep() : "");
    }
    
    private void resetarStatusCpf() {
        cpfLoadingIndicator.setVisible(false);
        cpfLoadingIndicator.setManaged(false);
        cpfStatusLabel.setVisible(false);
        cpfStatusLabel.setManaged(false);
    }
}

