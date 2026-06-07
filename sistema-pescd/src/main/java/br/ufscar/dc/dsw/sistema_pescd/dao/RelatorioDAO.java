package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.RelatorioFinal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelatorioDAO extends JpaRepository<RelatorioFinal, Long> {
}