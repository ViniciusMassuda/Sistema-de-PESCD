package br.ufscar.dc.dsw.sistema_pescd.config;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.PlanoTrabalhoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.PlanoTrabalho;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioDAO usuarioDAO;
    private final OfertaDAO ofertaDAO;
    private final InscricaoDAO inscricaoDAO;
    private final PlanoTrabalhoDAO planoTrabalhoDAO;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("\n=== CARREGANDO DADOS DE TESTE ===\n");

        // 1. Criar usuários
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setPassword("123456");
        admin.setNome("Administrador");
        admin.setRole(Usuario.Role.ADMIN);
        usuarioDAO.save(admin);

        Usuario sec = new Usuario();
        sec.setUsername("sec");
        sec.setPassword("123456");
        sec.setNome("Secretario");
        sec.setRole(Usuario.Role.SECRETARIO);
        usuarioDAO.save(sec);

        Usuario prof = new Usuario();
        prof.setUsername("prof");
        prof.setPassword("123456");
        prof.setNome("Professor Responsavel");
        prof.setRole(Usuario.Role.PROFESSOR);
        usuarioDAO.save(prof);

        Usuario aluno1 = new Usuario();
        aluno1.setUsername("aluno");
        aluno1.setPassword("123456");
        aluno1.setNome("Aluno 1");
        aluno1.setRole(Usuario.Role.ALUNO);
        usuarioDAO.save(aluno1);

        Usuario aluno2 = new Usuario();
        aluno2.setUsername("aluno2");
        aluno2.setPassword("123456");
        aluno2.setNome("Aluno 2");
        aluno2.setRole(Usuario.Role.ALUNO);
        usuarioDAO.save(aluno2);

        // 2. Criar ofertas
        Oferta oferta1 = new Oferta();
        oferta1.setNome("PESCD I - Estágio Docente");
        oferta1.setSemestre("2026/1");
        oferta1.setDataInicio(LocalDate.of(2026, 3, 1));
        oferta1.setDataFim(LocalDate.of(2026, 7, 15));
        oferta1.setProfessorResponsavel(prof);
        ofertaDAO.save(oferta1);

        Oferta oferta2 = new Oferta();
        oferta2.setNome("PESCD II - Prática de Ensino");
        oferta2.setSemestre("2026/1");
        oferta2.setDataInicio(LocalDate.of(2026, 3, 1));
        oferta2.setDataFim(LocalDate.of(2026, 7, 15));
        oferta2.setProfessorResponsavel(prof);
        ofertaDAO.save(oferta2);

        // 3. Criar inscrições para aluno1
        Inscricao insc1 = new Inscricao();
        insc1.setAluno(aluno1);
        insc1.setOferta(oferta1);
        insc1.setStatus(Inscricao.StatusAluno.NAO_ENVIADO);
        inscricaoDAO.save(insc1);

        Inscricao insc2 = new Inscricao();
        insc2.setAluno(aluno1);
        insc2.setOferta(oferta2);
        insc2.setStatus(Inscricao.StatusAluno.PLANO_APROVADO);
        inscricaoDAO.save(insc2);

        // 4. Criar PlanoTrabalho para a oferta2
        PlanoTrabalho planoTrabalho = new PlanoTrabalho();
        planoTrabalho.setCodigoDisciplina("DC-001");
        planoTrabalho.setNomeDisciplina("Estágio Docente Supervisionado");
        planoTrabalho.setCursoDisciplina("Ciência da Computação");
        planoTrabalho.setProfessorSupervisor(prof);
        planoTrabalho.setArquivoPdfPath("/uploads/planos/teste.pdf");
        planoTrabalho.setDataEnvio(LocalDateTime.now());
        planoTrabalhoDAO.save(planoTrabalho);

        // Associar o plano à inscrição2
        insc2.setPlanoTrabalho(planoTrabalho);
        inscricaoDAO.save(insc2);

        System.out.println("Dados carregados!");
        System.out.println("   Aluno1: oferta1 (NAO_ENVIADO), oferta2 (PLANO_APROVADO)");
        System.out.println("   Aluno2: sem inscrições");
    }
}