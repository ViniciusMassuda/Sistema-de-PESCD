package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador responsável pelas requisições da área do professor.
 *
 * @Controller - Indica que esta classe é um controlador Spring MVC
 * @RequestMapping("/professor") - Define a URL base /professor para todos os endpoints
 *
 * Todas as rotas mapeadas neste controlador são acessíveis apenas
 * por usuários com papel ROLE_PROFESSOR (configurado no Spring Security).
 */

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private InscricaoDAO inscricaoDAO;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private OfertaDAO ofertaDAO;

    @GetMapping("/lista-alunos")
    public String listaAlunos(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        List<Inscricao> vinculados = inscricaoDAO.findByProfessorVinculado(professor.getId());
        model.addAttribute("inscricoes", vinculados);
        model.addAttribute("professorLogadoId", professor.getId());
        // PR.03: ofertas do responsável para botão de encerramento
        model.addAttribute("ofertasResponsavel", ofertaDAO.findByProfessorResponsavelId(professor.getId()));
        return "professor/lista-alunos";
    }

    @GetMapping("/aprovar-plano/{inscricaoId}")
    public String exibirTelaAprovarPlano(@PathVariable Long inscricaoId,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        
        // rng-5: impede aprovacao se oferta ja concluida
        if (inscricao.getOferta().isEncerradaSecretario()) {
            return "redirect:/professor/lista-alunos?erro=oferta_concluida";
        }

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
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
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5: trava para oferta encerrada
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=acesso_negado";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!inscricao.getPlanoTrabalho().getProfessorSupervisor().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
        inscricao.setParecerPlano(parecerPlano);
        inscricao.setStatus(StatusAluno.PLANO_APROVADO);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=plano_aprovado";
    }

    @GetMapping("/aprovar-relatorio/{inscricaoId}")
    public String exibirTelaAprovarRelatorio(@PathVariable Long inscricaoId, 
                                            @AuthenticationPrincipal UserDetails userDetails,
                                            Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=oferta_concluida";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!inscricao.getPlanoTrabalho().getProfessorSupervisor().getId().equals(professor.getId())) {
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
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=acesso_negado";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!inscricao.getPlanoTrabalho().getProfessorSupervisor().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
        inscricao.setParecerRelatorioSupervisor(parecerRelatorio);
        inscricao.setFrequenciaSupervisor(frequencia);
        inscricao.setNotaSupervisor(nota);
        inscricao.setStatus(StatusAluno.RELATORIO_APROVADO_SUPERVISOR);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=relatorio_aprovado";
    }

    @GetMapping("/concluir-relatorio/{inscricaoId}")
    public String exibirTelaConcluirRelatorio(@PathVariable Long inscricaoId, 
                                             @AuthenticationPrincipal UserDetails userDetails,
                                             Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=oferta_concluida";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
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
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=acesso_negado";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!inscricao.getOferta().getProfessorResponsavel().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
        inscricao.setParecerRelatorioResponsavel(parecerResponsavel);
        inscricao.setFrequenciaResponsavel(frequenciaResponsavel);
        inscricao.setNotaResponsavel(notaResponsavel);
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=relatorio_concluido";
    }

    @GetMapping("/avaliar-documentacao/{inscricaoId}")
    public String exibirTelaAvaliarDocumentacao(@PathVariable Long inscricaoId, 
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=oferta_concluida";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
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
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        // rng-5
        if (inscricao.getOferta().isEncerradaSecretario()) return "redirect:/professor/lista-alunos?erro=acesso_negado";

        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!inscricao.getOferta().getProfessorResponsavel().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
        inscricao.setParecerRelatorioResponsavel(parecerResponsavel);
        inscricao.setFrequenciaResponsavel(frequenciaResponsavel);
        inscricao.setNotaResponsavel(notaResponsavel);
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=documentacao_avaliada";
    }

    // PR.03 – Encerrar Oferta

    // PR.03: valida pré-condições e calcula estatísticas para confirmação de encerramento
    @GetMapping("/encerrar-oferta/{ofertaId}")
    public String exibirTelaEncerrarOferta(@PathVariable Long ofertaId,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           Model model) {
        Oferta oferta = ofertaDAO.findById(ofertaId).orElseThrow();

        if (oferta.isEncerradaSecretario()) {
            return "redirect:/professor/lista-alunos?erro=oferta_ja_encerrada";
        }
        if (oferta.isConcluidaProfessor()) {
            return "redirect:/professor/lista-alunos?erro=oferta_ja_concluida_professor";
        }
        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!oferta.getProfessorResponsavel().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=nao_responsavel";
        }
        // PR.03: todos os alunos devem estar CONCLUIDO_RESPONSAVEL
        List<Inscricao> inscricoes = inscricaoDAO.findByOfertaId(ofertaId);
        boolean todosConluidos = inscricoes.stream()
                .allMatch(i -> i.getStatus() == StatusAluno.CONCLUIDO_RESPONSAVEL);
        if (!todosConluidos) {
            return "redirect:/professor/lista-alunos?erro=alunos_pendentes";
        }

        // PR.03: estatísticas consolidadas
        double mediaFrequencia = inscricoes.stream()
                .filter(i -> i.getFrequenciaResponsavel() != null)
                .mapToInt(Inscricao::getFrequenciaResponsavel)
                .average()
                .orElse(0.0);

        Map<String, Long> contagemNotas = new HashMap<>();
        for (String nota : List.of("A", "B", "C", "D", "E")) {
            long count = inscricoes.stream()
                    .filter(i -> nota.equals(i.getNotaResponsavel()))
                    .count();
            contagemNotas.put(nota, count);
        }
        long viaEstagio = inscricoes.stream()
                .filter(i -> i.getPlanoTrabalho() != null)
                .count();
        long viaDocumentacao = inscricoes.stream()
                .filter(i -> i.getDocumentacaoComprobatoria() != null)
                .count();

        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricoes", inscricoes);
        model.addAttribute("mediaFrequencia", String.format("%.1f", mediaFrequencia));
        model.addAttribute("contagemNotas", contagemNotas);
        model.addAttribute("viaEstagio", viaEstagio);
        model.addAttribute("viaDocumentacao", viaDocumentacao);
        model.addAttribute("totalAlunos", inscricoes.size());

        return "professor/encerrar-oferta";
    }

    // PR.03: persiste lições aprendidas e marca oferta como concluída pelo professor
    @PostMapping("/encerrar-oferta")
    public String processarEncerrarOferta(@RequestParam("ofertaId") Long ofertaId,
                                          @RequestParam("licoesAprendidas") String licoesAprendidas,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        Oferta oferta = ofertaDAO.findById(ofertaId).orElseThrow();

        if (oferta.isEncerradaSecretario() || oferta.isConcluidaProfessor()) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
        Usuario professor = usuarioDAO.findByUsername(userDetails.getUsername()).orElseThrow();
        if (!oferta.getProfessorResponsavel().getId().equals(professor.getId())) {
            return "redirect:/professor/lista-alunos?erro=acesso_negado";
        }
        // PR.03: revalida status antes de persistir
        List<Inscricao> inscricoes = inscricaoDAO.findByOfertaId(ofertaId);
        boolean todosConcluidos = inscricoes.stream()
                .allMatch(i -> i.getStatus() == StatusAluno.CONCLUIDO_RESPONSAVEL);
        if (!todosConcluidos) {
            return "redirect:/professor/lista-alunos?erro=alunos_pendentes";
        }

        oferta.setLicoesAprendidas(licoesAprendidas);
        oferta.setConcluidaProfessor(true);
        oferta.setDataConcluidaProfessor(LocalDateTime.now());
        ofertaDAO.save(oferta);

        return "redirect:/professor/lista-alunos?sucesso=oferta_encerrada";
    }
}
