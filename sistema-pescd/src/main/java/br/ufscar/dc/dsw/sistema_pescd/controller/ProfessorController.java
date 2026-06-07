package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

    @Autowired
    private InscricaoDAO inscricaoDAO;

    @GetMapping("/lista-alunos")
    public String listaAlunos(Model model) {
        model.addAttribute("inscricoes", inscricaoDAO.findAll());
        return "professor/lista-alunos";
    }

    // ROTA DE ATALHO PARA TESTES
    @GetMapping("/debug/mudar-status/{id}/{novoStatus}")
    public String debugMudarStatus(@PathVariable Long id, @PathVariable StatusAluno novoStatus) {
        Inscricao i = inscricaoDAO.findById(id).orElseThrow();
        i.setStatus(novoStatus);
        inscricaoDAO.save(i);
        return "redirect:/professor/lista-alunos?sucesso=status_alterado";
    }

    @GetMapping("/aprovar-plano/{inscricaoId}")
    public String exibirTelaAprovarPlano(@PathVariable Long inscricaoId, Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        if (inscricao.getStatus() != StatusAluno.PLANO_ENVIADO) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }
        model.addAttribute("inscricao", inscricao);
        return "professor/aprovar-plano";
    }

    @PostMapping("/aprovar-plano")
    public String processarAprovarPlano(@RequestParam("inscricaoId") Long inscricaoId,
                                        @RequestParam("parecerPlano") String parecerPlano) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        inscricao.setParecerPlano(parecerPlano);
        inscricao.setStatus(StatusAluno.PLANO_APROVADO);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=plano_aprovado";
    }

    @GetMapping("/aprovar-relatorio/{inscricaoId}")
    public String exibirTelaAprovarRelatorio(@PathVariable Long inscricaoId, Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
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
                                            @RequestParam("nota") String nota) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        inscricao.setParecerRelatorioSupervisor(parecerRelatorio);
        inscricao.setFrequenciaSupervisor(frequencia);
        inscricao.setNotaSupervisor(nota);
        inscricao.setStatus(StatusAluno.RELATORIO_APROVADO_SUPERVISOR);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=relatorio_aprovado";
    }

    @GetMapping("/concluir-relatorio/{inscricaoId}")
    public String exibirTelaConcluirRelatorio(@PathVariable Long inscricaoId, Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
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
                                             @RequestParam("notaResponsavel") String notaResponsavel) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        inscricao.setParecerRelatorioResponsavel(parecerResponsavel);
        inscricao.setFrequenciaResponsavel(frequenciaResponsavel);
        inscricao.setNotaResponsavel(notaResponsavel);
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=relatorio_concluido";
    }

    @GetMapping("/avaliar-documentacao/{inscricaoId}")
    public String exibirTelaAvaliarDocumentacao(@PathVariable Long inscricaoId, Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
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
                                               @RequestParam("notaResponsavel") String notaResponsavel) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId).orElseThrow();
        inscricao.setParecerRelatorioResponsavel(parecerResponsavel);
        inscricao.setFrequenciaResponsavel(frequenciaResponsavel);
        inscricao.setNotaResponsavel(notaResponsavel);
        inscricao.setStatus(StatusAluno.CONCLUIDO_RESPONSAVEL);
        inscricaoDAO.save(inscricao);
        return "redirect:/professor/lista-alunos?sucesso=documentacao_avaliada";
    }
}
