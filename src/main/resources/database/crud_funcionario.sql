--create
CREATE OR REPLACE PROCEDURE proc_inserir_funcionario(
    f_nome VARCHAR(100), 
	f_cargo VARCHAR(50), 
    f_login VARCHAR(50), --email...
    f_senha VARCHAR (50)     
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    INSERT INTO funcionario (
        nome, cargo, login, senha
    )
    VALUES (
        f_nome, f_cargo, f_login, f_senha
    );
END;
$$;

--read
CREATE OR REPLACE FUNCTION funct_get_infos_funcionario(f_login VARCHAR(50))
RETURNS funcionario
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
		var_func funcionario%ROWTYPE;
BEGIN 
		select * into var_func from funcionario
		where login = f_login;
		IF NOT FOUND THEN
        	RAISE EXCEPTION 'Nenhum funcionário encontrado com o login %.', f_login;
    	END IF;
		return var_func; 
END; 
$$;

--update 
CREATE OR REPLACE PROCEDURE proc_atualizar_funcionario(
	f_curr_login VARCHAR (50),
	f_nome VARCHAR(100), 
	f_cargo VARCHAR(50), 
    f_login VARCHAR(50), --email...
    f_senha VARCHAR (50)     
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
		cargo 	 = COALESCE(f_cargo, cargo), 
        login    = COALESCE(f_login, login),
        senha    = COALESCE(f_senha, senha)        
	WHERE 
		login = f_curr_login;
		
END;
$$;

--delete 
CREATE OR REPLACE PROCEDURE proc_deletar_funcionario (
    f_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
    v_old_login VARCHAR(50);
    v_new_login VARCHAR(50);
BEGIN
    -- Busca login e garante existência
    SELECT login
    INTO v_old_login
    FROM funcionario
    WHERE id = f_id;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Funcionário com ID % não encontrado.', f_id;
    END IF;

    -- Renomeia email para evitar conflitos
    v_new_login := v_old_login || '_DESATIVADO_' || f_id || '_' || EXTRACT(EPOCH FROM NOW());

    UPDATE funcionario
    SET login = v_new_login
    WHERE id = f_id;
END;
$$;
