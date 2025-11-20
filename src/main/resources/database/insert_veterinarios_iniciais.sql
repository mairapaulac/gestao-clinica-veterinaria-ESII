-- -----------------------------------------------------------------------------
-- ORDEM DE INSERÇÃO:
-- 1. Proprietario (sem FKs)
-- 2. Veterinario (sem FKs)
-- 3. Funcionario (sem FKs)
-- 4. Paciente (FK para Proprietario)
-- 5. Consulta (FKs para Paciente e Veterinario)
-- 6. Tratamento (FK para Consulta)
-- 7. Catalogo_Medicamento (sem FKs)
-- 8. Estoque_Medicamento (FK para Catalogo_Medicamento)
-- 9. Tratamento_Medicamento (FKs para Tratamento e Estoque_Medicamento)
-- 10. Pagamento (FKs para Consulta e Funcionario)
-- -----------------------------------------------------------------------------

-- 1. PROPRIETARIO (5 Registros)
INSERT INTO proprietario (cpf, nome, telefone, email, rua, numero, bairro, cidade, estado, cep) VALUES
                                                                                                    ('11122233344', 'Maria Silva Oliveira', '(11) 98765-4321', 'maria.silva@email.com', 'Rua das Flores', '100', 'Jardim Paulista', 'São Paulo', 'SP', '01234-000'),
                                                                                                    ('55566677788', 'João Pedro Santos', '(21) 99887-6655', 'joao.santos@email.com', 'Avenida Principal', '50', 'Copacabana', 'Rio de Janeiro', 'RJ', '20000-001'),
                                                                                                    ('99900011122', 'Ana Clara Lima', '(31) 91234-5678', 'ana.clara@email.com', 'Rua Boa Esperança', '12', 'Centro', 'Belo Horizonte', 'MG', '30100-002'),
                                                                                                    ('44455566677', 'Carlos Alberto Souza', '(81) 97654-3210', 'carlos.souza@email.com', 'Travessa Sol', '5', 'Boa Viagem', 'Recife', 'PE', '51020-003'),
                                                                                                    ('22233344455', 'Laura Mendes Costa', '(61) 95432-1098', 'laura.costa@email.com', 'Quadra 10 Bloco C', '300', 'Asa Sul', 'Brasília', 'DF', '70000-004');


-- 2. VETERINARIO (4 Registros)
INSERT INTO veterinario (nome, crmv, telefone, especialidade, email, senha) VALUES
                                                                                ('Dr. Ricardo Alencar', 'CRMV/SP 1234', '(11) 91111-1111', 'Clínica Geral', 'ricardo.vet@clinica.com', 'vet123'),
                                                                                ('Dra. Juliana Torres', 'CRMV/RJ 5678', '(21) 92222-2222', 'Ortopedia', 'juliana.vet@clinica.com', 'vet456'),
                                                                                ('Dr. Lucas Ferreira', 'CRMV/MG 9012', '(31) 93333-3333', 'Dermatologia', 'lucas.vet@clinica.com', 'vet789'),
                                                                                ('Dr. Veter Teste', 'CRMV/SP 9999', '(11) 99999-9999', 'Clínica Geral', 'veter', 'veter');

-- 3. FUNCIONARIO (5 Registros)
INSERT INTO funcionario (nome, cargo, login, senha, e_gerente) VALUES
                                                                   ('Fábio Junior', 'Atendente', 'fabio.atendente', 'func123', FALSE),
                                                                   ('Gabriela Lima', 'Gerente', 'gabriela.gerente', 'admin456', TRUE),
                                                                   ('Henrique Dias', 'Auxiliar', 'henrique.aux', 'func789', FALSE),
                                                                   ('Administrador', 'Administrador', 'admin', 'admin', TRUE),
                                                                   ('Funcionário Teste', 'Atendente', 'func', 'func', FALSE);


-- 4. PACIENTE (5 Registros)
-- id_proprietario referencia CPF do Proprietario
INSERT INTO paciente (nome, especie, raca, data_nascimento, id_proprietario, ativo) VALUES
                                                                                        ('Rex', 'Cachorro', 'Golden Retriever', '2019-05-10', '11122233344', TRUE),
                                                                                        ('Mimi', 'Gato', 'Siamês', '2021-01-20', '55566677788', TRUE),
                                                                                        ('Thor', 'Cachorro', 'Pitbull', '2017-11-01', '99900011122', TRUE),
                                                                                        ('Piu', 'Pássaro', 'Calopsita', '2023-03-15', '44455566677', TRUE),
                                                                                        ('Sombra', 'Cachorro', 'Vira-Lata', '2020-08-25', '11122233344', TRUE); -- Outro paciente da Maria


