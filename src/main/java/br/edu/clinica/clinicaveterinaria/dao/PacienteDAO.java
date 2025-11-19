package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Proprietario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void inserirPaciente(Paciente paciente) throws SQLException {
        String sql = "CALL proc_inserir_paciente(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getEspecie());
            stmt.setString(3, paciente.getRaca());
            stmt.setDate(4, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(5, paciente.getProprietario().getCpf());
            
            stmt.execute();
        }
    }

    public List<Paciente> listarTodos() throws SQLException {
        List<Paciente> listaPaciente = new ArrayList<>();
        
        String sql = "SELECT p.id, p.nome, p.especie, p.raca, p.data_nascimento, " +
                     "pr.cpf, pr.nome AS prop_nome, pr.telefone AS prop_tel, pr.email AS prop_email, " +
                     "pr.rua, pr.numero, pr.bairro, pr.cidade, pr.estado, pr.cep " +
                     "FROM paciente p " +
                     "JOIN proprietario pr ON p.id_proprietario = pr.cpf " +
                     "WHERE p.ativo = true " +
                     "ORDER BY p.nome";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Proprietario proprietario = new Proprietario();
                proprietario.setCpf(rs.getString("cpf"));
                proprietario.setNome(rs.getString("prop_nome"));
                proprietario.setEmail(rs.getString("prop_email"));
                proprietario.setTelefone(rs.getString("prop_tel"));
                proprietario.setRua(rs.getString("rua"));
                proprietario.setNumero(rs.getString("numero"));
                proprietario.setBairro(rs.getString("bairro"));
                proprietario.setCidade(rs.getString("cidade"));
                proprietario.setEstado(rs.getString("estado"));
                proprietario.setCep(rs.getString("cep"));

                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("id"));
                paciente.setNome(rs.getString("nome"));
                paciente.setEspecie(rs.getString("especie"));
                paciente.setRaca(rs.getString("raca"));
                
                Date dataNasc = rs.getDate("data_nascimento");
                if (dataNasc != null) {
                    paciente.setDataNascimento(dataNasc.toLocalDate());
                }
                
                paciente.setProprietario(proprietario);
                listaPaciente.add(paciente);
            }
        }
        
        return listaPaciente;
    }

    public void atualizarPaciente(Paciente paciente) throws SQLException {
        String sql = "CALL proc_atualizar_paciente(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, paciente.getId());
            stmt.setString(2, paciente.getNome());
            stmt.setString(3, paciente.getEspecie());
            stmt.setString(4, paciente.getRaca());
            stmt.setDate(5, Date.valueOf(paciente.getDataNascimento()));
            stmt.setString(6, paciente.getProprietario().getCpf());
            
            stmt.execute();
        }
    }

    public void deletarPaciente(int id) throws SQLException {
        String sql = "CALL proc_deletar_paciente_soft(?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    public Paciente buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.nome, p.especie, p.raca, p.data_nascimento, " +
                     "pr.cpf, pr.nome AS prop_nome, pr.telefone AS prop_tel, pr.email AS prop_email, " +
                     "pr.rua, pr.numero, pr.bairro, pr.cidade, pr.estado, pr.cep " +
                     "FROM paciente p " +
                     "JOIN proprietario pr ON p.id_proprietario = pr.cpf " +
                     "WHERE p.id = ? AND p.ativo = true";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Proprietario proprietario = new Proprietario();
                proprietario.setCpf(rs.getString("cpf"));
                proprietario.setNome(rs.getString("prop_nome"));
                proprietario.setEmail(rs.getString("prop_email"));
                proprietario.setTelefone(rs.getString("prop_tel"));
                proprietario.setRua(rs.getString("rua"));
                proprietario.setNumero(rs.getString("numero"));
                proprietario.setBairro(rs.getString("bairro"));
                proprietario.setCidade(rs.getString("cidade"));
                proprietario.setEstado(rs.getString("estado"));
                proprietario.setCep(rs.getString("cep"));

                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("id"));
                paciente.setNome(rs.getString("nome"));
                paciente.setEspecie(rs.getString("especie"));
                paciente.setRaca(rs.getString("raca"));
                
                Date dataNasc = rs.getDate("data_nascimento");
                if (dataNasc != null) {
                    paciente.setDataNascimento(dataNasc.toLocalDate());
                }
                
                paciente.setProprietario(proprietario);
                return paciente;
            }
        }
        return null;
    }
}
