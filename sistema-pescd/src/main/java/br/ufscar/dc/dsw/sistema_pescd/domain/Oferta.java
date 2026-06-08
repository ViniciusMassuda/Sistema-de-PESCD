package br.ufscar.dc.dsw.sistema_pescd.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "Oferta")
public class Oferta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nome;
    @NotBlank
    @Column(nullable = false)
    private String semestre;
    @NotNull
    @Column(nullable = false)
    private LocalDate dataInicio;
    @NotNull
    @Column(nullable = false)
    private LocalDate dataFim;
    @Column(name = "encerrada_secretario", nullable = false)
    private boolean encerradaSecretario = false;
    @Column(name = "concluida_professor", nullable = false)
    private boolean concluidaProfessor = false;
    @ManyToOne
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private Usuario professorResponsavel;
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;
    @Column(name = "usuario_criador")
    private String usuarioCriador;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }
    public Usuario getProfessorResponsavel() { return professorResponsavel; }
    public void setProfessorResponsavel(Usuario professorResponsavel) { this.professorResponsavel = professorResponsavel; }
    public boolean isEncerradaSecretario() { return encerradaSecretario; }
    public void setEncerradaSecretario(boolean encerradaSecretario) { this.encerradaSecretario = encerradaSecretario; }
    public boolean isConcluidaProfessor() { return concluidaProfessor; }
    public void setConcluidaProfessor(boolean concluidaProfessor) { this.concluidaProfessor = concluidaProfessor; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getUsuarioCriador() { return usuarioCriador; }
    public void setUsuarioCriador(String usuarioCriador) { this.usuarioCriador = usuarioCriador; }
    public String getStatusCalculado() {
        if (this.encerradaSecretario) return "Concluída";
        if (this.concluidaProfessor) return "Aguardando encerramento do secretário";
        if (java.time.LocalDate.now().isAfter(this.getDataFim())) return "Em atraso";
        return "Em andamento";
    }
}
