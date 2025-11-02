package br.edu.clinica.clinicaveterinaria.model;

public class Veterinario {
    private int id;
    private String nome;
    private String CRMV;
    private String telefone;
    private String especialidade;

    public Veterinario(String nome, String CRMV, String telefone, String especialidade) {
        this.nome = nome;
        this.CRMV = CRMV;
        this.telefone = telefone;
        this.especialidade = especialidade;
    }

    public int getId() {
        return id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCRMV() {
        return CRMV;
    }

    public void setCRMV(String CRMV) {
        this.CRMV = CRMV;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
