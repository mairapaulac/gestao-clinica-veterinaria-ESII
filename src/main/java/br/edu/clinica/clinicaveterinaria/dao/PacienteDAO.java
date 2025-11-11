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

    private static final String NOME_TABELA = "paciente";

    public void inserirPaciente(Paciente paciente) {

        String sql = "INSERT INTO + NOME_TABELA + (nomepaciente, especie, raca, data_nascimento, fkproprietario) VALUES (?, ?, ?, ?, ?)";
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

        String sql = "SELECT p.pkidpaciente, p.nomepaciente, p.especie, p.raca, p.data_nascimento," +
                     "pr.pkid_proprietario AS prop_id, pr.nomeproprietario AS prop_nome, pr.telefone AS prop_tel, pr.email AS prop_email" +
                     "FROM + NOME_TABELA + p" +
                     "JOIN proprietario pr ON p.fkproprietario = pr.pkid_proprietario";

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
                paciente.setId(rs.getInt("pkidpaciente"));
                paciente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
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

        String sql = "UPDATE + NOME_TABELA + SET nome = ?, especie = ?, raca = ?, data_nascimento = ?, fkproprietario = ? WHERE pkidpaciente = ? ";

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
        String sql = "DELETE FROM + NOME_TABELA + WHERE pkidpaciente = ?";

        try(Connection conn = ConnectionFactory.getConnection()) {

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

}
