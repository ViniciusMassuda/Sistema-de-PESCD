package br.ufscar.dc.dsw.sistema_pescd.dto.response;

import java.time.LocalDate;

public class OfertaAlunoResponseDTO {

    private Long id;
    private String nomeOferta;
    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String professorResponsavel;
    private String statusOferta;

    public OfertaAlunoResponseDTO() {}

    public OfertaAlunoResponseDTO(Long id, String nomeOferta, String semestre, LocalDate dataInicio,
                                  LocalDate dataFim, String professorResponsavel, String statusOferta) {
        this.id = id;
        this.nomeOferta = nomeOferta;
        this.semestre = semestre;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.professorResponsavel = professorResponsavel;
        this.statusOferta = statusOferta;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeOferta() { return nomeOferta; }
    public void setNomeOferta(String nomeOferta) { this.nomeOferta = nomeOferta; }

    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getProfessorResponsavel() { return professorResponsavel; }
    public void setProfessorResponsavel(String professorResponsavel) { this.professorResponsavel = professorResponsavel; }

    public String getStatusOferta() { return statusOferta; }
    public void setStatusOferta(String statusOferta) { this.statusOferta = statusOferta; }
}