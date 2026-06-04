package br.ufscar.dc.dsw.sistema_pescd.config;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseChecker implements CommandLineRunner {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private OfertaDAO ofertaDAO;

    @Autowired
    private InscricaoDAO inscricaoDAO;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n========================================");
        System.out.println("=== VERIFICANDO BANCO DE DADOS ===");
        System.out.println("========================================\n");

        System.out.println("📋 USUÁRIOS:");
        System.out.println("----------------------------------------");
        for (Usuario u : usuarioDAO.findAll()) {
            System.out.println("  ID: " + u.getId() +
                    " | Username: " + u.getUsername() +
                    " | Nome: " + u.getNome() +
                    " | Role: " + u.getRole());
        }

        System.out.println("\n📋 OFERTAS:");
        System.out.println("----------------------------------------");
        for (Oferta o : ofertaDAO.findAll()) {
            System.out.println("  ID: " + o.getId() +
                    " | Nome: " + o.getNome() +
                    " | Semestre: " + o.getSemestre() +
                    " | Professor ID: " + o.getProfessorResponsavel().getId());
        }

        System.out.println("\n📋 INSCRIÇÕES:");
        System.out.println("----------------------------------------");
        for (Inscricao i : inscricaoDAO.findAll()) {
            System.out.println("  ID: " + i.getId() +
                    " | Aluno ID: " + i.getAluno().getId() +
                    " | Aluno Nome: " + i.getAluno().getNome() +
                    " | Oferta ID: " + i.getOferta().getId() +
                    " | Oferta Nome: " + i.getOferta().getNome());
        }

        System.out.println("\n========================================");
        System.out.println("=== FIM DA VERIFICAÇÃO ===");
        System.out.println("========================================\n");
    }
}