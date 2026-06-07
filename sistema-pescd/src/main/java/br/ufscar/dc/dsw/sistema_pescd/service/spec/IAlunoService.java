package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.DocumentacaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.PlanoTrabalhoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.RelatorioRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.DocumentacaoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.RelatorioResponseDTO;

import java.util.List;

public interface IAlunoService {
    List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno);

    // NOVO: Enviar plano de trabalho
    PlanoTrabalhoResponseDTO enviarPlanoTrabalho(Long ofertaId, Usuario aluno, PlanoTrabalhoRequestDTO request);
    boolean podeEnviarPlano(Long ofertaId, Usuario aluno);

    // NOVOS MÉTODOS PARA AL.03
    DocumentacaoResponseDTO enviarDocumentacao(Long ofertaId, Usuario aluno, DocumentacaoRequestDTO request);
    boolean podeEnviarDocumentacao(Long ofertaId, Usuario aluno);

    RelatorioResponseDTO enviarRelatorio(Long ofertaId, Usuario aluno, RelatorioRequestDTO request);
    boolean podeEnviarRelatorio(Long ofertaId, Usuario aluno);
}