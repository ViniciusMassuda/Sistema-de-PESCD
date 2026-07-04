// É a Interface que define o contrato para os serviços de Oferta.
// Ela dita quais operações o controlador pode acionar para gerenciar o ciclo de vida das ofertas.
package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import java.util.List;

public interface IOfertaService {
    // Contrato para o método que traz todas as ofertas ordenadas por período/semestre
    List<Oferta> buscarTodosOrdenado();

    // Contrato para o método que persiste ou atualiza os dados de uma oferta no banco
    void salvar(Oferta oferta);

    Oferta buscarPorId(Long id);

    void excluir(Long id);

    void encerrar(Long id, String usuarioLogado);
}
