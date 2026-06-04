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

    // Controla se o secretário deu o fechamento administrativo final (S.04)
    @Column(name = "encerrada_secretario", nullable = false)
    private boolean encerradaSecretario = false;

    // Controla se o professor finalizou a entrega das notas/atividades da turma
    @Column(name = "concluida_professor", nullable = false)
    private boolean concluidaProfessor = false;

    @ManyToOne
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private Usuario professorResponsavel;

    // Registra o momento exato em que a oferta foi salva no sistema
    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    // Grava o e-mail do secretário responsável por criar este registro
    @Column(name = "usuario_criador")
    private String usuarioCriador;

    // Getters and Setters
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

    // GETTERS E SETTERS DAS FLAGS
    public boolean isEncerradaSecretario() { return encerradaSecretario; }
    public void setEncerradaSecretario(boolean encerradaSecretario) { this.encerradaSecretario = encerradaSecretario; }

    public boolean isConcluidaProfessor() { return concluidaProfessor; }
    public void setConcluidaProfessor(boolean concluidaProfessor) { this.concluidaProfessor = concluidaProfessor; }

    // GETTERS E SETTERS DA S.01
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
    public String getUsuarioCriador() { return usuarioCriador; }
    public void setUsuarioCriador(String usuarioCriador) { this.usuarioCriador = usuarioCriador; }

    // Método auxiliar da S.03: Calcula o status dinamicamente
    // Este método calcula o estado real da oferta em tempo de execução.
    // Evita salvar strings fixas no banco, eliminando o risco de dados desatualizados.
    public String getStatusCalculado() {
        // 1. Se o secretário já fechou a oferta, ela está totalmente encerrada
        if (this.encerradaSecretario) {
            return "Concluída";
        }
        // 2. Se o professor terminou sua parte, fica esperando o encerramento do secretário
        if (this.concluidaProfessor) {
            return "Aguardando encerramento do secretário";
        }
        // 3. Se não foi fechada, mas a data limite já passou, o sistema acusa o atraso
        if (java.time.LocalDate.now().isAfter(this.getDataFim())) {
            return "Em atraso";
        }
        // 4. Se está dentro do prazo e sem pendências de fechamento, segue ativa
        return "Em andamento";
    }
}


