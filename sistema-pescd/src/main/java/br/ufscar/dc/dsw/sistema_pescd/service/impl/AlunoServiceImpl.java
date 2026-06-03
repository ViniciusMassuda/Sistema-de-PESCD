package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.mapper.OfertaMapper;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlunoServiceImpl implements IAlunoService {

    @Autowired
    private InscricaoDAO inscricaoDAO;

    @Autowired
    private OfertaMapper ofertaMapper;

    @Override
    public List<OfertaAlunoResponseDTO> buscarOfertasPorAluno(Usuario aluno) {
        List<Inscricao> inscricoes = inscricaoDAO.findByAlunoId(aluno.getId());

        return inscricoes.stream()
                .map(inscricao -> {
                    Oferta oferta = inscricao.getOferta();
                    String status = calcularStatusOferta(oferta);
                    return ofertaMapper.toDto(oferta, status);
                })
                .collect(Collectors.toList());
    }

    private String calcularStatusOferta(Oferta oferta) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataInicio = oferta.getDataInicio();
        LocalDate dataFim = oferta.getDataFim();

        if (hoje.isBefore(dataInicio)) {
            return "Não iniciada";
        } else if (hoje.isAfter(dataFim)) {
            return "Atrasada";
        } else {
            return "Em andamento";
        }
    }
}