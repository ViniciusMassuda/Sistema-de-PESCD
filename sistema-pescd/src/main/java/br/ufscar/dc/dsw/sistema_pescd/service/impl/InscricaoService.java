// Gerencia as inscricoes dos alunos em ofertas.
// Conta a quantidade de alunos matriculados.

package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
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
        // Conta quantos alunos estao na oferta selecionada.
        return dao.countByOferta(oferta);
    }

    // ADICIONADO PARA A S.03: Busca todas as inscrições de uma determinada oferta
    @Override
    public List<Inscricao> buscarPorOferta(Oferta oferta) {
        return dao.findByOferta(oferta);
    }
}
