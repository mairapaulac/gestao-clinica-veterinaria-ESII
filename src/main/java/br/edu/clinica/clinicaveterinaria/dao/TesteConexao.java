package br.edu.clinica.clinicaveterinaria.dao; // Ajuste o pacote se necessário


import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {
        Connection conn = null;        
        try {
            System.out.println("Tentando conectar ao banco de dados...");

            // Chama o método que você criou para obter a conexão
            conn = ConnectionFactory.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("--- SUCESSO! Conexão estabelecida e aberta. ---");
                System.out.println("Banco de Dados: " + conn.getCatalog());
            } else {
                System.out.println("--- ATENÇÃO: Conexão retornou nula ou fechada. ---");
            }

        } catch (SQLException e) {
            System.err.println("!!! ERRO! Falha na conexão com o PostgreSQL. !!!");
            System.err.println("Verifique:");
            System.err.println("  1. Se o servidor PostgreSQL está rodando.");
            System.err.println("  2. A URL, Usuário e Senha no config.properties.");
            System.err.println("Detalhes do erro: " + e.getMessage());
            // Removendo e.printStackTrace() para um output mais limpo no console:
            // e.printStackTrace(); 

        } catch (RuntimeException e) {
            System.err.println("!!! ERRO! Falha ao carregar o arquivo config.properties. !!!");
            System.err.println("Detalhes: " + e.getMessage());
        } 
        
        finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Conexão fechada.");
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar a conexão.");
                }
            }
        }
    }
}