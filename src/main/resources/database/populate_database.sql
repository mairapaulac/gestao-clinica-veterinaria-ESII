-- ============================================================================
-- SCRIPT DE POPULAÇÃO MASSIVA DO BANCO DE DADOS
-- Clínica Veterinária - Dados para Testes
-- ============================================================================
-- Este script popula todas as tabelas com muitos dados para testes
-- Execute após criar as tabelas e os CRUDs
-- ============================================================================

-- Limpar dados existentes (CUIDADO: Remove todos os dados!)
-- Descomente as linhas abaixo se quiser limpar antes de popular
TRUNCATE TABLE tratamento_medicamento CASCADE;
TRUNCATE TABLE pagamento CASCADE;
TRUNCATE TABLE tratamento CASCADE;
TRUNCATE TABLE consulta CASCADE;
TRUNCATE TABLE estoque_medicamento CASCADE;
TRUNCATE TABLE catalogo_medicamento CASCADE;
TRUNCATE TABLE paciente CASCADE;
TRUNCATE TABLE funcionario CASCADE;
TRUNCATE TABLE veterinario CASCADE;
TRUNCATE TABLE proprietario CASCADE;

-- ============================================================================
-- 1. PROPRIETÁRIOS (100 registros)
-- ============================================================================
INSERT INTO proprietario (cpf, nome, telefone, email, rua, numero, bairro, cidade, estado, cep) VALUES
('11111111111', 'Ana Paula Santos', '(11) 98765-4321', 'ana.paula@email.com', 'Rua das Flores', '100', 'Jardim Paulista', 'São Paulo', 'SP', '01234-000'),
('22222222222', 'Bruno Oliveira Silva', '(11) 97654-3210', 'bruno.oliveira@email.com', 'Av. Paulista', '1500', 'Bela Vista', 'São Paulo', 'SP', '01310-100'),
('33333333333', 'Carla Mendes Costa', '(11) 96543-2109', 'carla.mendes@email.com', 'Rua Augusta', '200', 'Consolação', 'São Paulo', 'SP', '01305-000'),
('44444444444', 'Daniel Ferreira Lima', '(21) 99887-6655', 'daniel.ferreira@email.com', 'Av. Atlântica', '500', 'Copacabana', 'Rio de Janeiro', 'RJ', '22010-000'),
('55555555555', 'Eduarda Alves Pereira', '(21) 98765-4321', 'eduarda.alves@email.com', 'Rua do Catete', '300', 'Catete', 'Rio de Janeiro', 'RJ', '22220-000'),
('66666666666', 'Felipe Rodrigues Souza', '(21) 97654-3210', 'felipe.rodrigues@email.com', 'Av. Nossa Senhora de Copacabana', '800', 'Copacabana', 'Rio de Janeiro', 'RJ', '22050-000'),
('77777777777', 'Gabriela Martins Rocha', '(31) 91234-5678', 'gabriela.martins@email.com', 'Av. Afonso Pena', '1000', 'Centro', 'Belo Horizonte', 'MG', '30130-000'),
('88888888888', 'Henrique Barbosa Araújo', '(31) 92345-6789', 'henrique.barbosa@email.com', 'Rua da Bahia', '500', 'Centro', 'Belo Horizonte', 'MG', '30160-000'),
('99999999999', 'Isabela Nunes Cardoso', '(31) 93456-7890', 'isabela.nunes@email.com', 'Av. Contorno', '2000', 'Funcionários', 'Belo Horizonte', 'MG', '30110-000'),
('10101010101', 'João Pedro Teixeira', '(81) 97654-3210', 'joao.teixeira@email.com', 'Av. Boa Viagem', '100', 'Boa Viagem', 'Recife', 'PE', '51020-000'),
('11111111112', 'Karina Lopes Dias', '(81) 96543-2109', 'karina.lopes@email.com', 'Rua do Riachuelo', '200', 'Boa Vista', 'Recife', 'PE', '50050-000'),
('12121212121', 'Lucas Gomes Freitas', '(81) 95432-1098', 'lucas.gomes@email.com', 'Av. Conde da Boa Vista', '500', 'Boa Vista', 'Recife', 'PE', '50060-000'),
('13131313131', 'Mariana Ribeiro Campos', '(61) 95432-1098', 'mariana.ribeiro@email.com', 'SQN 105 Bloco A', '100', 'Asa Norte', 'Brasília', 'DF', '70736-000'),
('14141414141', 'Nicolas Castro Monteiro', '(61) 94321-0987', 'nicolas.castro@email.com', 'SQN 205 Bloco B', '200', 'Asa Norte', 'Brasília', 'DF', '70836-000'),
('15151515151', 'Olivia Ramos Duarte', '(61) 93210-9876', 'olivia.ramos@email.com', 'SQS 305 Bloco C', '300', 'Asa Sul', 'Brasília', 'DF', '70336-000'),
('16161616161', 'Pedro Henrique Azevedo', '(48) 98765-4321', 'pedro.azevedo@email.com', 'Av. Beira Mar Norte', '500', 'Centro', 'Florianópolis', 'SC', '88015-700'),
('17171717171', 'Quésia Torres Machado', '(48) 97654-3210', 'quesia.torres@email.com', 'Rua Felipe Schmidt', '200', 'Centro', 'Florianópolis', 'SC', '88010-000'),
('18181818181', 'Rafaela Cunha Moreira', '(48) 96543-2109', 'rafaela.cunha@email.com', 'Av. Hercílio Luz', '1000', 'Centro', 'Florianópolis', 'SC', '88020-000'),
('19191919191', 'Samuel Pires Correia', '(41) 98765-4321', 'samuel.pires@email.com', 'Av. Sete de Setembro', '500', 'Centro', 'Curitiba', 'PR', '80060-000'),
('20202020202', 'Tatiana Moura Rezende', '(41) 97654-3210', 'tatiana.moura@email.com', 'Rua XV de Novembro', '200', 'Centro', 'Curitiba', 'PR', '80020-310'),
('21212121212', 'Ulisses Farias Batista', '(41) 96543-2109', 'ulisses.farias@email.com', 'Av. Batel', '1000', 'Batel', 'Curitiba', 'PR', '80420-090'),
('22222222223', 'Vanessa Aguiar Nascimento', '(85) 98765-4321', 'vanessa.aguiar@email.com', 'Av. Beira Mar', '500', 'Meireles', 'Fortaleza', 'CE', '60165-121'),
('23232323232', 'Wagner Siqueira Fonseca', '(85) 97654-3210', 'wagner.siqueira@email.com', 'Rua Ildefonso Albano', '200', 'Aldeota', 'Fortaleza', 'CE', '60135-100'),
('24242424242', 'Xavier Tavares Guimarães', '(85) 96543-2109', 'xavier.tavares@email.com', 'Av. Dom Luís', '1000', 'Meireles', 'Fortaleza', 'CE', '60160-230'),
('25252525252', 'Yasmin Vieira Andrade', '(71) 98765-4321', 'yasmin.vieira@email.com', 'Av. Sete de Setembro', '500', 'Centro', 'Salvador', 'BA', '40060-000'),
('26262626262', 'Zeca Borges Pinheiro', '(71) 97654-3210', 'zeca.borges@email.com', 'Rua Chile', '200', 'Pelourinho', 'Salvador', 'BA', '40020-000'),
('27272727272', 'Adriana Coelho Vasconcelos', '(71) 96543-2109', 'adriana.coelho@email.com', 'Av. Oceânica', '1000', 'Barra', 'Salvador', 'BA', '40140-130'),
('28282828282', 'Bernardo Dantas Leite', '(51) 98765-4321', 'bernardo.dantas@email.com', 'Av. Borges de Medeiros', '500', 'Centro', 'Porto Alegre', 'RS', '90020-020'),
('29292929292', 'Camila Espinosa Barros', '(51) 97654-3210', 'camila.espinosa@email.com', 'Rua dos Andradas', '200', 'Centro', 'Porto Alegre', 'RS', '90020-000'),
('30303030303', 'Diego Fontes Carvalho', '(51) 96543-2109', 'diego.fontes@email.com', 'Av. Ipiranga', '1000', 'Azenha', 'Porto Alegre', 'RS', '90160-090'),
('31313131313', 'Elaine Guerreiro Melo', '(62) 98765-4321', 'elaine.guerreiro@email.com', 'Av. T-2', '500', 'Setor Bueno', 'Goiânia', 'GO', '74210-010'),
('32323232323', 'Fernando Holanda Xavier', '(62) 97654-3210', 'fernando.holanda@email.com', 'Av. T-4', '200', 'Setor Bueno', 'Goiânia', 'GO', '74210-020'),
('33333333334', 'Giovanna Macedo Paiva', '(62) 96543-2109', 'giovanna.macedo@email.com', 'Av. T-10', '1000', 'Setor Oeste', 'Goiânia', 'GO', '74115-040'),
('34343434343', 'Hugo Queiroz Santana', '(92) 98765-4321', 'hugo.queiroz@email.com', 'Av. Eduardo Ribeiro', '500', 'Centro', 'Manaus', 'AM', '69010-001'),
('35353535353', 'Ingrid Rios Bento', '(92) 97654-3210', 'ingrid.rios@email.com', 'Rua 10 de Julho', '200', 'Centro', 'Manaus', 'AM', '69010-060'),
('36363636363', 'Jorge Valente Matos', '(92) 96543-2109', 'jorge.valente@email.com', 'Av. Getúlio Vargas', '1000', 'Centro', 'Manaus', 'AM', '69020-000'),
('37373737373', 'Larissa Bessa Teles', '(98) 98765-4321', 'larissa.bessa@email.com', 'Av. Beira Mar', '500', 'Renascença', 'São Luís', 'MA', '65075-000'),
('38383838383', 'Marcos Antunes Brito', '(98) 97654-3210', 'marcos.antunes@email.com', 'Rua do Sol', '200', 'Centro', 'São Luís', 'MA', '65010-000'),
('39393939393', 'Natália Cordeiro Lira', '(98) 96543-2109', 'natalia.cordeiro@email.com', 'Av. dos Holandeses', '1000', 'Calhau', 'São Luís', 'MA', '65071-380'),
('40404040404', 'Otávio Diniz Falcão', '(84) 98765-4321', 'otavio.diniz@email.com', 'Av. Eng. Roberto Freire', '500', 'Ponta Negra', 'Natal', 'RN', '59090-000'),
('41414141414', 'Patrícia Evaristo Galvão', '(84) 97654-3210', 'patricia.evaristo@email.com', 'Rua Princesa Isabel', '200', 'Centro', 'Natal', 'RN', '59010-000'),
('42424242424', 'Renato Figueiredo Lacerda', '(84) 96543-2109', 'renato.figueiredo@email.com', 'Av. Hermes da Fonseca', '1000', 'Tirol', 'Natal', 'RN', '59020-000'),
('43434343434', 'Sabrina Guedes Maciel', '(79) 98765-4321', 'sabrina.guedes@email.com', 'Av. Beira Mar', '500', 'Atalaia', 'Aracaju', 'SE', '49037-000'),
('44444444445', 'Thiago Horta Neves', '(79) 97654-3210', 'thiago.horta@email.com', 'Rua João Pessoa', '200', 'Centro', 'Aracaju', 'SE', '49010-000'),
('45454545454', 'Úrsula Ivo Medeiros', '(79) 96543-2109', 'ursula.ivo@email.com', 'Av. Ivo do Prado', '1000', 'Centro', 'Aracaju', 'SE', '49010-100'),
('46464646464', 'Vitor Jales Nobre', '(86) 98765-4321', 'vitor.jales@email.com', 'Av. Frei Serafim', '500', 'Centro', 'Teresina', 'PI', '64000-000'),
('47474747474', 'Wanessa Kátia Oliveira', '(86) 97654-3210', 'wanessa.katia@email.com', 'Rua Areolino de Abreu', '200', 'Centro', 'Teresina', 'PI', '64001-000'),
('48484848484', 'Yuri Lemos Pacheco', '(86) 96543-2109', 'yuri.lemos@email.com', 'Av. Maranhão', '1000', 'Centro', 'Teresina', 'PI', '64000-200'),
('49494949494', 'Zuleika Moura Quintino', '(27) 98765-4321', 'zuleika.moura@email.com', 'Av. Jerônimo Monteiro', '500', 'Centro', 'Vitória', 'ES', '29010-000'),
('50505050505', 'Amanda Nascimento Rabelo', '(27) 97654-3210', 'amanda.nascimento@email.com', 'Rua da Lama', '200', 'Praia do Canto', 'Vitória', 'ES', '29055-000'),
('51515151515', 'Breno Oliveira Sampaio', '(27) 96543-2109', 'breno.oliveira@email.com', 'Av. Nossa Senhora da Penha', '1000', 'Centro', 'Vitória', 'ES', '29020-000'),
('52525252525', 'Cíntia Prado Tavares', '(65) 98765-4321', 'cintia.prado@email.com', 'Av. Getúlio Vargas', '500', 'Centro', 'Cuiabá', 'MT', '78005-000'),
('53535353535', 'Davi Queiroz Uchoa', '(65) 97654-3210', 'davi.queiroz@email.com', 'Rua Barão de Melgaço', '200', 'Centro', 'Cuiabá', 'MT', '78020-000'),
('54545454545', 'Ester Ramos Viana', '(65) 96543-2109', 'ester.ramos@email.com', 'Av. Isaac Póvoas', '1000', 'Centro', 'Cuiabá', 'MT', '78005-100'),
('55555555556', 'Fábio Silva Wanderley', '(68) 98765-4321', 'fabio.silva@email.com', 'Av. Getúlio Vargas', '500', 'Centro', 'Rio Branco', 'AC', '69900-000'),
('56565656565', 'Gisele Torres Ximenes', '(68) 97654-3210', 'gisele.torres@email.com', 'Rua Benjamin Constant', '200', 'Centro', 'Rio Branco', 'AC', '69900-100'),
('57575757575', 'Heitor Alves Yunes', '(68) 96543-2109', 'heitor.alves@email.com', 'Av. Ceará', '1000', 'Centro', 'Rio Branco', 'AC', '69900-200'),
('58585858585', 'Iara Mendes Zago', '(69) 98765-4321', 'iara.mendes@email.com', 'Av. Carlos Gomes', '500', 'Centro', 'Porto Velho', 'RO', '76801-000'),
('59595959595', 'Júlio César Abreu', '(69) 97654-3210', 'julio.cesar@email.com', 'Rua Dom Pedro II', '200', 'Centro', 'Porto Velho', 'RO', '76801-100'),
('60606060606', 'Kelly Cristina Barros', '(69) 96543-2109', 'kelly.cristina@email.com', 'Av. 7 de Setembro', '1000', 'Centro', 'Porto Velho', 'RO', '76801-200'),
('61616161616', 'Leonardo Dias Campos', '(96) 98765-4321', 'leonardo.dias@email.com', 'Av. FAB', '500', 'Centro', 'Macapá', 'AP', '68900-000'),
('62626262626', 'Marina Elisa Dutra', '(96) 97654-3210', 'marina.elisa@email.com', 'Rua Cândido Mendes', '200', 'Centro', 'Macapá', 'AP', '68900-100'),
('63636363636', 'Nelson Farias Evangelista', '(96) 96543-2109', 'nelson.farias@email.com', 'Av. Iracema Carvão Nunes', '1000', 'Centro', 'Macapá', 'AP', '68900-200'),
('64646464646', 'Olívia Gomes Fonseca', '(95) 98765-4321', 'olivia.gomes@email.com', 'Av. Ville Roy', '500', 'Centro', 'Boa Vista', 'RR', '69301-000'),
('65656565656', 'Paulo Henrique Guimarães', '(95) 97654-3210', 'paulo.henrique@email.com', 'Rua Benjamin Constant', '200', 'Centro', 'Boa Vista', 'RR', '69301-100'),
('66666666667', 'Raquel Inácio Horta', '(95) 96543-2109', 'raquel.inacio@email.com', 'Av. Ville Roy', '1000', 'Centro', 'Boa Vista', 'RR', '69301-200'),
('67676767676', 'Sérgio Júnior Ivo', '(63) 98765-4321', 'sergio.junior@email.com', 'Av. JK', '500', 'Centro', 'Palmas', 'TO', '77001-000'),
('68686868686', 'Tânia Kátia Lacerda', '(63) 97654-3210', 'tania.katia@email.com', 'Rua 15 de Novembro', '200', 'Centro', 'Palmas', 'TO', '77001-100'),
('69696969696', 'Ubirajara Lemos Machado', '(63) 96543-2109', 'ubirajara.lemos@email.com', 'Av. Teotônio Segurado', '1000', 'Plano Diretor Sul', 'Palmas', 'TO', '77060-000'),
('70707070707', 'Valéria Moura Nascimento', '(83) 98765-4321', 'valeria.moura@email.com', 'Av. Epitácio Pessoa', '500', 'Tambauzinho', 'João Pessoa', 'PB', '58040-000'),
('71717171717', 'Wagner Siqueira Oliveira', '(83) 97654-3210', 'wagner.siqueira2@email.com', 'Rua das Trincheiras', '200', 'Centro', 'João Pessoa', 'PB', '58013-000'),
('72727272727', 'Xavier Tavares Pires', '(83) 96543-2109', 'xavier.tavares2@email.com', 'Av. Cabo Branco', '1000', 'Cabo Branco', 'João Pessoa', 'PB', '58045-000'),
('73737373737', 'Yasmin Vieira Queiroz', '(88) 98765-4321', 'yasmin.vieira2@email.com', 'Av. Dom Luís', '500', 'Centro', 'Juazeiro do Norte', 'CE', '63010-000'),
('74747474747', 'Zeca Borges Rios', '(88) 97654-3210', 'zeca.borges2@email.com', 'Rua São Pedro', '200', 'Centro', 'Juazeiro do Norte', 'CE', '63010-100'),
('75757575757', 'Adriana Coelho Santana', '(88) 96543-2109', 'adriana.coelho2@email.com', 'Av. Padre Cícero', '1000', 'Centro', 'Juazeiro do Norte', 'CE', '63010-200'),
('76767676767', 'Bernardo Dantas Teles', '(87) 98765-4321', 'bernardo.dantas2@email.com', 'Av. Agamenon Magalhães', '500', 'Centro', 'Petrolina', 'PE', '56304-000'),
('77777777778', 'Camila Espinosa Brito', '(87) 97654-3210', 'camila.espinosa2@email.com', 'Rua da Aurora', '200', 'Centro', 'Petrolina', 'PE', '56304-100'),
('78787878787', 'Diego Fontes Cordeiro', '(87) 96543-2109', 'diego.fontes2@email.com', 'Av. Cardoso de Sá', '1000', 'Centro', 'Petrolina', 'PE', '56304-200'),
('79797979797', 'Elaine Guerreiro Lira', '(75) 98765-4321', 'elaine.guerreiro2@email.com', 'Av. Getúlio Vargas', '500', 'Centro', 'Feira de Santana', 'BA', '44001-000'),
('80808080808', 'Fernando Holanda Falcão', '(75) 97654-3210', 'fernando.holanda2@email.com', 'Rua Conselheiro Franco', '200', 'Centro', 'Feira de Santana', 'BA', '44001-100'),
('81818181818', 'Giovanna Macedo Galvão', '(75) 96543-2109', 'giovanna.macedo2@email.com', 'Av. Senhor dos Passos', '1000', 'Centro', 'Feira de Santana', 'BA', '44001-200'),
('82828282828', 'Hugo Queiroz Lacerda', '(47) 98765-4321', 'hugo.queiroz2@email.com', 'Av. Beira Rio', '500', 'Centro', 'Joinville', 'SC', '89201-000'),
('83838383838', 'Ingrid Rios Maciel', '(47) 97654-3210', 'ingrid.rios2@email.com', 'Rua Princesa Isabel', '200', 'Centro', 'Joinville', 'SC', '89201-100'),
('84848484848', 'Jorge Valente Neves', '(47) 96543-2109', 'jorge.valente2@email.com', 'Av. Juscelino Kubitschek', '1000', 'América', 'Joinville', 'SC', '89204-000'),
('85858585858', 'Larissa Bessa Medeiros', '(54) 98765-4321', 'larissa.bessa2@email.com', 'Av. Borges de Medeiros', '500', 'Centro', 'Caxias do Sul', 'RS', '95020-000'),
('86868686868', 'Marcos Antunes Nobre', '(54) 97654-3210', 'marcos.antunes2@email.com', 'Rua Os Dezoito do Forte', '200', 'Centro', 'Caxias do Sul', 'RS', '95020-100'),
('88888888889', 'Natália Cordeiro Oliveira', '(54) 96543-2109', 'natalia.cordeiro2@email.com', 'Av. Júlio de Castilhos', '1000', 'Centro', 'Caxias do Sul', 'RS', '95020-200'),
('89898989898', 'Otávio Diniz Pacheco', '(19) 98765-4321', 'otavio.diniz2@email.com', 'Av. Norte-Sul', '500', 'Cambuí', 'Campinas', 'SP', '13025-000'),
('90909090909', 'Patrícia Evaristo Quintino', '(19) 97654-3210', 'patricia.evaristo2@email.com', 'Rua Barão de Jaguara', '200', 'Centro', 'Campinas', 'SP', '13010-000'),
('91919191919', 'Renato Figueiredo Rabelo', '(19) 96543-2109', 'renato.figueiredo2@email.com', 'Av. John Boyd Dunlop', '1000', 'Swift', 'Campinas', 'SP', '13034-685'),
('92929292929', 'Sabrina Guedes Sampaio', '(12) 98765-4321', 'sabrina.guedes2@email.com', 'Av. Paulista', '500', 'Centro', 'São José dos Campos', 'SP', '12210-000'),
('93939393939', 'Thiago Horta Tavares', '(12) 97654-3210', 'thiago.horta2@email.com', 'Rua XV de Novembro', '200', 'Centro', 'São José dos Campos', 'SP', '12210-100'),
('94949494949', 'Úrsula Ivo Uchoa', '(12) 96543-2109', 'ursula.ivo2@email.com', 'Av. Andrômeda', '1000', 'Jardim Satélite', 'São José dos Campos', 'SP', '12230-000'),
('95959595959', 'Vitor Jales Viana', '(16) 98765-4321', 'vitor.jales2@email.com', 'Av. Paulista', '500', 'Centro', 'Ribeirão Preto', 'SP', '14010-000'),
('96969696969', 'Wanessa Kátia Wanderley', '(16) 97654-3210', 'wanessa.katia2@email.com', 'Rua General Osório', '200', 'Centro', 'Ribeirão Preto', 'SP', '14010-100'),
('97979797979', 'Yuri Lemos Ximenes', '(16) 96543-2109', 'yuri.lemos2@email.com', 'Av. Nove de Julho', '1000', 'Centro', 'Ribeirão Preto', 'SP', '14010-200'),
('98989898989', 'Zuleika Moura Yunes', '(14) 98765-4321', 'zuleika.moura2@email.com', 'Av. Nações Unidas', '500', 'Centro', 'Bauru', 'SP', '17010-000'),
('99999999990', 'Amanda Nascimento Zago', '(14) 97654-3210', 'amanda.nascimento2@email.com', 'Rua Primeiro de Agosto', '200', 'Centro', 'Bauru', 'SP', '17010-100');

