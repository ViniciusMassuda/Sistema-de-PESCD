package br.ufscar.dc.dsw.sistema_pescd.mapper;

import br.ufscar.dc.dsw.sistema_pescd.domain.RelatorioFinal;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.RelatorioRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.RelatorioResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class RelatorioMapper {

    public RelatorioFinal toEntity(RelatorioRequestDTO dto, String arquivoPath) {
        return new RelatorioFinal(dto.getFrequencia(), arquivoPath);
    }

    public RelatorioResponseDTO toResponseDTO(RelatorioFinal relatorio, String mensagem) {
        return new RelatorioResponseDTO(
                relatorio.getId(),
                relatorio.getFrequencia(),
                relatorio.getArquivoPdfPath(),
                relatorio.getDataEnvio(),
                mensagem
        );
    }
}