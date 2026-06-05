package br.ufscar.dc.dsw.sistema_pescd.dao;
import java.util.List;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscricaoDAO extends JpaRepository<Inscricao, Long> {
    long countByOferta(Oferta oferta);

    // ADICIONADO PARA A S.02: Busca as inscrições de uma oferta para listar na tela
    List<Inscricao> findByOferta(Oferta oferta);

    // ADICIONADO PARA A S.02: Evita matricular o mesmo aluno duas vezes na mesma oferta
    boolean existsByOfertaAndAluno(Oferta oferta, Usuario aluno);

    // Busca todas as inscrições de um aluno (usado no AL.01 e AL.02)
    List<Inscricao> findByAlunoId(Long alunoId);

    // Busca inscrição por aluno e oferta (usado no AL.02)
    java.util.Optional<Inscricao> findByAlunoAndOferta(Usuario aluno, Oferta oferta);
}