-- ============================================================================
-- 2. VETERINÁRIOS (15 registros)
-- ============================================================================
INSERT INTO veterinario (nome, crmv, telefone, especialidade, email, senha) VALUES
('Dr. Ricardo Alencar', 'CRMV/SP 1234', '(11) 91111-1111', 'Clínica Geral', 'ricardo.vet@clinica.com', 'vet123'),
('Dra. Juliana Torres', 'CRMV/RJ 5678', '(21) 92222-2222', 'Ortopedia', 'juliana.vet@clinica.com', 'vet456'),
('Dr. Lucas Ferreira', 'CRMV/MG 9012', '(31) 93333-3333', 'Dermatologia', 'lucas.vet@clinica.com', 'vet789'),
('Dra. Ana Paula Mendes', 'CRMV/SP 2345', '(11) 94444-4444', 'Cardiologia', 'ana.mendes@clinica.com', 'vet101'),
('Dr. Bruno Oliveira', 'CRMV/RJ 3456', '(21) 95555-5555', 'Neurologia', 'bruno.oliveira@clinica.com', 'vet202'),
('Dra. Carla Santos', 'CRMV/MG 4567', '(31) 96666-6666', 'Oftalmologia', 'carla.santos@clinica.com', 'vet303'),
('Dr. Daniel Costa', 'CRMV/SP 5678', '(11) 97777-7777', 'Oncologia', 'daniel.costa@clinica.com', 'vet404'),
('Dra. Eliana Lima', 'CRMV/RJ 6789', '(21) 98888-8888', 'Endocrinologia', 'eliana.lima@clinica.com', 'vet505'),
('Dr. Fernando Silva', 'CRMV/MG 7890', '(31) 99999-9999', 'Anestesiologia', 'fernando.silva@clinica.com', 'vet606'),
('Dra. Gabriela Rocha', 'CRMV/SP 8901', '(11) 91010-1010', 'Cirurgia Geral', 'gabriela.rocha@clinica.com', 'vet707'),
('Dr. Henrique Alves', 'CRMV/RJ 9012', '(21) 92121-2121', 'Medicina de Emergência', 'henrique.alves@clinica.com', 'vet808'),
('Dra. Isabela Pereira', 'CRMV/MG 0123', '(31) 93232-3232', 'Medicina Preventiva', 'isabela.pereira@clinica.com', 'vet909'),
('Dr. João Pedro Souza', 'CRMV/SP 1235', '(11) 94343-4343', 'Clínica Geral', 'joao.souza@clinica.com', 'vet010'),
('Dra. Karina Martins', 'CRMV/RJ 2346', '(21) 95454-5454', 'Dermatologia', 'karina.martins@clinica.com', 'vet111'),
('Dr. Leandro Barbosa', 'CRMV/MG 3457', '(31) 96565-6565', 'Ortopedia', 'leandro.barbosa@clinica.com', 'vet212');

