package br.ufscar.dc.dsw.sistema_pescd.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorio_final")
public class RelatorioFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(nullable = false)
    private Integer frequencia;

    @NotBlank
    @Column(nullable = false)
    private String arquivoPdfPath;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dataEnvio;

    // Construtores
    public RelatorioFinal() {}

    public RelatorioFinal(Integer frequencia, String arquivoPdfPath) {
        this.frequencia = frequencia;
        this.arquivoPdfPath = arquivoPdfPath;
        this.dataEnvio = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getFrequencia() { return frequencia; }
    public void setFrequencia(Integer frequencia) { this.frequencia = frequencia; }

    public String getArquivoPdfPath() { return arquivoPdfPath; }
    public void setArquivoPdfPath(String arquivoPdfPath) { this.arquivoPdfPath = arquivoPdfPath; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }
}