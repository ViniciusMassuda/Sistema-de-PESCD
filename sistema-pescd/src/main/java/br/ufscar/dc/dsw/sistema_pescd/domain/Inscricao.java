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

    // NOVOS CAMPOS PARA AL.02
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAluno statusAluno = StatusAluno.NAO_ENVIADO;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plano_trabalho_id")
    private PlanoTrabalho planoTrabalho;

    private LocalDateTime dataEnvioPlano;
    private LocalDateTime dataAprovacaoPlano;

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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }

    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }

    public StatusAluno getStatusAluno() { return statusAluno; }
    public void setStatusAluno(StatusAluno statusAluno) { this.statusAluno = statusAluno; }

    public PlanoTrabalho getPlanoTrabalho() { return planoTrabalho; }
    public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) { this.planoTrabalho = planoTrabalho; }

    public LocalDateTime getDataEnvioPlano() { return dataEnvioPlano; }
    public void setDataEnvioPlano(LocalDateTime dataEnvioPlano) { this.dataEnvioPlano = dataEnvioPlano; }

    public LocalDateTime getDataAprovacaoPlano() { return dataAprovacaoPlano; }
    public void setDataAprovacaoPlano(LocalDateTime dataAprovacaoPlano) { this.dataAprovacaoPlano = dataAprovacaoPlano; }
}