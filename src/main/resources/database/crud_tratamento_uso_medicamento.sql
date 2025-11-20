CREATE OR REPLACE PROCEDURE proc_inserir_uso_medicamento(
    p_id_tratamento INTEGER,
    p_id_estoque_medicamento INTEGER,
    p_quantidade_utilizada INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_estoque_disponivel INTEGER;
BEGIN
    -- 1. Verifica se o tratamento e o lote de estoque existem
    IF NOT EXISTS (SELECT 1 FROM tratamento WHERE id = p_id_tratamento) THEN
       RAISE EXCEPTION 'Tratamento com ID (%) não encontrado.', p_id_tratamento;
END IF;

    IF NOT EXISTS (
        SELECT 1 FROM estoque_medicamento
        WHERE id = p_id_estoque_medicamento
          AND data_validade >= CURRENT_DATE
    ) THEN
       RAISE EXCEPTION 'Lote de estoque com ID (%) não encontrado ou está VENCIDO.',
       p_id_estoque_medicamento;
END IF;

    -- 2. Calcula estoque disponível (com proteção contra NULL)
SELECT
    em.quantidade_inicial - COALESCE(SUM(tum.quantidade_utilizada), 0)
INTO v_estoque_disponivel
FROM estoque_medicamento em
         LEFT JOIN tratamento_medicamento tum
                   ON tum.id_estoque_medicamento = em.id
WHERE em.id = p_id_estoque_medicamento
GROUP BY em.quantidade_inicial;

-- Se vier NULL por algum motivo, assume estoque total
IF v_estoque_disponivel IS NULL THEN
SELECT quantidade_inicial
INTO v_estoque_disponivel
FROM estoque_medicamento
WHERE id = p_id_estoque_medicamento;
END IF;

    -- 3. Verifica se há estoque suficiente
    IF v_estoque_disponivel < p_quantidade_utilizada THEN
        RAISE EXCEPTION
            'Estoque insuficiente no lote %: % disponíveis, % solicitados.',
            p_id_estoque_medicamento, v_estoque_disponivel, p_quantidade_utilizada;
END IF;

    -- 4. Insere o uso
INSERT INTO tratamento_medicamento (
    id_tratamento, id_estoque_medicamento, quantidade_utilizada
)
VALUES (
           p_id_tratamento, p_id_estoque_medicamento, p_quantidade_utilizada
       );
END;
$$;

CREATE OR REPLACE FUNCTION funct_get_uso_medicamento(
    p_id_tratamento INTEGER,
    p_id_estoque_medicamento INTEGER
)
RETURNS tratamento_medicamento
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
var_uso tratamento_medicamento%ROWTYPE;
BEGIN
SELECT * INTO var_uso FROM tratamento_medicamento
WHERE id_tratamento = p_id_tratamento
  AND id_estoque_medicamento = p_id_estoque_medicamento;

IF NOT FOUND THEN
            RAISE EXCEPTION 'Uso de medicamento não encontrado para o Tratamento % e Lote %.', p_id_tratamento, p_id_estoque_medicamento;
END IF;

RETURN var_uso;
END;
$$;

CREATE OR REPLACE PROCEDURE proc_atualizar_uso_medicamento(
    p_id_tratamento INTEGER,
    p_id_estoque_medicamento INTEGER,
    p_nova_quantidade INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
DECLARE
v_old_quantidade INTEGER;
    v_estoque_disponivel_ajustado INTEGER;
    v_diferenca INTEGER;
BEGIN
    -- 1. Verifica se o registro existe
SELECT quantidade_utilizada
INTO v_old_quantidade
FROM tratamento_medicamento
WHERE id_tratamento = p_id_tratamento
  AND id_estoque_medicamento = p_id_estoque_medicamento;

IF NOT FOUND THEN
        RAISE EXCEPTION
            'Uso de medicamento não encontrado para o Tratamento % e Lote %.',
            p_id_tratamento, p_id_estoque_medicamento;
END IF;

    -- 2. VALIDAÇÃO DE LOTE VENCIDO
    IF EXISTS (
        SELECT 1 FROM estoque_medicamento
        WHERE id = p_id_estoque_medicamento
          AND data_validade < CURRENT_DATE
    ) THEN
        RAISE EXCEPTION
            'Não é possível atualizar uso: lote % está vencido.',
            p_id_estoque_medicamento;
END IF;

    -- 3. Diferença entre antiga e nova quantidade
    v_diferenca := p_nova_quantidade - v_old_quantidade;

    -- 4. Se houve aumento no uso, valida estoque
    IF v_diferenca > 0 THEN

SELECT
    em.quantidade_inicial - COALESCE(SUM(tum.quantidade_utilizada), 0)
INTO v_estoque_disponivel_ajustado
FROM estoque_medicamento em
         LEFT JOIN tratamento_medicamento tum
                   ON tum.id_estoque_medicamento = em.id
WHERE em.id = p_id_estoque_medicamento
GROUP BY em.quantidade_inicial;

-- Proteção NULL
IF v_estoque_disponivel_ajustado IS NULL THEN
SELECT quantidade_inicial
INTO v_estoque_disponivel_ajustado
FROM estoque_medicamento
WHERE id = p_id_estoque_medicamento;
END IF;

        IF v_estoque_disponivel_ajustado < v_diferenca THEN
             RAISE EXCEPTION
                'Estoque insuficiente no lote % para o aumento. Disponível: % | Aumento solicitado: %.',
                p_id_estoque_medicamento, v_estoque_disponivel_ajustado, v_diferenca;
END IF;

END IF;

    -- 5. Atualiza registro
UPDATE tratamento_medicamento
SET quantidade_utilizada = p_nova_quantidade
WHERE id_tratamento = p_id_tratamento
  AND id_estoque_medicamento = p_id_estoque_medicamento;

END;
$$;


CREATE OR REPLACE PROCEDURE proc_deletar_uso_medicamento(
    p_id_tratamento INTEGER,
    p_id_estoque_medicamento INTEGER
)
LANGUAGE plpgsql
SECURITY DEFINER
AS $$
BEGIN
    -- 1. Verifica se o registro existe
    IF NOT EXISTS (SELECT 1 FROM tratamento_medicamento
                   WHERE id_tratamento = p_id_tratamento AND id_estoque_medicamento = p_id_estoque_medicamento) THEN
        RAISE EXCEPTION 'Uso de medicamento não encontrado para o Tratamento % e Lote %.', p_id_tratamento, p_id_estoque_medicamento;
END IF;

    -- 2. Deleta o registro (Hard Delete)
DELETE FROM tratamento_medicamento
WHERE id_tratamento = p_id_tratamento
  AND id_estoque_medicamento = p_id_estoque_medicamento;

END;
$$;