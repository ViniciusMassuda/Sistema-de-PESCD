package br.ufscar.dc.dsw.sistema_pescd.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * DTO de resposta com estatisticas consolidadas da oferta.
 */
@Getter
@Setter
@NoArgsConstructor
public class EstatisticasOfertaResponseDTO {

    private Long ofertaId;
    private String nomeOferta;
    private String semestre;
    private int totalAlunos;
    private double mediaFrequencia;
    private long creditosViaEstagio;
    private long creditosViaDocumentacao;
    private Map<String, Long> contagemNotas;
}
