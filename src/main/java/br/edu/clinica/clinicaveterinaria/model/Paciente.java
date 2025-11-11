package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDate;

public class Paciente {
    private int id;
    private String nome;
    private String raca;
    private String especie;
    private LocalDate dataNascimento;
    private Proprietario proprietario;

    public Paciente() {}

    public Paciente(String nome, String raca, String especie, LocalDate dataNascimento, Proprietario proprietario) {
        this.nome = nome;
        this.raca = raca;
        this.especie = especie;
        this.dataNascimento = dataNascimento;
        this.proprietario = proprietario;
    }

    //Construtor adicionado caso precise ler algo do banco
    public Paciente(int id, String nome, String raca, String especie, LocalDate dataNascimento, Proprietario proprietario) {
        this(nome, raca, especie, dataNascimento, proprietario);
        this.id = id;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

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

    public Proprietario getProprietario() {
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

