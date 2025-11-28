# 🐾 PetManager - Sistema de Gestão de Clínica Veterinária

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-blue.svg)](https://openjfx.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13+-336791.svg?logo=postgresql)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

Sistema desktop completo para gestão de clínicas veterinárias, desenvolvido em JavaFX com arquitetura MVC. O PetManager oferece uma solução integrada para gerenciar pacientes, agendamentos, estoque, funcionários, faturamento e relatórios.

## 📋 Índice

- [Funcionalidades](#-funcionalidades)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Configuração](#-configuração)
- [Como Executar](#-como-executar)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Modelo de Dados](#-modelo-de-dados)
- [Arquitetura](#-arquitetura)
- [Funcionalidades Detalhadas](#-funcionalidades-detalhadas)
- [Desenvolvimento](#-desenvolvimento)
- [Contribuindo](#-contribuindo)

## ✨ Funcionalidades

### 🔐 Autenticação e Controle de Acesso
- Sistema de login para veterinários e funcionários
- Diferentes níveis de permissão (Administrador, Funcionário, Veterinário)
- Gerenciamento de sessão de usuário
- Controle de acesso baseado em perfil

### 🐕 Gestão de Pacientes
- Cadastro completo de pacientes (animais)
- Edição de informações de pacientes
- Visualização de lista de pacientes
- Detalhes completos do paciente
- Histórico de consultas e tratamentos
- Busca automática de proprietário por CPF
- Preenchimento automático de dados do tutor

### 📅 Agendamentos
- Criação de novos agendamentos
- Visualização de agenda de consultas
- Gerenciamento de horários disponíveis
- Associação de paciente e veterinário
- Controle de disponibilidade

### 💊 Gestão de Estoque
- Cadastro de medicamentos
- Controle de quantidade em estoque
- Visualização de disponibilidade
- Detalhes de medicamentos (fabricante, princípio ativo, lote)
- Controle de validade e lotes
- Alertas de estoque baixo

### 👥 Gestão de Funcionários
- Cadastro de novos funcionários
- Edição de dados de funcionários
- Listagem de todos os funcionários
- Definição de permissões e cargos
- Controle de acesso diferenciado

### 💰 Faturamento e Controle Financeiro
- Faturamento de consultas realizadas
- Registro de pagamentos
- Visualização de faturamento
- Controle financeiro completo
- Histórico de pagamentos
- Múltiplos métodos de pagamento

### 📊 Histórico e Relatórios
- Visualização de histórico completo do paciente
- Detalhes de consultas anteriores
- Relatórios do sistema
- Análise de dados
- Geração de PDFs (Apache PDFBox)

### 🏥 Tratamentos e Consultas
- Registro de novos tratamentos
- Associação de medicamentos ao tratamento
- Definição de dosagem e frequência
- Acompanhamento de evolução
- Histórico completo de tratamentos
- Registro de diagnósticos

## 🛠 Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **JavaFX 17** - Framework para interface gráfica
- **Maven** - Gerenciamento de dependências e build

### Banco de Dados
- **PostgreSQL 13+** - Sistema de gerenciamento de banco de dados relacional
- **PL/pgSQL** - Procedimentos armazenados e funções

### Bibliotecas
- **PostgreSQL JDBC Driver (42.7.8)** - Driver para conexão com PostgreSQL
- **Apache PDFBox (2.0.29)** - Geração de relatórios em PDF
- **JUnit 5 (5.10.2)** - Framework de testes

### Arquitetura
- **Padrão MVC** (Model-View-Controller)
- **DAO Pattern** (Data Access Object)
- **JavaFX FXML** - Interface declarativa

## 📦 Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Java Development Kit (JDK) 17** ou superior
  - [Download Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17)
  - [Download OpenJDK](https://adoptium.net/)
- **Maven 3.6+**
  - [Download Maven](https://maven.apache.org/download.cgi)
- **PostgreSQL 13+**
  - [Download PostgreSQL](https://www.postgresql.org/download/)
- **Git** (opcional, para clonar o repositório)
  - [Download Git](https://git-scm.com/downloads)

## 🚀 Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/gestao-clinica-veterinaria-ESII.git
cd gestao-clinica-veterinaria-ESII
```

### 2. Configure o PostgreSQL

#### Criar o banco de dados

```bash
# Conecte-se ao PostgreSQL
psql -U postgres

# Crie o banco de dados
CREATE DATABASE "clinica-veterinaria";

# Conecte-se ao banco criado
\c clinica-veterinaria
```

#### Executar scripts de inicialização

```bash
# Navegue até o diretório dos scripts SQL
cd src/main/resources/database

# Execute o script de inicialização
psql -U postgres -d clinica-veterinaria -f init_vet.sql
```

**Nota:** O script `init_vet.sql` executa automaticamente todos os scripts necessários:
- Criação de tabelas (`tables.sql`)
- Criação de procedimentos e funções (CRUDs)
- Inserção de dados iniciais (veterinários)

### 3. Configure as credenciais do banco de dados

Edite o arquivo `src/main/resources/config.properties`:

```properties
# Configurações do Banco de Dados PostgreSQL
db.url=jdbc:postgresql://localhost:5432/clinica-veterinaria
db.user=postgres
db.password=sua_senha_aqui
```

**⚠️ Importante:** Altere a senha para a senha do seu PostgreSQL.

## ⚙️ Configuração

### Configuração do Banco de Dados

O arquivo `config.properties` contém as configurações de conexão:

```properties
db.url=jdbc:postgresql://localhost:5432/clinica-veterinaria
db.user=postgres
db.password=sua_senha
```

### Configuração de Fontes

O sistema utiliza a fonte **Poppins** para uma interface moderna. As fontes estão localizadas em:
```
src/main/resources/br/edu/clinica/clinicaveterinaria/fonts/
```

Se as fontes não forem encontradas, o sistema utilizará as fontes padrão do sistema operacional.

## 🎮 Como Executar

### Opção 1: Executar com Maven (Recomendado)

```bash
# Compilar o projeto
mvn clean compile

# Executar a aplicação
mvn javafx:run
```

### Opção 2: Executar com JavaFX Maven Plugin

```bash
mvn clean javafx:run
```

### Opção 3: Executar JAR gerado

```bash
# Gerar JAR executável
mvn clean package

# Executar o JAR
java --module-path /caminho/para/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -jar target/clinicaVeterinaria-1.0-SNAPSHOT.jar
```

### Credenciais Padrão

Após a inicialização do banco de dados, você pode usar as credenciais dos veterinários inseridos pelo script `insert_veterinarios_iniciais.sql` ou criar novos usuários através da interface.

## 📁 Estrutura do Projeto

```
gestao-clinica-veterinaria-ESII/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/edu/clinica/clinicaveterinaria/
│   │   │       ├── controller/          # Controladores (lógica de apresentação)
│   │   │       │   ├── LoginController.java
│   │   │       │   ├── HomeController.java
│   │   │       │   ├── PacientesController.java
│   │   │       │   ├── CadastrarPacienteController.java
│   │   │       │   ├── AgendamentosController.java
│   │   │       │   ├── EstoqueController.java
│   │   │       │   ├── FuncionariosController.java
│   │   │       │   ├── FaturamentoController.java
│   │   │       │   └── ...
│   │   │       ├── dao/                  # Data Access Object (acesso a dados)
│   │   │       │   ├── ConnectionFactory.java
│   │   │       │   ├── PacienteDAO.java
│   │   │       │   ├── ProprietarioDAO.java
│   │   │       │   ├── ConsultaDAO.java
│   │   │       │   ├── VeterinarioDAO.java
│   │   │       │   └── ...
│   │   │       ├── model/                 # Modelos de dados (entidades)
│   │   │       │   ├── Paciente.java
│   │   │       │   ├── Proprietario.java
│   │   │       │   ├── Veterinario.java
│   │   │       │   ├── Funcionario.java
│   │   │       │   ├── Consulta.java
│   │   │       │   ├── Tratamento.java
│   │   │       │   └── ...
│   │   │       ├── util/                  # Utilitários
│   │   │       │   └── DatabaseErrorHandler.java
│   │   │       ├── view/                  # Gerenciamento de views
│   │   │       │   ├── MainApplication.java
│   │   │       │   ├── SceneManager.java
│   │   │       │   └── SessionManager.java
│   │   │       └── module-info.java       # Configuração de módulos Java
│   │   │
│   │   └── resources/
│   │       ├── br/edu/clinica/clinicaveterinaria/
│   │       │   ├── *.fxml                 # Arquivos FXML (interface)
│   │       │   ├── css/                    # Estilos CSS
│   │       │   ├── fonts/                  # Fontes personalizadas
│   │       │   └── images/                 # Imagens e ícones
│   │       ├── database/                  # Scripts SQL
│   │       │   ├── tables.sql              # Criação de tabelas
│   │       │   ├── crud_*.sql              # Procedimentos CRUD
│   │       │   ├── init_vet.sql            # Script de inicialização
│   │       │   └── ...
│   │       └── config.properties           # Configurações do banco
│   │
│   └── test/                               # Testes unitários
│
├── target/                                 # Arquivos compilados (gerado)
├── pom.xml                                 # Configuração Maven
└── README.md                               # Este arquivo
```

## 🗄️ Modelo de Dados

### Entidades Principais

#### Proprietário
- CPF (PK)
- Nome, Telefone, Email
- Endereço completo (Rua, Número, Bairro, Cidade, Estado, CEP)

#### Paciente
- ID (PK)
- Nome, Espécie, Raça
- Data de Nascimento
- ID Proprietário (FK)

#### Veterinário
- ID (PK)
- Nome, CRMV (único)
- Telefone, Especialidade
- Email, Senha

#### Funcionário
- ID (PK)
- Nome, Cargo
- Login (único), Senha
- É Gerente (boolean)

#### Consulta
- ID (PK)
- Data Consulta, Diagnóstico
- ID Paciente (FK)
- ID Veterinário (FK)

#### Tratamento
- ID (PK)
- Descrição
- ID Consulta (FK)

#### Medicamento (Catálogo)
- ID (PK)
- Nome Comercial
- Princípio Ativo, Fabricante

#### Estoque de Medicamento
- ID (PK)
- ID Medicamento (FK)
- Número Lote, Data Validade
- Quantidade Inicial, Data Entrada

#### Pagamento
- ID (PK)
- Valor Total, Data Pagamento
- Método Pagamento
- ID Consulta (FK)
- ID Funcionário (FK)

### Relacionamentos

```
Proprietário (1) ────< (N) Paciente
Paciente (1) ────< (N) Consulta
Veterinário (1) ────< (N) Consulta
Consulta (1) ────< (N) Tratamento
Tratamento (N) ────< (N) Estoque_Medicamento
Funcionário (1) ────< (N) Pagamento
Consulta (1) ────< (N) Pagamento
```

## 🏗️ Arquitetura

### Padrão MVC (Model-View-Controller)

- **Model**: Classes de domínio em `model/` que representam as entidades do sistema
- **View**: Arquivos FXML em `resources/` que definem a interface gráfica
- **Controller**: Classes em `controller/` que gerenciam a lógica de apresentação e interação

### Padrão DAO (Data Access Object)

- Classes em `dao/` que encapsulam o acesso ao banco de dados
- Uso de procedimentos armazenados (stored procedures) para operações CRUD
- Factory pattern para gerenciamento de conexões (`ConnectionFactory`)

### Gerenciamento de Sessão

- `SessionManager`: Gerencia o usuário logado (veterinário ou funcionário)
- `SceneManager`: Gerencia a navegação entre telas
- Controle de permissões baseado no tipo de usuário

## 📖 Funcionalidades Detalhadas

### Cadastro de Paciente

O cadastro de paciente é uma funcionalidade central do sistema:

1. **Busca Automática de Proprietário**: Ao digitar o CPF do tutor, o sistema busca automaticamente no banco de dados
2. **Preenchimento Automático**: Se o proprietário já existe, todos os campos são preenchidos automaticamente
3. **Validação Completa**: Validação de todos os campos obrigatórios
4. **Edição**: A mesma tela permite editar pacientes existentes

**Campos do Paciente:**
- Nome (obrigatório)
- Espécie
- Raça
- Data de Nascimento (obrigatório, não pode ser futura)

**Campos do Proprietário:**
- CPF (obrigatório, busca automática)
- Nome completo (obrigatório)
- Telefone (obrigatório)
- E-mail (obrigatório)
- Endereço completo (todos obrigatórios)

### Sistema de Permissões

O sistema possui três níveis de acesso:

1. **Veterinário**: Acesso completo exceto Relatórios
2. **Funcionário Administrador**: Acesso completo a todas as funcionalidades
3. **Funcionário**: Acesso limitado (sem Relatórios)

## 🔧 Desenvolvimento

### Compilar o Projeto

```bash
mvn clean compile
```

### Executar Testes

```bash
mvn test
```

### Gerar JAR Executável

```bash
mvn clean package
```

### Estrutura de Módulos Java

O projeto utiliza Java Modules (JPMS). O arquivo `module-info.java` define:
- Dependências de módulos
- Exportações públicas
- Aberturas para JavaFX FXML

### Banco de Dados - Procedimentos Armazenados

O sistema utiliza extensivamente procedimentos armazenados PostgreSQL (PL/pgSQL) para:
- Inserção de dados (`proc_inserir_*`)
- Atualização de dados (`proc_atualizar_*`)
- Busca de informações (`funct_get_*`)
- Validações de negócio no banco de dados

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 👥 Autores

- **Julia Novais** - [@julia-novais](https://github.com/julia-novais)
- **Maíra Paula** - [@mairapaulac](https://github.com/mairapaulac)
- **Rafael Emanuel** - [@R4f53l](https://github.com/R4f53l)
- **Yago Guirra** - [@yaaggo](https://github.com/yaaggo)
