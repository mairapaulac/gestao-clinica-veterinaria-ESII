package br.edu.clinica.clinicaveterinaria.model;

public class UsuarioSistema {
    private int id;
    private String nome;
    private String tipo; // "FUNCIONARIO", "VETERINARIO"
    private String cargo;
    private String login;
    private String email;
    private boolean isGerente;
    private String crmv;
    private String telefone;
    private String especialidade;
    
    // Para Funcionário
    private Funcionario funcionario;
    
    // Para Veterinário
    private Veterinario veterinario;

    public UsuarioSistema(Funcionario funcionario) {
        this.funcionario = funcionario;
        this.id = funcionario.getId();
        this.nome = funcionario.getNome();
        this.tipo = "FUNCIONARIO";
        this.cargo = funcionario.getCargo();
        this.login = funcionario.getLogin();
        this.isGerente = funcionario.isGerente();
    }

    public UsuarioSistema(Veterinario veterinario) {
        this.veterinario = veterinario;
        this.id = veterinario.getId();
        this.nome = veterinario.getNome();
        this.tipo = "VETERINARIO";
        this.cargo = "Veterinário";
        this.login = veterinario.getEmail();
        this.email = veterinario.getEmail();
        this.crmv = veterinario.getCRMV();
        this.telefone = veterinario.getTelefone();
        this.especialidade = veterinario.getEspecialidade();
        this.isGerente = false;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getCargo() {
        return cargo;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public boolean isGerente() {
        return isGerente;
    }

    public String getCrmv() {
        return crmv;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    public void setNome(String nome) {
        this.nome = nome;
        if (funcionario != null) {
            funcionario.setNome(nome);
        }
        if (veterinario != null) {
            veterinario.setNome(nome);
        }
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
        if (funcionario != null) {
            funcionario.setCargo(cargo);
        }
    }

    public void setLogin(String login) {
        this.login = login;
        if (funcionario != null) {
            funcionario.setLogin(login);
        }
    }

    public void setEmail(String email) {
        this.email = email;
        if (veterinario != null) {
            veterinario.setEmail(email);
        }
    }

    public void setGerente(boolean gerente) {
        isGerente = gerente;
        if (funcionario != null) {
            funcionario.setGerente(gerente);
        }
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
        if (veterinario != null) {
            veterinario.setCRMV(crmv);
        }
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
        if (veterinario != null) {
            veterinario.setTelefone(telefone);
        }
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
        if (veterinario != null) {
            veterinario.setEspecialidade(especialidade);
        }
    }
}




