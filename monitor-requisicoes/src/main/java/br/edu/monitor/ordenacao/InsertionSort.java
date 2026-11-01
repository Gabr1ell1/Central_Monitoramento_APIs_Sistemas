package br.edu.monitor.ordenacao;

import br.edu.monitor.model.Requisicao;
import java.util.Comparator;

/**
 * ORDENAÇÃO POR INSERÇÃO: Insertion Sort.
 *
 * Ideia: como organizar cartas na mão. O início do vetor é a parte já
 * ordenada; cada novo elemento é "encaixado" na posição certa dentro dela,
 * deslocando os maiores uma casa para a direita.
 *
 * Pseudocódigo:
 *   para i de 1 até n-1:
 *       chave = v[i]
 *       j = i - 1
 *       enquanto j >= 0 e v[j] > chave:
 *           v[j+1] = v[j]; j = j - 1
 *       v[j+1] = chave
 *
 * Complexidade: O(n²) no pior caso; O(n) se já estiver ordenado. Estável.
 */
public class InsertionSort implements Ordenador {

    @Override public String getId() { return "insertionsort"; }
    @Override public String getNome() { return "Insertion Sort"; }
    @Override public String getCategoria() { return "Inserção"; }
    @Override public String getComplexidade() { return "O(n²)"; }

    @Override
    public void ordenar(Requisicao[] vetor, Comparator<Requisicao> comparador) {
        for (int i = 1; i < vetor.length; i++) {
            Requisicao chave = vetor[i];       // elemento a encaixar
            int j = i - 1;
            while (j >= 0 && comparador.compare(vetor[j], chave) > 0) {
                vetor[j + 1] = vetor[j];       // desloca o maior para a direita
                j--;
            }
            vetor[j + 1] = chave;              // encaixa na posição livre
        }
    }
}
