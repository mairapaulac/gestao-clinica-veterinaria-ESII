CREATE OR REPLACE PROCEDURE proc_inserir_paciente(
    p_nome VARCHAR,
    p_especie VARCHAR,
    p_raca VARCHAR,
    p_data_nascimento DATE,
    p_id_proprietario VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM proprietario WHERE cpf = p_id_proprietario) THEN
       RAISE EXCEPTION 'Proprietário com CPF (%) não encontrado. Não é possível inserir o paciente.', p_id_proprietario;
END IF;

INSERT INTO paciente (
    nome, especie, raca, data_nascimento, id_proprietario
)
VALUES (
           p_nome, p_especie, p_raca, p_data_nascimento, p_id_proprietario
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_infos_paciente(p_id INT)
RETURNS paciente
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_paciente paciente%ROWTYPE;
BEGIN
SELECT * INTO var_paciente FROM paciente
WHERE id = p_id;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhum paciente encontrado com o ID %.', p_id;
END IF;

RETURN var_paciente;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_paciente(
    p_id INT,
    p_nome VARCHAR,
    p_especie VARCHAR,
    p_raca VARCHAR,
    p_data_nascimento DATE,
    p_id_proprietario_novo VARCHAR -- Opcional, para troca de proprietário
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o paciente existe
    IF NOT EXISTS (SELECT 1 FROM paciente WHERE id = p_id) THEN
       RAISE EXCEPTION 'Paciente com ID % não encontrado.', p_id;
END IF;

    -- Se um novo ID de proprietário foi fornecido, verifica se ele existe
    IF p_id_proprietario_novo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM proprietario WHERE cpf = p_id_proprietario_novo) THEN
       RAISE EXCEPTION 'Novo Proprietário com CPF % não encontrado.', p_id_proprietario_novo;
END IF;

UPDATE paciente
SET
    nome              = COALESCE(p_nome, nome),
    especie           = COALESCE(p_especie, especie),
    raca              = COALESCE(p_raca, raca),
    data_nascimento   = COALESCE(p_data_nascimento, data_nascimento),
    id_proprietario   = COALESCE(p_id_proprietario_novo, id_proprietario)
WHERE
    id = p_id;

END;
$$;



CREATE OR REPLACE PROCEDURE proc_deletar_paciente_soft(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o paciente existe
    IF NOT EXISTS (SELECT 1 FROM paciente WHERE id = p_id) THEN
        RAISE EXCEPTION 'Paciente com ID % não encontrado.', p_id;
END IF;


UPDATE paciente
SET ativo = FALSE
WHERE id = p_id;
END;
$$;