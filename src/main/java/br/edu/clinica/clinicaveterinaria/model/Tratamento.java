package br.edu.clinica.clinicaveterinaria.model;

public class Tratamento {
    private int id;
    private String descricao;
    private Consulta consulta;

    public Tratamento(String descricao, Consulta consulta) {
        this.descricao = descricao;
        this.consulta = consulta;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public void setId(int id) {
        this.id = id;
    }
}
