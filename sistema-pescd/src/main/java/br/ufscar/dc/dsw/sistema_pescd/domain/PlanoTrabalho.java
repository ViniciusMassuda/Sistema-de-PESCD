package br.ufscar.dc.dsw.sistema_pescd.domain;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Entity
@Table(name = "plano_trabalho")
public class PlanoTrabalho {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String codigoDisciplina;
    @NotBlank
    @Column(nullable = false)
    private String nomeDisciplina;
    @NotBlank
    @Column(nullable = false)
    private String cursoDisciplina;
    @ManyToOne
    @JoinColumn(name = "professor_supervisor_id", nullable = false)
    private Usuario professorSupervisor;
    @NotBlank
    @Column(nullable = false)
    private String arquivoPdfPath;
    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataEnvio;
    @Column(length = 1000)
    private String parecer;
    private LocalDateTime dataParecer;
    public PlanoTrabalho() {}
    public PlanoTrabalho(String cd, String nd, String crd, Usuario ps, String ap) {
        this.codigoDisciplina = cd; this.nomeDisciplina = nd; this.cursoDisciplina = crd;
        this.professorSupervisor = ps; this.arquivoPdfPath = ap; this.dataEnvio = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCodigoDisciplina() { return codigoDisciplina; }
    public void setCodigoDisciplina(String codigoDisciplina) { this.codigoDisciplina = codigoDisciplina; }
    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }
    public String getCursoDisciplina() { return cursoDisciplina; }
    public void setCursoDisciplina(String cursoDisciplina) { this.cursoDisciplina = cursoDisciplina; }
    public Usuario getProfessorSupervisor() { return professorSupervisor; }
    public void setProfessorSupervisor(Usuario professorSupervisor) { this.professorSupervisor = professorSupervisor; }
    public String getArquivoPdfPath() { return arquivoPdfPath; }
    public void setArquivoPdfPath(String arquivoPdfPath) { this.arquivoPdfPath = arquivoPdfPath; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
    public String getParecer() { return parecer; }
    public void setParecer(String parecer) { this.parecer = parecer; }
    public LocalDateTime getDataParecer() { return dataParecer; }
    public void setDataParecer(LocalDateTime dataParecer) { this.dataParecer = dataParecer; }
}
