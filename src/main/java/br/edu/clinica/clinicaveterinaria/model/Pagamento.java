package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDateTime;

public class Pagamento {
    private int id;
    private LocalDateTime dataPagamento;
    private String metodoPagamento;
    private float valorTotal;
    private Funcionario funcionario;
    private Consulta consulta;

    public Pagamento(String metodoPagamento, float valorTotal, LocalDateTime dataPagamento, Funcionario funcionario, Consulta consulta) {
        this.metodoPagamento = metodoPagamento;
        this.dataPagamento = dataPagamento;
        this.valorTotal = valorTotal;
        this.funcionario = funcionario;
        this.consulta = consulta;
    }

    public int getId() {
        return id;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public float getValorTotal() {
        return valorTotal;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public void setValorTotal(float valorTotal) {
        this.valorTotal = valorTotal;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }

    public void setId(int id) {
        this.id = id;
    }
}
