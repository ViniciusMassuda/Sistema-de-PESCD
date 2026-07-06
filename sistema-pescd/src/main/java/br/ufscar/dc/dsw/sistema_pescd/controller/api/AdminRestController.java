package br.ufscar.dc.dsw.sistema_pescd.controller.api;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final IUsuarioService service;

    // Injeção por construtor sem @Autowired
    public AdminRestController(IUsuarioService service) {
        this.service = service;
    }

    // 1. Listar secretários e professores
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = service.buscarSecretariosEProfessores();
        return ResponseEntity.ok(usuarios);
    }

    // 2. Buscar um usuário específico pelo ID
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable("id") Long id) {
        Usuario usuario = service.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    // 3. Criar/Salvar um novo usuário
    @PostMapping("/usuarios")
    public ResponseEntity<Usuario> salvar(@Valid @RequestBody Usuario usuario) {
        service.salvar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    // 4. Editar dados de um usuário existente
    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @Valid @RequestBody Usuario dadosAtualizados) {
        // 1. Busca o usuário real do banco
        Usuario usuarioExistente = service.buscarPorId(id);
        if (usuarioExistente == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Copia os campos que vieram do Postman para o usuário do banco
        usuarioExistente.setNome(dadosAtualizados.getNome());
        usuarioExistente.setUsername(dadosAtualizados.getUsername());

        // Se a senha veio preenchida no JSON, atualiza ela também
        if (dadosAtualizados.getPassword() != null && !dadosAtualizados.getPassword().isEmpty()) {
            usuarioExistente.setPassword(dadosAtualizados.getPassword());
        }

        if (dadosAtualizados.getRole() != null) {
            usuarioExistente.setRole(dadosAtualizados.getRole());
        }

        // 3. Salva o objeto que já existia, agora atualizado
        service.salvar(usuarioExistente);

        return ResponseEntity.ok(usuarioExistente);
    }

    // 5. Excluir usuário
    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        Usuario usuario = service.buscarPorId(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // 6. Listar as roles disponíveis
    @GetMapping("/roles")
    public ResponseEntity<Usuario.Role[]> listarRoles() {
        return ResponseEntity.ok(new Usuario.Role[] {Usuario.Role.SECRETARIO, Usuario.Role.PROFESSOR});
    }
}
