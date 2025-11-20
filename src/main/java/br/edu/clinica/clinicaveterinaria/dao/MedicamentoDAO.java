package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Medicamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    public List<Medicamento> listarTodos() throws SQLException {
        String sql = "SELECT c.id, c.nome_comercial, c.fabricante, COALESCE(SUM(e.quantidade_inicial), 0) as quantidade_total " +
                     "FROM catalogo_medicamento c " +
                     "LEFT JOIN estoque_medicamento e ON c.id = e.id_medicamento " +
                     "WHERE c.nome_comercial NOT LIKE '%_DESATIVADO_%' " +
                     "GROUP BY c.id, c.nome_comercial, c.fabricante " +
                     "ORDER BY c.nome_comercial";

        List<Medicamento> medicamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome_comercial");
                String fabricante = rs.getString("fabricante");
                int quantidade = rs.getInt("quantidade_total");

                Medicamento medicamento = new Medicamento(id, nome, fabricante, quantidade);
                medicamentos.add(medicamento);
            }
        }
        return medicamentos;
    }

    public void inserir(Medicamento medicamento) throws SQLException {
        String sqlCatalogo = "INSERT INTO catalogo_medicamento (nome_comercial, fabricante, principio_ativo) VALUES (?, ?, ?)";
        String sqlEstoque = "INSERT INTO estoque_medicamento (id_medicamento, numero_lote, data_validade, quantidade_inicial, data_entrada) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            int idMedicamento;
            try (PreparedStatement pstmtCatalogo = conn.prepareStatement(sqlCatalogo, Statement.RETURN_GENERATED_KEYS)) {
                pstmtCatalogo.setString(1, medicamento.getNome());
                pstmtCatalogo.setString(2, medicamento.getFabricante());
                pstmtCatalogo.setString(3, medicamento.getPrincipioAtivo());
                pstmtCatalogo.executeUpdate();

                try (ResultSet generatedKeys = pstmtCatalogo.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idMedicamento = generatedKeys.getInt(1);
                        medicamento.setId(idMedicamento);
                    } else {
                        throw new SQLException("Falha ao obter o ID do medicamento, nenhuma linha afetada.");
                    }
                }
            }

            try (PreparedStatement pstmtEstoque = conn.prepareStatement(sqlEstoque)) {
                pstmtEstoque.setInt(1, idMedicamento);
                pstmtEstoque.setString(2, medicamento.getNumeroLote());
                pstmtEstoque.setDate(3, Date.valueOf(medicamento.getDataValidade()));
                pstmtEstoque.setInt(4, medicamento.getQuantidade());
                pstmtEstoque.setDate(5, Date.valueOf(medicamento.getDataEntrada()));
                pstmtEstoque.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void atualizar(Medicamento medicamento) throws SQLException {
        String sqlCatalogo = "UPDATE catalogo_medicamento SET nome_comercial = ?, fabricante = ?, principio_ativo = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtCatalogo = conn.prepareStatement(sqlCatalogo)) {
                pstmtCatalogo.setString(1, medicamento.getNome());
                pstmtCatalogo.setString(2, medicamento.getFabricante());
                pstmtCatalogo.setString(3, medicamento.getPrincipioAtivo());
                pstmtCatalogo.setInt(4, medicamento.getId());
                pstmtCatalogo.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public void excluir(int idMedicamento) throws SQLException {
        String sqlEstoque = "DELETE FROM estoque_medicamento WHERE id_medicamento = ?";
        String sqlCatalogo = "CALL proc_deletar_medicamento_catalogo(?)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtEstoque = conn.prepareStatement(sqlEstoque)) {
                pstmtEstoque.setInt(1, idMedicamento);
                pstmtEstoque.executeUpdate();
            }

            try (CallableStatement cstmtCatalogo = conn.prepareCall(sqlCatalogo)) {
                cstmtCatalogo.setInt(1, idMedicamento);
                cstmtCatalogo.execute();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    public Medicamento buscarPorId(int idMedicamento) throws SQLException {
        String sql = "SELECT c.id, c.nome_comercial, c.fabricante, c.principio_ativo, " +
                     "e.numero_lote, e.quantidade_inicial, e.data_validade, e.data_entrada " +
                     "FROM catalogo_medicamento c " +
                     "JOIN estoque_medicamento e ON c.id = e.id_medicamento " +
                     "WHERE c.id = ? AND c.nome_comercial NOT LIKE '%_DESATIVADO_%' " +
                     "ORDER BY e.data_entrada DESC LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idMedicamento);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome_comercial");
                String fabricante = rs.getString("fabricante");
                String principioAtivo = rs.getString("principio_ativo");
                String numeroLote = rs.getString("numero_lote");
                int quantidade = rs.getInt("quantidade_inicial");
                LocalDate dataValidade = rs.getDate("data_validade").toLocalDate();
                LocalDate dataEntrada = rs.getDate("data_entrada").toLocalDate();

                return new Medicamento(id, nome, fabricante, principioAtivo, numeroLote, quantidade, dataValidade, dataEntrada);
            }
        }
        return null;
    }
}