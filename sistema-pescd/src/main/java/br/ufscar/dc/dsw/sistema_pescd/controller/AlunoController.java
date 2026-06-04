package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.PlanoTrabalhoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private IAlunoService alunoService;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private OfertaDAO ofertaDAO;

    @GetMapping("/ofertas")
    public String listarOfertas(@AuthenticationPrincipal UserDetails userDetails,
                                Model model,
                                HttpSession session) {
        Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        session.setAttribute("usuarioNome", aluno.getNome());

        List<OfertaAlunoResponseDTO> ofertas = alunoService.buscarOfertasPorAluno(aluno);

        if (ofertas.isEmpty()) {
            model.addAttribute("semOfertas", true);
        } else {
            model.addAttribute("ofertas", ofertas);
            model.addAttribute("semOfertas", false);
        }

        return "aluno/ofertas";
    }

    // NOVO: Exibir formulário de envio de plano
    @GetMapping("/oferta/{id}/plano")
    public String mostrarFormularioPlano(@PathVariable Long id,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model,
                                         RedirectAttributes redirectAttributes) {
        Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verificar se pode enviar plano
        if (!alunoService.podeEnviarPlano(id, aluno)) {
            redirectAttributes.addFlashAttribute("error",
                    "Não é possível enviar plano para esta oferta. Verifique se a oferta está em andamento e se você ainda não enviou o plano.");
            return "redirect:/aluno/ofertas";
        }

        Oferta oferta = ofertaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta não encontrada"));

        // Buscar lista de professores para o dropdown
        List<Usuario> professores = usuarioDAO.findByRole(Usuario.Role.PROFESSOR);

        model.addAttribute("ofertaId", id);
        model.addAttribute("ofertaNome", oferta.getNome());
        model.addAttribute("professores", professores);
        model.addAttribute("planoRequest", new PlanoTrabalhoRequestDTO());

        return "aluno/enviar-plano";
    }

    // NOVO: Processar envio do plano
//    @PostMapping("/oferta/{id}/plano")
//    public String enviarPlano(@PathVariable Long id,
//                              @Valid @ModelAttribute("planoRequest") PlanoTrabalhoRequestDTO request,
//                              @AuthenticationPrincipal UserDetails userDetails,
//                              RedirectAttributes redirectAttributes) {
//        try {
//            Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
//                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
//
//            PlanoTrabalhoResponseDTO response = alunoService.enviarPlanoTrabalho(id, aluno, request);
//
//            redirectAttributes.addFlashAttribute("success", response.getMensagem());
//
//        } catch (Exception e) {
//            redirectAttributes.addFlashAttribute("error", "Erro ao enviar plano: " + e.getMessage());
//            return "redirect:/aluno/oferta/" + id + "/plano";
//        }
//
//        return "redirect:/aluno/ofertas";
//    }
    @PostMapping("/oferta/{id}/plano")
    public String enviarPlano(@PathVariable Long id,
                              @Valid @ModelAttribute("planoRequest") PlanoTrabalhoRequestDTO request,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            PlanoTrabalhoResponseDTO response = alunoService.enviarPlanoTrabalho(id, aluno, request);

            redirectAttributes.addFlashAttribute("success", response.getMensagem());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao enviar plano: " + e.getMessage());
            return "redirect:/aluno/oferta/" + id + "/plano";
        }

        return "redirect:/aluno/ofertas";
    }
}