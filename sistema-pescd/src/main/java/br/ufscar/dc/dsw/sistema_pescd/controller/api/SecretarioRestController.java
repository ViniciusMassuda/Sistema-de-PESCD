package br.ufscar.dc.dsw.sistema_pescd.controller.api;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IOfertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secretario")
public class SecretarioRestController {

    private final IOfertaService ofertaService;
    private final OfertaDAO ofertaDAO;
    private final InscricaoDAO inscricaoDAO;
    private final UsuarioDAO usuarioDAO;

    // Construtor único para injeção de dependências automática
    public SecretarioRestController(IOfertaService ofertaService,
                                    OfertaDAO ofertaDAO,
                                    InscricaoDAO inscricaoDAO,
                                    UsuarioDAO usuarioDAO) {
        this.ofertaService = ofertaService;
        this.ofertaDAO = ofertaDAO;
        this.inscricaoDAO = inscricaoDAO;
        this.usuarioDAO = usuarioDAO;
    }

    // LISTAR TODAS AS OFERTAS ATIVAS
    @GetMapping("/ofertas")
    public ResponseEntity<List<Oferta>> listarTodas() {
        List<Oferta> todasAsOfertas = ofertaService.buscarTodosOrdenado();

        // Mantém apenas as ofertas cuja situação seja diferente de "Concluída"
        List<Oferta> ofertasAtivas = todasAsOfertas.stream()
                .filter(o -> !o.getStatusCalculado().equals("Concluída"))
                .toList();

        return ResponseEntity.ok(ofertasAtivas);
    }

    // BUSCAR UMA OFERTA POR ID
    @GetMapping("/ofertas/{id}")
    public ResponseEntity<Oferta> buscarPorId(@PathVariable Long id) {
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(oferta);
    }

    // CRIAR UMA OFERTA (COM VALIDAÇÕES E CRITÉRIOS DE ACEITE)

    // CADASTRAR NOVA OFERTA
    @PostMapping("/ofertas")
    public ResponseEntity<?> criar(@RequestBody Oferta oferta) {
        // RN-1: Fallback automático se o nome for deixado em branco
        if (oferta.getNome() == null || oferta.getNome().trim().isEmpty()) {
            oferta.setNome("Oferta - Semestre " + oferta.getSemestre());
        }

        // Validação lógica extraída do controller original (Consistência Cronológica)
        if (oferta.getDataFim() != null && oferta.getDataInicio() != null &&
                oferta.getDataFim().isBefore(oferta.getDataInicio())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Data final deve ser posterior à inicial"));
        }

        // RN-2: Metadados de Auditoria
        oferta.setDataCriacao(LocalDateTime.now());
        oferta.setUsuarioCriador("api-admin@ufscar.br");

        ofertaService.salvar(oferta);
        return ResponseEntity.ok(oferta);
    }

    // EXCLUIR OFERTA
    @DeleteMapping("/ofertas/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }
        ofertaService.excluir(id);
        return ResponseEntity.noContent().build();
    }


    // LISTAR ALUNOS MATRICULADOS EM UMA OFERTA
    @GetMapping("/ofertas/{id}/alunos")
    public ResponseEntity<List<Usuario>> listarAlunosDaOferta(@PathVariable("id") Long id) {
        Oferta oferta = ofertaDAO.findById(id).orElse(null);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        List<Inscricao> inscricoes = inscricaoDAO.findByOferta(oferta);
        List<Usuario> alunos = inscricoes.stream()
                .map(Inscricao::getAluno)
                .toList();

        return ResponseEntity.ok(alunos);
    }

    // DETALHES MICRO (HISTÓRICO INDIVIDUAL DA INSCRIÇÃO DO ALUNO)

