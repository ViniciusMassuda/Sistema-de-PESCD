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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.ufscar.dc.dsw.sistema_pescd.dto.request.DocumentacaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.DocumentacaoResponseDTO;

import br.ufscar.dc.dsw.sistema_pescd.dto.request.RelatorioRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.RelatorioResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.domain.PlanoTrabalho;
import java.util.List;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.PlanoTrabalho;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/aluno")
@RequiredArgsConstructor
public class AlunoController {

    private final IAlunoService alunoService;
    private final UsuarioDAO usuarioDAO;
    private final OfertaDAO ofertaDAO;
    private final InscricaoDAO inscricaoDAO;

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
            return "redirect:/aluno/ofertas";
        }

        return "redirect:/aluno/ofertas";
    }

    // AL.03 - Exibir formulário de envio de documentação
    @GetMapping("/oferta/{id}/documentacao")
    public String mostrarFormularioDocumentacao(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                Model model,
                                                RedirectAttributes redirectAttributes) {
        Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!alunoService.podeEnviarDocumentacao(id, aluno)) {
            redirectAttributes.addFlashAttribute("error",
                    "Não é possível enviar documentação para esta oferta.");
            return "redirect:/aluno/ofertas";
        }

        Oferta oferta = ofertaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta não encontrada"));

        model.addAttribute("ofertaId", id);
        model.addAttribute("ofertaNome", oferta.getNome());
        model.addAttribute("documentacaoRequest", new DocumentacaoRequestDTO());

        return "aluno/enviar-documentacao";
    }

    // AL.03 - Processar envio de documentação
    @PostMapping("/oferta/{id}/documentacao")
    public String enviarDocumentacao(@PathVariable Long id,
                                     @Valid @ModelAttribute("documentacaoRequest") DocumentacaoRequestDTO request,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     RedirectAttributes redirectAttributes) {
        try {
            Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            DocumentacaoResponseDTO response = alunoService.enviarDocumentacao(id, aluno, request);

            redirectAttributes.addFlashAttribute("success", response.getMensagem());

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/aluno/ofertas";
        }

        return "redirect:/aluno/ofertas";
    }

    // AL.04 - Exibir formulário de envio de relatório
    @GetMapping("/oferta/{id}/relatorio")
    public String mostrarFormularioRelatorio(@PathVariable Long id,
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             Model model,
                                             RedirectAttributes redirectAttributes) {
        Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (!alunoService.podeEnviarRelatorio(id, aluno)) {
            redirectAttributes.addFlashAttribute("error",
                    "Não é possível enviar relatório para esta oferta.");
            return "redirect:/aluno/ofertas";
        }

        Oferta oferta = ofertaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta não encontrada"));

        // Buscar inscrição para pegar o PlanoTrabalho
        Inscricao inscricao = inscricaoDAO.findByAlunoAndOferta(aluno, oferta)
                .orElseThrow(() -> new RuntimeException("Inscrição não encontrada"));

        PlanoTrabalho planoTrabalho = inscricao.getPlanoTrabalho();

        model.addAttribute("ofertaId", id);
        model.addAttribute("ofertaNome", oferta.getNome());
        model.addAttribute("planoTrabalho", planoTrabalho);
        model.addAttribute("relatorioRequest", new RelatorioRequestDTO());

        return "aluno/enviar-relatorio";
    }

    // AL.04 - Processar envio de relatório
    @PostMapping("/oferta/{id}/relatorio")
    public String enviarRelatorio(@PathVariable Long id,
                                  @Valid @ModelAttribute("relatorioRequest") RelatorioRequestDTO request,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        try {
            Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            RelatorioResponseDTO response = alunoService.enviarRelatorio(id, aluno, request);

            redirectAttributes.addFlashAttribute("success", response.getMensagem());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erro ao enviar relatório: " + e.getMessage());
            return "redirect:/aluno/ofertas";
        }

        return "redirect:/aluno/ofertas";
    }


}