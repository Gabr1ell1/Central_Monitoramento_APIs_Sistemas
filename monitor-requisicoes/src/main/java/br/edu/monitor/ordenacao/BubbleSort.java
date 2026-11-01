package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * ORDENAÇÃO POR TROCA: Bubble Sort (método da bolha).
 *
 * Ideia: percorre o vetor comparando vizinhos; se estiverem fora de ordem,
 * troca. A cada passada o maior elemento "flutua" até o fim, como uma bolha.
 *
 * Pseudocódigo:
 *   para i de 0 até n-2:
 *       trocou = falso
 *       para j de 0 até n-2-i:
 *           se v[j] > v[j+1]: troca(v[j], v[j+1]); trocou = verdadeiro
 *       se não trocou: pare (já está ordenado)
 *
 * Complexidade: O(n²) no pior caso; O(n) se já estiver ordenado. Estável.
 */
public class BubbleSort implements Ordenador {

    @Override public String getId() { return "bubblesort"; }
    @Override public String getNome() { return "Bubble Sort"; }
    @Override public String getCategoria() { return "Troca"; }
    @Override public String getComplexidade() { return "O(n²)"; }

    @Override
    public void ordenar(Requisicao[] vetor, Comparator<Requisicao> comparador) {
        int tamanho_vetor = vetor.length;
        for (int i = 0; i < tamanho_vetor - 1; i++) {
            boolean trocou = false;
            // a cada passada, os últimos i elementos já estão em sua posição final
            for (int j = 0; j < tamanho_vetor - 1 - i; j++) {
                if (comparador.compare(vetor[j], vetor[j + 1]) > 0) {
                    Requisicao auxiliar = vetor[j];       // troca dos vizinhos
                    vetor[j] = vetor[j + 1];
                    vetor[j + 1] = auxiliar;
                    trocou = true;
                }
            }
            if (!trocou) {
                break;      // nenhuma troca na passada: vetor ordenado
            }
        }
    }
}
