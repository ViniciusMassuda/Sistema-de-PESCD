// É a Interface que define o contrato para os serviços de Inscrição.
// Ela apenas declara quais métodos a camada de negócios é obrigada a implementar.

package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import java.util.List;

public interface IInscricaoService {
    // Contrato para o método que conta o total de alunos matriculados em uma oferta
    long contarPorOferta(Oferta oferta);

    // Contrato para o método da S.03 que busca a lista de alunos de uma oferta para o painel
    List<Inscricao> buscarPorOferta(Oferta oferta);
}


