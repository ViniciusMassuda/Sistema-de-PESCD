package br.ufscar.dc.dsw.sistema_pescd.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Inscricao")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relaciona a inscrição a um usuário específico que possui o papel de Aluno
    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Usuario aluno;

    // Relaciona a inscrição à oferta de disciplina correspondente
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

    private LocalDateTime dataEnvioPlano;
    private LocalDateTime dataAprovacaoPlano;

    // ADICIONADO PARA A S.03 (RN-2): Armazena o status do andamento do aluno
    //@Column(name = "status", nullable = false)
    //private String status = "não enviado"; // Inicia com o padrão exigido

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public PlanoTrabalho getPlanoTrabalho() { return planoTrabalho; }
    public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) { this.planoTrabalho = planoTrabalho; }

    public DocumentacaoComprobatoria getDocumentacaoComprobatoria() { return documentacaoComprobatoria; }
    public void setDocumentacaoComprobatoria(DocumentacaoComprobatoria documentacaoComprobatoria) {
        this.documentacaoComprobatoria = documentacaoComprobatoria;
    }

    public LocalDateTime getDataEnvioPlano() { return dataEnvioPlano; }
    public void setDataEnvioPlano(LocalDateTime dataEnvioPlano) { this.dataEnvioPlano = dataEnvioPlano; }

    public LocalDateTime getDataAprovacaoPlano() { return dataAprovacaoPlano; }
    public void setDataAprovacaoPlano(LocalDateTime dataAprovacaoPlano) { this.dataAprovacaoPlano = dataAprovacaoPlano; }

    // Getters e Setters do Status
    // Alterado para ENUM para maior segurança de tipos
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAluno status = StatusAluno.NAO_ENVIADO;


    // Getters e Setters do Status
    public StatusAluno getStatus() { return status; }
    public void setStatus(StatusAluno status) { this.status = status; }

    public RelatorioFinal getRelatorioFinal() { return relatorioFinal; }
    public void setRelatorioFinal(RelatorioFinal relatorioFinal) { this.relatorioFinal = relatorioFinal; }

    public String getParecerPlano() { return parecerPlano; }
    public void setParecerPlano(String parecerPlano) { this.parecerPlano = parecerPlano; }
    // Enum para status do aluno
    public enum StatusAluno {
        NAO_ENVIADO("não enviado"),
        PLANO_ENVIADO("plano enviado"),
        PLANO_APROVADO("plano aprovado"),
        DOCUMENTACAO_ENVIADA("documentação enviada"),
        RELATORIO_ENVIADO("relatório enviado"),
        RELATORIO_APROVADO_SUPERVISOR("relatório aprovado pelo supervisor"),
        CONCLUIDO_RESPONSAVEL("concluído pelo responsável");

        private final String descricao;

        StatusAluno(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}


