package br.ufscar.dc.dsw.sistema_pescd.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class DocumentacaoRequestDTO {

    @NotBlank(message = "{validation.instituicao.required}")
    private String instituicao;

    @NotBlank(message = "{validation.nomeDisciplina.required}")
    private String nomeDisciplina;

    @NotBlank(message = "{validation.cursoDisciplina.required}")
    private String cursoDisciplina;

    @NotNull(message = "{validation.cargaHoraria.required}")
    private Integer cargaHoraria;

    @NotNull(message = "{validation.arquivo.required}")
    private MultipartFile arquivo;

    // Getters e Setters
    public String getInstituicao() { return instituicao; }
    public void setInstituicao(String instituicao) { this.instituicao = instituicao; }

    public String getNomeDisciplina() { return nomeDisciplina; }
    public void setNomeDisciplina(String nomeDisciplina) { this.nomeDisciplina = nomeDisciplina; }

    public String getCursoDisciplina() { return cursoDisciplina; }
    public void setCursoDisciplina(String cursoDisciplina) { this.cursoDisciplina = cursoDisciplina; }

    public Integer getCargaHoraria() { return cargaHoraria; }
    public void setCargaHoraria(Integer cargaHoraria) { this.cargaHoraria = cargaHoraria; }

    public MultipartFile getArquivo() { return arquivo; }
    public void setArquivo(MultipartFile arquivo) { this.arquivo = arquivo; }
}