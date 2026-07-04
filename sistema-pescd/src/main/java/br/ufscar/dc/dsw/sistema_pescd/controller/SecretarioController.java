package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.dao.InscricaoDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.dao.OfertaDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IOfertaService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IUsuarioService;
import br.ufscar.dc.dsw.sistema_pescd.service.impl.InscricaoService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/secretario")
public class SecretarioController {

    private final IOfertaService ofertaService;
    private final IUsuarioService usuarioService;
    private final UsuarioDAO usuarioDAO;
    private final InscricaoDAO inscricaoDAO;
    private final OfertaDAO ofertaDAO;
    private final InscricaoService inscricaoService;


    // Construtor único para injeção automática de dependências
    public SecretarioController(IOfertaService ofertaService,
                                IUsuarioService usuarioService,
                                UsuarioDAO usuarioDAO,
                                InscricaoDAO inscricaoDAO,
                                OfertaDAO ofertaDAO,
                                InscricaoService inscricaoService) {
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.usuarioDAO = usuarioDAO;
        this.inscricaoDAO = inscricaoDAO;
        this.ofertaDAO = ofertaDAO;
        this.inscricaoService = inscricaoService;
    }


    // NAVEGAÇÃO BÁSICA E VISUALIZAÇÃO


    @GetMapping("/home")
    public String home() {
        return "secretario/home";
    }

    @GetMapping("/ofertas")
    public String listar(Model model) {
        // 1. Busca todas as ofertas
        List<Oferta> todasAsOfertas = ofertaService.buscarTodosOrdenado();

        // 2. Filtra removendo as ofertas cujo status calculado seja "Concluída"
        List<Oferta> ofertasAtivas = todasAsOfertas.stream()
                .filter(o -> !o.getStatusCalculado().equals("Concluída"))
                .toList();

        // 3. Envia apenas as ativas para a tabela HTML
        model.addAttribute("ofertas", ofertasAtivas);
        return "secretario/oferta/lista";
    }

    @GetMapping("/ofertas/cadastrar")
    public String cadastrar(Oferta oferta, Model model) {
        // Injeta a lista de professores para popular o <select> do formulário de cadastro
        model.addAttribute("professores", usuarioService.buscarProfessores());
        return "secretario/oferta/cadastro";
    }

    @GetMapping("/ofertas/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, Model model) {
        model.addAttribute("oferta", ofertaService.buscarPorId(id));
        model.addAttribute("professores", usuarioService.buscarProfessores());
        return "secretario/oferta/cadastro";
    }

    @GetMapping("/ofertas/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
        ofertaService.excluir(id);
        attr.addFlashAttribute("success", "Oferta excluída com sucesso.");
        return "redirect:/secretario/ofertas";
    }



    // HISTÓRIA S.01: CRIAÇÃO DE OFERTA COM METADADOS E FALLBACK DE NOME


    @PostMapping("/ofertas/salvar")
    public String salvar(@Valid Oferta oferta,
                         BindingResult result,
                         RedirectAttributes attr,
                         Model model,
                         java.security.Principal principal) {

        // RN-1: Fallback automático se o nome da oferta for deixado em branco
        if (oferta.getNome() == null || oferta.getNome().trim().isEmpty()) {
            oferta.setNome("Oferta - Semestre " + oferta.getSemestre());
        }

        // Validação de restrições do formulário
        if (result.hasErrors()) {
            model.addAttribute("professores", usuarioService.buscarProfessores());
            return "secretario/oferta/cadastro";
        }

        // Validação lógica de consistência cronológica de datas
        if (oferta.getDataFim().isBefore(oferta.getDataInicio())) {
            model.addAttribute("erro", "Data final deve ser posterior à inicial");
            model.addAttribute("professores", usuarioService.buscarProfessores());
            return "secretario/oferta/cadastro";
        }

        // RN-2: Captura automática e gravação do Timestamp e Usuário logado (Auditoria)
        oferta.setDataCriacao(LocalDateTime.now());
        String usuarioLogado = (principal != null) ? principal.getName() : "secretario@ufscar.br";
        oferta.setUsuarioCriador(usuarioLogado);

        ofertaService.salvar(oferta);

        attr.addFlashAttribute("success", "Oferta salva com sucesso.");
        return "redirect:/secretario/ofertas";
    }



