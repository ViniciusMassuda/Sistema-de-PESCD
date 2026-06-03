package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import java.util.List;

public interface IAlunoService {
    List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno);
}