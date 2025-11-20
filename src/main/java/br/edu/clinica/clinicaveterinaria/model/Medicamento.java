package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDate;
import java.util.Objects;

public class Medicamento {
    private int id;
    private String nome;
    private String fabricante;
    private String principioAtivo;
    private String numeroLote;
    private int quantidade;
    private LocalDate dataValidade;
    private LocalDate dataEntrada;

    public Medicamento(int id, String nome, String fabricante, String principioAtivo, String numeroLote, int quantidade, LocalDate dataValidade, LocalDate dataEntrada) {
        this.id = id;
        this.nome = nome;
        this.fabricante = fabricante;
        this.principioAtivo = principioAtivo;
        this.numeroLote = numeroLote;
        this.quantidade = quantidade;
        this.dataValidade = dataValidade;
        this.dataEntrada = dataEntrada;
    }

    public Medicamento(String nome, String fabricante, String principioAtivo, String numeroLote, int quantidade, LocalDate dataValidade, LocalDate dataEntrada) {
        this(0, nome, fabricante, principioAtivo, numeroLote, quantidade, dataValidade, dataEntrada);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getPrincipioAtivo() { return principioAtivo; }
    public void setPrincipioAtivo(String principioAtivo) { this.principioAtivo = principioAtivo; }
    public String getNumeroLote() { return numeroLote; }
    public void setNumeroLote(String numeroLote) { this.numeroLote = numeroLote; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public LocalDate getDataValidade() { return dataValidade; }
    public void setDataValidade(LocalDate dataValidade) { this.dataValidade = dataValidade; }
    public LocalDate getDataEntrada() { return dataEntrada; }
    public void setDataEntrada(LocalDate dataEntrada) { this.dataEntrada = dataEntrada; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicamento that = (Medicamento) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Medicamento(int id, String nome, String fabricante, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.fabricante = fabricante;
        this.quantidade = quantidade;
    }
}
