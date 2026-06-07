package br.ufscar.dc.dsw.sistema_pescd.mapper;
import br.ufscar.dc.dsw.sistema_pescd.domain.*;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import org.springframework.stereotype.Component;
@Component
public class OfertaMapper {
    public OfertaAlunoResponseDTO toDto(Oferta o, String so, Inscricao i) {
        if (o == null) return null;
        OfertaAlunoResponseDTO d = new OfertaAlunoResponseDTO();
        d.setId(o.getId()); d.setNomeOferta(o.getNome()); d.setSemestre(o.getSemestre());
        d.setDataInicio(o.getDataInicio()); d.setDataFim(o.getDataFim());
        if (o.getProfessorResponsavel() != null) d.setProfessorResponsavel(o.getProfessorResponsavel().getNome());
        d.setStatusOferta(so);
        if (i != null && i.getStatus() != null) d.setStatusAluno(i.getStatus().getDescricao());
        else d.setStatusAluno(StatusAluno.NAO_ENVIADO.getDescricao());
        return d;
    }
}
