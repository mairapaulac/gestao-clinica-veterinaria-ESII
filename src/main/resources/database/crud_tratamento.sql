CREATE OR REPLACE PROCEDURE proc_inserir_tratamento(
    p_id_consulta INTEGER,
    p_descricao VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se a consulta referenciada existe
    IF NOT EXISTS (SELECT 1 FROM consulta WHERE id = p_id_consulta) THEN
       RAISE EXCEPTION 'Consulta com ID % não encontrada. Não é possível inserir o tratamento.', p_id_consulta;
END IF;

INSERT INTO tratamento (
    descricao, id_consulta
)
VALUES (
           p_descricao, p_id_consulta
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_infos_tratamento(p_id INT)
RETURNS tratamento
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_tratamento tratamento%ROWTYPE;
BEGIN
SELECT * INTO var_tratamento FROM tratamento
WHERE id = p_id;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhum tratamento encontrado com o ID %.', p_id;
END IF;

RETURN var_tratamento;
END;
$$;


CREATE OR REPLACE PROCEDURE proc_atualizar_tratamento(
    p_id INT,
    p_descricao VARCHAR,
    p_id_consulta_novo INTEGER -- Opcional, para correção
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM tratamento WHERE id = p_id) THEN
       RAISE EXCEPTION 'Tratamento com ID % não encontrado.', p_id;
END IF;


    IF p_id_consulta_novo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM consulta WHERE id = p_id_consulta_novo) THEN
       RAISE EXCEPTION 'Nova Consulta com ID % não encontrada.', p_id_consulta_novo;
END IF;

UPDATE tratamento
SET
    descricao   = COALESCE(p_descricao, descricao),
    id_consulta = COALESCE(p_id_consulta_novo, id_consulta)
WHERE
    id = p_id;

END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_tratamento(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN

    IF NOT EXISTS (SELECT 1 FROM tratamento WHERE id = p_id) THEN
        RAISE EXCEPTION 'Tratamento com ID % não encontrado.', p_id;
END IF;


DELETE FROM tratamento_medicamento
WHERE id_tratamento = p_id;


DELETE FROM tratamento
WHERE id = p_id;
END;
$$;