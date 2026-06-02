// InscricaoService.java
// Gerencia as inscricoes dos alunos em ofertas.
// Conta a quantidade de alunos matriculados.

package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IInscricaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InscricaoService implements IInscricaoService {

    @Autowired
    private InscricaoDAO dao;

    public long contarPorOferta(Oferta oferta) {
        // Conta quantos alunos estao na oferta selecionada.
        return dao.countByOferta(oferta);
    }
}
