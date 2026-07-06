# Roteiro de Modificações do Projeto

Este documento resume as modificações realizadas no projeto **Sistema-de-PESCD** para a migração do banco de dados e melhorias de código.

---

## 1. Migração de Banco de Dados para PostgreSQL
* **Alteração**: Substituição do banco de dados em memória H2 pelo PostgreSQL.
* **Arquivos modificados**:
  * `pom.xml`: Removido o driver H2 e adicionado o driver JDBC do PostgreSQL.
  * `application.properties`: Atualizado com as configurações de URL, driver e dialeto do PostgreSQL.

## 2. Configuração de Credenciais Dinâmicas
* **Alteração**: Alterado o login e senha de acesso ao banco para usar variáveis de ambiente com fallbacks padrões.
* **Arquivos modificados**:
  * `application.properties`: Definido `spring.datasource.username=${DB_USERNAME:postgres}` e `spring.datasource.password=${DB_PASSWORD:postgres}` para evitar conflitos de senhas locais entre os membros da equipe e proteger credenciais no repositório.

## 3. Compatibilidade com Java 25
* **Alteração**: Atualização da versão do Lombok e ajuste no plugin do compilador do Maven para suportar a compilação no Java 25.
* **Arquivos modificados**:
  * `pom.xml`: Lombok atualizado para `1.18.40` e configurado o processador de anotações dentro do `maven-compiler-plugin`.

## 4. Injeção de Dependências via Construtor (Lombok)
* **Alteração**: Removido o uso da anotação `@Autowired` em todo o código fonte da aplicação. Todas as dependências agora são injetadas via construtor automático usando a anotação `@RequiredArgsConstructor` do Lombok com propriedades declaradas como `private final`.
* **Arquivos modificados**:
  * Todos os Controllers, Services, classes de configuração (`DatabaseChecker`, `DataLoader`) e segurança (`UsuarioDetailsServiceImpl`).

## 5. Desacoplamento de DAOs nos Controladores
* **Alteração**: Remoção completa das injeções de repositórios (DAOs) em `ProfessorController` e `SecretarioController`. Os controladores agora dependem unicamente das interfaces de serviço.
* **Arquivos modificados**:
  * `ProfessorController.java`, `SecretarioController.java`.
  * `IInscricaoService.java`, `InscricaoService.java`, `IOfertaService.java`, `OfertaService.java`, `IUsuarioService.java`, `UsuarioService.java`.
  * `alunos.html`: Adicionado botão visual "Remover Aluno" para acionar a rota de exclusão no serviço.

## 6. Confirmação de Logout
* **Alteração**: Adicionada uma tela de confirmação de logout quando o usuário tenta acessar `/logout` via método GET, prevenindo erros de redirecionamento ou bloqueio CSRF.
* **Arquivos modificados**:
  * `IndexController.java`, `WebSecurityConfig.java`.
  * Novo arquivo `logout-confirm.html`.
