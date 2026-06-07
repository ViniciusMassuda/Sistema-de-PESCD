package br.ufscar.dc.dsw.sistema_pescd.mapper;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import org.springframework.stereotype.Component;

import static br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao.StatusAluno.*;

@Component
public class OfertaMapper {

    public OfertaAlunoResponseDTO toDto(Oferta oferta, String statusOferta, Inscricao inscricao) {
        if (oferta == null) {
            return null;
        }

        OfertaAlunoResponseDTO dto = new OfertaAlunoResponseDTO();
        dto.setId(oferta.getId());
        dto.setNomeOferta(oferta.getNome());
        dto.setSemestre(oferta.getSemestre());
        dto.setDataInicio(oferta.getDataInicio());
        dto.setDataFim(oferta.getDataFim());

        if (oferta.getProfessorResponsavel() != null) {
            dto.setProfessorResponsavel(oferta.getProfessorResponsavel().getNome());
        }

        dto.setStatusOferta(statusOferta);

        // Define o status do aluno (para o botão na tabela)
        if (inscricao != null && inscricao.getStatus() != null) {
            switch (inscricao.getStatus()) {
                case NAO_ENVIADO:
                    dto.setStatusAluno("não enviado");
                    break;
                case PLANO_ENVIADO:
                    dto.setStatusAluno("plano enviado");
                    break;
                case PLANO_APROVADO:
                    dto.setStatusAluno("plano aprovado");
                    break;
                case DOCUMENTACAO_ENVIADA:
                    dto.setStatusAluno("documentação enviada");
                    break;
                case RELATORIO_ENVIADO:
                    dto.setStatusAluno("relatório enviado");
                    break;
                default:
                    dto.setStatusAluno(inscricao.getStatus().name().toLowerCase().replace("_", " "));
                    break;
            }
        } else {
            dto.setStatusAluno("não enviado");
        }

        return dto;
    }
}