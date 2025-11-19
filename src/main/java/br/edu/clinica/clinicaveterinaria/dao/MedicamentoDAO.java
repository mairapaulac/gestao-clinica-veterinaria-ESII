package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Medicamento;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO {

    /**
     * Lista os medicamentos com base na função do banco de dados que calcula o estoque disponível.
     * NOTA: Esta função não retorna a data de validade individual do lote, pois agrega o estoque.
     * A data de validade será nula no objeto Medicamento retornado.
     */
    public List<Medicamento> listarTodos() throws SQLException {
        // Consulta temporária para diagnóstico: Lista todos os medicamentos do catálogo,
        // mesmo sem estoque, para garantir que a tabela seja populada.
        String sql = "SELECT c.id, c.nome_comercial, c.fabricante, COALESCE(SUM(e.quantidade_inicial), 0) as quantidade_total " +
                     "FROM catalogo_medicamento c " +
                     "LEFT JOIN estoque_medicamento e ON c.id = e.id_medicamento " +
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

                // Usando o construtor para a lista, sem detalhes de lote/validade.
                Medicamento medicamento = new Medicamento(id, nome, fabricante, quantidade);
                medicamentos.add(medicamento);
            }
        }
        return medicamentos;
    }

    /**
     * Insere um novo medicamento e seu lote de estoque inicial.
     * NOTA: Utiliza JDBC transacional direto porque as procedures existentes
     * (proc_inserir_medicamento_catalogo) não retornam o ID gerado,
     * impossibilitando a inserção subsequente no estoque.
     */
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
                        medicamento.setId(idMedicamento); // Atualiza o ID no objeto
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
                    ex.printStackTrace(); // Logar falha no rollback
                }
            }
            throw e; // Re-lança a exceção original
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace(); // Logar falha ao fechar conexão
                }
            }
        }
    }

    /**
     * Atualiza as informações de um medicamento.
     * NOTA: Esta implementação atualiza apenas os dados da tabela 'catalogo_medicamento'.
     * A atualização da tabela 'estoque_medicamento' não é realizada pois a UI não
     * especifica qual lote individual deve ser alterado caso existam múltiplos lotes.
     */
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

            // A lógica para atualizar o lote específico em estoque_medicamento é ambígua e,
            // portanto, omitida para evitar inconsistência de dados. A UI precisaria ser
            // adaptada para gerenciar lotes individualmente.

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

    /**
     * Exclui um medicamento do catálogo e todos os seus lotes do estoque.
     * NOTA: As procedures existentes (proc_deletar_medicamento_catalogo) fazem um soft delete,
     * o que conflita com a exclusão de lotes. Usamos JDBC para um hard delete completo.
     */
    public void excluir(int idMedicamento) throws SQLException {
        String sqlEstoque = "DELETE FROM estoque_medicamento WHERE id_medicamento = ?";
        String sqlCatalogo = "DELETE FROM catalogo_medicamento WHERE id_medicamento = ?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Primeiro deleta da tabela 'estoque_medicamento' para evitar violação de FK
            try (PreparedStatement pstmtEstoque = conn.prepareStatement(sqlEstoque)) {
                pstmtEstoque.setInt(1, idMedicamento);
                pstmtEstoque.executeUpdate();
            }

            // Depois deleta da tabela 'catalogo_medicamento'
            try (PreparedStatement pstmtCatalogo = conn.prepareStatement(sqlCatalogo)) {
                pstmtCatalogo.setInt(1, idMedicamento);
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

    public Medicamento buscarPorId(int idMedicamento) throws SQLException {
        String sql = "SELECT c.id, c.nome_comercial, c.fabricante, c.principio_ativo, " +
                     "e.numero_lote, e.quantidade_inicial, e.data_validade, e.data_entrada " +
                     "FROM catalogo_medicamento c " +
                     "JOIN estoque_medicamento e ON c.id = e.id_medicamento " +
                     "WHERE c.id = ? " +
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
        return null; // Retorna nulo se não encontrar
    }
}