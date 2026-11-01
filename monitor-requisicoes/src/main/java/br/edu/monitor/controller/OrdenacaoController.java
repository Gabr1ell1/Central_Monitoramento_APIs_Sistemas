package br.edu.monitor.controller;

import br.edu.monitor.ordenacao.CampoOrdenacao;
import br.edu.monitor.ordenacao.ResultadoOrdenacao;
import br.edu.monitor.service.OrdenacaoService;
import br.edu.monitor.service.RequisicaoService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Tela de classificação dos dados do entrada.txt. */
@Controller
@RequestMapping("/ordenacao")
public class OrdenacaoController {

    private final OrdenacaoService ordenacao;
    private final RequisicaoService requisicoes;

    public OrdenacaoController(OrdenacaoService ordenacao, RequisicaoService requisicoes) {
        this.ordenacao = ordenacao;
        this.requisicoes = requisicoes;
    }

    @GetMapping
    public String formulario(Model model) {
        preencher(model);
        return "ordenacao";
    }

    /** Executa um algoritmo (ou todos) e mostra métricas e amostras antes/depois. */
    @PostMapping
    public String ordenar(@RequestParam CampoOrdenacao campo, @RequestParam String algoritmo, Model model) {
        List<ResultadoOrdenacao> resultados = ordenacao.ordenar(campo, algoritmo);

        // Descobre o algoritmo mais rápido (só faz sentido comparando mais de um)
        String mais_rapido = "";
        double menor_tempo = Double.MAX_VALUE;
        for (ResultadoOrdenacao r : resultados) {
            if (r.getTempoOrdenacaoMs() < menor_tempo) {
                menor_tempo = r.getTempoOrdenacaoMs();
                mais_rapido = r.getAlgoritmo().getId();
            }
        }
        model.addAttribute("resultados", resultados);
        model.addAttribute("maisRapido", resultados.size() > 1 ? mais_rapido : "");
        model.addAttribute("campoSelecionado", campo);
        model.addAttribute("algoritmoSelecionado", algoritmo);
        preencher(model);
        return "ordenacao";
    }

    private void preencher(Model model) {
        model.addAttribute("ativo", "ordenacao");
        model.addAttribute("campos", CampoOrdenacao.values());
        model.addAttribute("algoritmos", ordenacao.getAlgoritmos());
        model.addAttribute("totalRegistros", requisicoes.quantidade());
    }
}
