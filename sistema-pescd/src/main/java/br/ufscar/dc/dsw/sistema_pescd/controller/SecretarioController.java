package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.domain.Inscricao;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IOfertaService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IUsuarioService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IInscricaoService;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/secretario")
@RequiredArgsConstructor
public class SecretarioController {

    private final IOfertaService ofertaService;
    private final IUsuarioService usuarioService;
    private final IInscricaoService inscricaoService;

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

    // Exibe a tela de gerenciamento de alunos inscritos em uma oferta específica
    @GetMapping("/ofertas/{id}/alunos")
    public String gerenciarAlunos(@PathVariable("id") Long id, Model model) {
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return "redirect:/secretario/ofertas";
        }

        List<Inscricao> inscricoes = inscricaoService.buscarPorOferta(oferta);
        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricoes", inscricoes);
        return "secretario/oferta/alunos";
    }

    // Exclui a inscrição de um aluno na oferta
    @GetMapping("/ofertas/{oid}/alunos/excluir/{id}")
    public String excluirAluno(@PathVariable("oid") Long oid, @PathVariable("id") Long id, RedirectAttributes attr) {
        inscricaoService.excluir(id);
        attr.addFlashAttribute("success", "Aluno removido da oferta com sucesso.");
        return "redirect:/secretario/ofertas/" + oid + "/alunos";
    }

    // Aciona o serviço para realizar a matrícula manual de um aluno
    @PostMapping("/ofertas/{id}/alunos/manual")
    public String adicionarAlunoManual(@PathVariable("id") Long ofertaId,
                                       @RequestParam("nome") String nome,
                                       @RequestParam("email") String email,
                                       @RequestParam("senha") String senha,
                                       RedirectAttributes attr) {
        try {
            // Refatorado: Delega o cadastro e a matrícula manual para a camada de serviço
            inscricaoService.matricularAlunoManual(ofertaId, nome, email, senha);
            attr.addFlashAttribute("success", "Aluno adicionado com sucesso!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Captura erros de validação ou de negócio disparados pelo serviço
            attr.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao adicionar aluno: " + e.getMessage());
        }
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    // Aciona o serviço para realizar a importação de alunos em lote via arquivo CSV
    @PostMapping("/ofertas/{id}/alunos/csv")
    public String importarAlunosCsv(@PathVariable("id") Long ofertaId,
                                    @RequestParam("file") MultipartFile file,
                                    RedirectAttributes attr) {
        try {
            // Refatorado: Delega a leitura, parseamento do CSV e o cadastro em lote para a camada de serviço
            inscricaoService.importarAlunosCsv(ofertaId, file);
            attr.addFlashAttribute("success", "Arquivo CSV processado com sucesso!");
        } catch (RuntimeException e) {
            attr.addFlashAttribute("erro", e.getMessage());
        } catch (Exception e) {
            attr.addFlashAttribute("erro", "Erro ao processar o CSV: " + e.getMessage());
        }
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    // HISTÓRIA S.03: DETALHES, INSCRIÇÕES E HISTÓRICO INDIVIDUAL DO ALUNO

    // Permite analisar a oferta de forma macro (dados gerais e lista completa de alunos)
    @GetMapping("/ofertas/{id}/detalhes")
    public String detalhesOferta(@PathVariable("id") Long id, Model model) {
        Oferta oferta = ofertaService.buscarPorId(id);
        if (oferta == null) {
            return "redirect:/secretario/ofertas";
        }

        model.addAttribute("oferta", oferta);
        model.addAttribute("inscricoes", inscricaoService.buscarPorOferta(oferta));
        return "secretario/oferta/detalhes";
    }

    // Permite analisar a inscrição de forma micro (dados individuais de histórico de um aluno)
    @GetMapping("/inscricao/{id}/detalhes")
    public String detalhesInscricao(@PathVariable("id") Long id, Model model) {
        Inscricao inscricao = inscricaoService.buscarPorId(id);
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

    // USER STORY SURPRESA: EXPORTAR RELATÓRIO EM TEXTO DOS ALUNOS DA OFERTA

    // Mapeia uma requisição HTTP GET para esta URL, capturando o ID da oferta
    @GetMapping("/ofertas/{id}/alunos/exportar")
    // Indica que o retorno do método deve ser escrito diretamente no corpo da resposta HTTP
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> exportarAlunos(@PathVariable("id") Long id) {

        // 1. Busca a oferta no banco de dados usando o ID fornecido na URL
        Oferta oferta = ofertaService.buscarPorId(id);

        // Se a oferta não existir, retorna um erro
        if (oferta == null) return org.springframework.http.ResponseEntity.notFound().build();

        // 2. Busca todas as inscrições (matrículas) vinculadas àquela oferta específica
        List<Inscricao> inscricoes = inscricaoService.buscarPorOferta(oferta);

        // 3. Instancia um StringBuilder para montar o conteúdo do arquivo de texto
        StringBuilder txt = new StringBuilder();

        // Escreve o título do relatório
        txt.append("=== LISTA DE ALUNOS - ").append(oferta.getNome()).append(" ===\n");

        // Escreve o cabeçalho das colunas do relatório
        txt.append("RA, Nome, Email\n");

        // 4. Percorre a lista de inscrições para extrair os dados de cada aluno
        for (Inscricao ins : inscricoes) {
            txt.append(ins.getAluno().getPassword()).append(", ")
                    .append(ins.getAluno().getNome()).append(", ")
                    .append(ins.getAluno().getUsername()).append("\n");
        }

        // 5. Converte todo o texto acumulado no StringBuilder em um array de bytes
        byte[] csvBytes = txt.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // 6. Monta e retorna a resposta
        return org.springframework.http.ResponseEntity.ok()
                // Cabeçalho que força o navegador a abrir a janela de download como um anexo com o nome definido
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=alunos_oferta_" + id + ".txt")
                // Define o tipo de mídia da resposta como texto puro
                .contentType(org.springframework.http.MediaType.TEXT_PLAIN)
                // Injeta o array de bytes (o conteúdo do arquivo)
                .body(csvBytes);
    }
}
