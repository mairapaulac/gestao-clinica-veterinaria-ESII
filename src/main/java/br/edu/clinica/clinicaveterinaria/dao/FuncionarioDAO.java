package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void adicionarFuncionario(Funcionario funcionario) throws SQLException {
        // CALL proc_inserir_funcionario(v_nome, v_cargo, v_login, v_senha, v_e_gerente)
        String sql = "CALL proc_inserir_funcionario(?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             // Adiciona Statement.RETURN_GENERATED_KEYS para capturar o ID (SERIAL)
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getCargo());
            pstmt.setString(3, funcionario.getLogin());
            pstmt.setString(4, funcionario.getSenha());
            pstmt.setBoolean(5, funcionario.isGerente()); // Usa o status 'e_gerente'

            pstmt.executeUpdate();

            // Captura o ID gerado pelo banco e atualiza o objeto Java
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    funcionario.setId(rs.getInt(1));
                }
            }

            System.out.println("Funcionário " + funcionario.getNome() + " inserido com sucesso!");
        }
    }

    // READ (Listar Todos)
    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        // Ajuste o SELECT para não incluir a coluna 'senha'
        String sql = "SELECT id, nome, cargo, login, e_gerente FROM funcionario";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Funcionario func = new Funcionario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getBoolean("e_gerente"),
                        rs.getString("cargo"),
                        rs.getString("login"),
                        null
                );
                listaFuncionarios.add(func);
            }
        }
        return listaFuncionarios;
    }

    public boolean verificar_login_funcionario(String login, String senha_fornecida) throws SQLException {

        if (login == null || senha_fornecida == null) {
            return false;
        }

        String sql = "SELECT funct_validar_login(?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, senha_fornecida);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean(1);
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Erro ao verificar login do funcionário.", e);
        }

        return false;
    }

    public Funcionario buscarPorId (int id) throws SQLException {
        String sql = "SELECT id, nome, cargo, login, e_gerente FROM funcionario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Funcionario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getBoolean("e_gerente"),
                            rs.getString("cargo"),
                            rs.getString("login"),
                            null
                    );
                }
            }
        }
        return null;
    }

    public void atualizarFuncionario(Funcionario f) throws SQLException {
        String sql = "CALL proc_atualizar_funcionario(?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, f.getId());
            pstmt.setString(2, f.getNome());
            pstmt.setString(3, f.getCargo());
            pstmt.setString(4, f.getLogin());
            pstmt.setString(5, f.getSenha());
            pstmt.setBoolean(6, f.isGerente());

            pstmt.executeUpdate();
        }
    }

    public void deletarFuncionario(int id) throws SQLException {
        String sql = "CALL proc_deletar_funcionario(?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }


}


