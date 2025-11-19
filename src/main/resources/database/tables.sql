CREATE TABLE proprietario (
                              cpf VARCHAR PRIMARY KEY,
                              nome VARCHAR NOT NULL,
                              telefone VARCHAR NOT NULL,
                              email VARCHAR UNIQUE NOT NULL,

                              rua VARCHAR NOT NULL,
                              numero VARCHAR NOT NULL,
                              bairro VARCHAR NOT NULL,
                              cidade VARCHAR NOT NULL,
                              estado VARCHAR NOT NULL,
                              cep VARCHAR NOT NULL
);


CREATE TABLE veterinario (
                             id SERIAL PRIMARY KEY,
                             nome VARCHAR NOT NULL,
                             crmv VARCHAR UNIQUE NOT NULL,
                             telefone VARCHAR,
                             especialidade VARCHAR,
                             email VARCHAR UNIQUE not null,
                             senha VARCHAR not null

);


CREATE TABLE funcionario (
                             id SERIAL PRIMARY KEY,
                             nome VARCHAR NOT NULL,
                             cargo VARCHAR,
                             login VARCHAR UNIQUE,
                             senha VARCHAR,
                             e_gerente boolean default false
);


CREATE TABLE paciente (
                          id SERIAL PRIMARY KEY,
                          nome VARCHAR NOT NULL,
                          especie VARCHAR,
                          raca VARCHAR,
                          data_nascimento DATE,
                          id_proprietario VARCHAR NOT NULL REFERENCES proprietario(cpf),
                          ativo boolean default true
);


CREATE TABLE consulta (
                          id SERIAL PRIMARY KEY,
                          data_consulta TIMESTAMP,
                          diagnostico TEXT,
                          id_paciente INTEGER REFERENCES paciente(id),
                          id_veterinario INTEGER REFERENCES veterinario(id)
);


CREATE TABLE tratamento (
                            id SERIAL PRIMARY KEY,
                            descricao VARCHAR,
                            id_consulta INTEGER REFERENCES consulta(id)
);


CREATE TABLE catalogo_medicamento (
                                      id SERIAL PRIMARY KEY,
                                      nome_comercial VARCHAR NOT NULL,
                                      principio_ativo VARCHAR,
                                      fabricante VARCHAR
);

CREATE TABLE estoque_medicamento (
                                     id SERIAL PRIMARY KEY,
                                     id_medicamento INTEGER NOT NULL REFERENCES catalogo_medicamento(id),
                                     numero_lote VARCHAR NOT NULL,
                                     data_validade DATE NOT NULL,
                                     quantidade_inicial INTEGER NOT NULL,
                                     data_entrada DATE NOT NULL,
                                     UNIQUE (id_medicamento, numero_lote)
);

CREATE TABLE tratamento_medicamento (
                                        id_tratamento INTEGER REFERENCES tratamento(id),
                                        id_estoque_medicamento INTEGER REFERENCES estoque_medicamento(id),
                                        quantidade_utilizada INTEGER,
                                        PRIMARY KEY (id_tratamento, id_estoque_medicamento)
);


CREATE TABLE pagamento (
                           id SERIAL PRIMARY KEY,
                           valor_total DECIMAL(10,2),
                           data_pagamento TIMESTAMP,
                           metodo_pagamento VARCHAR,
                           id_consulta INTEGER REFERENCES consulta(id),
                           id_funcionario INTEGER REFERENCES funcionario(id)
);