// Arquivo: HomeController.java - Criado para o sistema PESCD
// Este codigo foi feito de forma simples para facilitar o entendimento
package br.ufscar.dc.dsw.sistema_pescd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/professor/home")
    public String professorHome() { return "professor/home"; }

    @GetMapping("/aluno/home")
    public String alunoHome() {
        return "redirect:/aluno/ofertas";
    }
}


