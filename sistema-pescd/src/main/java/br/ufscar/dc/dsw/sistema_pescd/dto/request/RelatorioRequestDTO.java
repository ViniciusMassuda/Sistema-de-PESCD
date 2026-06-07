package br.ufscar.dc.dsw.sistema_pescd.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class RelatorioRequestDTO {

    @NotNull(message = "{validation.frequencia.required}")
    @Min(value = 0, message = "{validation.frequencia.min}")
    @Max(value = 100, message = "{validation.frequencia.max}")
    private Integer frequencia;

    @NotNull(message = "{validation.arquivo.required}")
    private MultipartFile arquivo;

    // Getters e Setters
    public Integer getFrequencia() { return frequencia; }
    public void setFrequencia(Integer frequencia) { this.frequencia = frequencia; }

    public MultipartFile getArquivo() { return arquivo; }
    public void setArquivo(MultipartFile arquivo) { this.arquivo = arquivo; }
}