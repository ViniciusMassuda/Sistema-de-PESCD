package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.DocumentacaoComprobatoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentacaoDAO extends JpaRepository<DocumentacaoComprobatoria, Long> {
}