package br.edu.monitor.pesquisa;

import br.edu.monitor.model.Requisicao;
import br.edu.monitor.ordenacao.CampoOrdenacao;
import java.util.ArrayList;
import java.util.List;

/** Resultado de uma pesquisa: o que foi achado e quanto custou. */
public class ResultadoPesquisa {

    private final String arquivo;
    private final String metodo;                 // "Linear" ou "Binária"
    private final CampoOrdenacao campo;
    private final String valor;
    private final long comparacoes;
    private final double tempoMs;
    private final List<Requisicao> encontrados;
    private final String mensagem;               // aviso ou erro (pode ser nulo)

    public ResultadoPesquisa(String arquivo, String metodo, CampoOrdenacao campo, String valor,
                             long comparacoes, double tempoMs, List<Requisicao> encontrados, String mensagem) {
        this.arquivo = arquivo;
        this.metodo = metodo;
        this.campo = campo;
        this.valor = valor;
        this.comparacoes = comparacoes;
        this.tempoMs = tempoMs;
        this.encontrados = encontrados;
        this.mensagem = mensagem;
    }

    /** Atalho para resultados que são apenas uma mensagem (erro de validação). */
    public static ResultadoPesquisa aviso(String arquivo, String mensagem) {
        return new ResultadoPesquisa(arquivo, "-", null, "", 0, 0, new ArrayList<>(), mensagem);
    }

    public String getArquivo() { return arquivo; }
    public String getMetodo() { return metodo; }
    public CampoOrdenacao getCampo() { return campo; }
    public String getValor() { return valor; }
    public long getComparacoes() { return comparacoes; }
    public double getTempoMs() { return tempoMs; }
    public List<Requisicao> getEncontrados() { return encontrados; }
    public String getMensagem() { return mensagem; }
}
