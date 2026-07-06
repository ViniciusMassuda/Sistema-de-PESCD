package br.ufscar.dc.dsw.sistema_pescd.controller.api;

import br.ufscar.dc.dsw.sistema_pescd.dto.AcaoInscricaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EncerrarOfertaRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.EstatisticasOfertaResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.InscricaoDTO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IProfessorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller para o perfil do Professor (Supervisor e Responsável).
 * Todos os endpoints utilizam o parâmetro 'username' como simulação temporária
 * de autenticação para testes independentes no Postman (sem JWT configurado).
 */
@RestController
@RequestMapping("/api/professor")
@RequiredArgsConstructor
@Tag(name = "Professor", description = "Endpoints do perfil Professor (Supervisor e Responsável)")
public class ProfessorRestController {

    private final IProfessorService professorService;

    @GetMapping("/lista-alunos")
    @Operation(summary = "Listar alunos vinculados",
            description = "Retorna todas as inscrições vinculadas ao professor (como supervisor ou responsável).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Professor não encontrado")
    })
            // Busca todos os alunos que este professor orienta (seja como supervisor ou responsável)
    public ResponseEntity<?> listarAlunos(@RequestParam("username") String username) {
        try {
            List<InscricaoDTO> lista = professorService.listarAlunosVinculados(username);
            return ResponseEntity.ok(lista);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/aprovar-plano")
    @Operation(summary = "Aprovar plano de trabalho",
            description = "O professor supervisor aprova o plano de trabalho do aluno. Status esperado: PLANO_ENVIADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano aprovado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio")
    })
            // Recebe o ID da inscrição via JSON, valida as regras de negócio e aprova o plano de trabalho
    public ResponseEntity<?> aprovarPlano(@RequestBody AcaoInscricaoRequestDTO request,
                                          @RequestParam("username") String username) {
        try {
            professorService.aprovarPlano(request, username);
            return ResponseEntity.ok(Map.of("message", "Plano de trabalho aprovado com sucesso."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/aprovar-relatorio")
    @Operation(summary = "Aprovar relatório de estágio",
            description = "O professor supervisor avalia o relatório com parecer, frequência e nota sugerida. "
                    + "Status esperado: RELATORIO_ENVIADO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório aprovado pelo supervisor"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio")
    })
            // O Professor Supervisor aprova o relatório do aluno e sugere uma nota/frequência
    public ResponseEntity<?> aprovarRelatorio(@RequestBody AcaoInscricaoRequestDTO request,
                                              @RequestParam("username") String username) {
        try {
            professorService.aprovarRelatorio(request, username);
            return ResponseEntity.ok(Map.of("message", "Relatório aprovado pelo supervisor com sucesso."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/concluir-relatorio")
    @Operation(summary = "Concluir relatório (responsável)",
            description = "O professor responsável dá o parecer final do relatório. "
                    + "Status esperado: RELATORIO_APROVADO_SUPERVISOR.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório concluído pelo responsável"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio")
    })
            // O Professor Responsável dá o parecer final e definitivo após o supervisor ter aprovado
    public ResponseEntity<?> concluirRelatorio(@RequestBody AcaoInscricaoRequestDTO request,
                                               @RequestParam("username") String username) {
        try {
            professorService.concluirRelatorio(request, username);
            return ResponseEntity.ok(Map.of("message", "Relatório concluído pelo professor responsável."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/avaliar-documentacao")
    @Operation(summary = "Avaliar documentação de dispensa",
            description = "O professor responsável avalia a documentação comprobatória de dispensa. "
                    + "Status esperado: DOCUMENTACAO_ENVIADA.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documentação avaliada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio")
    })
            // Avalia os documentos PDF de alunos que solicitaram dispensa ou aproveitamento de horas
    public ResponseEntity<?> avaliarDocumentacao(@RequestBody AcaoInscricaoRequestDTO request,
                                                 @RequestParam("username") String username) {
        try {
            professorService.avaliarDocumentacao(request, username);
            return ResponseEntity.ok(Map.of("message", "Documentação de dispensa avaliada com sucesso."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/encerrar-oferta/{ofertaId}")
    @Operation(summary = "Consultar estatísticas para encerramento",
            description = "Valida pré-condições e calcula estatísticas consolidadas da oferta. "
                    + "Só avança se todos os alunos estiverem CONCLUIDO_RESPONSAVEL.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estatísticas calculadas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Pré-condições não atendidas")
    })
            // Valida se a oferta pode ser encerrada (todos alunos concluídos) e calcula as estatísticas finais
    public ResponseEntity<?> buscarEstatisticasOferta(@PathVariable Long ofertaId,
                                                      @RequestParam("username") String username) {
        try {
            EstatisticasOfertaResponseDTO dto = professorService.buscarEstatisticasOferta(ofertaId, username);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/encerrar-oferta")
    @Operation(summary = "Encerrar oferta",
            description = "Salva as lições aprendidas e marca a oferta como concluída pelo professor. "
                    + "A oferta ficará aguardando o encerramento definitivo do secretário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Oferta encerrada com sucesso pelo professor"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou regra de negócio")
    })
            // Salva as lições aprendidas (feedback) e marca a oferta como encerrada pelo professor
    public ResponseEntity<?> encerrarOferta(@RequestBody EncerrarOfertaRequestDTO request,
                                            @RequestParam("username") String username) {
        try {
            professorService.encerrarOferta(request, username);
            return ResponseEntity.ok(Map.of("message",
                    "Oferta encerrada com sucesso. Aguardando encerramento definitivo do secretário."));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
