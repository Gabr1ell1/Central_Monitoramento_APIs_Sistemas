package br.edu.monitor.controller;

import br.edu.monitor.service.GeografiaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Módulo Geografia: tabelas hash de países (fronteiras) e de cidades (ordem alfabética). */
@Controller
public class GeografiaController {

    private final GeografiaService geografia;

    public GeografiaController(GeografiaService geografia) {
        this.geografia = geografia;
    }

    @GetMapping("/geografia")
    public String exibir(@RequestParam(required = false) String pais,
                         @RequestParam(required = false) String cidade,
                         Model model) {
        model.addAttribute("ativo", "geografia");
        model.addAttribute("paises", geografia.listarPaises());
        model.addAttribute("quantidadePaises", geografia.getQuantidadePaises());
        model.addAttribute("capacidadePaises", geografia.getCapacidadePaises());
        model.addAttribute("cidades", geografia.listarCidades());
        model.addAttribute("quantidadeCidades", geografia.getQuantidadeCidades());

        if (pais != null && !pais.isBlank()) {
            model.addAttribute("paisBuscado", pais);
            model.addAttribute("paisAchado", geografia.buscarPais(pais));   // null se não existir
        }
        if (cidade != null && !cidade.isBlank()) {
            model.addAttribute("cidadeBuscada", cidade);
            model.addAttribute("baldeCidade", geografia.buscarCidade(cidade)); // -1 se não existir
        }
        return "geografia";
    }

    @PostMapping("/geografia/pais")
    public String adicionarPais(@RequestParam String nome, @RequestParam(defaultValue = "") String fronteiras,
                                RedirectAttributes aviso) {
        if (nome.isBlank()) {
            aviso.addFlashAttribute("mensagem", "Informe o nome do país.");
        } else {
            geografia.adicionarPais(nome, fronteiras);
            aviso.addFlashAttribute("mensagem", "País " + nome.trim() + " gravado na tabela hash.");
        }
        return "redirect:/geografia";
    }

    @PostMapping("/geografia/cidade")
    public String adicionarCidade(@RequestParam String nome, RedirectAttributes aviso) {
        boolean inseriu = geografia.adicionarCidade(nome);
        aviso.addFlashAttribute("mensagem", inseriu
                ? "Cidade " + nome.trim() + " inserida na posição alfabética."
                : "Cidade vazia ou já cadastrada.");
        return "redirect:/geografia";
    }
}
