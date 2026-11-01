package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * Campos pelos quais os registros podem ser ordenados ou pesquisados.
 * Concentra a regra de comparação em um só lugar: os algoritmos de ordenação
 * e as buscas usam exatamente a mesma regra, o que garante resultados coerentes.
 */
public enum CampoOrdenacao {

    TEMPO("Tempo de resposta"),
    CODIGO("Código HTTP"),
    SERVICO("Serviço"),
    DATA("Data");

    private final String rotulo;

    CampoOrdenacao(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    /** Compara dois registros por este campo (negativo: a vem antes; positivo: b vem antes). */
    public int comparar(Requisicao a, Requisicao b) {
        return switch (this) {
            case TEMPO -> Long.compare(numero(a.getTempoRespostaMs()), numero(b.getTempoRespostaMs()));
            case CODIGO -> Long.compare(numero(a.getCodigoHttp()), numero(b.getCodigoHttp()));
            case SERVICO -> String.CASE_INSENSITIVE_ORDER.compare(texto(a.getServico()), texto(b.getServico()));
            case DATA -> texto(a.getDataRequisicao()).compareTo(texto(b.getDataRequisicao()));
        };
    }

    /**
     * Compara um registro com um valor digitado pelo usuário (usado nas buscas).
     * Lança NumberFormatException se o campo for numérico e o valor não for um número.
     */
    public int compararComChave(Requisicao r, String chave) {
        String valor = chave.trim();
        return switch (this) {
            case TEMPO -> Long.compare(numero(r.getTempoRespostaMs()), Long.parseLong(valor));
            case CODIGO -> Long.compare(numero(r.getCodigoHttp()), Long.parseLong(valor));
            case SERVICO -> String.CASE_INSENSITIVE_ORDER.compare(texto(r.getServico()), valor);
            case DATA -> texto(r.getDataRequisicao()).compareTo(valor);
        };
    }

    /** Devolve este campo como Comparator, formato que os algoritmos de ordenação recebem. */
    public Comparator<Requisicao> comparador() {
        return this::comparar;
    }

    private static long numero(Number valor) {
        return valor == null ? 0 : valor.longValue();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }
}