-- ============================================================================
-- 3. FUNCIONÁRIOS (15 registros)
-- ============================================================================
INSERT INTO funcionario (nome, cargo, login, senha, e_gerente) VALUES
('Fábio Junior', 'Atendente', 'fabio.atendente', 'func123', FALSE),
('Gabriela Lima', 'Gerente', 'gabriela.gerente', 'admin456', TRUE),
('Henrique Dias', 'Auxiliar', 'henrique.aux', 'func789', FALSE),
('Administrador', 'Administrador', 'admin', 'admin', TRUE),
('Funcionário Teste', 'Atendente', 'func', 'func', FALSE),
('Mariana Costa', 'Atendente', 'mariana.costa', 'func001', FALSE),
('Pedro Santos', 'Recepcionista', 'pedro.santos', 'func002', FALSE),
('Juliana Oliveira', 'Auxiliar Administrativo', 'juliana.oliveira', 'func003', FALSE),
('Rafael Silva', 'Gerente de Operações', 'rafael.silva', 'admin001', TRUE),
('Camila Ferreira', 'Atendente', 'camila.ferreira', 'func004', FALSE),
('Lucas Mendes', 'Auxiliar de Estoque', 'lucas.mendes', 'func005', FALSE),
('Ana Paula Rocha', 'Supervisora', 'ana.rocha', 'admin002', TRUE),
('Bruno Alves', 'Atendente', 'bruno.alves', 'func006', FALSE),
('Carla Dias', 'Recepcionista', 'carla.dias', 'func007', FALSE),
('Daniel Pereira', 'Auxiliar', 'daniel.pereira', 'func008', FALSE);

