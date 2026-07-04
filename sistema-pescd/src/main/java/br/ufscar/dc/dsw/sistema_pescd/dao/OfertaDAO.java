package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OfertaDAO extends JpaRepository<Oferta, Long> {
    @Query("SELECT o FROM Oferta o ORDER BY o.semestre DESC")
    List<Oferta> findAllOrderedBySemestre();

    // PR.03: ofertas por professor responsável
    List<Oferta> findByProfessorResponsavelId(Long professorId);
}
