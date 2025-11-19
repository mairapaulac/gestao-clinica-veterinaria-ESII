
CREATE OR REPLACE PROCEDURE proc_inserir_consulta(
    p_data_consulta TIMESTAMP,
    p_diagnostico TEXT,
    p_id_paciente INTEGER,
    p_id_veterinario INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM paciente WHERE id = p_id_paciente AND ativo = TRUE) THEN
       RAISE EXCEPTION 'Paciente com ID (%) não encontrado ou inativo.', p_id_paciente;
END IF;


    IF NOT EXISTS (SELECT 1 FROM veterinario WHERE id = p_id_veterinario) THEN
       RAISE EXCEPTION 'Veterinário com ID (%) não encontrado.', p_id_veterinario;
END IF;

INSERT INTO consulta (
    data_consulta, diagnostico, id_paciente, id_veterinario
)
VALUES (
           p_data_consulta, p_diagnostico, p_id_paciente, p_id_veterinario
       );
END;
$$;


CREATE OR REPLACE FUNCTION funct_get_infos_consulta(p_id INT)
RETURNS consulta
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_consulta consulta%ROWTYPE;
BEGIN
SELECT * INTO var_consulta FROM consulta
WHERE id = p_id;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhuma consulta encontrada com o ID %.', p_id;
END IF;

RETURN var_consulta;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_consulta(
    p_id INT,
    p_data_consulta TIMESTAMP,
    p_diagnostico TEXT,
    p_id_paciente_novo INTEGER,
    p_id_veterinario_novo INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM consulta WHERE id = p_id) THEN
       RAISE EXCEPTION 'Consulta com ID % não encontrada.', p_id;
END IF;


    IF p_id_paciente_novo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM paciente WHERE id = p_id_paciente_novo) THEN
       RAISE EXCEPTION 'Novo Paciente com ID % não encontrado.', p_id_paciente_novo;
END IF;


    IF p_id_veterinario_novo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM veterinario WHERE id = p_id_veterinario_novo) THEN
       RAISE EXCEPTION 'Novo Veterinário com ID % não encontrado.', p_id_veterinario_novo;
END IF;

UPDATE consulta
SET
    data_consulta   = COALESCE(p_data_consulta, data_consulta),
    diagnostico     = COALESCE(p_diagnostico, diagnostico),
    id_paciente     = COALESCE(p_id_paciente_novo, id_paciente),
    id_veterinario  = COALESCE(p_id_veterinario_novo, id_veterinario)
WHERE
    id = p_id;

END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_consulta_soft(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM consulta WHERE id = p_id) THEN
        RAISE EXCEPTION 'Consulta com ID % não encontrada.', p_id;
END IF;


UPDATE consulta
SET ativo = FALSE
WHERE id = p_id;
END;
$$;