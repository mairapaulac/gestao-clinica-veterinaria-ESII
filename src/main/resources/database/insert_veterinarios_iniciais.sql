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


-- 2. VETERINARIO (3 Registros)
INSERT INTO veterinario (nome, crmv, telefone, especialidade, email, senha) VALUES
                                                                                ('Dr. Ricardo Alencar', 'CRMV/SP 1234', '(11) 91111-1111', 'Clínica Geral', 'ricardo.vet@clinica.com', 'vet123'),
                                                                                ('Dra. Juliana Torres', 'CRMV/RJ 5678', '(21) 92222-2222', 'Ortopedia', 'juliana.vet@clinica.com', 'vet456'),
                                                                                ('Dr. Lucas Ferreira', 'CRMV/MG 9012', '(31) 93333-3333', 'Dermatologia', 'lucas.vet@clinica.com', 'vet789');

-- 3. FUNCIONARIO (3 Registros)
INSERT INTO funcionario (nome, cargo, login, senha, e_gerente) VALUES
                                                                   ('Fábio Junior', 'Atendente', 'fabio.atendente', 'func123', FALSE),
                                                                   ('Gabriela Lima', 'Gerente', 'gabriela.gerente', 'admin456', TRUE), -- Gerente/Adm (privilégio)
                                                                   ('Henrique Dias', 'Auxiliar', 'henrique.aux', 'func789', FALSE);


-- 4. PACIENTE (5 Registros)
-- id_proprietario referencia CPF do Proprietario
INSERT INTO paciente (nome, especie, raca, data_nascimento, id_proprietario, ativo) VALUES
                                                                                        ('Rex', 'Cachorro', 'Golden Retriever', '2019-05-10', '11122233344', TRUE),
                                                                                        ('Mimi', 'Gato', 'Siamês', '2021-01-20', '55566677788', TRUE),
                                                                                        ('Thor', 'Cachorro', 'Pitbull', '2017-11-01', '99900011122', TRUE),
                                                                                        ('Piu', 'Pássaro', 'Calopsita', '2023-03-15', '44455566677', TRUE),
                                                                                        ('Sombra', 'Cachorro', 'Vira-Lata', '2020-08-25', '11122233344', TRUE); -- Outro paciente da Maria


-- 5. CONSULTA (3 Registros)
-- id_paciente referencia ID gerado de Paciente. id_veterinario referencia ID gerado de Veterinario.
-- As IDs sequenciais (1, 2, 3) são usadas, mas em um cenário real é preciso ter certeza da ID gerada.
INSERT INTO consulta (data_consulta, diagnostico, id_paciente, id_veterinario) VALUES
                                                                                   ('2025-10-25 10:00:00', 'Check-up anual, peso ideal.', 1, 1),
                                                                                   ('2025-10-26 14:30:00', 'Lesão na pata traseira. Suspeita de luxação.', 3, 2),
                                                                                   ('2025-11-01 09:00:00', 'Reação alérgica na pele. Iniciar tratamento tópico.', 5, 3);


-- 6. TRATAMENTO (3 Registros)
-- id_consulta referencia ID gerado de Consulta.
INSERT INTO tratamento (descricao, id_consulta) VALUES
                                                    ('Vacinação V10 e aplicação de vermífugo.', 1),
                                                    ('Aplicação de anti-inflamatório e tala provisória. Agendar raio-x.', 2),
                                                    ('Prescrição de pomada tópica e troca de ração para hipoalergênica.', 3);


-- 7. CATALOGO_MEDICAMENTO (3 Registros)
INSERT INTO catalogo_medicamento (nome_comercial, principio_ativo, fabricante) VALUES
                                                                                   ('Vacina V10 Plus', 'Múltiplos Antígenos', 'VetLabs'),
                                                                                   ('Meloxivet', 'Meloxicam', 'PharmaVet'),
                                                                                   ('Dermacare', 'Hidrocortisona', 'VetDerm');

-- 8. ESTOQUE_MEDICAMENTO (3 Registros)
-- id_medicamento referencia ID gerado de Catalogo_Medicamento.
INSERT INTO estoque_medicamento (id_medicamento, numero_lote, data_validade, quantidade_inicial, data_entrada) VALUES
                                                                                                                   (1, 'V10-2025-01', '2026-03-01', 50, '2025-09-01'),
                                                                                                                   (2, 'MELOX-24-A', '2024-12-31', 100, '2025-08-15'),
                                                                                                                   (3, 'DERMA-26-Z', '2027-06-01', 75, '2025-10-10');

-- 9. TRATAMENTO_MEDICAMENTO (2 Registros)
-- id_tratamento referencia ID gerado de Tratamento. id_estoque_medicamento referencia ID gerado de Estoque_Medicamento.
INSERT INTO tratamento_medicamento (id_tratamento, id_estoque_medicamento, quantidade_utilizada) VALUES
                                                                                                     (1, 1, 1),
                                                                                                     (3, 3, 10);

-- 10. PAGAMENTO (3 Registros)
-- id_consulta referencia ID gerado de Consulta. id_funcionario referencia ID gerado de Funcionario.
INSERT INTO pagamento (valor_total, data_pagamento, metodo_pagamento, id_consulta, id_funcionario) VALUES
                                                                                                       (150.00, '2025-10-25 10:15:00', 'Pix', 1, 1),
                                                                                                       (220.50, '2025-10-26 15:00:00', 'Cartão', 2, 1),
                                                                                                       (85.00, '2025-11-01 09:30:00', 'Dinheiro', 3, 2);