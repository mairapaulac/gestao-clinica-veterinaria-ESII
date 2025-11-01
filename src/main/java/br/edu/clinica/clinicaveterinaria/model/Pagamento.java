package br.edu.clinica.clinicaveterinaria.model;

import java.time.LocalDate;

public class Pagamento {
    private int id;
    private LocalDate dataPagamento;
    private String metodoPagamento;
    private float valorTotal;
    private Funcionario funcionario;
    private Consulta consulta;


    public Pagamento(String metodoPagamento, float valorTotal, LocalDate dataPagamento, Funcionario funcionario, Consulta consulta) {
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

    public LocalDate getDataPagamento() {
        return dataPagamento;
    }

    public float getValorTotal() {
        return valorTotal;
    }

    public Funcionario getFuncionario(Funcionario funcionario) {
        return funcionario;
    }

    public Consulta getConsulta(Consulta consulta) {
        return consulta;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public void setDataPagamento(LocalDate dataPagamento) {
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

}
