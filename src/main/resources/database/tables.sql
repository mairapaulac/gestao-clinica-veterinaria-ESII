CREATE TABLE Proprietario (
  id integer PRIMARY KEY,
  nome varchar(100) NOT NULL,
  telefone varchar(20) NOT NULL,
  email varchar(255) UNIQUE NOT NULL,

  rua varchar(100) NOT NULL,
  numero varchar(10) NOT NULL,
  bairro varchar(80) NOT NULL,
  cidade varchar(80) NOT NULL,
  estado varchar(2) NOT NULL,
  cep varchar(10) NOT NULL
);


CREATE TABLE veterinario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    crmv VARCHAR(20) UNIQUE NOT NULL,
    telefone VARCHAR(20),
    especialidade VARCHAR(100)
);

CREATE TABLE funcionario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(50),
    login VARCHAR(50) UNIQUE,
    senha VARCHAR(50)
);

CREATE TABLE paciente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    especie VARCHAR(50),
    raca VARCHAR(50),
    data_nascimento DATE,
    id_proprietario INTEGER NOT NULL REFERENCES proprietario(id)
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
    descricao VARCHAR(255),
    id_consulta INTEGER REFERENCES consulta(id)
);

CREATE TABLE medicamento (
    id SERIAL PRIMARY KEY,
    nome_medicamento VARCHAR(100)
);

-- TABELA N:N CORRETA
CREATE TABLE tratamento_medicamento (
    id_medicamento INTEGER REFERENCES medicamento(id),
    id_tratamento INTEGER REFERENCES tratamento(id),
    quantidade_utilizada INTEGER,
    PRIMARY KEY (id_medicamento, id_tratamento)
);

CREATE TABLE pagamento (
    id SERIAL PRIMARY KEY,
    valor_total DECIMAL(10,2),
    data_pagamento TIMESTAMP,
    metodo_pagamento VARCHAR(50),
    id_consulta INTEGER REFERENCES consulta(id),
    id_funcionario INTEGER REFERENCES funcionario(id)
);
