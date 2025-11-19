package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Proprietario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProprietarioDAO {

    public void inserirProprietario(Proprietario proprietario) throws SQLException {
        String sql = "CALL proc_inserir_proprietario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, proprietario.getCpf());
            stmt.setString(2, proprietario.getNome());
            stmt.setString(3, proprietario.getTelefone());
            stmt.setString(4, proprietario.getEmail());
            stmt.setString(5, proprietario.getRua());
            stmt.setString(6, proprietario.getNumero());
            stmt.setString(7, proprietario.getBairro());
            stmt.setString(8, proprietario.getCidade());
            stmt.setString(9, proprietario.getEstado());
            stmt.setString(10, proprietario.getCep());
            
            stmt.execute();
        }
    }

    public Proprietario buscarPorCpf(String cpf) throws SQLException {
        String sql = "SELECT * FROM proprietario WHERE cpf = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return criarProprietarioDoResultSet(rs);
            }
        }
        return null;
    }

    public Proprietario buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM proprietario WHERE nome = ? LIMIT 1";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return criarProprietarioDoResultSet(rs);
            }
        }
        return null;
    }

    public List<Proprietario> listarTodos() throws SQLException {
        List<Proprietario> lista = new ArrayList<>();
        String sql = "SELECT * FROM proprietario ORDER BY nome";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                lista.add(criarProprietarioDoResultSet(rs));
            }
        }
        return lista;
    }

    public void atualizarProprietario(Proprietario proprietario) throws SQLException {
        String sql = "CALL proc_atualizar_proprietario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, proprietario.getCpf());
            stmt.setString(2, proprietario.getNome());
            stmt.setString(3, proprietario.getTelefone());
            stmt.setString(4, proprietario.getEmail());
            stmt.setString(5, proprietario.getRua());
            stmt.setString(6, proprietario.getNumero());
            stmt.setString(7, proprietario.getBairro());
            stmt.setString(8, proprietario.getCidade());
            stmt.setString(9, proprietario.getEstado());
            stmt.setString(10, proprietario.getCep());
            
            stmt.execute();
        }
    }

    private Proprietario criarProprietarioDoResultSet(ResultSet rs) throws SQLException {
        Proprietario prop = new Proprietario();
        prop.setCpf(rs.getString("cpf"));
        prop.setNome(rs.getString("nome"));
        prop.setTelefone(rs.getString("telefone"));
        prop.setEmail(rs.getString("email"));
        prop.setRua(rs.getString("rua"));
        prop.setNumero(rs.getString("numero"));
        prop.setBairro(rs.getString("bairro"));
        prop.setCidade(rs.getString("cidade"));
        prop.setEstado(rs.getString("estado"));
        prop.setCep(rs.getString("cep"));
        return prop;
    }
}
