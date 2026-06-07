package br.ufscar.dc.dsw.sistema_pescd.dto.response;

import java.time.LocalDateTime;

public class RelatorioResponseDTO {

    private Long id;
    private Integer frequencia;
    private String arquivoPdfPath;
    private LocalDateTime dataEnvio;
    private String mensagem;

    public RelatorioResponseDTO() {}

    public RelatorioResponseDTO(Long id, Integer frequencia, String arquivoPdfPath,
                                LocalDateTime dataEnvio, String mensagem) {
        this.id = id;
        this.frequencia = frequencia;
        this.arquivoPdfPath = arquivoPdfPath;
        this.dataEnvio = dataEnvio;
        this.mensagem = mensagem;
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

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}