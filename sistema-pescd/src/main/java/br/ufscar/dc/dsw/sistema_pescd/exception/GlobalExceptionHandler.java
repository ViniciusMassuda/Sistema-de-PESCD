//package br.ufscar.dc.dsw.sistema_pescd.exception;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.multipart.MaxUploadSizeExceededException;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import org.springframework.web.servlet.view.RedirectView;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(MaxUploadSizeExceededException.class)
//    public String handleMaxSizeException(MaxUploadSizeExceededException exc,
//                                         HttpServletRequest request,
//                                         RedirectAttributes redirectAttributes) {
//        redirectAttributes.addFlashAttribute("error",
//                "❌ Arquivo muito grande! O tamanho máximo permitido é 5MB.");
//        return "redirect:/aluno/ofertas";
//    }
//}