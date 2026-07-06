package br.ufscar.dc.dsw.sistema_pescd.controller.api;

import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.DocumentacaoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.PlanoTrabalhoRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.request.RelatorioRequestDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.DocumentacaoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.PlanoTrabalhoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.RelatorioResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller para o perfil do Aluno.
 * Todos os endpoints utilizam o parâmetro 'username' como simulação temporária
 * de autenticação para testes independentes no Postman (sem JWT configurado).
 */
@RestController
@RequestMapping("/api/aluno")
@RequiredArgsConstructor
@Tag(name = "Aluno", description = "Endpoints do perfil Aluno (Submissão de Plano, Relatório e Documentação)")
public class AlunoRestController {

    private final IAlunoService alunoService;
    private final UsuarioDAO usuarioDAO;

    @GetMapping("/ofertas")
    @Operation(summary = "Listar ofertas do aluno",
            description = "Retorna todas as ofertas em que o aluno está inscrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Aluno não encontrado")
    })
    public ResponseEntity<?> listarOfertas(@RequestParam("username") String username) {
        try {
            Usuario aluno = buscarAluno(username);
            List<OfertaAlunoResponseDTO> ofertas = alunoService.buscarOfertasPorAluno(aluno);
            return ResponseEntity.ok(ofertas);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping(value = "/oferta/{id}/plano", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar plano de trabalho",
            description = "Submete o plano de trabalho em PDF para a oferta especificada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação, arquivo inválido ou regra de negócio")
    })
    public ResponseEntity<?> enviarPlano(@PathVariable Long id,
                                         @Valid @ModelAttribute PlanoTrabalhoRequestDTO request,
                                         @RequestParam("username") String username) {
        try {
            Usuario aluno = buscarAluno(username);
            PlanoTrabalhoResponseDTO response = alunoService.enviarPlanoTrabalho(id, aluno, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar o upload: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/oferta/{id}/documentacao", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar documentação comprobatória",
            description = "Submete a documentação comprobatória em PDF para a oferta especificada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Documentação enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação, arquivo inválido ou regra de negócio")
    })
    public ResponseEntity<?> enviarDocumentacao(@PathVariable Long id,
                                                @Valid @ModelAttribute DocumentacaoRequestDTO request,
                                                @RequestParam("username") String username) {
        try {
            Usuario aluno = buscarAluno(username);
            DocumentacaoResponseDTO response = alunoService.enviarDocumentacao(id, aluno, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar o upload: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/oferta/{id}/relatorio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Enviar relatório de estágio",
            description = "Submete o relatório final em PDF para a oferta especificada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relatório enviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação, arquivo inválido ou regra de negócio")
    })
    public ResponseEntity<?> enviarRelatorio(@PathVariable Long id,
                                             @Valid @ModelAttribute RelatorioRequestDTO request,
                                             @RequestParam("username") String username) {
        try {
            Usuario aluno = buscarAluno(username);
            RelatorioResponseDTO response = alunoService.enviarRelatorio(id, aluno, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Erro ao processar o upload: " + e.getMessage()));
        }
    }

    private Usuario buscarAluno(String username) {
        return usuarioDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado: " + username));
    }
}
