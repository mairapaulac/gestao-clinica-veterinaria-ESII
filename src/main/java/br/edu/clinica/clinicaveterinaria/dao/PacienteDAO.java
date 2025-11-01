package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Paciente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PacienteDAO {

    public void Create(Paciente paciente) {

        String sql = "INSERT INTO usuario (nome, especie, raca, dataNascimento) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

}
