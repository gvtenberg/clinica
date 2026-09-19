# Atividade 4 — Sistema de Clínica com Spring Boot, JPA e Herança

Projeto desenvolvido para a disciplina de Programação Web II do curso de
Sistemas para Internet do IFTO - _Campus_ Palmas.

Aplicação web para gerenciamento de pacientes, médicos e consultas, com foco
no mapeamento de entidades JPA, na definição de relacionamentos entre elas,
na implementação das operações CRUD e no mapeamento de herança entre
entidades.

## Tecnologias

- Java 17
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation, DevTools)
- Hibernate (implementação JPA)
- Thymeleaf
- Banco de dados H2
- Maven

## Modelo de dados

### Hierarquia de herança

A modelagem original de `Paciente` e `Medico` foi reorganizada em uma
hierarquia de herança, com os atributos comuns concentrados na superclasse:

```
Pessoa (abstrata)
├── id, telefone, email
│
├── PessoaFisica (abstrata)
│   ├── nome, cpf
│   │
│   ├── Paciente
│   └── Medico
│
└── PessoaJuridica
    └── razaoSocial, cnpj
```

| Entidade | Atributos próprios | Atributos herdados |
|---|---|---|
| `Pessoa` | id, telefone, email | — |
| `PessoaFisica` | nome, cpf | telefone, email |
| `PessoaJuridica` | razaoSocial, cnpj | telefone, email |
| `Paciente` | consultas | id, telefone, email, nome, cpf |
| `Medico` | crm, consultas | id, telefone, email, nome, cpf |

`PessoaJuridica` faz parte da hierarquia conforme pedido na atividade, mas
não possui CRUD próprio no sistema.

### Estratégia de mapeamento

A herança é mapeada com `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`,
declarada na superclasse `Pessoa`. Cada classe concreta (`Paciente`, `Medico`,
`PessoaJuridica`) gera sua própria tabela, com todas as colunas herdadas
repetidas nela. Não existem tabelas para as classes abstratas `Pessoa` e
`PessoaFisica`.

Como consequência dessa estratégia, o identificador é gerado por uma única
sequência (`pessoa_seq`), compartilhada entre todas as subclasses. Isso
garante que um paciente e um médico nunca tenham o mesmo id, mesmo estando
em tabelas diferentes. Isso é necessário para que consultas polimórficas sobre
`Pessoa` (quando existirem) retornem resultados consistentes.

### Relacionamentos

| Entidade | Atributos próprios | Relacionamentos |
|---|---|---|
| Paciente | (herda de PessoaFisica) | 1 paciente para 0..* consultas |
| Medico | crm (herda de PessoaFisica) | 1 médico para 0..* consultas |
| Consulta | data, valor, observacao | 0..* consultas para 1 paciente e 1 médico |

A entidade `Consulta` é o lado dono das duas associações, ou seja, é na tabela
`consulta` que ficam as chaves estrangeiras `id_paciente` e `id_medico`.

## Funcionalidades

- Cadastrar, listar, atualizar e excluir pacientes
- Cadastrar, listar, atualizar e excluir médicos
- Cadastrar, listar, atualizar e excluir consultas
- Visualizar todas as consultas de um paciente
- Visualizar todas as consultas realizadas por um médico
- Validação dos formulários com mensagens exibidas ao usuário, incluindo
  validação de formato para telefone, e-mail e CPF
- Bloqueio da exclusão de pacientes e médicos que possuam consultas cadastradas
- Máscaras de digitação para telefone e CPF nos formulários

## Estrutura do projeto

```
src/main/java/br/edu/ifto/clinica/
├── controller/          # Recebe as requisições e define as views
├── model/
│   ├── entity/          # Entidades JPA (Pessoa, PessoaFisica, PessoaJuridica,
│   │                       Paciente, Medico, Consulta)
│   └── repository/      # Acesso ao banco de dados via EntityManager
└── ConfiguracaoSpringMvc.java

src/main/resources/
├── templates/           # Views Thymeleaf
├── static/
│   ├── css/              # Folha de estilos
│   └── js/                # Máscaras de formatação dos formulários
├── import.sql            # Dados iniciais
└── application.properties
```

## Como executar

Pré-requisitos: JDK 17 ou superior.

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

A aplicação fica disponível em `http://localhost:8080`, que redireciona para a
listagem de consultas.

## Banco de dados

O H2 é utilizado em modo arquivo (`./clinicadb.mv.db`), com a propriedade
`ddl-auto=create-drop`: o esquema é recriado a cada inicialização e os dados do
`import.sql` são carregados novamente.

Para manter os dados entre execuções, altere em `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=update
```

O console do H2 fica em `http://localhost:8080/h2-console`, com os seguintes dados:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:file:./clinicadb` |
| User Name | `sa` |
| Password | `sa` |

## Dados iniciais

O arquivo `import.sql` é lido pelo Hibernate após a criação do esquema. Como as
instruções ocupam mais de uma linha, foi necessário configurar o extrator
apropriado em `application.properties`:

```properties
spring.jpa.properties.hibernate.hbm2ddl.import_files_sql_extractor=multi-line
```

Ao final do arquivo, o contador da sequência compartilhada de Pessoa e o da
coluna de autoincremento de Consulta são reiniciados, pois os registros
iniciais são inseridos com identificadores explícitos.