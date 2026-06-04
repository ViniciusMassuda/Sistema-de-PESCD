package br.ufscar.dc.dsw.sistema_pescd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/secretario/home")
    public String secretarioHome() {
        return "secretario/home";
    }

    @GetMapping("/professor/home")
    public String professorHome() {
        return "professor/home";
    }


    @GetMapping("/aluno/home")
    public String alunoHome() {
        // Redireciona para a lista de ofertas do aluno
        return "redirect:/aluno/ofertas";
    }
}