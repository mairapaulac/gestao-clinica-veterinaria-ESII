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
        String sql = "INSERT INTO " + NOME_TABELA + " (nome, crmv, telefoneveterinario, especialidade) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, veterinario.getNome());
            pstmt.setString(2, veterinario.getCRMV());
            pstmt.setString(3, veterinario.getTelefone());
            pstmt.setString(4, veterinario.getEspecialidade());
            pstmt.executeUpdate();

            System.out.println("Veterinário " + veterinario.getNome() + " inserido com sucesso!");
        }
    }
    //READ
    public List<Veterinario> listarTodos() throws SQLException {
        List<Veterinario> listaVeterinarios = new ArrayList<>();
        String sql = "SELECT pkidveterinario, nome, crmv, telefoneveterinario, especialidade FROM " + NOME_TABELA;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Veterinario vet = new Veterinario(
                    rs.getInt("pkidveterinario"),
                    rs.getString("nome"),
                    rs.getString("crmv"),
                    rs.getString("telefoneveterinario"),
                    rs.getString("especialidade")
                );
                listaVeterinarios.add(vet);
            }
        }
        return listaVeterinarios;
    }
    //UPDATE
    public void atualizarVeterinario(Veterinario veterinario) throws SQLException {
        String sql = "UPDATE " + NOME_TABELA + " SET nome = ?, crmv = ?, telefoneveterinario = ?, especialidade = ? WHERE pkidveterinario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, veterinario.getNome());
            pstmt.setString(2, veterinario.getCRMV());
            pstmt.setString(3, veterinario.getTelefone());
            pstmt.setString(4, veterinario.getEspecialidade());
            pstmt.setInt(5, veterinario.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Veterinário com ID " + veterinario.getId() + " atualizado com sucesso!");
            } else {
                System.out.println("Nenhum veterinário encontrado com ID " + veterinario.getId());
            }
        }
    }
    //DELETE
    public void deletarVeterinario(int id) throws SQLException {
        String sql = "DELETE FROM " + NOME_TABELA + " WHERE pkidveterinario = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Veterinário com ID " + id + " deletado com sucesso!");
            } else {
                System.out.println("Nenhum veterinário encontrado com ID " + id);
            }
        }
    }

}
