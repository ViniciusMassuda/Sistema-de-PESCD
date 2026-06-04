package br.ufscar.dc.dsw.sistema_pescd.dto.response;

import java.time.LocalDateTime;

public class PlanoTrabalhoResponseDTO {

    private Long id;
    private String codigoDisciplina;
    private String nomeDisciplina;
    private String cursoDisciplina;
    private String professorSupervisorNome;
    private String arquivoPdfPath;
    private LocalDateTime dataEnvio;
    private String mensagem;

    public PlanoTrabalhoResponseDTO() {}

    public PlanoTrabalhoResponseDTO(Long id, String codigoDisciplina, String nomeDisciplina,
                                    String cursoDisciplina, String professorSupervisorNome,
                                    String arquivoPdfPath, LocalDateTime dataEnvio, String mensagem) {
        this.id = id;
        this.codigoDisciplina = codigoDisciplina;
        this.nomeDisciplina = nomeDisciplina;
        this.cursoDisciplina = cursoDisciplina;
        this.professorSupervisorNome = professorSupervisorNome;
        this.arquivoPdfPath = arquivoPdfPath;
        this.dataEnvio = dataEnvio;
        this.mensagem = mensagem;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoDisciplina() { return codigoDisciplina; }
    public void setCodigoDisciplina(String codigoDisciplina) { this.codigoDisciplina = codigoDisciplina; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    public String getCursoDisciplina() { return cursoDisciplina; }
    public void setCursoDisciplina(String cursoDisciplina) { this.cursoDisciplina = cursoDisciplina; }

    public String getProfessorSupervisorNome() { return professorSupervisorNome; }
    public void setProfessorSupervisorNome(String professorSupervisorNome) { this.professorSupervisorNome = professorSupervisorNome; }

    public String getArquivoPdfPath() { return arquivoPdfPath; }
    public void setArquivoPdfPath(String arquivoPdfPath) { this.arquivoPdfPath = arquivoPdfPath; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}