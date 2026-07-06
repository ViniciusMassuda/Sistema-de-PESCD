// AdminController.java
// Gerencia o cadastro de secretarios e professores.
// Controla a lista, criacao, edicao e exclusao de usuarios.

package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IUsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IUsuarioService service;

    @GetMapping("/home")
    public String home() {
        return "admin/home";
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        // Busca secretarios e professores para mostrar na lista.
        model.addAttribute("usuarios", service.buscarSecretariosEProfessores());
        return "admin/usuario/lista";
    }

    @GetMapping("/usuarios/cadastrar")
    public String cadastrar(Usuario usuario, Model model) {
        // Prepara a tela para criar um novo usuario.
        model.addAttribute("roles", new Usuario.Role[] {Usuario.Role.SECRETARIO, Usuario.Role.PROFESSOR});
        return "admin/usuario/cadastro";
    }

    @PostMapping("/usuarios/salvar")
    public String salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attr, Model model) {
        // Salva o usuario no banco se os dados estiverem certos.
        if (result.hasErrors()) {
            model.addAttribute("roles", new Usuario.Role[] {Usuario.Role.SECRETARIO, Usuario.Role.PROFESSOR});
            return "admin/usuario/cadastro";
        }
        service.salvar(usuario);
        attr.addFlashAttribute("success", "Usuario salvo com sucesso.");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/usuarios/editar/{id}")
    public String preEditar(@PathVariable("id") Long id, Model model) {
        // Busca um usuario especifico para mudar os dados dele.
        model.addAttribute("usuario", service.buscarPorId(id));
        model.addAttribute("roles", new Usuario.Role[] {Usuario.Role.SECRETARIO, Usuario.Role.PROFESSOR});
        return "admin/usuario/cadastro";
    }

    @GetMapping("/usuarios/excluir/{id}")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes attr) {
        // Apaga o usuario do sistema.
        service.excluir(id);
        attr.addFlashAttribute("success", "Usuario excluido com sucesso.");
        return "redirect:/admin/usuarios";
    }
}
