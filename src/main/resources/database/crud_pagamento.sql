CREATE OR REPLACE PROCEDURE proc_inserir_pagamento(
    p_valor_total DECIMAL(10,2),
    p_data_pagamento TIMESTAMP,
    p_metodo_pagamento VARCHAR,
    p_id_consulta INTEGER,
    p_id_funcionario INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- 1. Verifica se a consulta referenciada existe
    IF NOT EXISTS (SELECT 1 FROM consulta WHERE id = p_id_consulta) THEN
       RAISE EXCEPTION 'Consulta com ID (%) não encontrada.', p_id_consulta;
END IF;

    -- 2. Verifica se o funcionário referenciado existe
    IF NOT EXISTS (SELECT 1 FROM funcionario WHERE id = p_id_funcionario) THEN
       RAISE EXCEPTION 'Funcionário com ID (%) não encontrado.', p_id_funcionario;
END IF;

INSERT INTO pagamento (
    valor_total, data_pagamento, metodo_pagamento, id_consulta, id_funcionario
)
VALUES (
           p_valor_total, p_data_pagamento, p_metodo_pagamento, p_id_consulta, p_id_funcionario
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_infos_pagamento(p_id INT)
RETURNS pagamento
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_pagamento pagamento%ROWTYPE;
BEGIN
SELECT * INTO var_pagamento FROM pagamento
WHERE id = p_id;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Nenhum registro de pagamento encontrado com o ID %.', p_id;
END IF;

RETURN var_pagamento;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_pagamento(
    p_id INT,
    p_valor_total DECIMAL(10,2),
    p_data_pagamento TIMESTAMP,
    p_metodo_pagamento VARCHAR,
    p_id_consulta_novo INTEGER,
    p_id_funcionario_novo INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o pagamento existe
    IF NOT EXISTS (SELECT 1 FROM pagamento WHERE id = p_id) THEN
       RAISE EXCEPTION 'Pagamento com ID % não encontrado.', p_id;
END IF;

    -- Se houver novo ID de consulta, verifica sua existência
    IF p_id_consulta_novo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM consulta WHERE id = p_id_consulta_novo) THEN
       RAISE EXCEPTION 'Nova Consulta com ID % não encontrada.', p_id_consulta_novo;
END IF;

    -- Se houver novo ID de funcionário, verifica sua existência
    IF p_id_funcionario_novo IS NOT NULL AND NOT EXISTS (SELECT 1 FROM funcionario WHERE id = p_id_funcionario_novo) THEN
       RAISE EXCEPTION 'Novo Funcionário com ID % não encontrado.', p_id_funcionario_novo;
END IF;

UPDATE pagamento
SET
    valor_total      = COALESCE(p_valor_total, valor_total),
    data_pagamento   = COALESCE(p_data_pagamento, data_pagamento),
    metodo_pagamento = COALESCE(p_metodo_pagamento, metodo_pagamento),
    id_consulta      = COALESCE(p_id_consulta_novo, id_consulta),
    id_funcionario   = COALESCE(p_id_funcionario_novo, id_funcionario)
WHERE
    id = p_id;

END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_pagamento(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o pagamento existe
    IF NOT EXISTS (SELECT 1 FROM pagamento WHERE id = p_id) THEN
        RAISE EXCEPTION 'Pagamento com ID % não encontrado.', p_id;
END IF;

    -- Exclui o registro (Hard Delete)
DELETE FROM pagamento
WHERE id = p_id;
END;
$$;