-- ============================================================================
-- 4. PACIENTES (200 registros - distribuídos entre os proprietários)
-- ============================================================================
-- Vou criar múltiplos pacientes por proprietário para ter uma boa distribuição
INSERT INTO paciente (nome, especie, raca, data_nascimento, id_proprietario, ativo) VALUES
-- Proprietário 1 (Ana Paula Santos)
('Rex', 'Cachorro', 'Golden Retriever', '2019-05-10', '11111111111', TRUE),
('Luna', 'Cachorro', 'Labrador', '2020-03-15', '11111111111', TRUE),
('Bolinha', 'Gato', 'Persa', '2021-07-20', '11111111111', TRUE),
-- Proprietário 2 (Bruno Oliveira Silva)
('Thor', 'Cachorro', 'Pitbull', '2018-11-01', '22222222222', TRUE),
('Mimi', 'Gato', 'Siamês', '2022-01-10', '22222222222', TRUE),
-- Proprietário 3 (Carla Mendes Costa)
('Max', 'Cachorro', 'Bulldog', '2019-08-25', '33333333333', TRUE),
('Nina', 'Gato', 'Maine Coon', '2020-12-05', '33333333333', TRUE),
('Pipoca', 'Cachorro', 'Vira-Lata', '2021-04-18', '33333333333', TRUE),
-- Proprietário 4 (Daniel Ferreira Lima)
('Zeus', 'Cachorro', 'Rottweiler', '2017-06-12', '44444444444', TRUE),
('Lola', 'Cachorro', 'Poodle', '2020-09-30', '44444444444', TRUE),
-- Proprietário 5 (Eduarda Alves Pereira)
('Bella', 'Cachorro', 'Yorkshire', '2019-02-14', '55555555555', TRUE),
('Charlie', 'Gato', 'British Shorthair', '2021-11-22', '55555555555', TRUE),
('Mel', 'Cachorro', 'Beagle', '2020-05-08', '55555555555', TRUE),
-- Proprietário 6 (Felipe Rodrigues Souza)
('Duke', 'Cachorro', 'Husky Siberiano', '2018-03-20', '66666666666', TRUE),
('Lily', 'Gato', 'Ragdoll', '2022-06-15', '66666666666', TRUE),
-- Proprietário 7 (Gabriela Martins Rocha)
('Rocky', 'Cachorro', 'Boxer', '2019-10-11', '77777777777', TRUE),
('Sophia', 'Gato', 'Sphynx', '2021-08-03', '77777777777', TRUE),
('Toby', 'Cachorro', 'Dachshund', '2020-01-25', '77777777777', TRUE),
-- Proprietário 8 (Henrique Barbosa Araújo)
('Jack', 'Cachorro', 'Border Collie', '2018-07-07', '88888888888', TRUE),
('Mia', 'Gato', 'Bengal', '2022-02-18', '88888888888', TRUE),
-- Proprietário 9 (Isabela Nunes Cardoso)
('Bentley', 'Cachorro', 'Chihuahua', '2019-12-30', '99999999999', TRUE),
('Coco', 'Gato', 'Scottish Fold', '2021-05-12', '99999999999', TRUE),
('Daisy', 'Cachorro', 'Shih Tzu', '2020-08-22', '99999999999', TRUE),
-- Proprietário 10 (João Pedro Teixeira)
('Cooper', 'Cachorro', 'Australian Shepherd', '2018-04-16', '10101010101', TRUE),
('Luna', 'Gato', 'Russian Blue', '2022-09-28', '10101010101', TRUE);

