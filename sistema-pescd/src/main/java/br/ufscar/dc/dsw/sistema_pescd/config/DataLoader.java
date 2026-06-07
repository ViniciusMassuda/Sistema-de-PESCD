package br.ufscar.dc.dsw.sistema_pescd.config;
import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.PlanoTrabalhoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Component
@Order(1)
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UsuarioDAO usuarioDAO;
    @Autowired
    private OfertaDAO ofertaDAO;
    @Autowired
    private InscricaoDAO inscricaoDAO;
    @Autowired
    private PlanoTrabalhoDAO planoTrabalhoDAO;
    @Override
    public void run(String... args) throws Exception {
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setNome("Admin");
        admin.setRole(Usuario.Role.ADMIN);
        usuarioDAO.save(admin);
        Usuario prof = new Usuario();
        prof.setUsername("prof");
        prof.setPassword("123456");
        prof.setNome("Prof");
        prof.setRole(Usuario.Role.PROFESSOR);
        usuarioDAO.save(prof);
        Usuario aluno1 = new Usuario();
        aluno1.setUsername("aluno");
        aluno1.setPassword("123456");
        aluno1.setNome("Aluno");
        aluno1.setRole(Usuario.Role.ALUNO);
        usuarioDAO.save(aluno1);
        Oferta oferta1 = new Oferta();
        oferta1.setNome("Oferta1");
        oferta1.setSemestre("2026/1");
        oferta1.setDataInicio(LocalDate.of(2026, 3, 1));
        oferta1.setDataFim(LocalDate.of(2026, 7, 15));
        oferta1.setProfessorResponsavel(prof);
        ofertaDAO.save(oferta1);
        Inscricao insc1 = new Inscricao();
        insc1.setAluno(aluno1);
        insc1.setOferta(oferta1);
        insc1.setStatus(StatusAluno.NAO_ENVIADO);
        inscricaoDAO.save(insc1);
    }
}
