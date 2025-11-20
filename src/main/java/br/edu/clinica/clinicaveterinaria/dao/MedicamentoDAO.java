package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Medicamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    public List<Medicamento> listarTodos() throws SQLException {
        // Usa a mesma função que calcula estoque disponível corretamente
        String sql = "SELECT c.id, c.nome_comercial, c.fabricante, " +
                     "COALESCE(qtd.quantidade_disponivel, 0) as quantidade_total " +
                     "FROM catalogo_medicamento c " +
                     "LEFT JOIN funct_quantidade_disponivel_por_medicamento() qtd ON c.id = qtd.id_medicamento " +
                     "WHERE c.nome_comercial NOT LIKE '%_DESATIVADO_%' " +
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
                     "e.id AS estoque_id, e.numero_lote, e.quantidade_inicial, e.data_validade, e.data_entrada, " +
                     "(e.quantidade_inicial - COALESCE(SUM(tm.quantidade_utilizada), 0)) AS quantidade_disponivel " +
                     "FROM catalogo_medicamento c " +
                     "JOIN estoque_medicamento e ON c.id = e.id_medicamento " +
                     "LEFT JOIN tratamento_medicamento tm ON e.id = tm.id_estoque_medicamento " +
                     "WHERE c.id = ? AND c.nome_comercial NOT LIKE '%_DESATIVADO_%' " +
                     "AND e.data_validade >= CURRENT_DATE " +
                     "GROUP BY c.id, c.nome_comercial, c.fabricante, c.principio_ativo, " +
                     "e.id, e.numero_lote, e.quantidade_inicial, e.data_validade, e.data_entrada " +
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
                int quantidadeDisponivel = rs.getInt("quantidade_disponivel");
                LocalDate dataValidade = rs.getDate("data_validade").toLocalDate();
                LocalDate dataEntrada = rs.getDate("data_entrada").toLocalDate();

                return new Medicamento(id, nome, fabricante, principioAtivo, numeroLote, quantidadeDisponivel, dataValidade, dataEntrada);
            }
        }
        return null;
    }

    public List<Medicamento> listarMedicamentosDisponiveis() throws SQLException {
        List<Medicamento> medicamentos = new ArrayList<>();
        String sql = "SELECT * FROM funct_quantidade_disponivel_por_medicamento()";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_medicamento");
                String nome = rs.getString("nome_comercial");
                int quantidadeDisponivel = rs.getInt("quantidade_disponivel");

                Medicamento medicamento = new Medicamento(id, nome, "", quantidadeDisponivel);
                medicamentos.add(medicamento);
            }
        }
        return medicamentos;
    }

    public int obterEstoqueDisponivel(int idMedicamento) throws SQLException {
        String sql = "SELECT quantidade_disponivel FROM funct_quantidade_disponivel_por_medicamento() WHERE id_medicamento = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idMedicamento);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("quantidade_disponivel");
            }
        }
        return 0;
    }

    public List<Medicamento> listarLotesDisponiveisPorMedicamento(int idMedicamento) throws SQLException {
        List<Medicamento> lotes = new ArrayList<>();
        String sql = "SELECT em.id, em.numero_lote, em.quantidade_inicial, em.data_validade, em.data_entrada, " +
                     "cm.nome_comercial, cm.fabricante, cm.principio_ativo, " +
                     "(em.quantidade_inicial - COALESCE(SUM(tm.quantidade_utilizada), 0)) AS quantidade_disponivel " +
                     "FROM estoque_medicamento em " +
                     "JOIN catalogo_medicamento cm ON em.id_medicamento = cm.id " +
                     "LEFT JOIN tratamento_medicamento tm ON em.id = tm.id_estoque_medicamento " +
                     "WHERE em.id_medicamento = ? AND em.data_validade >= CURRENT_DATE " +
                     "AND cm.nome_comercial NOT LIKE '%_DESATIVADO_%' " +
                     "GROUP BY em.id, em.numero_lote, em.quantidade_inicial, em.data_validade, em.data_entrada, " +
                     "cm.nome_comercial, cm.fabricante, cm.principio_ativo " +
                     "HAVING (em.quantidade_inicial - COALESCE(SUM(tm.quantidade_utilizada), 0)) > 0 " +
                     "ORDER BY em.data_validade ASC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idMedicamento);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int idEstoque = rs.getInt("id");
                String nome = rs.getString("nome_comercial");
                String fabricante = rs.getString("fabricante");
                String principioAtivo = rs.getString("principio_ativo");
                String numeroLote = rs.getString("numero_lote");
                int quantidadeDisponivel = rs.getInt("quantidade_disponivel");
                LocalDate dataValidade = rs.getDate("data_validade").toLocalDate();
                LocalDate dataEntrada = rs.getDate("data_entrada").toLocalDate();

                Medicamento medicamento = new Medicamento(idEstoque, nome, fabricante, principioAtivo, numeroLote, quantidadeDisponivel, dataValidade, dataEntrada);
                lotes.add(medicamento);
            }
        }
        return lotes;
    }
}