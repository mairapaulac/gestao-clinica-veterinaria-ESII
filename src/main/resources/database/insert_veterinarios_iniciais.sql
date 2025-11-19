-- Script para inserir veterinários iniciais no banco de dados
-- Execute este script após criar as tabelas

-- Inserir veterinários padrão (apenas se não existirem)
DO $$
BEGIN
    -- Verificar e inserir Dr. João Silva
    IF NOT EXISTS (SELECT 1 FROM veterinario WHERE crmv = 'CRMV-SP 12345') THEN
        INSERT INTO veterinario (nome, crmv, telefone, especialidade) 
        VALUES ('Dr. João Silva', 'CRMV-SP 12345', '(11) 99999-8888', 'Clínico Geral');
    END IF;
    
    -- Verificar e inserir Dra. Maria Souza
    IF NOT EXISTS (SELECT 1 FROM veterinario WHERE crmv = 'CRMV-SP 54321') THEN
        INSERT INTO veterinario (nome, crmv, telefone, especialidade) 
        VALUES ('Dra. Maria Souza', 'CRMV-SP 54321', '(11) 97777-6666', 'Cirurgiã');
    END IF;
END $$;

-- Verificar se foram inseridos
SELECT id, nome, crmv, telefone, especialidade FROM veterinario ORDER BY id;

