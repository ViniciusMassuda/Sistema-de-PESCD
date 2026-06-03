package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IInscricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class InscricaoService implements IInscricaoService {

    @Autowired
    private InscricaoDAO dao;

    @Override
    public long contarPorOferta(Oferta oferta) {
        return dao.countByOferta(oferta);
    }

    @Override
    public List<Inscricao> buscarPorAluno(Usuario aluno) {
        return dao.findByAlunoId(aluno.getId());
    }
}