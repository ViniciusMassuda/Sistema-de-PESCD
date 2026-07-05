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

// Define a classe como um controlador REST que gerencia respostas diretamente no corpo HTTP (JSON/Texto)
@RestController
// Centraliza o prefixo base de todas as URLs mapeadas neste controlador
@RequestMapping("/api/secretario")
public class SecretarioRestController {

    // Atributos privados e imutáveis das dependências necessárias
    private final IOfertaService ofertaService;
    private final OfertaDAO ofertaDAO;
    private final InscricaoDAO inscricaoDAO;
    private final UsuarioDAO usuarioDAO;

    // Construtor único para injeção de dependências automática gerenciada pelo Spring Framework
    public SecretarioRestController(IOfertaService ofertaService,
                                    OfertaDAO ofertaDAO,
                                    InscricaoDAO inscricaoDAO,
                                    UsuarioDAO usuarioDAO) {
        this.ofertaService = ofertaService;
        this.ofertaDAO = ofertaDAO;
        this.inscricaoDAO = inscricaoDAO;
        this.usuarioDAO = usuarioDAO;
    }


    // NAVEGAÇÃO BÁSICA, LEITURA E OPERAÇÕES CRUD DE OFERTAS

    // LISTAR TODAS AS OFERTAS ATIVAS
    @GetMapping("/ofertas")
    public ResponseEntity<List<Oferta>> listarTodas() {
        // Busca a lista completa de ofertas ordenadas pela camada de serviço
        List<Oferta> todasAsOfertas = ofertaService.buscarTodosOrdenado();

        // Filtra a lista via Stream removendo as ofertas cujo estado situacional seja "Concluída"
        List<Oferta> ofertasAtivas = todasAsOfertas.stream()
                .filter(o -> !o.getStatusCalculado().equals("Concluída"))
                .toList();

        // Retorna HTTP 200 OK contendo apenas as ofertas ativas no formato JSON
        return ResponseEntity.ok(ofertasAtivas);
    }

    // BUSCAR UMA OFERTA POR ID
    @GetMapping("/ofertas/{id}")
    public ResponseEntity<Oferta> buscarPorId(@PathVariable Long id) {
        // Realiza a busca no service utilizando o ID capturado no path da URL
        Oferta oferta = ofertaService.buscarPorId(id);

        // Se a oferta não for encontrada, retorna resposta HTTP 404 Not Found
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        // Se encontrada, retorna HTTP 200 OK com o objeto Oferta no corpo da resposta
        return ResponseEntity.ok(oferta);
    }

    // EXCLUIR OFERTA
    @DeleteMapping("/ofertas/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Busca a oferta antes da exclusão para verificar sua existência
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        // Executa o comando de deleção através do service
        ofertaService.excluir(id);

        // Retorna HTTP 204 No Content indicando sucesso na deleção sem corpo de resposta
        return ResponseEntity.noContent().build();
    }


    // HISTÓRIA S.01: CRIAÇÃO DE OFERTA COM METADADOS E FALLBACK DE NOME

    // CADASTRAR NOVA OFERTA
    @PostMapping("/ofertas")
    public ResponseEntity<?> criar(@RequestBody Oferta oferta) {
        // RN-1: Fallback automático se o nome for deixado nulo ou em branco pelo cliente
        if (oferta.getNome() == null || oferta.getNome().trim().isEmpty()) {
            oferta.setNome("Oferta - Semestre " + oferta.getSemestre());
        }

        // Validação lógica extraída do controller original: Consistência Cronológica das datas
        if (oferta.getDataFim() != null && oferta.getDataInicio() != null &&
                oferta.getDataFim().isBefore(oferta.getDataInicio())) {
            // Retorna HTTP 400 Bad Request se a data final for anterior à data inicial
            return ResponseEntity.badRequest().body(Map.of("error", "Data final deve ser posterior à inicial"));
        }

        // RN-2: Metadados para fins de auditoria inseridos de forma automatizada
        oferta.setDataCriacao(LocalDateTime.now());
        oferta.setUsuarioCriador("api-admin@ufscar.br");

        // Salva a nova oferta persistindo as alterações no banco
        ofertaService.salvar(oferta);

        // Retorna HTTP 200 OK com a oferta recém-criada
        return ResponseEntity.ok(oferta);
    }


