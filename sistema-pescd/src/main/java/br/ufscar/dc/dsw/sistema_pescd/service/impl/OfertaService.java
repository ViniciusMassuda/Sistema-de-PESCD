// OfertaService.java
// Cuida das operacoes de ofertas do PESCD.
// Busca as ofertas cadastradas no banco de dados.

package br.ufscar.dc.dsw.sistema_pescd.service.impl;

import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IOfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OfertaService implements IOfertaService {

    @Autowired
    private OfertaDAO dao;

    public List<Oferta> buscarTodosOrdenado() {
        // Busca todas as ofertas em ordem decrescente de semestre.
        return dao.findAllOrderedBySemestre();
    }
}
