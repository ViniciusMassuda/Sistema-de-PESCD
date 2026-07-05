package br.ufscar.dc.dsw.sistema_pescd.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO generico para avaliacoes do professor.
 */
@Getter
@Setter
@NoArgsConstructor
public class AcaoInscricaoRequestDTO {

    @NotNull(message = "O ID da inscrição é obrigatório.")
    private Long inscricaoId;

    @NotBlank(message = "O parecer é obrigatório.")
    private String parecer;

    @Min(value = 0, message = "A frequência deve ser entre 0 e 100.")
    @Max(value = 100, message = "A frequência deve ser entre 0 e 100.")
    private Integer frequencia;

    private String nota;
}
