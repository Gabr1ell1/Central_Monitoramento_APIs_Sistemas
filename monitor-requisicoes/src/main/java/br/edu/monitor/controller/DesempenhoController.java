package br.edu.monitor.controller;

import br.edu.monitor.repository.LogDesempenho;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Relatório de desempenho: mostra as últimas operações registradas em logs/desempenho.log. */
@Controller
public class DesempenhoController {

    private final LogDesempenho log;

    public DesempenhoController(LogDesempenho log) {
        this.log = log;
    }

    @GetMapping("/desempenho")
    public String exibir(Model model) {
        model.addAttribute("ativo", "desempenho");
        model.addAttribute("entradas", log.ler(200));
        model.addAttribute("arquivoLog", log.getArquivo().toString());
        return "desempenho";
    }
}
