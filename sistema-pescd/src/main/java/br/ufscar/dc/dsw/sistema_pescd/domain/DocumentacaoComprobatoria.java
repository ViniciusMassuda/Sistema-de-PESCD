package br.ufscar.dc.dsw.sistema_pescd.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentacao_comprobatoria")
public class DocumentacaoComprobatoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String instituicao;

    @NotBlank
    @Column(nullable = false)
    private String nomeDisciplina;

    @NotBlank
    @Column(nullable = false)
    private String cursoDisciplina;

    @NotNull
    @Column(nullable = false)
    private Integer cargaHoraria;

    @NotBlank
    @Column(nullable = false)
    private String arquivoPdfPath;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    // Construtores
    public DocumentacaoComprobatoria() {}

    public DocumentacaoComprobatoria(String instituicao, String nomeDisciplina,
                                     String cursoDisciplina, Integer cargaHoraria,
                                     String arquivoPdfPath) {
        this.instituicao = instituicao;
        this.nomeDisciplina = nomeDisciplina;
        this.cursoDisciplina = cursoDisciplina;
        this.cargaHoraria = cargaHoraria;
        this.arquivoPdfPath = arquivoPdfPath;
        this.dataEnvio = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    public String getCursoDisciplina() { return cursoDisciplina; }
    public void setCursoDisciplina(String cursoDisciplina) { this.cursoDisciplina = cursoDisciplina; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public String getArquivoPdfPath() { return arquivoPdfPath; }
    public void setArquivoPdfPath(String arquivoPdfPath) { this.arquivoPdfPath = arquivoPdfPath; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
}