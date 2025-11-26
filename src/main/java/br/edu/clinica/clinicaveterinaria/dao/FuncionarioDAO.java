package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FuncionarioDAO {

    public void adicionarFuncionario(Funcionario funcionario) throws SQLException {
        String sql = "INSERT INTO funcionario (nome, cargo, login, senha, e_gerente) VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, funcionario.getNome());
            pstmt.setString(2, funcionario.getCargo());
            pstmt.setString(3, funcionario.getLogin());
            pstmt.setString(4, funcionario.getSenha());
            pstmt.setBoolean(5, funcionario.isGerente());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    funcionario.setId(rs.getInt("id"));
                }
            }

        }
    }

    public List<Funcionario> listarTodos() throws SQLException {
        List<Funcionario> listaFuncionarios = new ArrayList<>();
        String sql = "SELECT id, nome, cargo, login, e_gerente FROM funcionario WHERE login NOT LIKE '%_DESATIVADO_%' ORDER BY nome";

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

    public Funcionario buscarPorLogin(String login) throws SQLException {
        String sql = "SELECT id, nome, cargo, login, e_gerente FROM funcionario WHERE login = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);

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
        atualizarFuncionario(f, f.getSenha());
    }

    public void atualizarFuncionario(Funcionario f, String senha) throws SQLException {
        String sql = "CALL proc_atualizar_funcionario(?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setString(1, f.getLogin());
            stmt.setString(2, f.getNome());
            stmt.setString(3, f.getCargo());
            stmt.setString(4, null);
            stmt.setString(5, senha);
            stmt.setBoolean(6, f.isGerente());

            stmt.execute();
        }
    }

    public void deletarFuncionario(int id) throws SQLException {
        // Verificar se há pagamentos relacionados antes de tentar excluir
        String sqlVerificarPagamentos = "SELECT COUNT(*) FROM pagamento WHERE id_funcionario = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmtVerificar = conn.prepareStatement(sqlVerificarPagamentos)) {
            
            pstmtVerificar.setInt(1, id);
            try (ResultSet rs = pstmtVerificar.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Usar soft delete (desativar) ao invés de excluir
                    String sql = "CALL proc_deletar_funcionario(?)";
                    try (CallableStatement cstmt = conn.prepareCall(sql)) {
                        cstmt.setInt(1, id);
                        cstmt.execute();
                    }
                    return;
                }
            }
        }
        
        // Se não há pagamentos, fazer exclusão direta
        String sql = "DELETE FROM funcionario WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
