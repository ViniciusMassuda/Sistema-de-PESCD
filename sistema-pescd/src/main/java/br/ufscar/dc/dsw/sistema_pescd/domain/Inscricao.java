package br.ufscar.dc.dsw.sistema_pescd.domain;

import jakarta.persistence.*;

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

    // ADICIONADO PARA A S.03 (RN-2): Armazena o status do andamento do aluno
    @Column(name = "status", nullable = false)
    private String status = "não enviado"; // Inicia com o padrão exigido

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
    // Getters e Setters do Status
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}


