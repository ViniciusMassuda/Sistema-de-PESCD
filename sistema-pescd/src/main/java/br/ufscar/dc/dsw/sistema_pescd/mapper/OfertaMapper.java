package br.ufscar.dc.dsw.sistema_pescd.mapper;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class OfertaMapper {

    public OfertaAlunoResponseDTO toDto(Oferta oferta, String status) {
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

        dto.setStatusOferta(status);

        return dto;
    }
}