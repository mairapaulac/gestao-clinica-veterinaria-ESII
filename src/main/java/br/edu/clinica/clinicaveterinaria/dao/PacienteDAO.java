package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void inserirPaciente(Paciente paciente) {

        String sql = "INSERT INTO usuario (nome, especie, raca, dataNascimento, idProprietario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getEspecie());
            stmt.setString(3, paciente.getRaca());
            stmt.setString(4, paciente.getDataNascimento().toString());
            stmt.setInt(5, paciente.getProprietario().getId());

            stmt.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

   //READ
    public List<Paciente> listarTodos() {
        List<Paciente> listaPaciente = new ArrayList<>();

        String sql = "SELECT p.id, p.nome, p.especie, p.raca, p.dataNascimento," +
                     "pr.id AS prop_id, pr.nome AS prop_nome, pr.telefone AS prop_tel, pr.email AS prop_email" +
                     "FROM paciente p" +
                     "JOIN proprietario pr ON p.idproprietario = pr.id";

        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);

            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {
                Proprietario proprietario = new Proprietario();
                proprietario.setId(rs.getInt("prop_id"));
                proprietario.setNome(rs.getString("prop_nome"));
                proprietario.setEmail(rs.getString("prop_email"));
                proprietario.setTelefone(rs.getString("prop_tel"));


                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("id"));
                paciente.setDataNascimento(rs.getDate("dataNascimento").toLocalDate());
                paciente.setProprietario(proprietario);
                paciente.setRaca(rs.getString("raca"));
                paciente.setEspecie(rs.getString("especie"));

                listaPaciente.add(paciente);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return listaPaciente;
    }

    public void update(Paciente paciente) {

        String sql = "UPDATE paciente SET nome = ?, especie = ?, raca = ?, data_nascimento = ?, idProprietario = ? WHERE id = ? ";

        try(Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getEspecie());
            stmt.setString(3, paciente.getRaca());
            stmt.setString(4, paciente.getDataNascimento().toString());
            stmt.setInt(5, paciente.getProprietario().getId());
            stmt.setInt(6, paciente.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM paciente WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
