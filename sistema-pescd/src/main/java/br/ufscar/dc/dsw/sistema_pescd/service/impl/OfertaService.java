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
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OfertaService implements IOfertaService {

    private final OfertaDAO dao;


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

    @Override
    @Transactional
    public void encerrar(Long id, String usuarioLogado) {
        Oferta oferta = dao.findById(id).orElse(null);
        if (oferta != null) {
            // 1. RN-2: Mudar a flag que o método getStatusCalculado() usa para retornar "Concluída"
            oferta.setEncerradaSecretario(true);

            // 2. RN-2: Registrar o timestamp do encerramento e o usuário nas colunas existentes
            oferta.setDataCriacao(LocalDateTime.now());
            oferta.setUsuarioCriador(usuarioLogado);

            // 3. Salva a oferta atualizada no banco de dados
            dao.save(oferta);
        }
    }

    // Retorna a lista de ofertas sob a responsabilidade de um professor específico
    @Override
    public List<Oferta> buscarPorProfessorResponsavel(Long professorId) {
        return dao.findByProfessorResponsavelId(professorId);
    }
}
