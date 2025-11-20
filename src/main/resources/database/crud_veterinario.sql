CREATE OR REPLACE PROCEDURE proc_inserir_veterinario(
    v_nome VARCHAR,
    v_crmv VARCHAR,
    v_telefone VARCHAR,
    v_especialidade VARCHAR,
    v_email VARCHAR,               -- NOVO
    v_senha VARCHAR                -- NOVO
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
INSERT INTO veterinario (
    nome, crmv, telefone, especialidade, email, senha
)
VALUES (
           v_nome, v_crmv, v_telefone, v_especialidade, v_email, v_senha
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_infos_veterinario (v_crmv VARCHAR)
RETURNS veterinario
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_vet veterinario%ROWTYPE;
BEGIN
SELECT * INTO var_vet FROM veterinario
WHERE crmv = v_crmv;
IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhum veterinário encontrado com o crmv %.', v_crmv;
END IF;
RETURN var_vet;
END;
$$;

CREATE OR REPLACE FUNCTION funct_validar_login_veterinario(
    p_email_fornecido VARCHAR, -- O login (email) fornecido pelo usuário
    p_senha_fornecida VARCHAR  -- A senha fornecida pelo usuário
)
RETURNS BOOLEAN
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_senha_armazenada VARCHAR;
BEGIN
    -- 1. Tenta buscar a senha armazenada (em texto puro, para seu caso acadêmico)
SELECT senha INTO v_senha_armazenada
FROM veterinario
WHERE email = p_email_fornecido;

-- 2. Verifica se o email (login) foi encontrado
IF NOT FOUND THEN
        RETURN FALSE; -- Veterinário não encontrado
END IF;

    -- 3. Compara a senha fornecida com a senha armazenada
    IF v_senha_armazenada = p_senha_fornecida THEN
        RETURN TRUE; -- Login bem-sucedido
ELSE
        RETURN FALSE; -- Senha incorreta
END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_veterinario(
    p_current_crmv VARCHAR,
    v_nome VARCHAR,
    v_crmv VARCHAR,
    v_telefone VARCHAR,
    v_especialidade VARCHAR,
    v_email VARCHAR,               -- NOVO
    v_senha VARCHAR                -- NOVO
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM veterinario WHERE crmv = p_current_crmv) THEN
       RAISE EXCEPTION 'Veterinário com crmv % não encontrado.', p_current_crmv;
END IF;

UPDATE veterinario
SET
    nome          = COALESCE(v_nome, nome),
    crmv          = COALESCE(v_crmv, crmv),
    telefone      = COALESCE(v_telefone, telefone),
    especialidade = COALESCE(v_especialidade, especialidade),
    email         = COALESCE(v_email, email),           -- NOVO
    senha         = COALESCE(v_senha, senha)           -- NOVO
WHERE
    crmv = p_current_crmv;

END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_veterinario(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_old_crmv VARCHAR; -- Alterado de VARCHAR(20) para VARCHAR
    v_new_crmv VARCHAR; -- Alterado de VARCHAR(20) para VARCHAR
BEGIN
    -- Busca crmv e garante existência
SELECT crmv
INTO v_old_crmv
FROM veterinario
WHERE id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Veterinario com ID % não encontrado.', p_id;
END IF;

    -- Renomeia CRMV para evitar conflitos (Soft Delete)
    v_new_crmv := v_old_crmv || '_DESATIVADO_' || p_id || '_' || EXTRACT(EPOCH FROM NOW());

UPDATE veterinario
SET crmv = v_new_crmv
WHERE id = p_id;
END;
$$;
