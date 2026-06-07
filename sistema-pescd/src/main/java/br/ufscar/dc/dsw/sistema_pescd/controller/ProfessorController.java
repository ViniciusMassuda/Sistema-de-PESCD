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

    //APROVAR PLANO

    @GetMapping("/aprovar-plano/{inscricaoId}")
    public String exibirTelaAprovarPlano(@PathVariable Long inscricaoId, Model model) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição inválida: " + inscricaoId));

        // ADAPTADO: usa getStatus() em vez de getStatusAluno()
        if (inscricao.getStatus() != StatusAluno.PLANO_ENVIADO) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }

        model.addAttribute("inscricao", inscricao);
        return "professor/aprovar-plano";
    }

    @PostMapping("/aprovar-plano")
    public String processarAprovarPlano(@RequestParam("inscricaoId") Long inscricaoId,
                                        @RequestParam("parecerPlano") String parecerPlano) {
        Inscricao inscricao = inscricaoDAO.findById(inscricaoId)
                .orElseThrow(() -> new IllegalArgumentException("Inscrição inválida: " + inscricaoId));

        // ADAPTADO: usa getStatus() em vez de getStatusAluno()
        if (inscricao.getStatus() != StatusAluno.PLANO_ENVIADO) {
            return "redirect:/professor/lista-alunos?erro=status_invalido";
        }

        inscricao.setParecerPlano(parecerPlano);
        // ADAPTADO: usa setStatus() em vez de setStatusAluno()
        inscricao.setStatus(StatusAluno.PLANO_APROVADO);
        inscricaoDAO.save(inscricao);

        return "redirect:/professor/lista-alunos?sucesso=plano_aprovado";
    }

    // PARA NÃO QUEBRAR descomenta quando integrar.

    /*
    @GetMapping("/aprovar-relatorio/{inscricaoId}")
    public String exibirTelaAprovarRelatorio(@PathVariable Long inscricaoId, Model model) {
        // Implementação do colega
        return "professor/aprovar-relatorio";
    }

    @PostMapping("/aprovar-relatorio")
    public String processarAprovarRelatorio(@RequestParam("inscricaoId") Long inscricaoId,
                                            @RequestParam("parecerRelatorio") String parecerRelatorio,
                                            @RequestParam("frequencia") Integer frequencia,
                                            @RequestParam("nota") String nota) {
        // Implementação do colega
        return "redirect:/professor/lista-alunos?sucesso=relatorio_aprovado";
    }

    @GetMapping("/concluir-relatorio/{inscricaoId}")
    public String exibirTelaConcluirRelatorio(@PathVariable Long inscricaoId, Model model) {
        // Implementação do colega
        return "professor/concluir-relatorio";
    }

    @PostMapping("/concluir-relatorio")
    public String processarConcluirRelatorio(@RequestParam("inscricaoId") Long inscricaoId,
                                             @RequestParam("parecerResponsavel") String parecerResponsavel,
                                             @RequestParam("frequenciaResponsavel") Integer frequenciaResponsavel,
                                             @RequestParam("notaResponsavel") String notaResponsavel) {
        // Implementação do colega
        return "redirect:/professor/lista-alunos?sucesso=relatorio_concluido";
    }

    @GetMapping("/avaliar-documentacao/{inscricaoId}")
    public String exibirTelaAvaliarDocumentacao(@PathVariable Long inscricaoId, Model model) {
        // Implementação do colega
        return "professor/avaliar-documentacao";
    }

    @PostMapping("/avaliar-documentacao")
    public String processarAvaliarDocumentacao(@RequestParam("inscricaoId") Long inscricaoId,
                                               @RequestParam("parecerResponsavel") String parecerResponsavel,
                                               @RequestParam("frequenciaResponsavel") Integer frequenciaResponsavel,
                                               @RequestParam("notaResponsavel") String notaResponsavel) {
        // Implementação do colega
        return "redirect:/professor/lista-alunos?sucesso=documentacao_avaliada";
    }
    */
}