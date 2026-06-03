package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.dao.UsuarioDAO;
import br.ufscar.dc.dsw.sistema_pescd.domain.Usuario;
import br.ufscar.dc.dsw.sistema_pescd.dto.response.OfertaAlunoResponseDTO;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IAlunoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private IAlunoService alunoService;

    @Autowired
    private UsuarioDAO usuarioDAO;

    @GetMapping("/ofertas")
    public String listarOfertas(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpSession session) {
        Usuario aluno = usuarioDAO.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Guarda o nome na sessão para usar no layout
        session.setAttribute("usuarioNome", aluno.getNome());

        List<OfertaAlunoResponseDTO> ofertas = alunoService.buscarOfertasPorAluno(aluno);

        if (ofertas.isEmpty()) {
            model.addAttribute("semOfertas", true);
        } else {
            model.addAttribute("ofertas", ofertas);
            model.addAttribute("semOfertas", false);
        }

        return "aluno/ofertas";
    }
}