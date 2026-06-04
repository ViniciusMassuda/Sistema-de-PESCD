package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OfertaDAO extends JpaRepository<Oferta, Long> {
    // Busca no banco e traz a lista de ofertas ordenada do semestre mais novo para o mais antigo
    @Query("SELECT o FROM Oferta o ORDER BY o.semestre DESC")
    List<Oferta> findAllOrderedBySemestre();
}


