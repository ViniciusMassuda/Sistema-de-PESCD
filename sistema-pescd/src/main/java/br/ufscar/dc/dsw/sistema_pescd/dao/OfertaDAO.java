// Arquivo: OfertaDAO.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OfertaDAO extends JpaRepository<Oferta, Long> {
    @Query("SELECT o FROM Oferta o ORDER BY o.semestre DESC")
    List<Oferta> findAllOrderedBySemestre();
}


