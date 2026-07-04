package br.ufscar.dc.dsw.sistema_pescd.dao;
import java.util.List;
import java.util.Optional;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InscricaoDAO extends JpaRepository<Inscricao, Long> {
    long countByOferta(Oferta oferta);
    List<Inscricao> findByOferta(Oferta oferta);
    boolean existsByOfertaAndAluno(Oferta oferta, Usuario aluno);
    List<Inscricao> findByAlunoId(Long alunoId);
    Optional<Inscricao> findByAlunoAndOferta(Usuario aluno, Oferta oferta);

    // busca alunos onde o professor eh o supervisor (no plano de trabalho)
    @Query("SELECT i FROM Inscricao i WHERE i.planoTrabalho.professorSupervisor.id = :professorId")
    List<Inscricao> findByProfessorSupervisor(@Param("professorId") Long professorId);

    // busca alunos onde o professor eh o responsavel pela oferta
    @Query("SELECT i FROM Inscricao i WHERE i.oferta.professorResponsavel.id = :professorId")
    List<Inscricao> findByProfessorResponsavel(@Param("professorId") Long professorId);

    // busca todos os alunos que tem alguma ligacao com o professor logado (responsavel ou supervisor)
    @Query("SELECT DISTINCT i FROM Inscricao i LEFT JOIN i.planoTrabalho p WHERE i.oferta.professorResponsavel.id = :professorId OR p.professorSupervisor.id = :professorId")
    List<Inscricao> findByProfessorVinculado(@Param("professorId") Long professorId);
    // PR.03: inscrições por oferta
    List<Inscricao> findByOfertaId(Long ofertaId);
}
