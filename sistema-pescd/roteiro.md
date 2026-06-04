# Roteiro Tecnico de Implementacao - PESCD

Este roteiro detalha a arquitetura do sistema e como as diferentes camadas se comunicam.

## 1. Arquitetura em Camadas

O sistema foi construido seguindo o padrao MVC (Model-View-Controller) com uma camada de servico intermediaria.

### A. Camada de Apresentacao (Web/Templates)
- **Tecnologia**: Thymeleaf e Bootstrap 5.
- **Arquivos**: Localizados em `src/main/resources/templates`.
- **Destaque**: O arquivo `layout.html` centraliza o menu e a internacionalizacao, servindo de base para todas as outras paginas.

### B. Camada de Controle (Controllers)
- **Localizacao**: `br.ufscar.dc.dsw.sistema_pescd.controller`.
- **IndexController**: Gerencia o acesso publico (V.01) e a rota de login.
- **AdminController**: Gerencia as rotas administrativas (AD.01) de gestao de usuarios.
- **HomeController**: Redireciona os usuarios para seus dashboards especificos.

### C. Camada de Servico (Services)
- **Localizacao**: `br.ufscar.dc.dsw.sistema_pescd.service`.
- **Papel**: Contem a logica de negocio, como a criptografia de senhas e filtros especificos de busca.
- **Implementacoes**: `UsuarioService`, `OfertaService` e `InscricaoService`.

### D. Camada de Dados (DAO/Repository)
- **Tecnologia**: Spring Data JPA.
- **Localizacao**: `br.ufscar.dc.dsw.sistema_pescd.dao`.
- **Papel**: Realiza a persistencia e consulta de dados no banco H2 de forma simplificada.

---

## 2. Fluxo de Execucao (Exemplo de Cadastro)
caminho que a informacao percorre ao salvar um novo secretario:

1. **Interface**: O Admin preenche o formulario em `cadastro.html`.
2. **Controller**: O `AdminController.salvar()` recebe os dados e valida se estao corretos.
3. **Service**: O `UsuarioService.salvar()` 
3. - Verificar se a senha precisa ser criptografada.
   - Chamar o encoder de seguranca.
4. **DAO**: O `UsuarioDAO` executa o comando SQL (INSERT) no banco de dados.
5. **Retorno**: O Controller redireciona o usuario para a lista com uma mensagem de sucesso.

---

## 3. Seguranca e Autenticacao (U.01)

O fluxo de login segue estes passos tecnicos:
- **Configuracao**: `WebSecurityConfig` define quais URLs sao protegidas.
- **Carregamento**: `UsuarioDetailsServiceImpl` busca o usuario no banco pelo login.
- **Sucesso**: `CustomAuthenticationSuccessHandler` analisa a ROLE do usuario e executa o redirecionamento:
  - ROLE_ADMIN -> /admin/home
  - ROLE_SECRETARIO -> /secretario/home
  - ROLE_PROFESSOR -> /professor/home
  - ROLE_ALUNO -> /aluno/home

---

## 4. Internacionalizacao (RNG-2)

- O sistema usa o `LocaleChangeInterceptor` (configuracao automatica do Spring).
- Os textos sao carregados de `messages.properties` e `messages_en.properties`.
- A troca e feita via link na interface que adiciona `?lang=pt` ou `?lang=en` na URL.
