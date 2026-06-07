package br.ufscar.dc.dsw.sistema_pescd.dto.response;

import java.time.LocalDateTime;

public class DocumentacaoResponseDTO {

    private Long id;
    private String instituicao;
    private String nomeDisciplina;
    private String cursoDisciplina;
    private Integer cargaHoraria;
    private String arquivoPdfPath;
    private LocalDateTime dataEnvio;
    private String mensagem;

    public DocumentacaoResponseDTO() {}

    public DocumentacaoResponseDTO(Long id, String instituicao, String nomeDisciplina,
                                   String cursoDisciplina, Integer cargaHoraria,
                                   String arquivoPdfPath, LocalDateTime dataEnvio,
                                   String mensagem) {
        this.id = id;
        this.instituicao = instituicao;
        this.nomeDisciplina = nomeDisciplina;
        this.cursoDisciplina = cursoDisciplina;
        this.cargaHoraria = cargaHoraria;
        this.arquivoPdfPath = arquivoPdfPath;
        this.dataEnvio = dataEnvio;
        this.mensagem = mensagem;
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

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}