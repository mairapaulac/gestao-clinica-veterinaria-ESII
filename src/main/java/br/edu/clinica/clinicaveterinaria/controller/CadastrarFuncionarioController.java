package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.FuncionarioDAO;
import br.edu.clinica.clinicaveterinaria.dao.VeterinarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import br.edu.clinica.clinicaveterinaria.model.UsuarioSistema;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import br.edu.clinica.clinicaveterinaria.view.MainApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CadastrarFuncionarioController {

    @FXML private Label lblTitle;
    @FXML private ComboBox<String> comboTipoUsuario;
    
    // Campos Funcionário
    @FXML private VBox vboxDadosFuncionario;
    @FXML private TextField txtNome;
    @FXML private TextField txtCargo;
    @FXML private TextField txtLogin;
    @FXML private PasswordField txtSenha;
    @FXML private CheckBox chkGerente;
    
    // Campos Veterinário
    @FXML private VBox vboxDadosVeterinario;
    @FXML private TextField txtNomeVet;
    @FXML private TextField txtCRMV;
    @FXML private TextField txtTelefoneVet;
    @FXML private TextField txtEspecialidade;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenhaVet;
    
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private UsuarioSistema usuarioToEdit;
    private ObservableList<UsuarioSistema> existingUsuarios;
    private UsuarioSistema newUsuario = null;
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();

    @FXML
    private void initialize() {
        configurarTipoUsuario();
        comboTipoUsuario.setOnAction(event -> alternarCampos());
        btnSalvar.setOnAction(event -> salvarUsuario());
        btnCancelar.setOnAction(event -> cancelar());
        
        // Iniciar com Veterinário selecionado para que a janela comece maior
        comboTipoUsuario.getSelectionModel().select("Veterinário");
        alternarCampos();
    }

    private void configurarTipoUsuario() {
        ObservableList<String> tipos = FXCollections.observableArrayList("Funcionário", "Veterinário");
        comboTipoUsuario.setItems(tipos);
    }

    private void alternarCampos() {
        String tipo = comboTipoUsuario.getSelectionModel().getSelectedItem();
        if (tipo == null) {
            vboxDadosFuncionario.setVisible(false);
            vboxDadosFuncionario.setManaged(false);
            vboxDadosVeterinario.setVisible(false);
            vboxDadosVeterinario.setManaged(false);
            return;
        }
        
        if (tipo.equals("Funcionário")) {
            vboxDadosFuncionario.setVisible(true);
            vboxDadosFuncionario.setManaged(true);
            vboxDadosVeterinario.setVisible(false);
            vboxDadosVeterinario.setManaged(false);
        } else if (tipo.equals("Veterinário")) {
            vboxDadosFuncionario.setVisible(false);
            vboxDadosFuncionario.setManaged(false);
            vboxDadosVeterinario.setVisible(true);
            vboxDadosVeterinario.setManaged(true);
        }
    }

    public void setUsuarioData(UsuarioSistema usuario, ObservableList<UsuarioSistema> usuarios) {
        this.existingUsuarios = usuarios;
        this.usuarioToEdit = usuario;

        if (usuario != null) {
            lblTitle.setText("Editar " + (usuario.getTipo().equals("FUNCIONARIO") ? "Funcionário" : "Veterinário"));
            btnSalvar.setText("Salvar");
            
            if (usuario.getTipo().equals("FUNCIONARIO")) {
                comboTipoUsuario.getSelectionModel().select("Funcionário");
                alternarCampos();
                txtNome.setText(usuario.getNome());
                txtCargo.setText(usuario.getCargo() != null ? usuario.getCargo() : "");
                txtLogin.setText(usuario.getLogin());
                txtLogin.setEditable(false);
                chkGerente.setSelected(usuario.isGerente());
            } else {
                comboTipoUsuario.getSelectionModel().select("Veterinário");
                alternarCampos();
                txtNomeVet.setText(usuario.getNome());
                txtCRMV.setText(usuario.getCrmv() != null ? usuario.getCrmv() : "");
                txtTelefoneVet.setText(usuario.getTelefone() != null ? usuario.getTelefone() : "");
                txtEspecialidade.setText(usuario.getEspecialidade() != null ? usuario.getEspecialidade() : "");
                txtEmail.setText(usuario.getEmail() != null ? usuario.getEmail() : "");
                txtEmail.setEditable(false);
            }
            comboTipoUsuario.setDisable(true);
        } else {
            lblTitle.setText("Cadastrar Novo Usuário");
            btnSalvar.setText("Cadastrar");
        }
    }

    public UsuarioSistema getNewUsuario() {
        return newUsuario;
    }

    private void salvarUsuario() {
        String tipo = comboTipoUsuario.getSelectionModel().getSelectedItem();
        if (tipo == null) {
            MainApplication.showErrorAlert("Erro de Validação", "Selecione o tipo de usuário.");
            return;
        }

        try {
            if (tipo.equals("Funcionário")) {
                salvarFuncionario();
            } else {
                salvarVeterinario();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MainApplication.showErrorAlert("Erro no Banco de Dados", "Erro ao salvar: " + e.getMessage());
        }
    }

    private void salvarFuncionario() throws SQLException {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O nome não pode estar em branco.");
            return;
        }

        String cargo = txtCargo.getText().trim();
        if (cargo.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O cargo não pode estar em branco.");
            return;
        }

        String login = txtLogin.getText().trim();
        if (login.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O login não pode estar em branco.");
            return;
        }

        if (usuarioToEdit == null) {
            // Verificar se login já existe
            for (UsuarioSistema u : existingUsuarios) {
                if (u.getLogin() != null && u.getLogin().equalsIgnoreCase(login)) {
                    MainApplication.showErrorAlert("Erro", "Um usuário com este login já existe.");
                    return;
                }
            }
        }

        String senha = txtSenha.getText();
        if (usuarioToEdit == null && senha.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "A senha é obrigatória para novos funcionários.");
            return;
        }

        boolean isGerente = chkGerente.isSelected();

        if (usuarioToEdit == null) {
            Funcionario funcionario = new Funcionario();
            funcionario.setNome(nome);
            funcionario.setCargo(cargo);
            funcionario.setLogin(login);
            funcionario.setSenha(senha);
            funcionario.setGerente(isGerente);
            
            funcionarioDAO.adicionarFuncionario(funcionario);
            newUsuario = new UsuarioSistema(funcionario);
            MainApplication.showSuccessAlert("Sucesso", "Funcionário cadastrado com sucesso!");
        } else {
            Funcionario funcionario = usuarioToEdit.getFuncionario();
            funcionario.setNome(nome);
            funcionario.setCargo(cargo);
            if (senha != null && !senha.isEmpty()) {
                funcionario.setSenha(senha);
            }
            funcionario.setGerente(isGerente);
            
            funcionarioDAO.atualizarFuncionario(funcionario, senha != null && !senha.isEmpty() ? senha : null);
            newUsuario = usuarioToEdit;
            MainApplication.showSuccessAlert("Sucesso", "Funcionário atualizado com sucesso!");
        }
        
        fecharJanela();
    }

    private void salvarVeterinario() throws SQLException {
        String nome = txtNomeVet.getText().trim();
        if (nome.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O nome não pode estar em branco.");
            return;
        }

        String crmv = txtCRMV.getText().trim();
        if (crmv.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O CRMV não pode estar em branco.");
            return;
        }

        String email = txtEmail.getText().trim();
        if (email.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "O email não pode estar em branco.");
            return;
        }

        if (usuarioToEdit == null) {
            // Verificar se email já existe
            try {
                Veterinario vetExistente = veterinarioDAO.buscarPorEmail(email);
                if (vetExistente != null) {
                    MainApplication.showErrorAlert("Erro", "Um veterinário com este email já existe.");
                    return;
                }
            } catch (SQLException e) {
                // Ignorar erro de busca, continuar com cadastro
            }
        }

        String senha = txtSenhaVet.getText();
        if (usuarioToEdit == null && senha.isEmpty()) {
            MainApplication.showErrorAlert("Erro de Validação", "A senha é obrigatória para novos veterinários.");
            return;
        }

        if (usuarioToEdit == null) {
            Veterinario veterinario = new Veterinario();
            veterinario.setNome(nome);
            veterinario.setCRMV(crmv);
            veterinario.setTelefone(txtTelefoneVet.getText().trim());
            veterinario.setEspecialidade(txtEspecialidade.getText().trim());
            veterinario.setEmail(email);
            veterinario.setSenha(senha);
            
            veterinarioDAO.adicionarVeterinario(veterinario);
            newUsuario = new UsuarioSistema(veterinario);
            MainApplication.showSuccessAlert("Sucesso", "Veterinário cadastrado com sucesso!");
        } else {
            Veterinario veterinario = usuarioToEdit.getVeterinario();
            veterinario.setNome(nome);
            veterinario.setCRMV(crmv);
            veterinario.setTelefone(txtTelefoneVet.getText().trim());
            veterinario.setEspecialidade(txtEspecialidade.getText().trim());
            veterinario.setEmail(email);
            
            veterinarioDAO.atualizarVeterinarioComSenha(veterinario, senha != null && !senha.isEmpty() ? senha : null);
            newUsuario = usuarioToEdit;
            MainApplication.showSuccessAlert("Sucesso", "Veterinário atualizado com sucesso!");
        }
        
        fecharJanela();
    }

    private void cancelar() {
        fecharJanela();
    }

    private void fecharJanela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
