CREATE OR REPLACE PROCEDURE proc_inserir_funcionario(
    v_nome VARCHAR,
    v_cargo VARCHAR,
    v_login VARCHAR,
    v_senha VARCHAR,
    v_e_gerente BOOLEAN DEFAULT FALSE -- NOVO
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
INSERT INTO funcionario (
    nome, cargo, login, senha, e_gerente
)
VALUES (
           v_nome, v_cargo, v_login, v_senha, v_e_gerente
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_validar_login(
    p_login VARCHAR,
    p_senha_fornecida VARCHAR
)
RETURNS BOOLEAN
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_senha_armazenada VARCHAR;
BEGIN

SELECT senha INTO v_senha_armazenada
FROM funcionario
WHERE login = p_login;


IF NOT FOUND THEN
        RETURN FALSE;
END IF;


    IF v_senha_armazenada = p_senha_fornecida THEN
        RETURN TRUE; -- Senha correta
ELSE
        RETURN FALSE; -- Senha incorreta
END IF;
END;
$$;

-- READ: Busca funcionário por Login (incluindo o status de gerente)
CREATE OR REPLACE FUNCTION funct_get_infos_funcionario(
    p_login VARCHAR
)
RETURNS SETOF funcionario
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
RETURN QUERY
SELECT
    id,
    nome,
    cargo,
    login,
    -- Senha é omitida por segurança, mesmo em nível acadêmico (é melhor)
    e_gerente
FROM funcionario
WHERE login = p_login;

IF NOT FOUND THEN
       RAISE EXCEPTION 'Funcionário com login % não encontrado.', p_login;
END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_funcionario(
    p_current_login VARCHAR, -- Login atual para encontrar o registro
    v_nome VARCHAR DEFAULT NULL,
    v_cargo VARCHAR DEFAULT NULL,
    v_login VARCHAR DEFAULT NULL,
    v_senha VARCHAR DEFAULT NULL,
    v_e_gerente BOOLEAN DEFAULT NULL -- NOVO
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM funcionario WHERE login = p_current_login) THEN
       RAISE EXCEPTION 'Funcionário com login % não encontrado.', p_current_login;
END IF;

UPDATE funcionario
SET
    nome          = COALESCE(v_nome, nome),
    cargo         = COALESCE(v_cargo, cargo),
    login         = COALESCE(v_login, login),
    senha         = COALESCE(v_senha, senha),
    e_gerente     = COALESCE(v_e_gerente, e_gerente) -- NOVO
WHERE
    login = p_current_login;

END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_funcionario (
    f_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_old_login VARCHAR; -- Alterado de VARCHAR(50) para VARCHAR
    v_new_login VARCHAR; -- Alterado de VARCHAR(50) para VARCHAR
BEGIN
    -- Busca login e garante existência
SELECT login
INTO v_old_login
FROM funcionario
WHERE id = f_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Funcionário com ID % não encontrado.', f_id;
END IF;

    -- Renomeia login para evitar conflitos (Soft Delete)
    v_new_login := v_old_login || '_DESATIVADO_' || f_id || '_' || EXTRACT(EPOCH FROM NOW());

UPDATE funcionario
SET login = v_new_login
WHERE id = f_id;
END;
$$;