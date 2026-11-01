package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * MÉTODO DA INTERCALAÇÃO: Merge Sort (divisão e conquista).
 *
 * Ideia: divide o vetor ao meio, ordena cada metade (recursivamente) e depois
 * INTERCALA as duas metades já ordenadas em um vetor único ordenado.
 *
 *   [8 3 5 1]  ->  [8 3] [5 1]  ->  [3 8] [1 5]  ->  [1 3 5 8]
 *
 * Pseudocódigo:
 *   mergesort(v, esq, dir):
 *       se esq >= dir: retorne
 *       meio = (esq + dir) / 2
 *       mergesort(v, esq, meio); mergesort(v, meio+1, dir)
 *       intercalar(v, esq, meio, dir)
 *
 * Complexidade: O(n log n) em todos os casos. Estável. Usa memória extra O(n).
 */
public class MergeSort implements Ordenador {

    @Override public String getId() { return "mergesort"; }
    @Override public String getNome() { return "Merge Sort"; }
    @Override public String getCategoria() { return "Intercalação"; }
    @Override public String getComplexidade() { return "O(n log n)"; }

    @Override
    public void ordenar(Requisicao[] vetor, Comparator<Requisicao> comparador) {
        if (vetor.length < 2) {
            return;
        }
        Requisicao[] auxiliar = new Requisicao[vetor.length];   // criado uma só vez e reutilizado
        dividir(vetor, auxiliar, 0, vetor.length - 1, comparador);
    }

    /** Divide o trecho [esq, dir] em duas metades e ordena cada uma. */
    private void dividir(Requisicao[] vetor, Requisicao[] auxiliar, int esq, int dir,
                         Comparator<Requisicao> comparador) {
        if (esq >= dir) {
            return;                         // trecho com 0 ou 1 elemento já está ordenado
        }
        int meio = (esq + dir) >>> 1;
        dividir(vetor, auxiliar, esq, meio, comparador);
        dividir(vetor, auxiliar, meio + 1, dir, comparador);
        intercalar(vetor, auxiliar, esq, meio, dir, comparador);
    }

    /** Junta as metades ordenadas [esq, meio] e [meio+1, dir] em um trecho ordenado. */
    private void intercalar(Requisicao[] vetor, Requisicao[] auxiliar, int esq, int meio, int dir,
                            Comparator<Requisicao> comparador) {
        System.arraycopy(vetor, esq, auxiliar, esq, dir - esq + 1);   // copia o trecho
        int i = esq;          // posição na metade esquerda
        int j = meio + 1;     // posição na metade direita
        for (int k = esq; k <= dir; k++) {
            if (i > meio) {
                vetor[k] = auxiliar[j++];                             // esquerda acabou
            } else if (j > dir) {
                vetor[k] = auxiliar[i++];                             // direita acabou
            } else if (comparador.compare(auxiliar[i], auxiliar[j]) <= 0) {
                vetor[k] = auxiliar[i++];                             // <= mantém a ordem dos iguais (estável)
            } else {
                vetor[k] = auxiliar[j++];
            }
        }
    }
}
