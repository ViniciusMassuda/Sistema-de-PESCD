// Arquivo: InscricaoDAO.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InscricaoDAO extends JpaRepository<Inscricao, Long> {
    long countByOferta(Oferta oferta);
    // Busca todas as inscrições de um aluno
    @Query("SELECT i FROM Inscricao i WHERE i.aluno.id = :alunoId")
    List<Inscricao> findByAlunoId(@Param("alunoId") Long alunoId);
}


