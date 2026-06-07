package br.ufscar.dc.dsw.sistema_pescd.mapper;

import br.ufscar.dc.dsw.sistema_pescd.domain.DocumentacaoComprobatoria;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.DocumentacaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.DocumentacaoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DocumentacaoMapper {

    public DocumentacaoComprobatoria toEntity(DocumentacaoRequestDTO dto, String arquivoPath) {
        return new DocumentacaoComprobatoria(
                dto.getInstituicao(),
                dto.getNomeDisciplina(),
                dto.getCursoDisciplina(),
                dto.getCargaHoraria(),
                arquivoPath
        );
    }

    public DocumentacaoResponseDTO toResponseDTO(DocumentacaoComprobatoria documentacao,
                                                 String mensagem) {
        return new DocumentacaoResponseDTO(
                documentacao.getId(),
                documentacao.getInstituicao(),
                documentacao.getNomeDisciplina(),
                documentacao.getCursoDisciplina(),
                documentacao.getCargaHoraria(),
                documentacao.getArquivoPdfPath(),
                documentacao.getDataEnvio(),
                mensagem
        );
    }
}