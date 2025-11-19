package br.edu.clinica.clinicaveterinaria.dao;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {
    private static final String NOME_TABELA = "veterinario";


    //CREATE
    public void adicionarVeterinario(Veterinario veterinario) throws SQLException {
        // 1. Alteração: Usar CALL para chamar a Stored Procedure
        String sql = "CALL proc_inserir_veterinario(?, ?, ?, ?)";

        // ATENÇÃO: Adicionamos Statement.RETURN_GENERATED_KEYS
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, veterinario.getNome());
            pstmt.setString(2, veterinario.getCRMV());
            pstmt.setString(3, veterinario.getTelefone());
            pstmt.setString(4, veterinario.getEspecialidade());

            pstmt.executeUpdate();

            // NOVO PASSO: Captura o ID gerado pelo banco e atualiza o objeto Java
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Assume que a coluna ID gerada é a primeira (índice 1)
                    veterinario.setId(rs.getInt(1));
                }
            }

            System.out.println("Veterinário " + veterinario.getNome() + " inserido com sucesso!");
        }
    }

    public Veterinario buscarPorCRMV(String crmv) throws SQLException {

        String sql = "SELECT * FROM funct_get_infos_veterinario(?)";
        Veterinario vet = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, crmv);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeia os dados retornados pela função para o objeto Veterinario
                    vet = new Veterinario(
                            rs.getInt("id"), // Assumindo que a função retorna 'id' (pkidveterinario)
                            rs.getString("nome"),
                            rs.getString("crmv"),
                            rs.getString("telefone"), // Sua coluna é 'telefone' no BD
                            rs.getString("especialidade")
                    );
                }
            }
        }
        // Se a função SQL falhar (CRMV não encontrado), ela lança uma exceção que o Java captura.
        return vet; // Retorna null se não encontrar
    }
    public void atualizarVeterinario(Veterinario veterinario) throws SQLException {

        String sql = "CALL proc_atualizar_veterinario(?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 1. CRMV ATUAL (para identificar o registro que será alterado)
            pstmt.setString(1, veterinario.getCRMV());
            // 2. Novo Nome
            pstmt.setString(2, veterinario.getNome());
            // 3. Novo CRMV (pode ser igual ao atual, se não mudar)
            pstmt.setString(3, veterinario.getCRMV());
            // 4. Novo Telefone
            pstmt.setString(4, veterinario.getTelefone());
            // 5. Nova Especialidade
            pstmt.setString(5, veterinario.getEspecialidade());

            pstmt.executeUpdate();
            System.out.println("Veterinário com CRMV " + veterinario.getCRMV() + " atualizado com sucesso!");
        }
    }

    public void deletarVeterinario(int id) throws SQLException {

        String sql = "CALL proc_deletar_veterinario(?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Mapeia o ID

            pstmt.executeUpdate();

            System.out.println("Veterinário com ID " + id + " desativado (Soft Delete) com sucesso!");
        }
    }

}
