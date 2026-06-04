package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.PlanoTrabalho;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanoTrabalhoDAO extends JpaRepository<PlanoTrabalho, Long> {
}