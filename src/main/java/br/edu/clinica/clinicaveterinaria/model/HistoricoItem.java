package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDateTime;

public class HistoricoItem {
    private int id;
    private LocalDateTime data;
    private String tipo; // "CONSULTA" ou "TRATAMENTO"
    private String descricao;
    private String diagnostico;
    private Veterinario veterinario;
    private Consulta consulta;
    private Tratamento tratamento;

    public HistoricoItem(Consulta consulta) {
        this.id = consulta.getId();
        this.data = consulta.getDataConsulta();
        this.tipo = "CONSULTA";
        this.descricao = consulta.getDiagnostico() != null ? consulta.getDiagnostico() : "Consulta sem diagnóstico";
        this.diagnostico = consulta.getDiagnostico();
        this.veterinario = consulta.getVeterinario();
        this.consulta = consulta;
    }

    public HistoricoItem(Tratamento tratamento) {
        this.tratamento = tratamento;
        this.consulta = tratamento.getConsulta();
        this.id = tratamento.getId();
        this.data = tratamento.getConsulta().getDataConsulta();
        this.tipo = "TRATAMENTO";
        this.descricao = tratamento.getDescricao();
        this.diagnostico = tratamento.getConsulta().getDiagnostico();
        this.veterinario = tratamento.getConsulta().getVeterinario();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public Tratamento getTratamento() {
        return tratamento;
    }
}