-- Continuando com mais pacientes para chegar a 200...
-- Vou usar uma abordagem mais eficiente com múltiplos INSERTs
DO $$
DECLARE
    proprietario_cpf VARCHAR;
    proprietario_rec RECORD;
    especies TEXT[] := ARRAY['Cachorro', 'Gato', 'Pássaro', 'Coelho', 'Hamster', 'Tartaruga'];
    racas_cachorro TEXT[] := ARRAY['Golden Retriever', 'Labrador', 'Pitbull', 'Bulldog', 'Poodle', 'Yorkshire', 'Beagle', 'Husky Siberiano', 'Boxer', 'Dachshund', 'Border Collie', 'Chihuahua', 'Shih Tzu', 'Rottweiler', 'Vira-Lata', 'Pastor Alemão', 'Doberman', 'Schnauzer', 'Maltês', 'Lhasa Apso'];
    racas_gato TEXT[] := ARRAY['Persa', 'Siamês', 'Maine Coon', 'British Shorthair', 'Ragdoll', 'Sphynx', 'Bengal', 'Scottish Fold', 'Russian Blue', 'Angorá', 'Siberiano', 'Abissínio', 'Birmanês', 'Chartreux', 'Norueguês'];
    racas_passaro TEXT[] := ARRAY['Calopsita', 'Canário', 'Periquito', 'Papagaio', 'Agapornis', 'Cacatua'];
    racas_coelho TEXT[] := ARRAY['Angorá', 'Holandês', 'Lionhead', 'Mini Rex', 'Netherland Dwarf'];
    racas_hamster TEXT[] := ARRAY['Sírio', 'Anão Russo', 'Roborovski', 'Chinês'];
    racas_tartaruga TEXT[] := ARRAY['Tigre d''água', 'Jabuti', 'Cágado'];
    especie TEXT;
    raca TEXT;
    nome_pet TEXT;
    data_nasc DATE;
    i INTEGER;
    j INTEGER;
    nomes_pets TEXT[] := ARRAY['Rex', 'Luna', 'Thor', 'Mimi', 'Max', 'Nina', 'Zeus', 'Bella', 'Charlie', 'Duke', 'Rocky', 'Sophia', 'Jack', 'Mia', 'Bentley', 'Coco', 'Cooper', 'Mel', 'Toby', 'Daisy', 'Lily', 'Pipoca', 'Bolinha', 'Lola', 'Piu', 'Fluffy', 'Whiskers', 'Shadow', 'Milo', 'Oscar', 'Simba', 'Tiger', 'Buddy', 'Max', 'Bailey', 'Lucy', 'Molly', 'Daisy', 'Lola', 'Sadie', 'Maggie', 'Chloe', 'Sophie', 'Stella', 'Penny', 'Zoey', 'Lily', 'Lulu', 'Nala', 'Ruby'];
