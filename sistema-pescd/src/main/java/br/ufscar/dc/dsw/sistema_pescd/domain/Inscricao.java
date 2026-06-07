package br.ufscar.dc.dsw.sistema_pescd.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Inscricao")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno;

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private Oferta oferta;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plano_trabalho_id")
    private PlanoTrabalho planoTrabalho;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "documentacao_id")
    private DocumentacaoComprobatoria documentacaoComprobatoria;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "relatorio_id")
    private RelatorioFinal relatorioFinal;

    @Column(name = "parecer_plano", columnDefinition = "TEXT")
    private String parecerPlano;

    @Column(name = "parecer_relatorio_supervisor", columnDefinition = "TEXT")
    private String parecerRelatorioSupervisor;

    @Column(name = "frequencia_supervisor")
    private Integer frequenciaSupervisor;

    @Column(name = "nota_supervisor", length = 1)
    private String notaSupervisor;

    @Column(name = "parecer_relatorio_responsavel", columnDefinition = "TEXT")
    private String parecerRelatorioResponsavel;

    @Column(name = "frequencia_responsavel")
    private Integer frequenciaResponsavel;

    @Column(name = "nota_responsavel", length = 1)
    private String notaResponsavel;

    private LocalDateTime dataEnvioPlano;
    private LocalDateTime dataAprovacaoPlano;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAluno status = StatusAluno.NAO_ENVIADO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
    public PlanoTrabalho getPlanoTrabalho() { return planoTrabalho; }
    public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) { this.planoTrabalho = planoTrabalho; }
    public DocumentacaoComprobatoria getDocumentacaoComprobatoria() { return documentacaoComprobatoria; }
    public void setDocumentacaoComprobatoria(DocumentacaoComprobatoria documentacaoComprobatoria) { this.documentacaoComprobatoria = documentacaoComprobatoria; }
    public LocalDateTime getDataEnvioPlano() { return dataEnvioPlano; }
    public void setDataEnvioPlano(LocalDateTime dataEnvioPlano) { this.dataEnvioPlano = dataEnvioPlano; }
    public LocalDateTime getDataAprovacaoPlano() { return dataAprovacaoPlano; }
    public void setDataAprovacaoPlano(LocalDateTime dataAprovacaoPlano) { this.dataAprovacaoPlano = dataAprovacaoPlano; }
    public StatusAluno getStatus() { return status; }
    public void setStatus(StatusAluno status) { this.status = status; }
    public RelatorioFinal getRelatorioFinal() { return relatorioFinal; }
    public void setRelatorioFinal(RelatorioFinal relatorioFinal) { this.relatorioFinal = relatorioFinal; }
    public String getParecerPlano() { return parecerPlano; }
    public void setParecerPlano(String parecerPlano) { this.parecerPlano = parecerPlano; }
    public String getParecerRelatorioSupervisor() { return parecerRelatorioSupervisor; }
    public void setParecerRelatorioSupervisor(String parecerRelatorioSupervisor) { this.parecerRelatorioSupervisor = parecerRelatorioSupervisor; }
    public Integer getFrequenciaSupervisor() { return frequenciaSupervisor; }
    public void setFrequenciaSupervisor(Integer frequenciaSupervisor) { this.frequenciaSupervisor = frequenciaSupervisor; }
    public String getNotaSupervisor() { return notaSupervisor; }
    public void setNotaSupervisor(String notaSupervisor) { this.notaSupervisor = notaSupervisor; }
    public String getParecerRelatorioResponsavel() { return parecerRelatorioResponsavel; }
    public void setParecerRelatorioResponsavel(String parecerRelatorioResponsavel) { this.parecerRelatorioResponsavel = parecerRelatorioResponsavel; }
    public Integer getFrequenciaResponsavel() { return frequenciaResponsavel; }
    public void setFrequenciaResponsavel(Integer frequenciaResponsavel) { this.frequenciaResponsavel = frequenciaResponsavel; }
    public String getNotaResponsavel() { return notaResponsavel; }
    public void setNotaResponsavel(String notaResponsavel) { this.notaResponsavel = notaResponsavel; }
}
