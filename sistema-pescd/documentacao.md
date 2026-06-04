
## 1. Visao Geral
O sistema automatiza o processo de ofertas e matriculas do Programa de Estagio Supervisionado de Capacitacao Docente (PESCD). Ele gerencia diferentes perfis de acesso e garante a seguranca dos dados.

## 2. Tecnologias Utilizadas
- Java 21: Linguagem principal.
- Spring Boot 3.4: Framework para criacao da aplicacao.
- Spring Security: Controle de acesso e protecao de dados.
- Spring Data JPA: Comunicacao com o banco de dados.
- H2 Database: Banco de dados em memoria para desenvolvimento.
- Thymeleaf: Motor de busca para criar as telas HTML.
- Bootstrap 5: Estilizacao visual das telas.

## 3. Perfis e Controle de Acesso (U.01)
O sistema identifica o usuario pelo seu papel (Role) e restringe o que ele pode ver:
- Visitante: Acesso livre a lista de ofertas na pagina inicial.
- Admin: Gerencia usuarios (Secretarios e Professores). Acesso em `/admin/**`.
- Secretario, Professor e Aluno: Possuem dashboards proprios que sao acessados apos o login.

### Fluxo de Login
1. O usuario insere nome e senha.
2. O sistema busca o usuario no banco e valida a senha criptografada.
3. Se o login for um sucesso, o `CustomAuthenticationSuccessHandler` redireciona o usuario para sua respectiva Home.

## 4. Gestao de Usuarios (AD.01)
O Administrador tem poder para:
- Listar todos os Secretarios e Professores.
- Criar novos usuarios definindo o papel.
- Editar informacoes de usuarios existentes.
- Excluir usuarios do sistema.

## 5. Exibicao de Ofertas (V.01)
Na pagina inicial, qualquer pessoa (Visitante) pode ver:
- Nome da oferta e Semestre (ordenado do mais novo para o mais antigo).
- Datas de inicio e fim das atividades.
- Nome do Professor Responsavel.
- Contagem em tempo real de quantos alunos estao inscritos.

## 6. Estrutura do Projeto (Roteiro)
- `domain`: Classes que representam as tabelas (Usuario, Oferta, Inscricao).
- `dao`: Interfaces que fazem as consultas ao banco (UsuarioDAO, etc).
- `service`: Camada que processa a logica (ex: embaralhar senhas).
- `controller`: Recebe as ordens do navegador e decide o que mostrar.
- `resources/templates`: Arquivos HTML da interface.
- `resources/messages`: Arquivos de traducao (Portugues e Ingles).

## 7. Dados de Teste
O sistema ja inicia com dados prontos definidos no arquivo `import.sql`.
- Senha padrao para todos os usuarios: `123456`
- Usuarios criados: `admin`, `sec`, `prof`, `aluno`.

