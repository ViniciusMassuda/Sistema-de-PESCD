package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.PlanoTrabalhoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.PlanoTrabalhoResponseDTO;

import java.util.List;

public interface IAlunoService {
    List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno);

    // NOVO: Enviar plano de trabalho
    PlanoTrabalhoResponseDTO enviarPlanoTrabalho(Long ofertaId, Usuario aluno,
                                                 PlanoTrabalhoRequestDTO request);

    // NOVO: Verificar se aluno pode enviar plano para uma oferta
    boolean podeEnviarPlano(Long ofertaId, Usuario aluno);
}