# Atividade 8 - Sistema de Clínica com Spring Security e usuários no banco de dados

Projeto desenvolvido para a disciplina de Programação Web II do curso de
Sistemas para Internet do IFTO - _Campus_ Palmas.

Aplicação web para gerenciamento de pacientes, médicos, secretários e
consultas, com autenticação e controle de acesso por perfil. Utiliza herança
entre entidades JPA, fragmentos HTML do Thymeleaf e Bootstrap 5.

## Tecnologias

- Java 17
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation, Security, DevTools)
- Hibernate (implementação JPA)
- Thymeleaf e Thymeleaf Extras Spring Security
- Bootstrap 5.3.8 (via CDN)
- Banco de dados H2
- Maven

## Autenticação e controle de acesso

Os usuários e perfis ficam no banco de dados. No login, o
`UsuarioDetailsService` busca a conta e o Spring Security compara a senha
digitada com o hash BCrypt armazenado.

| Perfil | Acesso |
|---|---|
| Visitante | Somente login e cadastro de paciente |
| `PACIENTE` | Somente as próprias consultas |
| `MEDICO` | Somente as próprias consultas |
| `SECRETARIO` | Consultas, cadastro e edição de pacientes, lista de médicos |
| `ADMIN` | Tudo do secretário, mais exclusão de pacientes e gerenciamento de médicos e secretários |

O paciente pode se cadastrar sozinho e já entra no sistema. Médicos e
secretários são cadastrados somente pelo administrador. O perfil inicial é
definido pelo sistema, e não pelo formulário.

### Contas iniciais

| Usuário | Senha | Perfil |
|---|---|---|
| `admin` | `admin` | ADMIN |
| `secretario` | `123` | SECRETARIO |
| `roberto`, `juliana`, `marcos`, `patricia` | `123` | MEDICO |
| `ana`, `carlos`, `mariana`, `joao`, `fernanda`, `lucas` | `123` | PACIENTE |

As senhas acima servem apenas para estudo.

## Fragmentos HTML e layout

As páginas da aplicação não repetem mais cabeçalho, navbar e rodapé - cada
uma reutiliza um conjunto de fragmentos comuns, definidos em
`templates/fragments/`:

| Fragmento | Responsabilidade |
|---|---|
| `layout.html` | Molde geral de toda página: recebe o `<head>` e o `<main>` como parâmetros e monta a estrutura completa em volta deles |
| `head.html` | Metadados, título da página (recebido como parâmetro) e o CDN do Bootstrap |
| `header.html` | Navbar, com o item do menu correspondente à seção atual destacado automaticamente |
| `footer.html` | Rodapé e os scripts do Bootstrap e das máscaras de formulário |

Cada view (`list.html`, `form.html`, `detalhes.html`) declara apenas seu
próprio `<head>` (com o título da página) e seu `<main>` - o restante da
estrutura vem do `layout.html`.

### Navbar com item ativo dinâmico

O item destacado na navbar não é fixo: a classe `NavegacaoControllerAdvice`
identifica, a partir da URL da requisição, em qual seção da aplicação o
usuário está (pacientes, médicos, secretários ou consultas) e disponibiliza essa
informação para a view através do atributo `paginaAtual`. Como o Thymeleaf
compartilha o mesmo contexto entre a página principal e os fragmentos que
ela utiliza, esse valor fica acessível dentro de `header.html` sem precisar
ser repassado manualmente por cada página.

### Bootstrap e responsividade

A interface é construída com Bootstrap 5, complementado por uma folha de
estilos própria (`static/css/styles.css`) enxuta, restrita à identidade
visual do projeto: a paleta de cores, aplicada sobrescrevendo as variáveis
CSS dos componentes do Bootstrap (`--bs-btn-*`, `--bs-navbar-*`,
`--bs-table-hover-bg`), e os indicadores de foco para navegação por teclado.
Espaçamento, grade e responsividade ficam inteiramente a cargo do Bootstrap.
Todas as views usam `class="container-md"`, e a folha de estilos própria limita
sua largura máxima em 1140px - o Bootstrap, por padrão, permitiria que ela
crescesse até 1320px em telas muito largas. Os campos dos formulários são
organizados em `row`/`col-md-*`, dispostos lado a lado em telas médias/grandes
e empilhados em telas estreitas. A navbar colapsa em um menu do tipo "hambúrguer" abaixo de 992px de largura.

## Modelo de dados

### Hierarquia de herança

A modelagem original de `Paciente` e `Medico` foi reorganizada em uma
hierarquia de herança, com os atributos comuns concentrados na superclasse:

```
Pessoa (abstrata)
├── id, telefone, email, usuario
│
├── PessoaFisica (abstrata)
│   ├── nome, cpf
│   │
│   ├── Paciente
│   ├── Medico
│   └── Secretario
│
└── PessoaJuridica
    └── razaoSocial, cnpj
```