    // HISTÓRIA S.02: VINCULAÇÃO E MATRÍCULA DE ALUNOS (MANUAL E EM LOTE VIA CSV)

    // FLUXO A: CADASTRO MANUAL DE ALUNO
    @PostMapping("/ofertas/{id}/alunos")
    public ResponseEntity<Map<String, String>> adicionarAlunoManual(@PathVariable("id") Long ofertaId,
                                                                    @RequestBody Map<String, String> payload) {
        try {
            // Busca a oferta correspondente para validar sua existência
            Oferta oferta = ofertaDAO.findById(ofertaId).orElse(null);
            if (oferta == null) return ResponseEntity.notFound().build();

            // Extrai as informações enviadas na estrutura JSON recebida no corpo do payload
            String nome = payload.get("nome");
            String email = payload.get("email");
            String senha = payload.get("senha");

            // RN-2: Verifica se o e-mail (username único) já existe cadastrado no banco de dados
            Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

            if (aluno == null) {
                // RN-1: Se não existir pré-cadastro, cria o novo usuário com perfil e permissões de ALUNO
                aluno = new Usuario();
                aluno.setNome(nome);
                aluno.setUsername(email);
                aluno.setPassword(senha);
                aluno.setRole(Usuario.Role.ALUNO);
                usuarioDAO.save(aluno);
            }

            // RN-2: Evita duplicidade de matrícula impedindo que o mesmo aluno entre na mesma oferta
            if (inscricaoDAO.existsByOfertaAndAluno(oferta, aluno)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Este aluno já está inscrito nesta oferta!"));
            }

            // Instancia e salva a nova inscrição realizando o vínculo associativo
            Inscricao inscricao = new Inscricao();
            inscricao.setOferta(oferta);
            inscricao.setAluno(aluno);
            inscricaoDAO.save(inscricao);

            // Retorna confirmação de sucesso com HTTP 200 OK
            return ResponseEntity.ok(Map.of("message", "Aluno adicionado com sucesso via API!"));
        } catch (Exception e) {
            // Captura falhas inesperadas retornando erro interno do servidor com HTTP 500
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // FLUXO B: UPLOAD E PROCESSAMENTO DE ARQUIVO CSV
    @PostMapping(value = "/ofertas/{id}/alunos/csv", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, String>> importarAlunosCsv(@PathVariable("id") Long ofertaId,
                                                                 @RequestParam("file") MultipartFile file) {
        // Retorna erro HTTP 400 se o arquivo multipart enviado estiver vazio
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Por favor, selecione um arquivo CSV."));
        }

        try {
            // Valida a existência da oferta enviada no path da requisição
            Oferta oferta = ofertaDAO.findById(ofertaId).orElse(null);
            if (oferta == null) return ResponseEntity.notFound().build();

            // Abre e gerencia o stream de leitura do arquivo em memória codificado em UTF-8
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String cabecalho = reader.readLine(); // Descarta a primeira linha correspondente ao cabeçalho descritivo
                String linha;

                // Executa a iteração linha por linha do corpo de dados do arquivo CSV
                while ((linha = reader.readLine()) != null) {
                    if (linha.trim().isEmpty()) continue; // Salta linhas em branco acidentais

                    // Quebra a string da linha usando o delimitador por vírgula padrão
                    String[] colunas = linha.split(",");
                    String ra = colunas[0].trim();
                    String nomeCompleto = colunas[1].trim();
                    String email = colunas[2].trim();

                    // Procura pelo aluno no sistema usando o endereço de e-mail mapeado
                    Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

                    if (aluno == null) {
                        // RN-3: Se não cadastrado previamente, gera credenciais baseadas no padrão (Username=Email, Senha=RA)
                        aluno = new Usuario();
                        aluno.setNome(nomeCompleto);
                        aluno.setUsername(email);
                        aluno.setPassword(ra);
                        aluno.setRole(Usuario.Role.ALUNO);
                        usuarioDAO.save(aluno);
                    }

                    // Se a matrícula não for duplicada, cria o registro associativo de inscrição para a oferta
                    if (!inscricaoDAO.existsByOfertaAndAluno(oferta, aluno)) {
                        Inscricao inscricao = new Inscricao();
                        inscricao.setOferta(oferta);
                        inscricao.setAluno(aluno);
                        inscricaoDAO.save(inscricao);
                    }
                }
            }
            // Retorna resposta de processamento concluído com HTTP 200 OK
            return ResponseEntity.ok(Map.of("message", "Arquivo CSV processado e alunos importados com sucesso!"));
        } catch (Exception e) {
            // Em caso de falha de I/O ou processamento, retorna erro estruturado com HTTP 500
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar o CSV: " + e.getMessage()));
        }
    }


    // HISTÓRIA S.03: DETALHES, INSCRIÇÕES E HISTÓRICO INDIVIDUAL DO ALUNO

    // LISTAR ALUNOS MATRICULADOS EM UMA OFERTA
    @GetMapping("/ofertas/{id}/alunos")
    public ResponseEntity<List<Usuario>> listarAlunosDaOferta(@PathVariable("id") Long id) {
        // Valida se a oferta cujo ID foi passado de fato existe
        Oferta oferta = ofertaDAO.findById(id).orElse(null);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        // Obtém todas as inscrições para mapear os dados da visão macro
        List<Inscricao> inscricoes = inscricaoDAO.findByOferta(oferta);

        // Isola e extrai apenas a entidade Aluno de cada inscrição gerando uma lista limpa de usuários
        List<Usuario> alunos = inscricoes.stream()
                .map(Inscricao::getAluno)
                .toList();

        // Retorna a lista contendo as entidades dos alunos filtrados
        return ResponseEntity.ok(alunos);
    }

    // BUSCAR DETALHES DE UMA INSCRIÇÃO ESPECÍFICA POR ID (Visão Micro/Histórico)
    @GetMapping("/inscricoes/{id}")
    public ResponseEntity<Inscricao> buscarInscricaoPorId(@PathVariable Long id) {
        // Busca a inscrição pelo ID do aluno
        Inscricao inscricao = inscricaoDAO.findById(id).orElse(null);
        if (inscricao == null) {
            return ResponseEntity.notFound().build();
        }
        // Retorna a inscrição correspondente com seus metadados de relacionamento carregados
        return ResponseEntity.ok(inscricao);
    }


    // HISTÓRIA S.04: ENCERRAMENTO DEFINITIVO DA OFERTA E AUDITORIA

    // ENCERRAMENTO VIA API
    @PostMapping("/ofertas/{id}/encerrar")
    public ResponseEntity<Map<String, String>> encerrarApi(@PathVariable Long id) {
        // Coleta e valida a existência da oferta enviada para finalização
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return ResponseEntity.notFound().build();
        }

        // Garante a transição de estados validando se a situação calculada permite o encerramento do secretário
        if (!"Aguardando encerramento do secretário".equals(oferta.getStatusCalculado())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Esta oferta não está aguardando encerramento."));
        }

        // Executa o gatilho de alteração no banco e cômputo dos créditos dos alunos matriculados
        ofertaService.encerrar(id, "api-admin@ufscar.br");
        return ResponseEntity.ok(Map.of("message", "Oferta encerrada com sucesso via API REST e créditos atribuídos."));
    }


