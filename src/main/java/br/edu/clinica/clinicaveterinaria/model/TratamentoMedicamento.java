package br.edu.clinica.clinicaveterinaria.model;

public class TratamentoMedicamento {
    private Medicamento medicamento;
    private Tratamento tratamento;
    private int quantidadeUtilizada;

    public TratamentoMedicamento(Medicamento medicamento, Tratamento tratamento, int quantidadeUtilizada) {
        this.medicamento = medicamento;
        this.tratamento = tratamento;
        this.quantidadeUtilizada = quantidadeUtilizada;
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }

    public Tratamento getTratamento() {
        return tratamento;
    }

    public void setTratamento(Tratamento tratamento) {
        this.tratamento = tratamento;
    }

    public int getQuantidadeUtilizada() {
        return quantidadeUtilizada;
    }

    public void setQuantidadeUtilizada(int quantidadeUtilizada) {
        this.quantidadeUtilizada = quantidadeUtilizada;
    }
}
