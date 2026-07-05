package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.dto.AcaoInscricaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EncerrarOfertaRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EstatisticasOfertaResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.InscricaoDTO;

import java.util.List;

/**
 * Servico para perfil do Professor.
 */
public interface IProfessorService {

    List<InscricaoDTO> listarAlunosVinculados(String username);

    void aprovarPlano(AcaoInscricaoRequestDTO request, String username);

    void aprovarRelatorio(AcaoInscricaoRequestDTO request, String username);

    void concluirRelatorio(AcaoInscricaoRequestDTO request, String username);

    void avaliarDocumentacao(AcaoInscricaoRequestDTO request, String username);

    EstatisticasOfertaResponseDTO buscarEstatisticasOferta(Long ofertaId, String username);

    void encerrarOferta(EncerrarOfertaRequestDTO request, String username);
}