    // U.S. SURPRESA - EXPORTAR RELATÓRIO EM TEXTO DOS ALUNOS DA OFERTA

    @GetMapping("/ofertas/{id}/alunos/exportar")
    public ResponseEntity<String> exportarAlunosTxt(@PathVariable Long id) {
        // Valida se a oferta alvo está cadastrada no sistema
        Oferta oferta = ofertaDAO.findById(id).orElse(null);
        if (oferta == null) return ResponseEntity.notFound().build();

        // Obtém a lista completa de inscrições associadas à disciplina
        List<Inscricao> inscricoes = inscricaoDAO.findByOferta(oferta);

        // Instancia um buffer StringBuilder para compilar o documento em modo de texto estruturado
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("Relatório de Matriculados - ").append(oferta.getNome()).append("\n");
        relatorio.append("--------------------------------------------------\n");

        // Consolida os dados dos estudantes linha por linha no buffer de memória
        for (Inscricao ins : inscricoes) {
            relatorio.append("Nome: ").append(ins.getAluno().getNome())
                    .append(" | Email: ").append(ins.getAluno().getUsername())
                    .append("\n");
        }

        // Retorna HTTP 200 OK com o conteúdo textual final em formato de String limpa no corpo da resposta
        return ResponseEntity.ok(relatorio.toString());
    }

}
