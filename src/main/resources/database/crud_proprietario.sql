--create
CREATE OR REPLACE PROCEDURE proc_inserir_proprietario(
    p_nome VARCHAR(100), 
    p_telefone VARCHAR(20), 
    p_email VARCHAR(255), 
    p_rua VARCHAR(100),
    p_numero VARCHAR(10),
    p_bairro VARCHAR(80),
    p_cidade VARCHAR(80),
    p_estado VARCHAR(2),
    p_cep VARCHAR(10)
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    INSERT INTO proprietario (
        nome, telefone, email, 
        rua, numero, bairro, cidade, estado, cep
    )
    VALUES (
        p_nome, p_telefone, p_email,
        p_rua, p_numero, p_bairro, p_cidade, p_estado, p_cep
    );
END;
$$;

--read
CREATE OR REPLACE FUNCTION funct_get_infos_proprietario(p_email VARCHAR(255))
RETURNS proprietario
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
		var_prop proprietario%ROWTYPE;
BEGIN 
		select * into var_prop from proprietario
		where email = p_email;
		IF NOT FOUND THEN
        	RAISE EXCEPTION 'Nenhum proprietário encontrado com o email %.', p_email;
    	END IF;
		return var_prop; 
END; 
$$;

--update 
CREATE OR REPLACE PROCEDURE proc_atualizar_proprietario (
	p_curr_email VARCHAR (100),
	p_nome VARCHAR(100), 
    p_telefone VARCHAR(20), 
    p_email VARCHAR(255), 
    p_rua VARCHAR(100),
    p_numero VARCHAR(10),
    p_bairro VARCHAR(80),
    p_cidade VARCHAR(80),
    p_estado VARCHAR(2),
    p_cep VARCHAR(10)	
)
LANGUAGE plpgsql
SECURITY DEFINER 
AS $$
BEGIN
	IF NOT EXISTS (SELECT 1 FROM proprietario WHERE email = p_curr_email) THEN
		RAISE EXCEPTION 'Proprietário com email % não encontrado.', p_curr_email;
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
		email = p_curr_email;
		
END;
$$;

--delete 
CREATE OR REPLACE PROCEDURE proc_deletar_proprietario(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_old_email VARCHAR(255);
    v_new_email VARCHAR(255);
BEGIN
    -- Busca email e garante existência
    SELECT email 
    INTO v_old_email
    FROM proprietario
    WHERE id = p_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Proprietário com ID % não encontrado.', p_id;
    END IF;

    -- Renomeia email para evitar conflitos
    v_new_email := v_old_email || '_DESATIVADO_' || p_id || '_' || EXTRACT(EPOCH FROM NOW());

    UPDATE proprietario
    SET email = v_new_email
    WHERE id = p_id;
END;
$$;
