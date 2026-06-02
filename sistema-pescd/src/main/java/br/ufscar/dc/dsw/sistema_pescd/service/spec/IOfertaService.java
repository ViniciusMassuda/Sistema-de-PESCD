// Arquivo: IOfertaService.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.service.spec;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import java.util.List;

public interface IOfertaService {
    List<Oferta> buscarTodosOrdenado();
}


