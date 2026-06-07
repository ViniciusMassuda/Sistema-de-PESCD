package br.ufscar.dc.dsw.sistema_pescd.dao;
import java.util.List;
import java.util.Optional;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InscricaoDAO extends JpaRepository<Inscricao, Long> {
    long countByOferta(Oferta oferta);
    List<Inscricao> findByOferta(Oferta oferta);
    boolean existsByOfertaAndAluno(Oferta oferta, Usuario aluno);
    List<Inscricao> findByAlunoId(Long alunoId);
    Optional<Inscricao> findByAlunoAndOferta(Usuario aluno, Oferta oferta);
}
