package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDateTime;

public class Consulta {
    private int id;
    private String diagnostico;
    private LocalDateTime dataConsulta;
    private Veterinario veterinario;
    private Paciente paciente;

    public Consulta() {}

    public Consulta(String diagnostico, LocalDateTime dataConsulta, Veterinario veterinario, Paciente paciente) {
        this.diagnostico = diagnostico;
        this.dataConsulta = dataConsulta;
        this.veterinario = veterinario;
        this.paciente = paciente;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public LocalDateTime getDataConsulta() {
        return dataConsulta;
    }

    public void setDataConsulta(LocalDateTime dataConsulta) {
        this.dataConsulta = dataConsulta;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
