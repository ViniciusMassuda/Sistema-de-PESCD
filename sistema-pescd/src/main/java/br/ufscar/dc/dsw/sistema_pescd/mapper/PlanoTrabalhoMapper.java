package br.ufscar.dc.dsw.sistema_pescd.mapper;

import br.ufscar.dc.dsw.sistema_pescd.domain.PlanoTrabalho;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.PlanoTrabalhoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.PlanoTrabalhoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PlanoTrabalhoMapper {

    public PlanoTrabalho toEntity(PlanoTrabalhoRequestDTO dto, Usuario professorSupervisor, String arquivoPath) {
        return new PlanoTrabalho(
                dto.getCodigoDisciplina(),
                dto.getNomeDisciplina(),
                dto.getCursoDisciplina(),
                professorSupervisor,
                arquivoPath
        );
    }

    public PlanoTrabalhoResponseDTO toResponseDTO(PlanoTrabalho plano, String mensagem) {
        return new PlanoTrabalhoResponseDTO(
                plano.getId(),
                plano.getCodigoDisciplina(),
                plano.getNomeDisciplina(),
                plano.getCursoDisciplina(),
                plano.getProfessorSupervisor().getNome(),
                plano.getArquivoPdfPath(),
                plano.getDataEnvio(),
                mensagem
        );
    }
}