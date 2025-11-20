package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {

    public void adicionarVeterinario(Veterinario veterinario) throws SQLException {
        String sql = "INSERT INTO veterinario (nome, crmv, telefone, especialidade) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, veterinario.getNome());
            pstmt.setString(2, veterinario.getCRMV());
            pstmt.setString(3, veterinario.getTelefone());
            pstmt.setString(4, veterinario.getEspecialidade());
            pstmt.executeUpdate();
        }
    }

    public List<Veterinario> listarTodos() throws SQLException {
        List<Veterinario> listaVeterinarios = new ArrayList<>();
        String sql = "SELECT id, nome, crmv, telefone, especialidade, email, senha FROM veterinario ORDER BY nome";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Veterinario vet = new Veterinario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("crmv"),
                        rs.getString("telefone"),
                        rs.getString("especialidade"),
                        rs.getString("email"),
                        rs.getString("senha")
                );
                listaVeterinarios.add(vet);
            }
        }
        return listaVeterinarios;
    }

    public Veterinario buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, crmv, telefone, especialidade, email, senha FROM veterinario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Veterinario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("crmv"),
                        rs.getString("telefone"),
                        rs.getString("especialidade"),
                        rs.getString("email"),
                        rs.getString("senha")
                );
            }
        }
        return null;
    }

    public Veterinario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, crmv, telefone, especialidade, email, senha FROM veterinario WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Veterinario(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("crmv"),
                        rs.getString("telefone"),
                        rs.getString("especialidade"),
                        rs.getString("email"),
                        rs.getString("senha")
                );
            }
        }
        return null;
    }

    public void atualizarVeterinario(Veterinario veterinario) throws SQLException {
        String sql = "UPDATE veterinario SET nome = ?, crmv = ?, telefone = ?, especialidade = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, veterinario.getNome());
            pstmt.setString(2, veterinario.getCRMV());
            pstmt.setString(3, veterinario.getTelefone());
            pstmt.setString(4, veterinario.getEspecialidade());
            pstmt.setInt(5, veterinario.getId());

            pstmt.executeUpdate();
        }
    }

    public void deletarVeterinario(int id) throws SQLException {
        String sql = "DELETE FROM veterinario WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

}