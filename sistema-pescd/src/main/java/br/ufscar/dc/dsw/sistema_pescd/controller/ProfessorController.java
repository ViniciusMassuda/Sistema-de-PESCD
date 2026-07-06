package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.AcaoInscricaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EncerrarOfertaRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EstatisticasOfertaResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IInscricaoService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IOfertaService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IProfessorService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IUsuarioService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final IProfessorService professorService;
    private final IInscricaoService inscricaoService;
    private final IOfertaService ofertaService;
    private final IUsuarioService usuarioService;

    @GetMapping("/lista-alunos")
    public String listaAlunos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario professor = usuarioService.buscarPorUsername(userDetails.getUsername());
        List<Inscricao> vinculados = inscricaoService.buscarPorProfessorVinculado(professor.getId());
        model.addAttribute("inscricoes", vinculados);
        model.addAttribute("professorLogadoId", professor.getId());
        model.addAttribute("ofertasResponsavel", ofertaService.buscarPorProfessorResponsavel(professor.getId()));
        return "professor/lista-alunos";
    }

    @GetMapping("/aprovar-plano/{inscricaoId}")
    public String exibirTelaAprovarPlano(@PathVariable Long inscricaoId,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         Model model) {
        Inscricao inscricao = inscricaoService.buscarPorId(inscricaoId);
        if (inscricao == null) {
            return "redirect:/professor/lista-alunos?erro=inscricao_nao_encontrada";
        }
        
        if (inscricao.getOferta().isEncerradaSecretario()) {
            return "redirect:/professor/lista-alunos?erro=oferta_concluida";
        }

        Usuario professor = usuarioService.buscarPorUsername(userDetails.getUsername());
        if (inscricao.getPlanoTrabalho() == null || 
            !inscricao.getPlanoTrabalho().getProfessorSupervisor().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=nao_supervisor";
        }
        if (inscricao.getStatus() != StatusAluno.PLANO_ENVIADO) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }
        model.addAttribute("inscricao", inscricao);
        return "professor/aprovar-plano";
    }

    @PostMapping("/aprovar-plano")
    public String processarAprovarPlano(@RequestParam("inscricaoId") Long inscricaoId,
                                        @RequestParam("parecerPlano") String parecerPlano,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AcaoInscricaoRequestDTO request = new AcaoInscricaoRequestDTO();
            request.setInscricaoId(inscricaoId);
            request.setParecer(parecerPlano);
            professorService.aprovarPlano(request, userDetails.getUsername());
            return "redirect:/professor/lista-alunos?sucesso=plano_aprovado";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
    }

    @GetMapping("/aprovar-relatorio/{inscricaoId}")
    public String exibirTelaAprovarRelatorio(@PathVariable Long inscricaoId, 
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             Model model) {
        Inscricao inscricao = inscricaoService.buscarPorId(inscricaoId);
        if (inscricao == null) {
            return "redirect:/professor/lista-alunos?erro=inscricao_nao_encontrada";
        }
        if (inscricao.getOferta().isEncerradaSecretario()) {
            return "redirect:/professor/lista-alunos?erro=oferta_concluida";
        }

        Usuario professor = usuarioService.buscarPorUsername(userDetails.getUsername());
        if (inscricao.getPlanoTrabalho() == null ||
            !inscricao.getPlanoTrabalho().getProfessorSupervisor().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=nao_supervisor";
        }
        if (inscricao.getStatus() != StatusAluno.RELATORIO_ENVIADO) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }
        model.addAttribute("inscricao", inscricao);
        return "professor/aprovar-relatorio";
    }

    @PostMapping("/aprovar-relatorio")
    public String processarAprovarRelatorio(@RequestParam("inscricaoId") Long inscricaoId,
                                            @RequestParam("parecerRelatorio") String parecerRelatorio,
                                            @RequestParam("frequencia") Integer frequencia,
                                            @RequestParam("nota") String nota,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AcaoInscricaoRequestDTO request = new AcaoInscricaoRequestDTO();
            request.setInscricaoId(inscricaoId);
            request.setParecer(parecerRelatorio);
            request.setFrequencia(frequencia);
            request.setNota(nota);
            professorService.aprovarRelatorio(request, userDetails.getUsername());
            return "redirect:/professor/lista-alunos?sucesso=relatorio_aprovado";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
    }

    @GetMapping("/concluir-relatorio/{inscricaoId}")
    public String exibirTelaConcluirRelatorio(@PathVariable Long inscricaoId, 
                                              @AuthenticationPrincipal UserDetails userDetails,
                                              Model model) {
        Inscricao inscricao = inscricaoService.buscarPorId(inscricaoId);
        if (inscricao == null) {
            return "redirect:/professor/lista-alunos?erro=inscricao_nao_encontrada";
        }
        if (inscricao.getOferta().isEncerradaSecretario()) {
            return "redirect:/professor/lista-alunos?erro=oferta_concluida";
        }

        Usuario professor = usuarioService.buscarPorUsername(userDetails.getUsername());
        if (!inscricao.getOferta().getProfessorResponsavel().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=nao_responsavel";
        }
        if (inscricao.getStatus() != StatusAluno.RELATORIO_APROVADO_SUPERVISOR) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }
        model.addAttribute("inscricao", inscricao);
        return "professor/concluir-relatorio";
    }

    @PostMapping("/concluir-relatorio")
    public String processarConcluirRelatorio(@RequestParam("inscricaoId") Long inscricaoId,
                                             @RequestParam("parecerResponsavel") String parecerResponsavel,
                                             @RequestParam("frequenciaResponsavel") Integer frequenciaResponsavel,
                                             @RequestParam("notaResponsavel") String notaResponsavel,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AcaoInscricaoRequestDTO request = new AcaoInscricaoRequestDTO();
            request.setInscricaoId(inscricaoId);
            request.setParecer(parecerResponsavel);
            request.setFrequencia(frequenciaResponsavel);
            request.setNota(notaResponsavel);
            professorService.concluirRelatorio(request, userDetails.getUsername());
            return "redirect:/professor/lista-alunos?sucesso=relatorio_concluido";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
    }

    @GetMapping("/avaliar-documentacao/{inscricaoId}")
    public String exibirTelaAvaliarDocumentacao(@PathVariable Long inscricaoId, 
                                                @AuthenticationPrincipal UserDetails userDetails,
                                                Model model) {
        Inscricao inscricao = inscricaoService.buscarPorId(inscricaoId);
        if (inscricao == null) {
            return "redirect:/professor/lista-alunos?erro=inscricao_nao_encontrada";
        }
        if (inscricao.getOferta().isEncerradaSecretario()) {
            return "redirect:/professor/lista-alunos?erro=oferta_concluida";
        }

        Usuario professor = usuarioService.buscarPorUsername(userDetails.getUsername());
        if (!inscricao.getOferta().getProfessorResponsavel().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=nao_responsavel";
        }
        if (inscricao.getStatus() != StatusAluno.DOCUMENTACAO_ENVIADA) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }
        model.addAttribute("inscricao", inscricao);
        return "professor/avaliar-documentacao";
    }

    @PostMapping("/avaliar-documentacao")
    public String processarAvaliarDocumentacao(@RequestParam("inscricaoId") Long inscricaoId,
                                               @RequestParam("parecerResponsavel") String parecerResponsavel,
                                               @RequestParam("frequenciaResponsavel") Integer frequenciaResponsavel,
                                               @RequestParam("notaResponsavel") String notaResponsavel,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        try {
            AcaoInscricaoRequestDTO request = new AcaoInscricaoRequestDTO();
            request.setInscricaoId(inscricaoId);
            request.setParecer(parecerResponsavel);
            request.setFrequencia(frequenciaResponsavel);
            request.setNota(notaResponsavel);
            professorService.avaliarDocumentacao(request, userDetails.getUsername());
            return "redirect:/professor/lista-alunos?sucesso=documentacao_avaliada";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
    }

    // PR.03 – Encerrar Oferta

    // PR.03: valida pré-condições e calcula estatísticas para confirmação de encerramento
    @GetMapping("/encerrar-oferta/{ofertaId}")
    public String exibirTelaEncerrarOferta(@PathVariable Long ofertaId,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           Model model) {
        try {
            EstatisticasOfertaResponseDTO estatisticas = professorService.buscarEstatisticasOferta(ofertaId, userDetails.getUsername());
            
            List<Inscricao> inscricoes = inscricaoService.buscarPorOferta(ofertaService.buscarPorId(ofertaId));
            Oferta oferta = ofertaService.buscarPorId(ofertaId);
            
            model.addAttribute("oferta", oferta);
            model.addAttribute("inscricoes", inscricoes);
            model.addAttribute("mediaFrequencia", String.format(Locale.US, "%.1f", estatisticas.getMediaFrequencia()));
            model.addAttribute("contagemNotas", estatisticas.getContagemNotas());
            model.addAttribute("viaEstagio", estatisticas.getCreditosViaEstagio());
            model.addAttribute("viaDocumentacao", estatisticas.getCreditosViaDocumentacao());
            model.addAttribute("totalAlunos", estatisticas.getTotalAlunos());

            return "professor/encerrar-oferta";
        } catch (IllegalStateException | IllegalArgumentException e) {
            String erro = "acesso_negado";
            if (e.getMessage().contains("encerrada pelo secretário")) erro = "oferta_ja_encerrada";
            else if (e.getMessage().contains("concluída pelo professor")) erro = "oferta_ja_concluida_professor";
            else if (e.getMessage().contains("responsável")) erro = "nao_responsavel";
            else if (e.getMessage().contains("status CONCLUIDO_RESPONSAVEL")) erro = "alunos_pendentes";
            return "redirect:/professor/lista-alunos?erro=" + erro;
        }
    }

    // PR.03: persiste lições aprendidas e marca oferta como concluída pelo professor
    @PostMapping("/encerrar-oferta")
    public String processarEncerrarOferta(@RequestParam("ofertaId") Long ofertaId,
                                          @RequestParam("licoesAprendidas") String licoesAprendidas,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            EncerrarOfertaRequestDTO req = new EncerrarOfertaRequestDTO();
            req.setOfertaId(ofertaId);
            req.setDescricaoLicoesAprendidas(licoesAprendidas);
            professorService.encerrarOferta(req, userDetails.getUsername());
            return "redirect:/professor/lista-alunos?sucesso=oferta_encerrada";
        } catch (IllegalStateException | IllegalArgumentException e) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
    }
}
