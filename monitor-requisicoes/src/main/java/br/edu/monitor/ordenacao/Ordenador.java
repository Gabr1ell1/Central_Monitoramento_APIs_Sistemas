package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * Contrato comum dos algoritmos de ordenação. Todos recebem um vetor e um
 * comparador e deixam o vetor ordenado (ordenação "in place").
 * Assim a aplicação executa e compara os quatro algoritmos da mesma forma.
 */
public interface Ordenador {

    /** Identificador curto, usado nos formulários (ex.: "bubblesort"). */
    String getId();

    /** Nome para exibição (ex.: "Bubble Sort"). */
    String getNome();

    /** Família do algoritmo segundo o enunciado: troca, inserção, seleção ou intercalação. */
    String getCategoria();

    /** Complexidade de tempo no pior caso. */
    String getComplexidade();

    /** Nome do arquivo de saída: o nome do algoritmo, como pede o enunciado. */
    default String getArquivoSaida() {
        return getId() + ".txt";
    }

    /** Ordena o vetor de forma crescente segundo o comparador. */
    void ordenar(Requisicao[] vetor, Comparator<Requisicao> comparador);
}
