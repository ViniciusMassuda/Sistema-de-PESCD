package br.ufscar.dc.dsw.sistema_pescd.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public RedirectView handleMaxSizeException(MaxUploadSizeExceededException exc,
                                               HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {

        // Define a mensagem de erro que o Thymeleaf vai ler
        redirectAttributes.addFlashAttribute("error",
                "Arquivo muito grande! O tamanho máximo permitido é 5MB.");

        String uri = request.getRequestURI(); // Ex: "/aluno/oferta/1/plano"

        try {
            // Verifica se a URI segue o padrão esperado e extrai o ID via Regex ou Split
            if (uri != null && uri.contains("/aluno/oferta/")) {
                // Quebra a URI pelas barras
                String[] partes = uri.split("/");

                // Na URL "/aluno/oferta/{id}/plano", o {id} estará após "oferta"
                for (int i = 0; i < partes.length; i++) {
                    if (partes[i].equals("oferta") && (i + 1) < partes.length) {
                        String ofertaId = partes[i + 1];
                        // Redireciona com sucesso de volta para o formulário específico
                        return new RedirectView("/aluno/oferta/" + ofertaId + "/plano", true);
                    }
                }
            }
        } catch (Exception e) {
            // Fallback silencioso caso algo dê errado no parse
        }

        // Se falhar em descobrir o ID, manda para a listagem geral como segurança
        return new RedirectView("/aluno/ofertas", true);
    }
}