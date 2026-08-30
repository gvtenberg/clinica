# Atividade 3 — Sistema de Clínica com Spring Boot e JPA

Projeto desenvolvido para a disciplina de Programação Web II do curso de
Sistemas para Internet do IFTO — Campus Palmas.

Aplicação web para gerenciamento de pacientes, médicos e consultas, com foco
no mapeamento de entidades JPA, na definição de relacionamentos entre elas e
na implementação das operações CRUD.

## Tecnologias

- Java 17
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation, DevTools)
- Hibernate (implementação JPA)
- Thymeleaf
- Banco de dados H2
- Maven

## Modelo de dados

O modelo segue o diagrama de classes fornecido na atividade:

| Entidade | Atributos | Relacionamentos |
|---|---|---|
| Paciente | nome, telefone | 1 paciente para 0..* consultas |
| Medico | nome, crm | 1 médico para 0..* consultas |
| Consulta | data, valor, observacao | 0..* consultas para 1 paciente e 1 médico |

A entidade `Consulta` é o lado dono das duas associações, ou seja, é na tabela
`consulta` que ficam as chaves estrangeiras `id_paciente` e `id_medico`.

## Funcionalidades

- Cadastrar, listar, atualizar e excluir pacientes
- Cadastrar, listar, atualizar e excluir médicos
- Cadastrar, listar, atualizar e excluir consultas
- Visualizar todas as consultas de um paciente
- Visualizar todas as consultas realizadas por um médico
- Validação dos formulários com mensagens exibidas ao usuário
- Bloqueio da exclusão de pacientes e médicos que possuam consultas cadastradas

## Estrutura do projeto

```
src/main/java/br/edu/ifto/atividade3/
├── controller/          # Recebe as requisições e define as views
├── model/
│   ├── entity/          # Entidades JPA
│   └── repository/      # Acesso ao banco de dados via EntityManager
└── ConfiguracaoSpringMvc.java

src/main/resources/
├── templates/           # Views Thymeleaf
├── static/css/          # Folha de estilos
├── import.sql           # Dados iniciais
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

Ao final do arquivo, os contadores das colunas de autoincremento são reiniciados
com `ALTER TABLE ... RESTART WITH`, pois os registros iniciais são inseridos com
identificadores explícitos.
