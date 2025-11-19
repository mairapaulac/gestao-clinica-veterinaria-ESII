CREATE OR REPLACE PROCEDURE proc_inserir_funcionario(
    f_nome VARCHAR,
    f_cargo VARCHAR,
    f_login VARCHAR,
    f_senha VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
INSERT INTO funcionario (
    nome, cargo, login, senha
)
VALUES (f_nome, f_cargo, f_login, f_senha);
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

CREATE OR REPLACE FUNCTION funct_get_infos_funcionario(f_login VARCHAR)
RETURNS TABLE (
    id INTEGER,
    nome VARCHAR,
    cargo VARCHAR,
    login VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
RETURN QUERY
SELECT
    id,
    nome,
    cargo,
    login
FROM funcionario
WHERE login = f_login;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhum funcionário encontrado com o login %.', f_login;
END IF;

END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_funcionario(
    f_curr_login VARCHAR,
    f_nome VARCHAR,
    f_cargo VARCHAR,
    f_login VARCHAR,
    f_senha VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM funcionario WHERE login = f_curr_login) THEN
       RAISE EXCEPTION 'Funcionário com login % não encontrado.', f_curr_login;
END IF;

UPDATE funcionario
SET
    nome     = COALESCE(f_nome, nome),
    cargo    = COALESCE(f_cargo, cargo),
    login    = COALESCE(f_login, login),
    senha    = COALESCE(f_senha, senha)
WHERE
    login = f_curr_login;

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