package br.edu.clinica.clinicaveterinaria.model;

public class Pagamento {
    private int id;
    private String metodoPagamento;
    private float valorTotal;
    private Funcionario funcionario;
    private Consulta consulta;


    public Pagamento(String metodoPagamento, float valorTotal, Funcionario funcionario, Consulta consulta) {
        this.metodoPagamento = metodoPagamento;
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

    public void setValorTotal(float valorTotal) {
        this.valorTotal = valorTotal;
    }

}
