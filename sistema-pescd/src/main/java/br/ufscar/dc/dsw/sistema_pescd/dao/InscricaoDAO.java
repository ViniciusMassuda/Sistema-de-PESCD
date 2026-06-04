// Arquivo: InscricaoDAO.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.dao;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InscricaoDAO extends JpaRepository<Inscricao, Long> {
    long countByOferta(Oferta oferta);
    // Busca todas as inscrições de um aluno
    @Query("SELECT i FROM Inscricao i WHERE i.aluno.id = :alunoId")
    List<Inscricao> findByAlunoId(@Param("alunoId") Long alunoId);

    // NOVO: Buscar inscrição por aluno e oferta
    @Query("SELECT i FROM Inscricao i WHERE i.aluno = :aluno AND i.oferta = :oferta")
    Optional<Inscricao> findByAlunoAndOferta(@Param("aluno") Usuario aluno, @Param("oferta") Oferta oferta);

    // NOVO: Atualizar status do aluno
    @Modifying
    @Transactional
    @Query("UPDATE Inscricao i SET i.statusAluno = :status, i.dataEnvioPlano = :dataEnvio WHERE i.id = :id")
    void updateStatusParaPlanoEnviado(@Param("id") Long id,
                                      @Param("status") Inscricao.StatusAluno status,
                                      @Param("dataEnvio") LocalDateTime dataEnvio);

}


