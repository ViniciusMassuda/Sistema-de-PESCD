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


     // Aciona o repositório customizado para retornar a lista de ofertas do banco.
     // Garante que na tela do secretário (lista.html) os semestres mais novos apareçam no topo.
    @Override
    public List<Oferta> buscarTodosOrdenado() {
        return dao.findAllOrderedBySemestre();
    }


    @Override
    @Transactional
    public void salvar(Oferta oferta) {
        dao.save(oferta);
    }

    @Override
    public Oferta buscarPorId(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        dao.deleteById(id);
    }
}