BEGIN
    -- Para cada proprietário existente, criar 1-3 pacientes
    FOR proprietario_rec IN SELECT cpf FROM proprietario ORDER BY cpf LOOP
        proprietario_cpf := proprietario_rec.cpf;
        
        -- Criar 1-3 pacientes por proprietário (usando hash do CPF para variar)
        FOR j IN 1..(1 + (ABS(HASHTEXT(proprietario_cpf)) % 3)) LOOP
            especie := especies[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(especies, 1))];
            
            CASE especie
                WHEN 'Cachorro' THEN raca := racas_cachorro[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(racas_cachorro, 1))];
                WHEN 'Gato' THEN raca := racas_gato[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(racas_gato, 1))];
                WHEN 'Pássaro' THEN raca := racas_passaro[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(racas_passaro, 1))];
                WHEN 'Coelho' THEN raca := racas_coelho[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(racas_coelho, 1))];
                WHEN 'Hamster' THEN raca := racas_hamster[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(racas_hamster, 1))];
                WHEN 'Tartaruga' THEN raca := racas_tartaruga[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(racas_tartaruga, 1))];
            END CASE;
            
            nome_pet := nomes_pets[1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % array_length(nomes_pets, 1))] || ' ' || SUBSTRING(proprietario_cpf, 9, 3) || j::TEXT;
            data_nasc := CURRENT_DATE - INTERVAL '1 year' * (1 + (ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % 8)) - INTERVAL '1 day' * ((ABS(HASHTEXT(proprietario_cpf || j::TEXT)) % 365));
            
            INSERT INTO paciente (nome, especie, raca, data_nascimento, id_proprietario, ativo)
            VALUES (nome_pet, especie, raca, data_nasc, proprietario_cpf, TRUE);
        END LOOP;
    END LOOP;
END $$;

-- ============================================================================
-- 5. CATÁLOGO DE MEDICAMENTOS (50 registros)
-- ============================================================================
INSERT INTO catalogo_medicamento (nome_comercial, principio_ativo, fabricante) VALUES
('Vacina V10 Plus', 'Múltiplos Antígenos', 'VetLabs'),
('Meloxivet', 'Meloxicam', 'PharmaVet'),
('Dermacare', 'Hidrocortisona', 'VetDerm'),
('Antipulgas Max', 'Fipronil', 'PetCare'),
('Vermífugo Pet', 'Praziquantel', 'VetPharma'),
('Antibiótico Vet', 'Amoxicilina', 'AnimalMed'),
('Anti-inflamatório Canino', 'Carprofeno', 'VetSolutions'),
('Suplemento Vitamínico', 'Vitamina B Complex', 'PetVital'),
('Shampoo Antifúngico', 'Cetoconazol', 'DermVet'),
('Colírio Veterinário', 'Cloranfenicol', 'OftalVet'),
('Analgésico Pet', 'Tramadol', 'PainVet'),
('Antialérgico Canino', 'Clemastina', 'AllergyVet'),
('Antiemético Vet', 'Metoclopramida', 'GastroVet'),
('Anticonvulsivante', 'Fenobarbital', 'NeuroVet'),
('Hormônio Tireoide', 'Levotiroxina', 'EndoVet'),
('Insulina Veterinária', 'Insulina', 'DiabVet'),
('Antiparasitário Oral', 'Milbemicina', 'ParasiteVet'),
('Pomada Cicatrizante', 'Óxido de Zinco', 'DermCare'),
('Soro Fisiológico Vet', 'Cloreto de Sódio', 'FluidVet'),
('Antisséptico Bucal', 'Clorexidina', 'OralVet'),
('Vacina Antirrábica', 'Vírus Inativado', 'VetLabs'),
('Vacina V8', 'Múltiplos Antígenos', 'VetLabs'),
('Vacina Gripe Canina', 'Vírus Inativado', 'VetLabs'),
('Vacina Leishmaniose', 'Proteína Recombinante', 'VetLabs'),
('Antibiótico Tópico', 'Neomicina', 'TopVet'),
('Spray Antisséptico', 'Povidona Iodada', 'DermVet'),
('Suplemento Articular', 'Glucosamina', 'JointVet'),
('Probiótico Pet', 'Lactobacillus', 'GutVet'),
('Antifúngico Oral', 'Itraconazol', 'FungusVet'),
('Antiparasitário Tópico', 'Selamectina', 'ParasiteVet'),
('Antibiótico Injetável', 'Ceftriaxona', 'InjVet'),
('Anestésico Local', 'Lidocaína', 'AnestVet'),
('Relaxante Muscular', 'Tizanidina', 'MuscleVet'),
('Antitussígeno', 'Butorfanol', 'RespVet'),
('Broncodilatador', 'Aminofilina', 'RespVet'),
('Diurético', 'Furosemida', 'CardioVet'),
('Antiarrítmico', 'Digoxina', 'CardioVet'),
('Anticoagulante', 'Heparina', 'BloodVet'),
('Hemostático', 'Ácido Tranexâmico', 'BloodVet'),
('Antianêmico', 'Sulfato Ferroso', 'BloodVet'),
('Protetor Hepático', 'Silibina', 'LiverVet'),
('Protetor Renal', 'Lactulose', 'KidneyVet'),
('Laxante Pet', 'Óleo Mineral', 'GutVet'),
('Antidiarreico', 'Caolin-Pectina', 'GutVet'),
('Antiespasmódico', 'Buscopan', 'GutVet'),
('Antiácido', 'Omeprazol', 'GutVet'),
('Antiflatulento', 'Simeticona', 'GutVet'),
('Suplemento de Cálcio', 'Carbonato de Cálcio', 'BoneVet'),
('Suplemento de Ferro', 'Sulfato Ferroso', 'BloodVet'),
('Suplemento de Ômega 3', 'Ácido Graxo', 'HeartVet');

