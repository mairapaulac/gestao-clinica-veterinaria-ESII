package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Consulta;
import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import br.edu.clinica.clinicaveterinaria.model.Pagamento;
import br.edu.clinica.clinicaveterinaria.model.Paciente;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PagamentoDAO {

    public void inserirPagamento(Pagamento pagamento) throws SQLException {
        String sql = "CALL proc_inserir_pagamento(?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setBigDecimal(1, java.math.BigDecimal.valueOf(pagamento.getValorTotal()));
            stmt.setTimestamp(2, Timestamp.valueOf(pagamento.getDataPagamento()));
            stmt.setString(3, pagamento.getMetodoPagamento());
            stmt.setInt(4, pagamento.getConsulta().getId());
            stmt.setInt(5, pagamento.getFuncionario().getId());
            
            stmt.execute();
        }
    }

    public Pagamento buscarPorId(int id) throws SQLException {
        String sql = "SELECT p.id, p.valor_total, p.data_pagamento, p.metodo_pagamento, " +
                     "p.id_consulta, p.id_funcionario, " +
                     "c.id AS cons_id, c.data_consulta, c.diagnostico, " +
                     "f.id AS func_id, f.nome AS func_nome, f.cargo, f.login " +
                     "FROM pagamento p " +
                     "JOIN consulta c ON p.id_consulta = c.id " +
                     "JOIN funcionario f ON p.id_funcionario = f.id " +
                     "WHERE p.id = ?";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return criarPagamentoDoResultSet(rs);
            }
        }
        return null;
    }

    public List<Pagamento> listarTodos() throws SQLException {
        List<Pagamento> pagamentos = new ArrayList<>();
        String sql = "SELECT p.id, p.valor_total, p.data_pagamento, p.metodo_pagamento, " +
                     "p.id_consulta, p.id_funcionario, " +
                     "c.id AS cons_id, c.data_consulta, c.diagnostico, " +
                     "pac.id AS pac_id, pac.nome AS pac_nome, pac.especie, pac.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade, " +
                     "f.id AS func_id, f.nome AS func_nome, f.cargo, f.login " +
                     "FROM pagamento p " +
                     "JOIN consulta c ON p.id_consulta = c.id " +
                     "JOIN paciente pac ON c.id_paciente = pac.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "JOIN funcionario f ON p.id_funcionario = f.id " +
                     "ORDER BY p.data_pagamento DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                pagamentos.add(criarPagamentoCompletoDoResultSet(rs));
            }
        }
        return pagamentos;
    }

    public List<Pagamento> listarPorConsulta(int idConsulta) throws SQLException {
        List<Pagamento> pagamentos = new ArrayList<>();
        String sql = "SELECT p.id, p.valor_total, p.data_pagamento, p.metodo_pagamento, " +
                     "p.id_consulta, p.id_funcionario, " +
                     "c.id AS cons_id, c.data_consulta, c.diagnostico, " +
                     "f.id AS func_id, f.nome AS func_nome, f.cargo, f.login " +
                     "FROM pagamento p " +
                     "JOIN consulta c ON p.id_consulta = c.id " +
                     "JOIN funcionario f ON p.id_funcionario = f.id " +
                     "WHERE p.id_consulta = ? " +
                     "ORDER BY p.data_pagamento DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idConsulta);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                pagamentos.add(criarPagamentoDoResultSet(rs));
            }
        }
        return pagamentos;
    }

    public List<Consulta> listarConsultasPendentes() throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT DISTINCT c.id, c.data_consulta, c.diagnostico, " +
                     "p.id AS pac_id, p.nome AS pac_nome, p.especie, p.raca, " +
                     "v.id AS vet_id, v.nome AS vet_nome, v.crmv, v.telefone AS vet_tel, v.especialidade " +
                     "FROM consulta c " +
                     "JOIN paciente p ON c.id_paciente = p.id " +
                     "JOIN veterinario v ON c.id_veterinario = v.id " +
                     "LEFT JOIN pagamento pag ON c.id = pag.id_consulta " +
                     "WHERE pag.id IS NULL " +
                     "ORDER BY c.data_consulta DESC";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
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
                consultas.add(consulta);
            }
        }
        return consultas;
    }

    public void atualizarPagamento(Pagamento pagamento) throws SQLException {
        String sql = "CALL proc_atualizar_pagamento(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, pagamento.getId());
            stmt.setBigDecimal(2, java.math.BigDecimal.valueOf(pagamento.getValorTotal()));
            stmt.setTimestamp(3, Timestamp.valueOf(pagamento.getDataPagamento()));
            stmt.setString(4, pagamento.getMetodoPagamento());
            stmt.setInt(5, pagamento.getConsulta().getId());
            stmt.setInt(6, pagamento.getFuncionario().getId());
            
            stmt.execute();
        }
    }

    public void deletarPagamento(int id) throws SQLException {
        String sql = "CALL proc_deletar_pagamento(?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, id);
            stmt.execute();
        }
    }

    private Pagamento criarPagamentoDoResultSet(ResultSet rs) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setId(rs.getInt("cons_id"));
        
        Timestamp timestamp = rs.getTimestamp("data_consulta");
        if (timestamp != null) {
            consulta.setDataConsulta(timestamp.toLocalDateTime());
        }
        consulta.setDiagnostico(rs.getString("diagnostico"));
        
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getInt("func_id"));
        funcionario.setNome(rs.getString("func_nome"));
        funcionario.setCargo(rs.getString("cargo"));
        funcionario.setLogin(rs.getString("login"));
        
        Pagamento pagamento = new Pagamento(
            rs.getString("metodo_pagamento"),
            rs.getFloat("valor_total"),
            rs.getTimestamp("data_pagamento").toLocalDateTime(),
            funcionario,
            consulta
        );
        pagamento.setId(rs.getInt("id"));
        
        return pagamento;
    }

    private Pagamento criarPagamentoCompletoDoResultSet(ResultSet rs) throws SQLException {
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
        
        Funcionario funcionario = new Funcionario();
        funcionario.setId(rs.getInt("func_id"));
        funcionario.setNome(rs.getString("func_nome"));
        funcionario.setCargo(rs.getString("cargo"));
        funcionario.setLogin(rs.getString("login"));
        
        Pagamento pagamento = new Pagamento(
            rs.getString("metodo_pagamento"),
            rs.getFloat("valor_total"),
            rs.getTimestamp("data_pagamento").toLocalDateTime(),
            funcionario,
            consulta
        );
        pagamento.setId(rs.getInt("id"));
        
        return pagamento;
    }
}
