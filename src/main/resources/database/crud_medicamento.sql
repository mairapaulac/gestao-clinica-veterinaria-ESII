--create
CREATE OR REPLACE PROCEDURE proc_inserir_medicamento(
    m_nome_medicamento VARCHAR (100)
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    INSERT INTO medicamento (
        nome_medicamento
    )
    VALUES (
        m_nome_medicamento
    );
END;
$$;

--read
CREATE OR REPLACE FUNCTION funct_get_infos_medicamento(m_id INT)
RETURNS medicamento
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
		var_med medicamento%ROWTYPE;
BEGIN 
		select * into var_med from medicamento
		where id = m_id;
		IF NOT FOUND THEN
        	RAISE EXCEPTION 'Nenhum medicamento encontrado com o id %.', m_id;
    	END IF;
		return var_med; 
END; 
$$;

--update
CREATE OR REPLACE PROCEDURE proc_atualizar_medicamento(
    m_id INT,
    m_nome_medicamento VARCHAR(100)
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o medicamento existe
    IF NOT EXISTS (SELECT 1 FROM medicamento WHERE id = m_id) THEN
        RAISE EXCEPTION 'Medicamento com ID % não encontrado.', m_id;
    END IF;

    UPDATE medicamento
    SET
        nome_medicamento = COALESCE(m_nome_medicamento, nome_medicamento)
    WHERE 
        id = m_id;
END;
$$;

--delete lógico
CREATE OR REPLACE PROCEDURE proc_deletar_medicamento(
    m_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_old_nome VARCHAR(100);
    v_new_nome VARCHAR(150);
BEGIN
    -- Busca nome e garante existência
    SELECT nome_medicamento
    INTO v_old_nome
    FROM medicamento
    WHERE id = m_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Medicamento com ID % não encontrado.', m_id;
    END IF;

    v_new_nome := v_old_nome || '_DESATIVADO_' || m_id || '_' || EXTRACT(EPOCH FROM clock_timestamp());

    UPDATE medicamento
    SET nome_medicamento = v_new_nome
    WHERE id = m_id;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_deletar_medicamento(
    m_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_old_nome VARCHAR(100);
    v_new_nome VARCHAR(150);
BEGIN
    -- Busca nome e garante existência
    SELECT nome_medicamento
    INTO v_old_nome
    FROM medicamento
    WHERE id = m_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Medicamento com ID % não encontrado.', m_id;
    END IF;

    -- Renomeia para manter histórico e evitar duplicação
    v_new_nome := v_old_nome || '_DESATIVADO_' || m_id || '_' || EXTRACT(EPOCH FROM clock_timestamp());

    UPDATE medicamento
    SET nome_medicamento = v_new_nome
    WHERE id = m_id;
END;
$$;



