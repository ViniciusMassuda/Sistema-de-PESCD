package br.ufscar.dc.dsw.sistema_pescd;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Diz ao sistema para carregar toda a aplicacao (banco, seguranca, rotas) para o teste
@SpringBootTest
// Prepara o MockMvc, uma ferramenta que simula um navegador para fazer requisicoes
@AutoConfigureMockMvc
public class CasosDeUsoTest {

    // Injeta o "simulador do navegador" dentro da classe
    @Autowired
    private MockMvc mockMvc;

    // V.01 - COMO Visitante, EU QUERO visualizar as ofertas de vagas de estagio.
    @Test // Avisa o Java que essa funcao e um teste automatizado
    public void testV01_VisitanteVisualizaOfertas() throws Exception {
        // Simula acessar a URL inicial do site ("/") via GET
        mockMvc.perform(get("/"))
                // Verifica se o servidor respondeu com status 200 (Deu tudo certo)
                .andExpect(status().isOk())
                // Verifica se a tela retornada se chama "index"
                .andExpect(view().name("index"))
                // Garante que o controlador enviou a lista de "ofertas" para preencher a tabela
                .andExpect(model().attributeExists("ofertas"));
    }

    // U.01 - COMO Usuario (Admin), EU QUERO me autenticar para acessar o sistema no meu perfil.
    @Test
    public void testU01_AdminLoginRedirecionaParaHome() throws Exception {
        // Simula preencher o formulario de login e clicar em Entrar com o usuario admin
        mockMvc.perform(formLogin("/login").user("admin").password("123456"))
                // Verifica se o login funcionou e pediu um redirecionamento (status 302)
                .andExpect(status().is3xxRedirection())
                // Verifica se o redirecionamento mandou o admin para sua rota correta
                .andExpect(redirectedUrl("/admin/home"));
    }

    // AD.01 - COMO Administrador, EU QUERO gerenciar cadastro de usuarios.
    // RN-4 O e-mail deve ser unico por usuario.
    @Test
    // Simula que o usuario logado para este teste e um ADMIN, liberando o acesso as rotas
    @WithMockUser(username="admin", roles={"ADMIN"})
    public void testAD01_AdministradorGerenciaUsuario_EmailUnico() throws Exception {
        // Simula o envio de um formulario POST de cadastro com a chave de seguranca CSRF
        mockMvc.perform(post("/admin/usuarios/salvar").with(csrf())
                .param("nome", "Novo Usuario")
                // Envia propositalmente um email que ja existe no banco para forcar o erro
                .param("username", "sec") 
                .param("password", "senha123")
                .param("role", "SECRETARIO"))
                // Como deu erro, o sistema fica na mesma pagina (Status 200 OK)
                .andExpect(status().isOk())
                // Confirma que a tela recarregada foi a de cadastro
                .andExpect(view().name("admin/usuario/cadastro"))
                // Confirma que o sistema apontou um erro de validacao no campo "username"
                .andExpect(model().attributeHasFieldErrors("usuario", "username"));
    }
}