package br.ufscar.dc.dsw.sistema_pescd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class PlanoTrabalhoRequestDTO {

    @NotBlank(message = "{validation.codigoDisciplina.required}")
    private String codigoDisciplina;

    @NotBlank(message = "{validation.nomeDisciplina.required}")
    private String nomeDisciplina;

    @NotBlank(message = "{validation.cursoDisciplina.required}")
    private String cursoDisciplina;

    @NotNull(message = "{validation.professorSupervisor.required}")
    private Long professorSupervisorId;

    @NotNull(message = "{validation.arquivo.required}")
    @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary", description = "Arquivo PDF do Plano (Máx 5MB)")
    private MultipartFile arquivo;

    // Getters e Setters
    public String getCodigoDisciplina() { return codigoDisciplina; }
    public void setCodigoDisciplina(String codigoDisciplina) { this.codigoDisciplina = codigoDisciplina; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    public String getCursoDisciplina() { return cursoDisciplina; }
    public void setCursoDisciplina(String cursoDisciplina) { this.cursoDisciplina = cursoDisciplina; }

    public Long getProfessorSupervisorId() { return professorSupervisorId; }
    public void setProfessorSupervisorId(Long professorSupervisorId) { this.professorSupervisorId = professorSupervisorId; }

    public MultipartFile getArquivo() { return arquivo; }
    public void setArquivo(MultipartFile arquivo) { this.arquivo = arquivo; }
}