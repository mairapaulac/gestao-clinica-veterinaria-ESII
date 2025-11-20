-- Script de migração para adicionar colunas faltantes nas tabelas
-- Execute este script ANTES de executar insert_veterinarios_iniciais.sql
-- se suas tabelas já existem sem essas colunas

-- ============================================
-- TABELA VETERINARIO
-- ============================================

-- Adiciona coluna email se não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'veterinario' 
        AND column_name = 'email'
    ) THEN
        ALTER TABLE veterinario ADD COLUMN email VARCHAR UNIQUE;
    END IF;
END $$;

-- Adiciona coluna senha se não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'veterinario' 
        AND column_name = 'senha'
    ) THEN
        ALTER TABLE veterinario ADD COLUMN senha VARCHAR;
    END IF;
END $$;

-- ============================================
-- TABELA FUNCIONARIO
-- ============================================

-- Adiciona coluna e_gerente se não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'funcionario' 
        AND column_name = 'e_gerente'
    ) THEN
        ALTER TABLE funcionario ADD COLUMN e_gerente BOOLEAN DEFAULT FALSE;
    END IF;
END $$;

-- ============================================
-- OBSERVAÇÕES
-- ============================================
-- Atualiza as colunas para NOT NULL se necessário (após popular com dados)
-- Descomente as linhas abaixo APÓS executar insert_veterinarios_iniciais.sql
-- ALTER TABLE veterinario ALTER COLUMN email SET NOT NULL;
-- ALTER TABLE veterinario ALTER COLUMN senha SET NOT NULL;

