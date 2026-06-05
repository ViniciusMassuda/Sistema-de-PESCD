package br.ufscar.dc.dsw.sistema_pescd.dao;
import java.util.List;
import java.util.Optional;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoDAO extends JpaRepository<Inscricao, Long> {
    long countByOferta(Oferta oferta);

    // ADICIONADO PARA A S.02: Busca as inscrições de uma oferta para listar na tela
    List<Inscricao> findByOferta(Oferta oferta);
    List<Inscricao> findByAlunoId(Long alunoId);
    // ADICIONADO PARA A S.02: Evita matricular o mesmo aluno duas vezes na mesma oferta
    boolean existsByOfertaAndAluno(Oferta oferta, Usuario aluno);
    Optional<Inscricao> findByAlunoAndOferta(Usuario aluno, Oferta oferta);
}


