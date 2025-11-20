CREATE OR REPLACE PROCEDURE proc_inserir_proprietario(
    p_cpf VARCHAR,                 -- NOVO: CPF como chave primária
    p_nome VARCHAR,
    p_telefone VARCHAR,
    p_email VARCHAR,
    p_rua VARCHAR,
    p_numero VARCHAR,
    p_bairro VARCHAR,
    p_cidade VARCHAR,
    p_estado VARCHAR,
    p_cep VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
INSERT INTO proprietario (
    cpf, nome, telefone, email,
    rua, numero, bairro, cidade, estado, cep
)VALUES (
            p_cpf, p_nome, p_telefone, p_email,
            p_rua, p_numero, p_bairro, p_cidade, p_estado, p_cep
        );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_infos_proprietario(p_cpf VARCHAR)
RETURNS proprietario
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_prop proprietario%ROWTYPE;
BEGIN
SELECT * INTO var_prop FROM proprietario
WHERE cpf = p_cpf;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhum proprietário encontrado com o CPF %.', p_cpf;
END IF;

RETURN var_prop;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_proprietario (
    p_cpf_alvo VARCHAR,           -- NOVO: CPF para identificar o registro
    p_nome VARCHAR,
    p_telefone VARCHAR,
    p_email VARCHAR,              -- Email pode ser alterado
    p_rua VARCHAR,
    p_numero VARCHAR,
    p_bairro VARCHAR,
    p_cidade VARCHAR,
    p_estado VARCHAR,
    p_cep VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM proprietario WHERE cpf = p_cpf_alvo) THEN
       RAISE EXCEPTION 'Proprietário com CPF % não encontrado.', p_cpf_alvo;
END IF;

UPDATE proprietario
SET
    nome     = COALESCE(p_nome, nome),
    telefone = COALESCE(p_telefone, telefone),
    email    = COALESCE(p_email, email),
    rua      = COALESCE(p_rua, rua),
    numero   = COALESCE(p_numero, numero),
    bairro   = COALESCE(p_bairro, bairro),
    cidade   = COALESCE(p_cidade, cidade),
    estado   = COALESCE(p_estado, estado),
    cep      = COALESCE(p_cep, cep)
WHERE
    cpf = p_cpf_alvo;

END;
$$;



CREATE OR REPLACE PROCEDURE proc_deletar_proprietario_soft(
    p_cpf_alvo VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_old_email VARCHAR;
    v_new_email VARCHAR;
BEGIN
    -- Busca email e garante existência
SELECT email
INTO v_old_email
FROM proprietario
WHERE cpf = p_cpf_alvo;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Proprietário com CPF % não encontrado.', p_cpf_alvo;
END IF;

    -- Renomeia email para evitar conflitos (usando CPF em vez de ID)
    v_new_email := v_old_email || '_DESATIVADO_' || p_cpf_alvo || '_' || EXTRACT(EPOCH FROM NOW());

UPDATE proprietario
SET email = v_new_email
WHERE cpf = p_cpf_alvo;
END;
$$;