| Entidade | Atributos próprios | Atributos herdados |
|---|---|---|
| `Pessoa` | id, telefone, email, usuario | - |
| `PessoaFisica` | nome, cpf | telefone, email |
| `PessoaJuridica` | razaoSocial, cnpj | telefone, email |
| `Paciente` | consultas | id, telefone, email, nome, cpf |
| `Medico` | crm, consultas | id, telefone, email, nome, cpf |
| `Secretario` | - | id, telefone, email, nome, cpf |

`PessoaJuridica` faz parte da hierarquia conforme pedido na atividade, mas
não possui CRUD próprio no sistema.

Todas as subclasses de `Pessoa` herdam também a conta (`usuario`).

| Entidade | Atributos |
|---|---|
| `Usuario` | id, usuario, senha, ativo, perfis |
| `Perfil` | id, nome |

### Estratégia de mapeamento

A herança é mapeada com `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`,
declarada na superclasse `Pessoa`. Cada classe concreta (`Paciente`, `Medico`,
`Secretario`, `PessoaJuridica`) gera sua própria tabela, com todas as colunas herdadas
repetidas nela. Não existem tabelas para as classes abstratas `Pessoa` e
`PessoaFisica`.

Como consequência dessa estratégia, o identificador é gerado por uma única
sequência (`pessoa_seq`), compartilhada entre todas as subclasses. Isso
garante que um paciente e um médico nunca tenham o mesmo id, mesmo estando
em tabelas diferentes.

### Relacionamentos

| Entidade | Atributos próprios | Relacionamentos |
|---|---|---|
| Paciente | (herda de PessoaFisica) | 1 paciente para 0..* consultas |
| Medico | crm (herda de PessoaFisica) | 1 médico para 0..* consultas |
| Consulta | data, valor, observacao | 0..* consultas para 1 paciente e 1 médico |
| Pessoa | usuario | 1 pessoa para 0..1 usuário (com cascade) |
| Usuario | perfis | 1..* usuários para 1..* perfis (tabela `usuario_perfil`) |

A entidade `Consulta` é o lado dono das duas associações, ou seja, é na tabela
`consulta` que ficam as chaves estrangeiras `id_paciente` e `id_medico`.

## Funcionalidades

- Cadastrar, listar, atualizar e excluir pacientes
- Cadastrar, listar, atualizar e excluir médicos
- Cadastrar, listar, atualizar e excluir consultas
- Cadastrar, listar, atualizar e excluir secretários
- Criar a conta de acesso junto com o cadastro da pessoa
- Autocadastro de pacientes, com entrada automática no sistema
- Login, logout e opção "lembrar de mim"
- Trocar a senha na edição do cadastro
- "Minhas consultas" do médico ou paciente logado
- Bloqueio de CPF e nome de usuário repetidos
- Visualizar todas as consultas de um paciente
- Visualizar todas as consultas realizadas por um médico
- Validação dos formulários com mensagens exibidas ao usuário, incluindo
  validação de formato para telefone, e-mail e CPF
- Bloqueio da exclusão de pacientes e médicos que possuam consultas cadastradas
- Máscaras de digitação para telefone e CPF nos formulários
- Interface responsiva, reutilizando cabeçalho, navbar e rodapé por meio de
  fragmentos HTML do Thymeleaf
- Listagens e campos de seleção ordenados alfabeticamente por nome
- Mensagens de erro e sucesso dispensáveis pelo usuário (alertas do Bootstrap)

## Estrutura do projeto

```
src/main/java/br/edu/ifto/clinica/
├── controller/          # Recebe as requisições e define as views
│                          Inclui o NavegacaoControllerAdvice, que expõe
│                          a seção atual para destacar o item da navbar
├── model/
│   ├── entity/          # Entidades JPA (Pessoa, PessoaFisica, PessoaJuridica,
│   │                       Paciente, Medico, Secretario, Consulta,
│   │                       Usuario, Perfil)
│   ├── security/        # Regras de acesso e busca dos usuários para o login
│   ├── validation/      # Grupos de validação (Cadastro e Edicao)
│   └── repository/      # Acesso ao banco de dados com Spring Data JPA
└── ConfiguracaoSpringMvc.java

src/main/resources/
├── templates/
│   ├── fragments/        # layout, head, header e footer, reutilizados por todas as views
│   ├── pacientes/
│   ├── medicos/
│   ├── secretarios/
│   ├── consultas/
│   ├── error/            # Páginas 403 e 404
│   ├── home.html
│   └── login.html
├── static/
│   ├── css/              # Folha de estilos própria
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

A aplicação fica disponível em `http://localhost:8080`. Sem login, ela abre a
página de login; depois de entrar, a página inicial.

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

Ao final do arquivo, o contador da sequência compartilhada de Pessoa e os das
colunas de autoincremento de Consulta, Perfil e Usuario são reiniciados, pois os registros
iniciais são inseridos com identificadores explícitos.