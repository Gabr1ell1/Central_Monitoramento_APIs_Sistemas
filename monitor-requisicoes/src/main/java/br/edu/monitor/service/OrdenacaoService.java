package br.edu.monitor.service;

import br.edu.monitor.model.Requisicao;
import br.edu.monitor.ordenacao.BubbleSort;
import br.edu.monitor.ordenacao.CampoOrdenacao;
import br.edu.monitor.ordenacao.ContadorComparacoes;
import br.edu.monitor.ordenacao.InsertionSort;
import br.edu.monitor.ordenacao.MergeSort;
import br.edu.monitor.ordenacao.Ordenador;
import br.edu.monitor.ordenacao.ResultadoOrdenacao;
import br.edu.monitor.ordenacao.SelectionSort;
import br.edu.monitor.repository.ArquivoRequisicoes;
import br.edu.monitor.repository.LogDesempenho;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Classificação dos dados do entrada.txt com os quatro algoritmos.
 *
 * Para cada algoritmo:
 *   1. copia o vetor original (todos partem dos mesmos dados desordenados);
 *   2. mede o tempo só da ordenação e conta as comparações;
 *   3. grava o arquivo de saída com o nome do algoritmo (ex.: mergesort.txt);
 *   4. registra o desempenho no log.
 */
@Service
public class OrdenacaoService {

    private static final int AMOSTRA = 10;     // quantos registros mostrar antes/depois

    private final List<Ordenador> algoritmos =
            List.of(new BubbleSort(), new InsertionSort(), new SelectionSort(), new MergeSort());

    private final ArquivoRequisicoes arquivo;
    private final LogDesempenho log;

    public OrdenacaoService(ArquivoRequisicoes arquivo, LogDesempenho log) {
        this.arquivo = arquivo;
        this.log = log;
    }

    public List<Ordenador> getAlgoritmos() {
        return algoritmos;
    }

    /** Executa um algoritmo (pelo id) ou todos, quando id_algoritmo for "todos". */
    public List<ResultadoOrdenacao> ordenar(CampoOrdenacao campo, String id_algoritmo) {
        // Lê o arquivo uma única vez e monta o vetor original
        Requisicao[] original = arquivo.ler(ArquivoRequisicoes.ARQUIVO_ENTRADA).paraLista().toArray(new Requisicao[0]);
        List<Requisicao> antes = amostra(original);

        List<ResultadoOrdenacao> resultados = new ArrayList<>();
        for (Ordenador algoritmo : algoritmos) {
            if (id_algoritmo.equals("todos") || id_algoritmo.equals(algoritmo.getId())) {
                resultados.add(executar(algoritmo, campo, original, antes));
            }
        }
        return resultados;
    }

    private ResultadoOrdenacao executar(Ordenador algoritmo, CampoOrdenacao campo,
                                        Requisicao[] original, List<Requisicao> antes) {
        long inicio_total = System.nanoTime();
        Requisicao[] copia = original.clone();                         // cada algoritmo ordena a sua cópia
        ContadorComparacoes contador = new ContadorComparacoes(campo.comparador());

        long inicio_ordenacao = System.nanoTime();
        algoritmo.ordenar(copia, contador);                            // <- único trecho cronometrado como "ordenação"
        double tempo_ordenacao = LogDesempenho.ms(inicio_ordenacao);

        // Arquivo de saída: leiaute + campo usado (a pesquisa binária precisa dessa informação)
        List<String> cabecalho = List.of(ArquivoRequisicoes.LEIAUTE,
                "ordenado-por=" + campo.name(),
                "algoritmo=" + algoritmo.getNome());
        arquivo.gravar(algoritmo.getArquivoSaida(), Arrays.asList(copia), cabecalho);
        double tempo_total = LogDesempenho.ms(inicio_total);

        log.registrar("ORDENACAO_" + algoritmo.getId().toUpperCase(), tempo_total,
                "campo=" + campo.name() + "; n=" + copia.length + "; ordenacao="
                        + String.format(java.util.Locale.ROOT, "%.3f", tempo_ordenacao)
                        + "ms; comparacoes=" + contador.getComparacoes());

        return new ResultadoOrdenacao(algoritmo, campo, copia.length, tempo_ordenacao, tempo_total,
                contador.getComparacoes(), antes, amostra(copia));
    }

    /** Copia os primeiros registros do vetor para exibição. */
    private List<Requisicao> amostra(Requisicao[] vetor) {
        return new ArrayList<>(Arrays.asList(vetor).subList(0, Math.min(AMOSTRA, vetor.length)));
    }
}
