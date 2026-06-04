// Arquivo: CustomAuthenticationSuccessHandler.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        
        String redirectUrl = switch (role) {
            case "ROLE_ADMIN" -> "/admin/home";
            case "ROLE_SECRETARIO" -> "/secretario/home";
            case "ROLE_PROFESSOR" -> "/professor/home";
            case "ROLE_ALUNO" -> "/aluno/home";
            default -> "/";
        };
        
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}


