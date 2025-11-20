package br.edu.clinica.clinicaveterinaria.view;

import br.edu.clinica.clinicaveterinaria.model.Funcionario;
import br.edu.clinica.clinicaveterinaria.model.Veterinario;

public class SessionManager {
    private static Funcionario funcionarioLogado;
    private static Veterinario veterinarioLogado;
    private static TipoUsuario tipoUsuario;

    public enum TipoUsuario {
        FUNCIONARIO,
        ADMINISTRADOR,
        VETERINARIO
    }

    public static void setFuncionarioLogado(Funcionario funcionario) {
        SessionManager.funcionarioLogado = funcionario;
        SessionManager.veterinarioLogado = null;
        if (funcionario != null && funcionario.isGerente()) {
            SessionManager.tipoUsuario = TipoUsuario.ADMINISTRADOR;
        } else {
            SessionManager.tipoUsuario = TipoUsuario.FUNCIONARIO;
        }
    }

    public static void setVeterinarioLogado(Veterinario veterinario) {
        SessionManager.veterinarioLogado = veterinario;
        SessionManager.funcionarioLogado = null;
        SessionManager.tipoUsuario = TipoUsuario.VETERINARIO;
    }

    public static Funcionario getFuncionarioLogado() {
        return funcionarioLogado;
    }

    public static Veterinario getVeterinarioLogado() {
        return veterinarioLogado;
    }

    public static TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public static String getNomeUsuario() {
        if (funcionarioLogado != null) {
            return funcionarioLogado.getNome();
        } else if (veterinarioLogado != null) {
            return veterinarioLogado.getNome();
        }
        return "Usuário";
    }

    public static String getTipoUsuarioString() {
        if (tipoUsuario == null) {
            return "Usuário";
        }
        switch (tipoUsuario) {
            case ADMINISTRADOR:
                return "Administrador";
            case FUNCIONARIO:
                return "Funcionário";
            case VETERINARIO:
                return "Veterinário";
            default:
                return "Usuário";
        }
    }

    public static void logout() {
        funcionarioLogado = null;
        veterinarioLogado = null;
        tipoUsuario = null;
    }

    public static boolean isAdministrador() {
        return tipoUsuario == TipoUsuario.ADMINISTRADOR;
    }

    public static boolean isVeterinario() {
        return tipoUsuario == TipoUsuario.VETERINARIO;
    }

    public static boolean isFuncionario() {
        return tipoUsuario == TipoUsuario.FUNCIONARIO || tipoUsuario == TipoUsuario.ADMINISTRADOR;
    }
}

