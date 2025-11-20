CREATE OR REPLACE PROCEDURE proc_inserir_lote_estoque(
    p_id_medicamento INTEGER,
    p_numero_lote VARCHAR,
    p_data_validade DATE,
    p_quantidade_inicial INTEGER,
    p_data_entrada DATE
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o medicamento existe
    IF NOT EXISTS (
        SELECT 1 FROM catalogo_medicamento WHERE id = p_id_medicamento
    ) THEN
        RAISE EXCEPTION 'Medicamento com ID % não encontrado no catálogo.', p_id_medicamento;
END IF;

    -- Quantidade precisa ser positiva
    IF p_quantidade_inicial <= 0 THEN
        RAISE EXCEPTION 'A quantidade inicial deve ser maior que zero.';
END IF;

    -- Insere o lote
INSERT INTO estoque_medicamento (
    id_medicamento, numero_lote, data_validade, quantidade_inicial, data_entrada
)
VALUES (
           p_id_medicamento, p_numero_lote, p_data_validade, p_quantidade_inicial, p_data_entrada
       );
END;
$$;



CREATE OR REPLACE FUNCTION funct_get_infos_lote_estoque(p_id INT)
RETURNS TABLE (
    id INT,
    id_medicamento INT,
    numero_lote VARCHAR,
    data_validade DATE,
    quantidade_inicial INT,
    data_entrada DATE
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
RETURN QUERY
SELECT e.id, e.id_medicamento, e.numero_lote, e.data_validade,
       e.quantidade_inicial, e.data_entrada
FROM estoque_medicamento e
WHERE e.id = p_id;

IF NOT FOUND THEN
        RAISE EXCEPTION 'Nenhum lote de estoque encontrado com o ID %.', p_id;
END IF;
END;
$$;


CREATE OR REPLACE PROCEDURE proc_atualizar_lote_estoque(
    p_id INT,
    p_numero_lote VARCHAR,
    p_data_validade DATE,
    p_quantidade_inicial INTEGER,
    p_data_entrada DATE
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o lote existe
    IF NOT EXISTS (SELECT 1 FROM estoque_medicamento WHERE id = p_id) THEN
        RAISE EXCEPTION 'Lote de estoque com ID % não encontrado.', p_id;
END IF;

    -- Quantidade não pode ser negativa
    IF p_quantidade_inicial IS NOT NULL AND p_quantidade_inicial <= 0 THEN
        RAISE EXCEPTION 'A quantidade inicial deve ser maior que zero.';
END IF;

UPDATE estoque_medicamento
SET
    numero_lote        = COALESCE(p_numero_lote, numero_lote),
    data_validade      = COALESCE(p_data_validade, data_validade),
    quantidade_inicial = COALESCE(p_quantidade_inicial, quantidade_inicial),
    data_entrada       = COALESCE(p_data_entrada, data_entrada)
WHERE id = p_id;
END;
$$;


CREATE OR REPLACE PROCEDURE proc_deletar_lote_estoque(
    p_id INT
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- Verifica se o lote existe
    IF NOT EXISTS (SELECT 1 FROM estoque_medicamento WHERE id = p_id) THEN
        RAISE EXCEPTION 'Lote de estoque com ID % não encontrado.', p_id;
END IF;

BEGIN
DELETE FROM estoque_medicamento
WHERE id = p_id;

EXCEPTION
        WHEN foreign_key_violation THEN
            RAISE EXCEPTION 'Não é possível deletar o lote %, pois ele está sendo utilizado em tratamentos.', p_id;
END;

END;
$$;

CREATE OR REPLACE FUNCTION funct_quantidade_disponivel_por_medicamento()
RETURNS TABLE (
    id_medicamento INT,
    nome_comercial VARCHAR,
    quantidade_disponivel NUMERIC -- Usamos NUMERIC para o cálculo
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
    WITH Saidas (id_lote, quantidade_usada) AS (
        -- 1. Calcula a quantidade TOTAL utilizada para CADA LOTE (estoque_medicamento.id)
        SELECT
            id_estoque_medicamento,
            SUM(quantidade_utilizada)
        FROM
            tratamento_medicamento
        GROUP BY
            id_estoque_medicamento
    )
SELECT
    cm.id AS id_medicamento,
    cm.nome_comercial,
    -- 2. Soma o ESTOQUE DISPONÍVEL (Entrada - Saída)
    SUM(em.quantidade_inicial) - COALESCE(SUM(S.quantidade_usada), 0) AS quantidade_disponivel
FROM
    catalogo_medicamento cm
        JOIN
    estoque_medicamento em
    ON em.id_medicamento = cm.id
        LEFT JOIN
    Saidas S
    ON S.id_lote = em.id
WHERE
    -- 3. FILTRO CRÍTICO: Exclui lotes cuja validade já expirou
    em.data_validade >= CURRENT_DATE
    -- 4. FILTRO: Exclui medicamentos desativados
    AND cm.nome_comercial NOT LIKE '%_DESATIVADO_%'
GROUP BY
    cm.id, cm.nome_comercial
HAVING
    -- 4. Exibe apenas os medicamentos que têm saldo positivo
    (SUM(em.quantidade_inicial) - COALESCE(SUM(S.quantidade_usada), 0)) > 0
ORDER BY
    cm.nome_comercial;
END;
$$;
