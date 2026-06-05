package br.ufscar.dc.dsw.sistema_pescd.config;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.StatusAluno;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private OfertaDAO ofertaDAO;

    @Autowired
    private InscricaoDAO inscricaoDAO;

    @Override
    public void run(String... args) throws Exception {

        System.out.println("\n=== INSERINDO DADOS DE TESTE ===\n");

        // Verificar se já existe inscrição
        if (inscricaoDAO.count() == 0) {

            // Buscar aluno (ID 4)
            Usuario aluno = usuarioDAO.findById(4L).orElse(null);
            // Buscar oferta (ID 1)
            Oferta oferta = ofertaDAO.findById(1L).orElse(null);

            if (aluno != null && oferta != null) {
                Inscricao inscricao = new Inscricao();
                inscricao.setAluno(aluno);
                inscricao.setOferta(oferta);
                inscricao.setStatusAluno(StatusAluno.NAO_ENVIADO);

                inscricaoDAO.save(inscricao);

                System.out.println(" Inscrição criada:");
                System.out.println("   Aluno: " + aluno.getNome() + " (ID: " + aluno.getId() + ")");
                System.out.println("   Oferta: " + oferta.getNome() + " (ID: " + oferta.getId() + ")");
                System.out.println("   Status: " + inscricao.getStatusAluno());
            } else {
                System.out.println("Aluno ou Oferta não encontrados!");
                System.out.println("   Aluno ID 4 existe? " + (aluno != null));
                System.out.println("   Oferta ID 1 existe? " + (oferta != null));
            }
        } else {
            System.out.println("Já existem " + inscricaoDAO.count() + " inscrição(ões) no banco.");
        }

        System.out.println("\n=== FIM DA INSERÇÃO ===\n");
    }
}