-- ============================================================================
-- 6. ESTOQUE DE MEDICAMENTOS (150 registros)
-- ============================================================================
DO $$
DECLARE
    med_id INTEGER;
    med_rec RECORD;
    lote_num TEXT;
    data_val DATE;
    qtd INTEGER;
    data_ent DATE;
    j INTEGER;
    lote_counter INTEGER;
BEGIN
    -- Para cada medicamento existente, criar 2-4 lotes
    FOR med_rec IN SELECT id FROM catalogo_medicamento ORDER BY id LOOP
        med_id := med_rec.id;
        lote_counter := 0;
        
        -- Criar 2-4 lotes por medicamento (usando hash do ID para variar)
        FOR j IN 1..(2 + (ABS(HASHTEXT(med_id::TEXT)) % 3)) LOOP
            lote_counter := lote_counter + 1;
            lote_num := 'LOTE-' || LPAD(med_id::TEXT, 3, '0') || '-' || LPAD(lote_counter::TEXT, 2, '0');
            data_val := CURRENT_DATE + INTERVAL '1 year' * (1 + (ABS(HASHTEXT(med_id::TEXT || j::TEXT)) % 3)) + INTERVAL '1 day' * ((ABS(HASHTEXT(med_id::TEXT || j::TEXT)) % 90));
            qtd := 50 + (ABS(HASHTEXT(med_id::TEXT || j::TEXT)) % 200);
            data_ent := CURRENT_DATE - INTERVAL '1 month' * ((ABS(HASHTEXT(med_id::TEXT || j::TEXT)) % 12)) - INTERVAL '1 day' * ((ABS(HASHTEXT(med_id::TEXT || j::TEXT)) % 30));
            
            INSERT INTO estoque_medicamento (id_medicamento, numero_lote, data_validade, quantidade_inicial, data_entrada)
            VALUES (med_id, lote_num, data_val, qtd, data_ent)
            ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;
END $$;

-- ============================================================================
-- 7. CONSULTAS (300 registros)
-- ============================================================================
DO $$
DECLARE
    paciente_id INTEGER;
    vet_id INTEGER;
    data_cons TIMESTAMP;
    diagnostico TEXT;
    diagnosticos TEXT[] := ARRAY[
        'Check-up anual, peso ideal.',
        'Lesão na pata traseira. Suspeita de luxação.',
        'Reação alérgica na pele. Iniciar tratamento tópico.',
        'Vômitos frequentes. Investigar gastrite.',
        'Tosse persistente. Possível infecção respiratória.',
        'Ferida na orelha. Tratamento local necessário.',
        'Obesidade. Orientação nutricional.',
        'Diarreia. Desidratação leve.',
        'Febre. Infecção bacteriana.',
        'Coceira excessiva. Possível alergia alimentar.',
        'Dificuldade para urinar. Cistite.',
        'Claudicação membro anterior.',
        'Secreção ocular. Conjuntivite.',
        'Perda de apetite. Investigar causas.',
        'Tremores. Possível intoxicação.',
        'Dificuldade respiratória. Bronquite.',
        'Ferida cirúrgica cicatrizando bem.',
        'Controle pós-operatório.',
        'Vacinação anual.',
        'Aplicação de vermífugo.',
        'Limpeza de tártaro.',
        'Castração realizada com sucesso.',
        'Exame de sangue normal.',
        'Raio-X sem alterações.',
        'Ultrassom abdominal normal.',
        'Eletrocardiograma normal.',
        'Pressão arterial normal.',
        'Glicemia controlada.',
        'Função renal preservada.',
        'Função hepática normal.'
    ];
    i INTEGER;
BEGIN
    -- Criar 300 consultas distribuídas nos últimos 2 anos
    FOR i IN 1..300 LOOP
        -- Selecionar paciente aleatório
        SELECT id INTO paciente_id FROM paciente ORDER BY RANDOM() LIMIT 1;
        
        -- Selecionar veterinário aleatório
        SELECT id INTO vet_id FROM veterinario ORDER BY RANDOM() LIMIT 1;
        
        -- Data aleatória nos últimos 2 anos
        data_cons := CURRENT_TIMESTAMP - INTERVAL '1 day' * (RANDOM() * 730)::INTEGER - INTERVAL '1 hour' * (RANDOM() * 8)::INTEGER;
        
        -- Diagnóstico aleatório
        diagnostico := diagnosticos[1 + (i % array_length(diagnosticos, 1))];
        
        INSERT INTO consulta (data_consulta, diagnostico, id_paciente, id_veterinario)
        VALUES (data_cons, diagnostico, paciente_id, vet_id);
    END LOOP;
END $$;

