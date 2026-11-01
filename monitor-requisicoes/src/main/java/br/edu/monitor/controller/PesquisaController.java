package br.edu.monitor.controller;

import br.edu.monitor.ordenacao.CampoOrdenacao;
import br.edu.monitor.service.PesquisaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Tela de pesquisa no arquivo de entrada (linear) ou nos arquivos ordenados (binária). */
@Controller
public class PesquisaController {

    private final PesquisaService pesquisa;

    public PesquisaController(PesquisaService pesquisa) {
        this.pesquisa = pesquisa;
    }

    @GetMapping("/pesquisa")
    public String pesquisar(@RequestParam(defaultValue = "entrada.txt") String arquivo,
                            @RequestParam(defaultValue = "TEMPO") CampoOrdenacao campo,
                            @RequestParam(defaultValue = "") String valor,
                            Model model) {
        model.addAttribute("ativo", "pesquisa");
        model.addAttribute("arquivos", pesquisa.arquivosDisponiveis());
        model.addAttribute("campos", CampoOrdenacao.values());
        model.addAttribute("arquivoSelecionado", arquivo);
        model.addAttribute("campoSelecionado", campo);
        model.addAttribute("valor", valor);
        if (!valor.isBlank()) {                       // só pesquisa quando o formulário foi enviado
            model.addAttribute("resultado", pesquisa.pesquisar(arquivo, campo, valor));
        }
        return "pesquisa";
    }
}
