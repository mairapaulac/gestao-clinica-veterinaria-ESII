package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Tratamento;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TratamentoDAO {

    public void inserirTratamento(Tratamento tratamento) throws SQLException {
        String sql = "CALL proc_inserir_tratamento(?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, tratamento.getConsulta().getId());
            stmt.setString(2, tratamento.getDescricao());
            
            stmt.execute();
        }
    }

    public int inserirTratamentoERetornarId(Tratamento tratamento) throws SQLException {
        String sql = "INSERT INTO tratamento (id_consulta, descricao) VALUES (?, ?) RETURNING id";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tratamento.getConsulta().getId());
            stmt.setString(2, tratamento.getDescricao());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        throw new SQLException("Falha ao inserir tratamento e obter ID");
    }

    public void inserirUsoMedicamento(int idTratamento, int idEstoqueMedicamento, int quantidade) throws SQLException {
        String sql = "CALL proc_inserir_uso_medicamento(?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, idTratamento);
            stmt.setInt(2, idEstoqueMedicamento);
            stmt.setInt(3, quantidade);
            
            stmt.execute();
        }
    }

    public Tratamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT t.id, t.descricao, t.id_consulta, " +
                     "c.id AS cons_id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM tratamento t " +
                     "JOIN consulta c ON t.id_consulta = c.id " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "WHERE t.id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Consulta consulta = criarConsultaDoResultSet(rs);
                Tratamento tratamento = new Tratamento(rs.getString("descricao"), consulta);
                tratamento.setId(rs.getInt("id"));
                return tratamento;
            }
        }
        return null;
    }

    public List<Tratamento> listarPorConsulta(int idConsulta) throws SQLException {
        List<Tratamento> tratamentos = new ArrayList<>();
        String sql = "SELECT t.id, t.descricao, t.id_consulta, " +
                     "c.id AS cons_id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM tratamento t " +
                     "JOIN consulta c ON t.id_consulta = c.id " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "WHERE t.id_consulta = ? " +
                     "ORDER BY t.id DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idConsulta);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Consulta consulta = criarConsultaDoResultSet(rs);
                Tratamento tratamento = new Tratamento(rs.getString("descricao"), consulta);
                tratamento.setId(rs.getInt("id"));
                tratamentos.add(tratamento);
            }
        }
        return tratamentos;
    }

    public List<Tratamento> listarPorPaciente(int idPaciente) throws SQLException {
        List<Tratamento> tratamentos = new ArrayList<>();
        String sql = "SELECT t.id, t.descricao, t.id_consulta, " +
                     "c.id AS cons_id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM tratamento t " +
                     "JOIN consulta c ON t.id_consulta = c.id " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "WHERE c.id_paciente = ? " +
                     "ORDER BY c.data_consulta DESC, t.id DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPaciente);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Consulta consulta = criarConsultaDoResultSet(rs);
                Tratamento tratamento = new Tratamento(rs.getString("descricao"), consulta);
                tratamento.setId(rs.getInt("id"));
                tratamentos.add(tratamento);
            }
        }
        return tratamentos;
    }

    public void atualizarTratamento(Tratamento tratamento) throws SQLException {
        String sql = "CALL proc_atualizar_tratamento(?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, tratamento.getId());
            stmt.setString(2, tratamento.getDescricao());
            stmt.setInt(3, tratamento.getConsulta().getId());
            
            stmt.execute();
        }
    }

    public void deletarTratamento(int id) throws SQLException {
        String sql = "CALL proc_deletar_tratamento(?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    private Consulta criarConsultaDoResultSet(ResultSet rs) throws SQLException {
        Paciente paciente = new Paciente();
        paciente.setId(rs.getInt("pac_id"));
        paciente.setNome(rs.getString("pac_nome"));
        paciente.setEspecie(rs.getString("especie"));
        paciente.setRaca(rs.getString("raca"));
        
        Veterinario veterinario = new Veterinario();
        veterinario.setId(rs.getInt("vet_id"));
        veterinario.setNome(rs.getString("vet_nome"));
        veterinario.setCRMV(rs.getString("crmv"));
        veterinario.setTelefone(rs.getString("vet_tel"));
        veterinario.setEspecialidade(rs.getString("especialidade"));
        
        Consulta consulta = new Consulta();
        consulta.setId(rs.getInt("cons_id"));
        
        Timestamp timestamp = rs.getTimestamp("data_consulta");
        if (timestamp != null) {
            consulta.setDataConsulta(timestamp.toLocalDateTime());
        }
        
        consulta.setDiagnostico(rs.getString("diagnostico"));
        consulta.setPaciente(paciente);
        consulta.setVeterinario(veterinario);
        
        return consulta;
    }
}
