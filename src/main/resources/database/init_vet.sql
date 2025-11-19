-- Arquivo: init_vet.sql

-- passo a passo para conectar:
-- digite psql -U postgres e coloque a senha do postgres
-- crie um banco de dados: crete database nome_do_banco
-- se conecte ao banco: \c nome_do_banco
-- pegue o endereco do arquivo init_vet.sql (ou entrar nesse endereco pelo proprio terminal)
-- escreva \i init_vet
-- eh p funcionar...

-- 1. CRIAÇÃO DA ESTRUTURA (TABELAS)
\echo '--- 1. Criando as Tabelas de Estrutura (Proprietario, Paciente, Consultas, Estoque...) ---'
\i tables.sql

-- 2. CRIAÇÃO DA LÓGICA DE NEGÓCIOS (CRUDS, VIEWS E FUNÇÕES)
\echo '--- 2. Criando os CRUDs das Tabelas ---'

\i crud_proprietario.sql
\i crud_veterinario.sql
\i crud_funcionario.sql
\i crud_paciente.sql

\i crud_consulta.sql
\i crud_tratamento.sql
\i crud_pagamento.sql

\i crud_catalogo_medicamento.sql
\i crud_estoque_medicamento.sql
\i crud_tratamento_uso_medicamento.sql

-- 3. INSERIR DADOS INICIAIS
\echo '--- 3. Inserindo Dados Iniciais (Veterinários) ---'
\i insert_veterinarios_iniciais.sql

\echo '--- Inicialização da Clínica Veterinária concluída com sucesso! ---'