    // BUSCAR DETALHES DE UMA INSCRIÇÃO ESPECÍFICA POR ID
    @GetMapping("/inscricoes/{id}")
    public ResponseEntity<Inscricao> buscarInscricaoPorId(@PathVariable Long id) {
        Inscricao inscricao = inscricaoDAO.findById(id).orElse(null);
        if (inscricao == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(inscricao);
    }

    // FLUXO A: CADASTRO MANUAL DE ALUNO
    @PostMapping("/ofertas/{id}/alunos")
    public ResponseEntity<Map<String, String>> adicionarAlunoManual(@PathVariable("id") Long ofertaId,
                                                                    @RequestBody Map<String, String> payload) {
        try {
            Oferta oferta = ofertaDAO.findById(ofertaId).orElse(null);
            if (oferta == null) return ResponseEntity.notFound().build();

            String nome = payload.get("nome");
            String email = payload.get("email");
            String senha = payload.get("senha");

            // RN-2: Verifica se o e-mail já existe no banco
            Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

            if (aluno == null) {
                // RN-1: Criação prévia com perfil ALUNO se for um novo usuário
                aluno = new Usuario();
                aluno.setNome(nome);
                aluno.setUsername(email);
                aluno.setPassword(senha);
                aluno.setRole(Usuario.Role.ALUNO);
                usuarioDAO.save(aluno);
            }

            // RN-2: Impede duplicidade de matrícula na mesma oferta
            if (inscricaoDAO.existsByOfertaAndAluno(oferta, aluno)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Este aluno já está inscrito nesta oferta!"));
            }

            Inscricao inscricao = new Inscricao();
            inscricao.setOferta(oferta);
            inscricao.setAluno(aluno);
            inscricaoDAO.save(inscricao);

            return ResponseEntity.ok(Map.of("message", "Aluno adicionado com sucesso via API!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // FLUXO B: UPLOAD E PROCESSAMENTO DE ARQUIVO CSV
    @PostMapping(value = "/ofertas/{id}/alunos/csv", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> importarAlunosCsv(@PathVariable("id") Long ofertaId,
                                                                 @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Por favor, selecione um arquivo CSV."));
        }

        try {
            Oferta oferta = ofertaDAO.findById(ofertaId).orElse(null);
            if (oferta == null) return ResponseEntity.notFound().build();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String cabecalho = reader.readLine(); // Ignora a linha de cabeçalho do CSV
                String linha;

                while ((linha = reader.readLine()) != null) {
                    if (linha.trim().isEmpty()) continue;

                    String[] colunas = linha.split(",");
                    String ra = colunas[0].trim();
                    String nomeCompleto = colunas[1].trim();
                    String email = colunas[2].trim();

                    Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

                    if (aluno == null) {
                        // RN-3: Se não cadastrado, cria credenciais baseadas no padrão (Username=Email, Senha=RA)
                        aluno = new Usuario();
                        aluno.setNome(nomeCompleto);
                        aluno.setUsername(email);
                        aluno.setPassword(ra);
                        aluno.setRole(Usuario.Role.ALUNO);
                        usuarioDAO.save(aluno);
                    }

                    // Inscreve o aluno caso o vínculo com a oferta atual ainda não exista
                    if (!inscricaoDAO.existsByOfertaAndAluno(oferta, aluno)) {
                        Inscricao inscricao = new Inscricao();
                        inscricao.setOferta(oferta);
                        inscricao.setAluno(aluno);
                        inscricaoDAO.save(inscricao);
                    }
                }
            }
            return ResponseEntity.ok(Map.of("message", "Arquivo CSV processado e alunos importados com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar o CSV: " + e.getMessage()));
        }
    }

    // ENCERRAMENTO DEFINITIVO DA OFERTA

    // ENCERRAMENTO VIA API
    @PostMapping("/ofertas/{id}/encerrar")
    public ResponseEntity<Map<String, String>> encerrarApi(@PathVariable Long id) {
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"Aguardando encerramento do secretário".equals(oferta.getStatusCalculado())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Esta oferta não está aguardando encerramento."));
        }

        ofertaService.encerrar(id, "api-admin@ufscar.br");
        return ResponseEntity.ok(Map.of("message", "Oferta encerrada com sucesso via API REST e créditos atribuídos."));
    }
}
