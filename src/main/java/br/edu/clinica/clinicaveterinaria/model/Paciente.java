package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDate;

public class Paciente {
    private int id;
    private String nome;
    private String raca;
    private String especie;
    private LocalDate dataNascimento;
    private Proprietario proprietario;

    public Paciente(String nome, String raca, String especie, LocalDate dataNascimento, Proprietario proprietario) {
        this.nome = nome;
        this.raca = raca;
        this.especie = especie;
        this.dataNascimento = dataNascimento;
        this.proprietario = proprietario;
    }

    public String getNome() {
        return nome;
    }

    public String getRaca() {
        return raca;
    }

    public String getEspecie() {
        return especie;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Proprietario getProprietario(Proprietario proprietario) {
        return proprietario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setProprietario(Proprietario proprietario) {
        this.proprietario = proprietario;
    }
}

