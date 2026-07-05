package br.ufscar.dc.dsw.sistema_pescd.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO rico contendo dados consolidados da inscricao.
 */
@Getter
@Setter
@NoArgsConstructor
public class InscricaoDTO {

    private Long inscricaoId;
    private String status;

    // Aluno
    private String nomeAluno;
    private String emailAluno;

    // Oferta
    private Long ofertaId;
    private String nomeOferta;
    private String semestre;
    private String professorResponsavelNome;

    // Plano de Trabalho
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String cursoDisciplina;
    private String professorSupervisorNome;
    private String arquivoPlanoPath;
    private LocalDateTime dataEnvioPlano;
    private String parecerPlano;
    private LocalDateTime dataAprovacaoPlano;

    // Relatorio Final
    private Integer frequenciaRelatorio;
    private String arquivoRelatorioPath;
    private LocalDateTime dataEnvioRelatorio;

    // Parecer do Supervisor
    private String parecerRelatorioSupervisor;
    private Integer frequenciaSupervisor;
    private String notaSupervisor;

    // Parecer do Responsavel
    private String parecerRelatorioResponsavel;
    private Integer frequenciaResponsavel;
    private String notaResponsavel;

    // Documentacao Comprobatoria
    private String instituicao;
    private String nomeDisciplinaDocumentacao;
    private String cursoDisciplinaDocumentacao;
    private Integer cargaHoraria;
    private String arquivoDocumentacaoPath;
    private LocalDateTime dataEnvioDocumentacao;
}
