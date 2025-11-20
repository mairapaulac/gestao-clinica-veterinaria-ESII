package br.edu.clinica.clinicaveterinaria.controller;

import br.edu.clinica.clinicaveterinaria.dao.FuncionarioDAO;
import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class CadastrarFuncionarioController {

    @FXML private Label lblTitle;
    @FXML private TextField txtNome;
    @FXML private TextField txtCargo;
    @FXML private TextField txtLogin;
    @FXML private TextField txtSenha;
    @FXML private CheckBox chkGerente;
    @FXML private Button btnSalvar;
    @FXML private Button btnCancelar;

    private Funcionario funcionarioToEdit;
    private ObservableList<Funcionario> existingFuncionarios;
    private Funcionario newFuncionario = null;
    private FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    @FXML
    private void initialize() {
        btnSalvar.setOnAction(event -> salvarFuncionario());
        btnCancelar.setOnAction(event -> cancelar());
    }

    public void setFuncionarioData(Funcionario funcionario, ObservableList<Funcionario> funcionarios) {
        this.existingFuncionarios = funcionarios;
        this.funcionarioToEdit = funcionario;

        if (funcionario != null) {
            lblTitle.setText("Editar Funcionário");
            btnSalvar.setText("Salvar");
            txtNome.setText(funcionario.getNome());
            txtCargo.setText(funcionario.getCargo() != null ? funcionario.getCargo() : "");
            txtLogin.setText(funcionario.getLogin());
            txtLogin.setEditable(false);
            chkGerente.setSelected(funcionario.isGerente());
        } else {
            lblTitle.setText("Cadastrar Novo Funcionário");
            btnSalvar.setText("Cadastrar");
        }
    }

    public Funcionario getNewFuncionario() {
        return newFuncionario;
    }

    private void salvarFuncionario() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            showAlert("Erro de Validação", "O nome não pode estar em branco.");
            return;
        }

        String cargo = txtCargo.getText().trim();
        if (cargo.isEmpty()) {
            showAlert("Erro de Validação", "O cargo não pode estar em branco.");
            return;
        }

        String login = txtLogin.getText().trim();
        if (login.isEmpty()) {
            showAlert("Erro de Validação", "O login não pode estar em branco.");
            return;
        }

        if (funcionarioToEdit == null) {
            for (Funcionario f : existingFuncionarios) {
                if (f.getLogin().equalsIgnoreCase(login)) {
                    showAlert("Erro", "Um funcionário com este login já existe.");
                    return;
                }
            }
        }

        String senha = txtSenha.getText();
        if (funcionarioToEdit == null && senha.isEmpty()) {
            showAlert("Erro de Validação", "A senha é obrigatória para novos funcionários.");
            return;
        }

        boolean isGerente = chkGerente.isSelected();

        try {
            if (funcionarioToEdit == null) {
                newFuncionario = new Funcionario();
                newFuncionario.setNome(nome);
                newFuncionario.setCargo(cargo);
                newFuncionario.setLogin(login);
                newFuncionario.setSenha(senha);
                newFuncionario.setGerente(isGerente);
                
                funcionarioDAO.adicionarFuncionario(newFuncionario);
                showAlert("Sucesso", "Funcionário cadastrado com sucesso!");
            } else {
                funcionarioToEdit.setNome(nome);
                funcionarioToEdit.setCargo(cargo);
                if (senha != null && !senha.isEmpty()) {
                    funcionarioToEdit.setSenha(senha);
                }
                funcionarioToEdit.setGerente(isGerente);
                
                funcionarioDAO.atualizarFuncionario(funcionarioToEdit, senha != null && !senha.isEmpty() ? senha : null);
                newFuncionario = funcionarioToEdit;
                showAlert("Sucesso", "Funcionário atualizado com sucesso!");
            }
            
            fecharJanela();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro no Banco de Dados", "Erro ao salvar funcionário: " + e.getMessage());
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

