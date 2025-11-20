package br.edu.clinica.clinicaveterinaria.model;

public class Funcionario {
    private int id;
    private String nome;
    private boolean isGerente;
    private String cargo;
    private String login;
    private String senha;

    public Funcionario (){}
    public Funcionario(int id, String nome, boolean isGerente, String cargo, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.isGerente = isGerente;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
    }

    public void setId (int id){
        this.id = id;
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


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setisGerente() {
        this.isGerente = true;
    }

    public void setGerente(boolean isGerente) {
        this.isGerente = isGerente;
    }

    public boolean isGerente() {
        return isGerente;
    }

    public void setCargo (String cargo){
        this.cargo = cargo;
    }
    public String getCargo() {
        return cargo;
    }
}
