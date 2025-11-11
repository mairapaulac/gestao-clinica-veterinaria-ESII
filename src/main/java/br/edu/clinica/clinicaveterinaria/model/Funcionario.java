package br.edu.clinica.clinicaveterinaria.model;

public class Funcionario {
    private int id;
    private String nome;
    private String cargo;
    private String login;
    private String senha;

    public Funcionario() {}

    public Funcionario(String nome, String cargo, String login, String senha) {
        this.nome = nome;
        this.cargo = cargo;
        this.login = login;
        this.senha = senha;
    }

    public Funcionario(int id, String nome, String cargo, String login, String senha) {
        this(nome, cargo, login, senha);
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

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
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
}