    // HISTÓRIA S.02: VINCULAÇÃO E MATRÍCULA DE ALUNOS (MANUAL E EM LOTE VIA CSV)


    // Exibe o painel de gerenciamento de alunos de uma oferta específica
    @GetMapping("/ofertas/{id}/alunos")
    public String gerenciarAlunos(@PathVariable("id") Long id, Model model) {
        Oferta oferta = ofertaDAO.findById(id).orElse(null);
        if (oferta == null) {
            return "redirect:/secretario/ofertas";
        }

        List<Inscricao> inscricoes = inscricaoDAO.findByOferta(oferta);
        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricoes", inscricoes);
        return "secretario/oferta/alunos";
    }

    @GetMapping("/ofertas/{oid}/alunos/excluir/{id}")
    public String excluirAluno(@PathVariable("oid") Long oid, @PathVariable("id") Long id, RedirectAttributes attr) {
        inscricaoDAO.deleteById(id);
        attr.addFlashAttribute("success", "Aluno removido da oferta com sucesso.");
        return "redirect:/secretario/ofertas/" + oid + "/alunos";
    }

    // Fluxo A: Cadastro Manual de Aluno na Oferta (S.02 - RN-1 e RN-2)
    @PostMapping("/ofertas/{id}/alunos/manual")
    public String adicionarAlunoManual(@PathVariable("id") Long ofertaId,
                                       @RequestParam("nome") String nome,
                                       @RequestParam("email") String email,
                                       @RequestParam("senha") String senha,
                                       RedirectAttributes attr) {
        try {
            Oferta oferta = ofertaDAO.findById(ofertaId).orElseThrow();

            // RN-2: Verifica se o usuário correspondente ao e-mail já existe no banco
            Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

            if (aluno == null) {
                // RN-1: Se não existir, realiza o cadastro prévio com perfil ALUNO
                aluno = new Usuario();
                aluno.setNome(nome);
                aluno.setUsername(email);
                aluno.setPassword(senha);
                aluno.setRole(Usuario.Role.ALUNO);
                usuarioDAO.save(aluno);
            }

            // RN-2: Evita duplicidade de matrícula do mesmo aluno na mesma oferta
            if (inscricaoDAO.existsByOfertaAndAluno(oferta, aluno)) {
                attr.addFlashAttribute("erro", "Este aluno já está inscrito nesta oferta!");
            } else {
                Inscricao inscricao = new Inscricao();
                inscricao.setOferta(oferta);
                inscricao.setAluno(aluno);
                inscricaoDAO.save(inscricao);
                attr.addFlashAttribute("success", "Aluno adicionado com sucesso!");
            }
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao adicionar aluno: " + e.getMessage());
        }
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    // Fluxo B: Upload e Processamento de Arquivo CSV (S.02 - RN-3)
    @PostMapping("/ofertas/{id}/alunos/csv")
    public String importarAlunosCsv(@PathVariable("id") Long ofertaId,
                                    @RequestParam("file") MultipartFile file,
                                    RedirectAttributes attr) {
        if (file.isEmpty()) {
            attr.addFlashAttribute("erro", "Por favor, selecione um arquivo CSV.");
            return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
        }

        try {
            Oferta oferta = ofertaDAO.findById(ofertaId).orElseThrow();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                String cabecalho = reader.readLine(); // Descarta o cabeçalho descritivo do CSV

                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (linha.trim().isEmpty()) continue;

                    String[] colunas = linha.split(",");
                    String ra = colunas[0].trim();
                    String nomeCompleto = colunas[1].trim();
                    String email = colunas[2].trim();

                    // Procura pelo aluno usando o e-mail informado
                    Usuario aluno = usuarioDAO.findByUsername(email).orElse(null);

                    if (aluno == null) {
                        // RN-3: Se não cadastrado, cria credenciais padrão (Username=Email, Senha=RA)
                        aluno = new Usuario();
                        aluno.setNome(nomeCompleto);
                        aluno.setUsername(email);
                        aluno.setPassword(ra);
                        aluno.setRole(Usuario.Role.ALUNO);
                        usuarioDAO.save(aluno);
                    }

                    // Inscreve o aluno na oferta caso o vínculo ainda não exista
                    if (!inscricaoDAO.existsByOfertaAndAluno(oferta, aluno)) {
                        Inscricao inscricao = new Inscricao();
                        inscricao.setOferta(oferta);
                        inscricao.setAluno(aluno);
                        inscricaoDAO.save(inscricao);
                    }
                }
            }
            attr.addFlashAttribute("success", "Arquivo CSV processado com sucesso!");
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao processar o CSV: " + e.getMessage());
        }

        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }



    // HISTÓRIA S.03: DETALHES, INSCRIÇÕES E HISTÓRICO INDIVIDUAL DO ALUNO


    // Permite analisar a oferta de forma macro (dados gerais e lista completa de alunos)
    @GetMapping("/ofertas/{id}/detalhes")
    public String detalhesOferta(@PathVariable("id") Long id, Model model) {
        Oferta oferta = ofertaDAO.findById(id).orElse(null);
        if (oferta == null) {
            return "redirect:/secretario/ofertas";
        }

        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricoes", inscricaoDAO.findByOferta(oferta));
        return "secretario/oferta/detalhes";
    }

    // Permite analisar a inscrição de forma micro (dados individuais de histórico de um aluno)
    @GetMapping("/inscricao/{id}/detalhes")
    public String detalhesInscricao(@PathVariable("id") Long id, Model model) {
        Inscricao inscricao = inscricaoDAO.findById(id).orElse(null);
        if (inscricao == null) {
            return "redirect:/secretario/ofertas";
        }

        model.addAttribute("inscricao", inscricao);
        return "secretario/oferta/detalhes_aluno";
    }


    // HISTÓRIA S.04: ENCERRAMENTO DEFINITIVO DA OFERTA E AUDITORIA


    @PostMapping("/ofertas/{id}/encerrar")
    public String encerrarOferta(@PathVariable("id") Long id,
                                 @RequestParam(value = "instrucoes", required = false) String instrucoes,
                                 RedirectAttributes attr,
                                 java.security.Principal principal) {
        try {
            Oferta oferta = ofertaService.buscarPorId(id);

            if (oferta == null) {
                attr.addFlashAttribute("erro", "Oferta não encontrada.");
                return "redirect:/secretario/ofertas";
            }

            if (!"Aguardando encerramento do secretário".equals(oferta.getStatusCalculado())) {
                attr.addFlashAttribute("erro", "Esta oferta não está aguardando encerramento.");
                return "redirect:/secretario/ofertas";
            }

            String usuarioLogado = (principal != null) ? principal.getName() : "secretario@ufscar.br";
            LocalDateTime timestampEncerramento = LocalDateTime.now();

            System.out.println("=== LOG DE ENCERRAMENTO (S.04) ===");
            System.out.println("Oferta ID: " + id);
            System.out.println("Status Alterado para: Concluída");
            System.out.println("Timestamp do Encerramento: " + timestampEncerramento);
            System.out.println("Responsável: " + usuarioLogado);
            System.out.println("Instruções de Encerramento (RN-3): " + instrucoes);
            System.out.println("==================================");

            // CHAMA O SERVICE QUE ATUALIZA O STATUS NO BANCO
            ofertaService.encerrar(id, usuarioLogado);

            attr.addFlashAttribute("success", "Oferta encerrada com sucesso e créditos atribuídos aos alunos!");

        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao encerrar a oferta: " + e.getMessage());
        }

        return "redirect:/secretario/ofertas";
    }
}
