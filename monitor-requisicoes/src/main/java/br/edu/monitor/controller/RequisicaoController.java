package br.edu.monitor.controller;

import br.edu.monitor.estruturas.ListaLigada;
import br.edu.monitor.estruturas.No;
import br.edu.monitor.model.Requisicao;
import br.edu.monitor.model.Situacao;
import br.edu.monitor.service.RequisicaoService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Página inicial: CRUD sobre o arquivo entrada.txt.
 * O controller só recebe o pedido, chama o serviço e escolhe a página;
 * as regras ficam em RequisicaoService.
 */
@Controller
public class RequisicaoController {

    /** Limite de linhas desenhadas na tabela (com milhares de registros a página ficaria pesada). */
    private static final int MAXIMO_NA_TELA = 100;

    private final RequisicaoService servico;

    public RequisicaoController(RequisicaoService servico) {
        this.servico = servico;
    }

    /** READ: mostra os registros do arquivo, com resumo, filtros e formulário (vazio ou de edição). */
    @GetMapping("/")
    public String listar(@RequestParam(required = false) Long editar,
                         @RequestParam(defaultValue = "") String q,
                         @RequestParam(defaultValue = "todos") String tipo,
                         @RequestParam(defaultValue = "") String situacao,
                         Model model) {

        ListaLigada<Requisicao> lista = servico.listar();
        String busca = q.trim().toLowerCase();

        // Uma única passada pela lista: calcula o resumo e separa os registros que passam nos filtros
        List<Requisicao> exibidos = new ArrayList<>();
        int total_filtrado = 0;
        long erros = 0;
        long lentas = 0;
        long abertos = 0;
        long maior_tempo = Requisicao.LIMITE_LENTA_MS * 2;     // referência mínima da barra de tempo
        for (Requisicao r : lista) {
            if (r.isErro()) erros++;
            if (r.isLenta()) lentas++;
            if (r.getStatusProblema() == Situacao.ABERTO) abertos++;
            if (r.getTempoRespostaMs() != null && r.getTempoRespostaMs() > maior_tempo) {
                maior_tempo = r.getTempoRespostaMs();
            }
            if (passaNosFiltros(r, busca, tipo, situacao)) {
                total_filtrado++;
                if (exibidos.size() < MAXIMO_NA_TELA) {
                    exibidos.add(r);
                }
            }
        }

        // Formulário: vazio para cadastrar, ou preenchido (busca por id na lista ligada) para editar
        No<Requisicao> no_edicao = editar == null ? null : lista.buscarNo(r -> editar.equals(r.getId()));
        Requisicao requisicao = no_edicao != null ? no_edicao.getDado() : new Requisicao();

        model.addAttribute("ativo", "registros");
        model.addAttribute("total", lista.tamanho());
        model.addAttribute("erros", erros);
        model.addAttribute("lentas", lentas);
        model.addAttribute("abertos", abertos);
        model.addAttribute("limite", Requisicao.LIMITE_LENTA_MS);
        model.addAttribute("registros", exibidos);
        model.addAttribute("totalFiltrado", total_filtrado);
        model.addAttribute("maximoNaTela", MAXIMO_NA_TELA);
        model.addAttribute("maior", maior_tempo);
        model.addAttribute("q", q);                      // devolvidos para os filtros continuarem preenchidos
        model.addAttribute("tipo", tipo);
        model.addAttribute("situacao", situacao);
        model.addAttribute("requisicao", requisicao);
        model.addAttribute("metodos", List.of("GET", "POST", "PUT", "DELETE"));
        model.addAttribute("situacoes", Situacao.values());
        model.addAttribute("pasta", servico.caminhoDaPasta());
        model.addAttribute("linhasArquivo", servico.linhasBrutas(30));
        return "index";
    }

    /** CREATE ou UPDATE: sem id cadastra (no início ou no fim); com id atualiza o registro existente. */
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Requisicao requisicao,
                         @RequestParam(defaultValue = "fim") String posicao,
                         RedirectAttributes aviso) {
        if (requisicao.getId() == null) {
            boolean no_inicio = posicao.equals("inicio");
            Requisicao nova = servico.incluir(requisicao, no_inicio);
            aviso.addFlashAttribute("mensagem",
                    "Registro #" + nova.getId() + " incluído no " + (no_inicio ? "início" : "fim") + " do arquivo.");
        } else if (servico.atualizar(requisicao)) {
            aviso.addFlashAttribute("mensagem", "Registro #" + requisicao.getId() + " atualizado.");
        } else {
            aviso.addFlashAttribute("mensagem", "Registro #" + requisicao.getId() + " não encontrado.");
        }
        return "redirect:/";
    }

    /** DELETE (LIFO): remove o último registro inserido. Um por execução. */
    @PostMapping("/excluir-ultimo")
    public String excluirUltimo(RedirectAttributes aviso) {
        Requisicao removida = servico.excluirUltimo();
        aviso.addFlashAttribute("mensagem", removida == null
                ? "O arquivo não tem registros para excluir."
                : "Registro #" + removida.getId() + " (" + removida.getServico() + ") excluído: era o último inserido (LIFO).");
        return "redirect:/";
    }

    /** CREATE (arquivo): recria o entrada.txt com o leiaute e os registros de exemplo. */
    @PostMapping("/criar-arquivo")
    public String criarArquivo(RedirectAttributes aviso) {
        servico.criarArquivo();
        aviso.addFlashAttribute("mensagem", "Arquivo entrada.txt recriado com 5 registros de exemplo.");
        return "redirect:/";
    }

    /** Gera registros simulados em volume, para comparar o desempenho dos algoritmos. */
    @PostMapping("/gerar")
    public String gerar(@RequestParam int quantidade, RedirectAttributes aviso) {
        int limitada = Math.max(1, Math.min(quantidade, 20000));   // trava entre 1 e 20.000
        servico.gerarSimulados(limitada);
        aviso.addFlashAttribute("mensagem", limitada + " registros simulados acrescentados ao fim do arquivo.");
        return "redirect:/";
    }

    // ------------------------------------------------------------------

    private boolean passaNosFiltros(Requisicao r, String busca, String tipo, String situacao) {
        if (!busca.isEmpty()) {
            String texto = (nz(r.getServico()) + " " + nz(r.getEndpoint())).toLowerCase();
            if (!texto.contains(busca)) {
                return false;
            }
        }
        boolean tipo_ok = switch (tipo) {
            case "erro" -> r.isErro();
            case "lenta" -> r.isLenta();
            case "problema" -> r.isErro() || r.isLenta();
            default -> true;
        };
        if (!tipo_ok) {
            return false;
        }
        return situacao.isEmpty()
                || (r.getStatusProblema() != null && r.getStatusProblema().name().equals(situacao));
    }

    private static String nz(String texto) {
        return texto == null ? "" : texto;
    }
}
