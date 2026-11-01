package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.List;

/** Resultado de uma execução de ordenação: métricas de desempenho e amostras antes/depois. */
public class ResultadoOrdenacao {

    private final Ordenador algoritmo;
    private final CampoOrdenacao campo;
    private final int tamanho;
    private final double tempoOrdenacaoMs;   // só o algoritmo
    private final double tempoTotalMs;       // algoritmo + gravação do arquivo de saída
    private final long comparacoes;
    private final List<Requisicao> antes;    // primeiros registros antes de ordenar
    private final List<Requisicao> depois;   // primeiros registros depois de ordenar

    public ResultadoOrdenacao(Ordenador algoritmo, CampoOrdenacao campo, int tamanho,
                              double tempoOrdenacaoMs, double tempoTotalMs, long comparacoes,
                              List<Requisicao> antes, List<Requisicao> depois) {
        this.algoritmo = algoritmo;
        this.campo = campo;
        this.tamanho = tamanho;
        this.tempoOrdenacaoMs = tempoOrdenacaoMs;
        this.tempoTotalMs = tempoTotalMs;
        this.comparacoes = comparacoes;
        this.antes = antes;
        this.depois = depois;
    }

    public Ordenador getAlgoritmo() { return algoritmo; }
    public CampoOrdenacao getCampo() { return campo; }
    public int getTamanho() { return tamanho; }
    public double getTempoOrdenacaoMs() { return tempoOrdenacaoMs; }
    public double getTempoTotalMs() { return tempoTotalMs; }
    public long getComparacoes() { return comparacoes; }
    public List<Requisicao> getAntes() { return antes; }
    public List<Requisicao> getDepois() { return depois; }
}
