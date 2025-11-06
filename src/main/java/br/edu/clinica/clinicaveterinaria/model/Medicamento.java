package br.edu.clinica.clinicaveterinaria.model;

public class Medicamento {
    private int id;
    private String nomeMedicamento;

    public Medicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }
    
    public String getNomeMedicamento() {
        return nomeMedicamento;
    }

    public void setNomeMedicamento(String nomeMedicamento) {
        this.nomeMedicamento = nomeMedicamento;
    }
}
