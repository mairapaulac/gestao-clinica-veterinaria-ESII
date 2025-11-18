--create
CREATE OR REPLACE PROCEDURE proc_inserir_veterinario(
    v_nome VARCHAR(100), 
	v_crmv VARCHAR(20), 
    v_telefone VARCHAR(20), 
    v_especialidade VARCHAR (100)     
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    INSERT INTO veterinario (
        nome, crmv, telefone, especialidade
    )
    VALUES (
        v_nome, v_crmv, v_telefone, v_especialidade
    );
END;
$$;

--read
CREATE OR REPLACE FUNCTION funct_get_infos_veterinario (v_crmv VARCHAR(20))
RETURNS veterinario
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
		var_vet veterinario%ROWTYPE;
BEGIN 
		select * into var_vet from veterinario
		where crmv = v_crmv;
		IF NOT FOUND THEN
        	RAISE EXCEPTION 'Nenhum veterinário encontrado com o crmv %.', v_crmv;
    	END IF;
		return var_vet; 
END; 
$$;

--update 
CREATE OR REPLACE PROCEDURE proc_atualizar_veterinario(
	p_current_crmv VARCHAR(20), 
	v_nome VARCHAR(100), 
	v_crmv VARCHAR(20), 
    v_telefone VARCHAR(20), 
    v_especialidade VARCHAR (100)   
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
		nome     = COALESCE(v_nome, nome),
		crmv 	 = COALESCE(v_crmv, crmv), 
        telefone = COALESCE(v_telefone, telefone),
        especialidade = COALESCE(v_especialidade, especialidade)        
	WHERE 
		crmv = p_current_crmv;
		
END;
$$;

--delete 
CREATE OR REPLACE PROCEDURE proc_deletar_veterinario(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_old_crmv VARCHAR(20);
    v_new_crmv VARCHAR(20);
BEGIN
    -- Busca crmv e garante existência
    SELECT crmv
    INTO v_old_crmv
    FROM veterinario
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Veterinario com ID % não encontrado.', p_id;
    END IF;

    -- Renomeia email para evitar conflitos
    v_new_crmv := v_old_crmv || '_DESATIVADO_' || p_id || '_' || EXTRACT(EPOCH FROM NOW());

    UPDATE veterinario
    SET crmv = v_new_crmv
    WHERE id = p_id;
END;
$$;
