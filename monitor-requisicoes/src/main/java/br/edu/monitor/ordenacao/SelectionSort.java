package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * ORDENAÇÃO POR SELEÇÃO: Selection Sort.
 *
 * Ideia: a cada passo, SELECIONA o menor elemento da parte ainda não
 * ordenada e o coloca na primeira posição dessa parte.
 *
 * Pseudocódigo:
 *   para i de 0 até n-2:
 *       menor = i
 *       para j de i+1 até n-1:
 *           se v[j] < v[menor]: menor = j
 *       se menor != i: troca(v[i], v[menor])
 *
 * Complexidade: O(n²) sempre (faz o mesmo número de comparações em qualquer
 * caso), mas no máximo n-1 trocas. NÃO é estável.
 */
public class SelectionSort implements Ordenador {

    @Override public String getId() { return "selectionsort"; }
    @Override public String getNome() { return "Selection Sort"; }
    @Override public String getCategoria() { return "Seleção"; }
    @Override public String getComplexidade() { return "O(n²)"; }

    @Override
    public void ordenar(Requisicao[] vetor, Comparator<Requisicao> comparador) {
        for (int i = 0; i < vetor.length - 1; i++) {
            int menor = i;                                       // posição do menor visto até agora
            for (int j = i + 1; j < vetor.length; j++) {
                if (comparador.compare(vetor[j], vetor[menor]) < 0) {
                    menor = j;
                }
            }
            if (menor != i) {                                    // coloca o menor na posição i
                Requisicao auxiliar = vetor[i];
                vetor[i] = vetor[menor];
                vetor[menor] = auxiliar;
            }
        }
    }
}
