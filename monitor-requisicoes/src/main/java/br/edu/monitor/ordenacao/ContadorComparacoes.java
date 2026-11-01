package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * Comparador "espião": delega a comparação ao comparador real e conta quantas
 * vezes foi chamado. O número de comparações é uma medida de esforço do
 * algoritmo que não depende da velocidade do computador.
 */
public class ContadorComparacoes implements Comparator<Requisicao> {

    private final Comparator<Requisicao> comparador_real;
    private long comparacoes;

    public ContadorComparacoes(Comparator<Requisicao> comparador_real) {
        this.comparador_real = comparador_real;
    }

    @Override
    public int compare(Requisicao a, Requisicao b) {
        comparacoes++;
        return comparador_real.compare(a, b);
    }

    public long getComparacoes() {
        return comparacoes;
    }
}
