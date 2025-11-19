CREATE OR REPLACE PROCEDURE proc_inserir_medicamento_catalogo(
    m_nome_comercial VARCHAR,	
    m_principio_ativo VARCHAR,
    m_fabricante VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
INSERT INTO catalogo_medicamento (
    nome_comercial, principio_ativo, fabricante
)
VALUES (
           m_nome_comercial, m_principio_ativo, m_fabricante
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_infos_medicamento_catalogo(m_id INT)
RETURNS catalogo_medicamento
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_med catalogo_medicamento%ROWTYPE;
BEGIN
SELECT * INTO var_med FROM catalogo_medicamento
WHERE id = m_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Nenhum medicamento de catálogo encontrado com o ID %.', m_id;
END IF;

RETURN var_med;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_medicamento_catalogo(
    m_id INT,
    m_nome_comercial VARCHAR,
    m_principio_ativo VARCHAR,
    m_fabricante VARCHAR
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o medicamento existe
    IF NOT EXISTS (SELECT 1 FROM catalogo_medicamento WHERE id = m_id) THEN
        RAISE EXCEPTION 'Medicamento de catálogo com ID % não encontrado.', m_id;
END IF;

UPDATE catalogo_medicamento
SET
    nome_comercial = COALESCE(m_nome_comercial, nome_comercial),
    principio_ativo = COALESCE(m_principio_ativo, principio_ativo),
    fabricante = COALESCE(m_fabricante, fabricante)
WHERE
    id = m_id;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_medicamento_catalogo(
    m_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_old_nome VARCHAR;
    v_new_nome VARCHAR;
BEGIN

SELECT nome_comercial
INTO v_old_nome
FROM catalogo_medicamento
WHERE id = m_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Medicamento de catálogo com ID % não encontrado.', m_id;
END IF;

    
    v_new_nome := v_old_nome || '_DESATIVADO_' || m_id || '_' || EXTRACT(EPOCH FROM clock_timestamp());

UPDATE catalogo_medicamento
SET nome_comercial = v_new_nome
WHERE id = m_id;
END;
$$;