-- 5. CONSULTA (3 Registros)
-- Usa subconsultas para buscar IDs reais baseados em campos únicos
INSERT INTO consulta (data_consulta, diagnostico, id_paciente, id_veterinario) VALUES
    ('2025-10-25 10:00:00', 'Check-up anual, peso ideal.', 
     (SELECT id FROM paciente WHERE nome = 'Rex' AND id_proprietario = '11122233344' LIMIT 1),
     (SELECT id FROM veterinario WHERE email = 'ricardo.vet@clinica.com' LIMIT 1)),
    ('2025-10-26 14:30:00', 'Lesão na pata traseira. Suspeita de luxação.', 
     (SELECT id FROM paciente WHERE nome = 'Thor' AND id_proprietario = '99900011122' LIMIT 1),
     (SELECT id FROM veterinario WHERE email = 'juliana.vet@clinica.com' LIMIT 1)),
    ('2025-11-01 09:00:00', 'Reação alérgica na pele. Iniciar tratamento tópico.', 
     (SELECT id FROM paciente WHERE nome = 'Sombra' AND id_proprietario = '11122233344' LIMIT 1),
     (SELECT id FROM veterinario WHERE email = 'lucas.vet@clinica.com' LIMIT 1));


-- 6. TRATAMENTO (3 Registros)
-- Usa subconsultas para buscar IDs reais das consultas baseadas em data e diagnóstico
INSERT INTO tratamento (descricao, id_consulta) VALUES
    ('Vacinação V10 e aplicação de vermífugo.', 
     (SELECT id FROM consulta WHERE data_consulta = '2025-10-25 10:00:00' AND diagnostico LIKE '%Check-up anual%' LIMIT 1)),
    ('Aplicação de anti-inflamatório e tala provisória. Agendar raio-x.', 
     (SELECT id FROM consulta WHERE data_consulta = '2025-10-26 14:30:00' AND diagnostico LIKE '%Lesão na pata%' LIMIT 1)),
    ('Prescrição de pomada tópica e troca de ração para hipoalergênica.', 
     (SELECT id FROM consulta WHERE data_consulta = '2025-11-01 09:00:00' AND diagnostico LIKE '%Reação alérgica%' LIMIT 1));


-- 7. CATALOGO_MEDICAMENTO (3 Registros)
INSERT INTO catalogo_medicamento (nome_comercial, principio_ativo, fabricante) VALUES
                                                                                   ('Vacina V10 Plus', 'Múltiplos Antígenos', 'VetLabs'),
                                                                                   ('Meloxivet', 'Meloxicam', 'PharmaVet'),
                                                                                   ('Dermacare', 'Hidrocortisona', 'VetDerm');

-- 8. ESTOQUE_MEDICAMENTO (3 Registros)
-- Usa subconsultas para buscar IDs reais dos medicamentos baseados no nome comercial
INSERT INTO estoque_medicamento (id_medicamento, numero_lote, data_validade, quantidade_inicial, data_entrada) VALUES
    ((SELECT id FROM catalogo_medicamento WHERE nome_comercial = 'Vacina V10 Plus' LIMIT 1), 'V10-2025-01', '2026-03-01', 50, '2025-09-01'),
    ((SELECT id FROM catalogo_medicamento WHERE nome_comercial = 'Meloxivet' LIMIT 1), 'MELOX-24-A', '2024-12-31', 100, '2025-08-15'),
    ((SELECT id FROM catalogo_medicamento WHERE nome_comercial = 'Dermacare' LIMIT 1), 'DERMA-26-Z', '2027-06-01', 75, '2025-10-10');

-- 9. TRATAMENTO_MEDICAMENTO (2 Registros)
-- Usa subconsultas para buscar IDs reais baseados em descrição e número de lote
INSERT INTO tratamento_medicamento (id_tratamento, id_estoque_medicamento, quantidade_utilizada) VALUES
    ((SELECT id FROM tratamento WHERE descricao LIKE '%Vacinação V10%' LIMIT 1),
     (SELECT id FROM estoque_medicamento WHERE numero_lote = 'V10-2025-01' LIMIT 1), 1),
    ((SELECT id FROM tratamento WHERE descricao LIKE '%Prescrição de pomada%' LIMIT 1),
     (SELECT id FROM estoque_medicamento WHERE numero_lote = 'DERMA-26-Z' LIMIT 1), 10);

-- 10. PAGAMENTO (3 Registros)
-- Usa subconsultas para buscar IDs reais baseados em data da consulta e login do funcionário
INSERT INTO pagamento (valor_total, data_pagamento, metodo_pagamento, id_consulta, id_funcionario) VALUES
    (150.00, '2025-10-25 10:15:00', 'Pix', 
     (SELECT id FROM consulta WHERE data_consulta = '2025-10-25 10:00:00' LIMIT 1),
     (SELECT id FROM funcionario WHERE login = 'fabio.atendente' LIMIT 1)),
    (220.50, '2025-10-26 15:00:00', 'Cartão', 
     (SELECT id FROM consulta WHERE data_consulta = '2025-10-26 14:30:00' LIMIT 1),
     (SELECT id FROM funcionario WHERE login = 'fabio.atendente' LIMIT 1)),
    (85.00, '2025-11-01 09:30:00', 'Dinheiro', 
     (SELECT id FROM consulta WHERE data_consulta = '2025-11-01 09:00:00' LIMIT 1),
     (SELECT id FROM funcionario WHERE login = 'gabriela.gerente' LIMIT 1));