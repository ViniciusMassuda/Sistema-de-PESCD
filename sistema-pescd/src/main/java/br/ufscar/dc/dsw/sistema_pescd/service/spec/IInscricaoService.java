package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import java.util.List;

public interface IInscricaoService {
    long contarPorOferta(Oferta oferta);

    List<Inscricao> buscarPorAluno(Usuario aluno);
}