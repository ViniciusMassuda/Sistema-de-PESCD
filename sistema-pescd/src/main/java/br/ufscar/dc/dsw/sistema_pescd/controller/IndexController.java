// IndexController.java
// Cuida da pagina inicial e da tela de login.
// Prepara a lista de ofertas que todos podem ver.

package br.ufscar.dc.dsw.sistema_pescd.controller;

import br.ufscar.dc.dsw.sistema_pescd.domain.Oferta;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IOfertaService;
import br.ufscar.dc.dsw.sistema_pescd.service.spec.IInscricaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final IOfertaService ofertaService;
    private final IInscricaoService inscricaoService;

    @GetMapping("/")
    public String index(Model model) {
        // Pega as ofertas e conta os alunos de cada uma.
        List<Oferta> ofertas = ofertaService.buscarTodosOrdenado();
        Map<Long, Long> inscritosMap = new HashMap<>();
        for (Oferta o : ofertas) {
            inscritosMap.put(o.getId(), inscricaoService.contarPorOferta(o));
        }
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("inscritosMap", inscritosMap);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        // Apenas mostra a tela de login.
        return "login";
    }

    @GetMapping("/logout")
    public String logout() {
        // Renderiza a página de confirmação de logout (solução definitiva contra CSRF)
        return "logout-confirm";
    }
}
