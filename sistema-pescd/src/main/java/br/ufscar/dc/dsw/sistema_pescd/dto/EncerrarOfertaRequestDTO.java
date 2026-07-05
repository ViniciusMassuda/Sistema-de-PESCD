package br.ufscar.dc.dsw.sistema_pescd.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO para encerramento de oferta.
 */
@Getter
@Setter
@NoArgsConstructor
public class EncerrarOfertaRequestDTO {

    @NotNull(message = "O ID da oferta é obrigatório.")
    private Long ofertaId;

    @NotBlank(message = "A descrição das lições aprendidas é obrigatória.")
    private String descricaoLicoesAprendidas;
}
