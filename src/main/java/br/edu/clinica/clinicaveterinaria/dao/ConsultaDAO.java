package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {

    public void inserirConsulta(Consulta consulta) throws SQLException {
        String sql = "CALL proc_inserir_consulta(?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataConsulta()));
            stmt.setString(2, consulta.getDiagnostico());
            stmt.setInt(3, consulta.getPaciente().getId());
            stmt.setInt(4, consulta.getVeterinario().getId());
            
            stmt.execute();
        }
    }

    public List<Consulta> listarTodas() throws SQLException {
        List<Consulta> listaConsulta = new ArrayList<>();
        
        String sql = "SELECT c.id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM consulta c " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "ORDER BY c.data_consulta DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                // Criar paciente
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
                consulta.setId(rs.getInt("id"));
                
                Timestamp timestamp = rs.getTimestamp("data_consulta");
                if (timestamp != null) {
                    consulta.setDataConsulta(timestamp.toLocalDateTime());
                }
                
                consulta.setDiagnostico(rs.getString("diagnostico"));
                consulta.setPaciente(paciente);
                consulta.setVeterinario(veterinario);
                
                listaConsulta.add(consulta);
            }
        }
        
        return listaConsulta;
    }

    public List<Consulta> listarPorDataEVeterinario(java.time.LocalDate data, int idVeterinario) throws SQLException {
        List<Consulta> listaConsulta = new ArrayList<>();
        
        String sql = "SELECT c.id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, " +
                     "v.id AS vet_id, v.nome AS vet_nome " +
                     "FROM consulta c " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "WHERE DATE(c.data_consulta) = ? AND v.id = ? " +
                     "ORDER BY c.data_consulta";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(data));
            stmt.setInt(2, idVeterinario);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Paciente paciente = new Paciente();
                paciente.setId(rs.getInt("pac_id"));
                paciente.setNome(rs.getString("pac_nome"));
                
                Veterinario veterinario = new Veterinario();
                veterinario.setId(rs.getInt("vet_id"));
                veterinario.setNome(rs.getString("vet_nome"));
                
                Consulta consulta = new Consulta();
                consulta.setId(rs.getInt("id"));
                
                Timestamp timestamp = rs.getTimestamp("data_consulta");
                if (timestamp != null) {
                    consulta.setDataConsulta(timestamp.toLocalDateTime());
                }
                
                consulta.setDiagnostico(rs.getString("diagnostico"));
                consulta.setPaciente(paciente);
                consulta.setVeterinario(veterinario);
                
                listaConsulta.add(consulta);
            }
        }
        
        return listaConsulta;
    }

    public void atualizarConsulta(Consulta consulta) throws SQLException {
        String sql = "CALL proc_atualizar_consulta(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, consulta.getId());
            stmt.setTimestamp(2, Timestamp.valueOf(consulta.getDataConsulta()));
            stmt.setString(3, consulta.getDiagnostico());
            stmt.setInt(4, consulta.getPaciente().getId());
            stmt.setInt(5, consulta.getVeterinario().getId());
            
            stmt.execute();
        }
    }

    public void deletarConsulta(int id) throws SQLException {
        Connection conn = ConnectionFactory.getConnection();
        try {
            // Desabilitar auto-commit para usar transação
            conn.setAutoCommit(false);
            
            // Primeiro, deletar todos os tratamentos associados à consulta
            // Isso também deleta automaticamente os registros em tratamento_medicamento
            String sqlTratamentos = "SELECT id FROM tratamento WHERE id_consulta = ?";
            List<Integer> idsTratamentos = new ArrayList<>();
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlTratamentos)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        idsTratamentos.add(rs.getInt("id"));
                    }
                }
            }
            
            // Deletar cada tratamento (isso também deleta os registros em tratamento_medicamento)
            for (Integer idTratamento : idsTratamentos) {
                // Usar a connection existente para manter a transação
                deletarTratamentoComConnection(conn, idTratamento);
            }
            
            // Agora deletar a consulta
            String sql = "DELETE FROM consulta WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // Commit da transação
            conn.commit();
        } catch (SQLException e) {
            // Rollback em caso de erro
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
    
    /**
     * Método auxiliar para deletar tratamento usando uma connection específica
     * (para manter a transação)
     */
    private void deletarTratamentoComConnection(Connection conn, int idTratamento) throws SQLException {
        // Primeiro deletar os registros em tratamento_medicamento
        String sqlMedicamentos = "DELETE FROM tratamento_medicamento WHERE id_tratamento = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlMedicamentos)) {
            stmt.setInt(1, idTratamento);
            stmt.executeUpdate();
        }
        
        // Depois deletar o tratamento
        String sqlTratamento = "DELETE FROM tratamento WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlTratamento)) {
            stmt.setInt(1, idTratamento);
            stmt.executeUpdate();
        }
    }

    public Consulta buscarPorId(int id) throws SQLException {
        String sql = "SELECT c.id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM consulta c " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "WHERE c.id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
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
                consulta.setId(rs.getInt("id"));
                
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
        return null;
    }

    public List<Consulta> listarPorPaciente(int idPaciente) throws SQLException {
        List<Consulta> listaConsulta = new ArrayList<>();
        
        String sql = "SELECT c.id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM consulta c " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "WHERE c.id_paciente = ? " +
                     "ORDER BY c.data_consulta DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPaciente);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
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
                consulta.setId(rs.getInt("id"));
                
                Timestamp timestamp = rs.getTimestamp("data_consulta");
                if (timestamp != null) {
                    consulta.setDataConsulta(timestamp.toLocalDateTime());
                }
                
                consulta.setDiagnostico(rs.getString("diagnostico"));
                consulta.setPaciente(paciente);
                consulta.setVeterinario(veterinario);
                
                listaConsulta.add(consulta);
            }
        }
        
        return listaConsulta;
    }
}
