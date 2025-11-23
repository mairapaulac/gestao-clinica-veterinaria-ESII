package br.edu.clinica.clinicaveterinaria.util;

import java.sql.SQLException;

/**
 * Classe utilitária para tratar erros do banco de dados e converter
 * mensagens técnicas em mensagens amigáveis ao usuário.
 */
public class DatabaseErrorHandler {
    
    // Códigos de erro do PostgreSQL
    private static final String UNIQUE_VIOLATION = "23505";
    private static final String FOREIGN_KEY_VIOLATION = "23503";
    private static final String NOT_NULL_VIOLATION = "23502";
    private static final String CHECK_VIOLATION = "23514";
    
    /**
     * Converte um SQLException em uma mensagem amigável ao usuário.
     * 
     * @param e SQLException capturada
     * @param contexto Contexto da operação (ex: "cadastrar paciente", "salvar proprietário")
     * @return Mensagem amigável ao usuário
     */
    public static String getFriendlyMessage(SQLException e, String contexto) {
        if (e == null) {
            return "Ocorreu um erro desconhecido ao " + contexto + ".";
        }
        
        String sqlState = e.getSQLState();
        String errorMessage = e.getMessage();
        
        // Erro de violação de constraint única
        if (UNIQUE_VIOLATION.equals(sqlState)) {
            return getUniqueViolationMessage(errorMessage);
        }
        
        // Erro de violação de chave estrangeira
        if (FOREIGN_KEY_VIOLATION.equals(sqlState)) {
            return getForeignKeyViolationMessage(errorMessage);
        }
        
        // Erro de violação de NOT NULL
        if (NOT_NULL_VIOLATION.equals(sqlState)) {
            return getNotNullViolationMessage(errorMessage);
        }
        
        // Erro de violação de CHECK
        if (CHECK_VIOLATION.equals(sqlState)) {
            return "Os dados informados não atendem aos requisitos do sistema.";
        }
        
        // Erro de conexão
        if (errorMessage != null && (
            errorMessage.contains("Connection") || 
            errorMessage.contains("conexão") ||
            errorMessage.contains("timeout") ||
            errorMessage.contains("network"))) {
            return "Não foi possível conectar ao banco de dados. Verifique sua conexão e tente novamente.";
        }
        
        // Mensagem genérica
        return "Ocorreu um erro ao " + contexto + ". Por favor, verifique os dados e tente novamente.";
    }
    
    /**
     * Trata erros de violação de constraint única.
     */
    private static String getUniqueViolationMessage(String errorMessage) {
        if (errorMessage == null) {
            return "Este registro já existe no sistema.";
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        
        // Proprietário - Email duplicado
        if (lowerMessage.contains("proprietario_email_key") || 
            lowerMessage.contains("proprietario") && lowerMessage.contains("email")) {
            return "Este e-mail já está cadastrado para outro proprietário. Por favor, use um e-mail diferente.";
        }
        
        // Proprietário - CPF duplicado
        if (lowerMessage.contains("proprietario_pkey") || 
            lowerMessage.contains("proprietario") && lowerMessage.contains("cpf")) {
            return "Este CPF já está cadastrado no sistema.";
        }
        
        // Veterinário - CRMV duplicado
        if (lowerMessage.contains("veterinario_crmv_key") || 
            lowerMessage.contains("veterinario") && lowerMessage.contains("crmv")) {
            return "Este CRMV já está cadastrado no sistema. Por favor, verifique o número informado.";
        }
        
        // Veterinário - Email duplicado
        if (lowerMessage.contains("veterinario_email_key") || 
            (lowerMessage.contains("veterinario") && lowerMessage.contains("email"))) {
            return "Este e-mail já está cadastrado para outro veterinário. Por favor, use um e-mail diferente.";
        }
        
        // Funcionário - Login duplicado
        if (lowerMessage.contains("funcionario_login_key") || 
            (lowerMessage.contains("funcionario") && lowerMessage.contains("login"))) {
            return "Este login já está em uso por outro funcionário. Por favor, escolha outro login.";
        }
        
        // Estoque Medicamento - Lote duplicado
        if (lowerMessage.contains("estoque_medicamento") && lowerMessage.contains("lote")) {
            return "Este número de lote já existe para este medicamento no estoque.";
        }
        
        // Mensagem genérica para constraint única
        if (lowerMessage.contains("duplicate key") || lowerMessage.contains("unique constraint")) {
            return "Este registro já existe no sistema. Por favor, verifique os dados informados.";
        }
        
        return "Este registro já existe no sistema. Por favor, verifique os dados informados.";
    }
    
    /**
     * Trata erros de violação de chave estrangeira.
     */
    private static String getForeignKeyViolationMessage(String errorMessage) {
        if (errorMessage == null) {
            return "Não é possível realizar esta operação pois existem registros relacionados.";
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        
        // Paciente relacionado a proprietário
        if (lowerMessage.contains("paciente") && lowerMessage.contains("proprietario")) {
            return "Não é possível excluir este proprietário pois existem pacientes cadastrados para ele.";
        }
        
        // Consulta relacionada a paciente/veterinário
        if (lowerMessage.contains("consulta")) {
            if (lowerMessage.contains("paciente")) {
                return "Não é possível excluir este paciente pois existem consultas cadastradas para ele.";
            }
            if (lowerMessage.contains("veterinario")) {
                return "Não é possível excluir este veterinário pois existem consultas cadastradas para ele.";
            }
        }
        
        // Tratamento relacionado a consulta
        if (lowerMessage.contains("tratamento") && lowerMessage.contains("consulta")) {
            return "Não é possível excluir esta consulta pois existem tratamentos cadastrados para ela.";
        }
        
        // Pagamento relacionado
        if (lowerMessage.contains("pagamento")) {
            return "Não é possível excluir este registro pois existem pagamentos relacionados.";
        }
        
        // Mensagem genérica
        return "Não é possível realizar esta operação pois existem registros relacionados no sistema.";
    }
    
    /**
     * Trata erros de violação de NOT NULL.
     */
    private static String getNotNullViolationMessage(String errorMessage) {
        if (errorMessage == null) {
            return "Todos os campos obrigatórios devem ser preenchidos.";
        }
        
        String lowerMessage = errorMessage.toLowerCase();
        
        // Identifica qual campo está faltando
        if (lowerMessage.contains("nome")) {
            return "O campo Nome é obrigatório e deve ser preenchido.";
        }
        if (lowerMessage.contains("email")) {
            return "O campo E-mail é obrigatório e deve ser preenchido.";
        }
        if (lowerMessage.contains("cpf")) {
            return "O campo CPF é obrigatório e deve ser preenchido.";
        }
        if (lowerMessage.contains("telefone")) {
            return "O campo Telefone é obrigatório e deve ser preenchido.";
        }
        if (lowerMessage.contains("crmv")) {
            return "O campo CRMV é obrigatório e deve ser preenchido.";
        }
        if (lowerMessage.contains("login")) {
            return "O campo Login é obrigatório e deve ser preenchido.";
        }
        
        return "Todos os campos obrigatórios devem ser preenchidos.";
    }
    
    /**
     * Obtém o título da mensagem de erro baseado no contexto.
     */
    public static String getErrorTitle(String contexto) {
        if (contexto == null || contexto.isEmpty()) {
            return "Erro";
        }
        
        return "Erro ao " + contexto;
    }
}

