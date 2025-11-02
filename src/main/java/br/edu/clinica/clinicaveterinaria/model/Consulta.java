package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDate;

public class Consulta {
    private int id;
    private String diagnostico;
    private LocalDate dataNascimento;
    private Veterinario veterinario;
    private Paciente paciente;


    public Consulta(String diagnostico, LocalDate dataNascimento, Veterinario veterinario, Paciente paciente) {
        this.diagnostico = diagnostico;
        this.dataNascimento = dataNascimento;
        this.veterinario = veterinario;
        this.paciente = paciente;
    }

    public int getId() {
        return id;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setDiagnostico(String diagnostico){
        this.diagnostico = diagnostico;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }


}