-- ============================================================================
-- 8. TRATAMENTOS (250 registros)
-- ============================================================================
DO $$
DECLARE
    consulta_id INTEGER;
    descricao TEXT;
    descricoes TEXT[] := ARRAY[
        'Vacinação V10 e aplicação de vermífugo.',
        'Aplicação de anti-inflamatório e tala provisória. Agendar raio-x.',
        'Prescrição de pomada tópica e troca de ração para hipoalergênica.',
        'Antibiótico oral por 7 dias. Retorno em 1 semana.',
        'Antiemético e dieta leve por 3 dias.',
        'Antibiótico tópico na orelha 2x ao dia por 10 dias.',
        'Plano de emagrecimento. Trocar ração e aumentar exercícios.',
        'Soro subcutâneo e dieta leve.',
        'Antibiótico injetável. Retorno em 48h.',
        'Dieta de eliminação por 4 semanas.',
        'Antibiótico e analgésico por 5 dias.',
        'Anti-inflamatório e repouso por 7 dias.',
        'Colírio antibiótico 3x ao dia por 7 dias.',
        'Investigação laboratorial. Coletar exames.',
        'Antídoto e fluidoterapia.',
        'Broncodilatador e antibiótico por 10 dias.',
        'Limpeza e curativo diário.',
        'Controle pós-operatório. Retirar pontos em 10 dias.',
        'Vacinação anual realizada.',
        'Vermífugo aplicado. Retornar em 3 meses.',
        'Limpeza de tártaro realizada. Manter higiene.',
        'Castração realizada. Cuidados pós-operatórios.',
        'Exames realizados. Resultados normais.',
        'Monitoramento contínuo.',
        'Tratamento preventivo.',
        'Fisioterapia 2x por semana.',
        'Acupuntura semanal.',
        'Hidroterapia 3x por semana.',
        'Suplementação vitamínica diária.',
        'Tratamento homeopático.'
    ];
    i INTEGER;
BEGIN
    -- Criar tratamentos para a maioria das consultas
    FOR i IN 1..250 LOOP
        -- Selecionar consulta aleatória
        SELECT id INTO consulta_id FROM consulta ORDER BY RANDOM() LIMIT 1;
        
        -- Verificar se já existe tratamento para esta consulta
        IF NOT EXISTS (SELECT 1 FROM tratamento WHERE id_consulta = consulta_id) THEN
            descricao := descricoes[1 + (i % array_length(descricoes, 1))];
            
            INSERT INTO tratamento (descricao, id_consulta)
            VALUES (descricao, consulta_id);
        END IF;
    END LOOP;
END $$;

-- ============================================================================
-- 9. TRATAMENTO_MEDICAMENTO (300 registros)
-- ============================================================================
DO $$
DECLARE
    tratamento_id INTEGER;
    estoque_id INTEGER;
    qtd_util INTEGER;
    i INTEGER;
BEGIN
    -- Criar relações entre tratamentos e medicamentos
    FOR i IN 1..300 LOOP
        -- Selecionar tratamento aleatório
        SELECT id INTO tratamento_id FROM tratamento ORDER BY RANDOM() LIMIT 1;
        
        -- Selecionar estoque aleatório
        SELECT id INTO estoque_id FROM estoque_medicamento ORDER BY RANDOM() LIMIT 1;
        
        -- Quantidade utilizada (1-20)
        qtd_util := 1 + (i % 20);
        
        -- Inserir se não existir
        INSERT INTO tratamento_medicamento (id_tratamento, id_estoque_medicamento, quantidade_utilizada)
        VALUES (tratamento_id, estoque_id, qtd_util)
        ON CONFLICT DO NOTHING;
    END LOOP;
END $$;

-- ============================================================================
-- 10. PAGAMENTOS (300 registros)
-- ============================================================================
DO $$
DECLARE
    consulta_id INTEGER;
    funcionario_id INTEGER;
    valor_total DECIMAL(10,2);
    data_pag TIMESTAMP;
    metodo_pag TEXT;
    metodos TEXT[] := ARRAY['Pix', 'Cartão', 'Dinheiro', 'Débito', 'Crédito'];
    i INTEGER;
BEGIN
    -- Criar pagamentos para as consultas
    FOR i IN 1..300 LOOP
        -- Selecionar consulta aleatória
        SELECT id INTO consulta_id FROM consulta ORDER BY RANDOM() LIMIT 1;
        
        -- Verificar se já existe pagamento para esta consulta
        IF NOT EXISTS (SELECT 1 FROM pagamento WHERE id_consulta = consulta_id) THEN
            -- Selecionar funcionário aleatório
            SELECT id INTO funcionario_id FROM funcionario ORDER BY RANDOM() LIMIT 1;
            
            -- Valor aleatório entre 50 e 500
            valor_total := 50.00 + (RANDOM() * 450.00);
            
            -- Data de pagamento próxima à data da consulta
            SELECT data_consulta + INTERVAL '1 hour' * (1 + (RANDOM() * 2)::INTEGER) INTO data_pag
            FROM consulta WHERE id = consulta_id;
            
            -- Método de pagamento aleatório
            metodo_pag := metodos[1 + (i % array_length(metodos, 1))];
            
            INSERT INTO pagamento (valor_total, data_pagamento, metodo_pagamento, id_consulta, id_funcionario)
            VALUES (valor_total, data_pag, metodo_pag, consulta_id, funcionario_id);
        END IF;
    END LOOP;
END $$;

-- ============================================================================
-- FIM DO SCRIPT DE POPULAÇÃO
-- ============================================================================
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'População do banco de dados concluída!';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Resumo dos dados inseridos:';
    RAISE NOTICE '  - Proprietários: %', (SELECT COUNT(*) FROM proprietario);
    RAISE NOTICE '  - Veterinários: %', (SELECT COUNT(*) FROM veterinario);
    RAISE NOTICE '  - Funcionários: %', (SELECT COUNT(*) FROM funcionario);
    RAISE NOTICE '  - Pacientes: %', (SELECT COUNT(*) FROM paciente);
    RAISE NOTICE '  - Consultas: %', (SELECT COUNT(*) FROM consulta);
    RAISE NOTICE '  - Tratamentos: %', (SELECT COUNT(*) FROM tratamento);
    RAISE NOTICE '  - Medicamentos (Catálogo): %', (SELECT COUNT(*) FROM catalogo_medicamento);
    RAISE NOTICE '  - Estoque de Medicamentos: %', (SELECT COUNT(*) FROM estoque_medicamento);
    RAISE NOTICE '  - Tratamento-Medicamento: %', (SELECT COUNT(*) FROM tratamento_medicamento);
    RAISE NOTICE '  - Pagamentos: %', (SELECT COUNT(*) FROM pagamento);
    RAISE NOTICE '========================================';
END $$;
