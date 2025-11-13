package br.edu.clinica.clinicaveterinaria.dao;

import br.edu.clinica.clinicaveterinaria.model.Veterinario;
import java.sql.SQLException;
import java.util.List;

public class TesteInsercaoVeterinario {

    public static void main(String[] args) {
        VeterinarioDAO dao = new VeterinarioDAO();
        
        try {
            // ==========================================================
            // 0. LIMPEZA (Para garantir que os testes comecem do zero)
            // ==========================================================
            // TRUNCATE reseta a tabela e a sequência do ID (SERIAL)
            // Use isso apenas em ambiente de teste!
            
            
            // ==========================================================
            // 1. CREATE (Adicionar um novo Veterinário)
            // ==========================================================
            System.out.println("--- 1. TESTE DE INSERÇÃO ---");
            
            // Note: O construtor sem ID é usado aqui, pois o banco gera o ID.
            Veterinario novoVet = new Veterinario(
                "Dr. Pedro Alvares", 
                "CRMV/RJ 55555", 
                "(21) 97777-7777", 
                "Cirurgia Geral"
            );
            dao.adicionarVeterinario(novoVet);
            
            // ==========================================================
            // 2. READ (Listar e confirmar a inserção)
            // ==========================================================
            System.out.println("\n--- 2. TESTE DE LEITURA (Lista Completa) ---");
            
            List<Veterinario> lista = dao.listarTodos();
            System.out.println("Veterinários encontrados (" + lista.size() + "):");
            
            // O primeiro veterinário na lista será o que acabamos de inserir (ID 1)
            Veterinario vetParaAtualizar = lista.get(0); 
            
            System.out.println("  ID: " + vetParaAtualizar.getId() + " | Nome: " + vetParaAtualizar.getNome());

            // ==========================================================
            // 3. UPDATE (Atualizar o veterinário recém-inserido)
            // ==========================================================
            System.out.println("\n--- 3. TESTE DE ATUALIZAÇÃO ---");
            
            // Alterar o telefone e a especialidade do objeto
            vetParaAtualizar.setTelefone("(21) 99999-0000 (NOVO)");
            vetParaAtualizar.setEspecialidade("Cirurgia Ortopédica");
            
            dao.atualizarVeterinario(vetParaAtualizar);
            
            // Verificação Rápida: Listar novamente
            List<Veterinario> listaAtualizada = dao.listarTodos();
            System.out.println("Verificação do ID " + vetParaAtualizar.getId() + " após UPDATE:");
            System.out.println("  Especialidade: " + listaAtualizada.get(0).getEspecialidade());
            
            // ==========================================================
            // 4. DELETE (Deletar o veterinário)
            // ==========================================================
            System.out.println("\n--- 4. TESTE DE DELEÇÃO ---");
            int idParaDeletar = vetParaAtualizar.getId();
            
            dao.deletarVeterinario(idParaDeletar);
            
            // Verificação final
            List<Veterinario> listaFinal = dao.listarTodos();
            System.out.println("\n--- FIM DOS TESTES ---");
            System.out.println("Registros restantes após DELETE: " + listaFinal.size());
            
            if (listaFinal.isEmpty()) {
                System.out.println("✅ CRUD COMPLETO E FUNCIONANDO COM SUCESSO!");
            }

        } catch (SQLException e) {
            System.err.println("!!! ERRO CRÍTICO DURANTE O TESTE DE CRUD !!!");
            System.err.println("Causa: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
}