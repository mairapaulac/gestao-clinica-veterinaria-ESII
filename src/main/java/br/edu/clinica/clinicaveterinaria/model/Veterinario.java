package br.edu.clinica.clinicaveterinaria.model;

public class Veterinario {
    private int id;
    private String nome;
    private String CRMV;
    private String telefone;
    private String especialidade;
    private String email;
    private String senha;

    public Veterinario() {}

    public Veterinario(String nome, String CRMV, String telefone, String especialidade) {
        this.nome = nome;
        this.CRMV = CRMV;
        this.telefone = telefone;
        this.especialidade = especialidade;
    }

    public Veterinario(int id, String nome, String CRMV, String telefone, String especialidade) {
        this(nome, CRMV, telefone, especialidade);
        this.id = id;
    }

    public Veterinario(int id, String nome, String CRMV, String telefone, String especialidade, String email, String senha) {
        this(id, nome, CRMV, telefone, especialidade);
        this.email = email;
        this.senha = senha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}