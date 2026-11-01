package br.edu.monitor.pesquisa;

import br.edu.monitor.model.Requisicao;
import br.edu.monitor.ordenacao.CampoOrdenacao;
import java.util.ArrayList;
import java.util.List;

/**
 * Algoritmos de pesquisa. Ambos devolvem as POSIÇÕES do vetor que contêm a
 * chave e contam as comparações feitas em contador[0] (um vetor de 1 posição
 * serve de "variável por referência").
 */
public final class Busca {

    private Busca() {
    }

    /**
     * BUSCA LINEAR (sequencial): examina um por um, do início ao fim.
     * Funciona em dados DESORDENADOS (arquivo de entrada). Custo O(n).
     *
     *   para i de 0 até n-1:
     *       se v[i] == chave: guarda i
     */
    public static List<Integer> linear(Requisicao[] vetor, CampoOrdenacao campo, String chave, long[] contador) {
        List<Integer> posicoes = new ArrayList<>();
        for (int i = 0; i < vetor.length; i++) {
            contador[0]++;
            if (campo.compararComChave(vetor[i], chave) == 0) {
                posicoes.add(i);
            }
        }
        return posicoes;
    }

    /**
     * BUSCA BINÁRIA: só funciona em dados ORDENADOS pelo mesmo campo (arquivos de saída).
     * A cada passo compara com o elemento do meio e descarta metade do vetor. Custo O(log n).
     *
     *   baixo = 0; alto = n-1
     *   enquanto baixo <= alto:
     *       meio = (baixo + alto) / 2
     *       se v[meio] == chave: achou
     *       senão se v[meio] < chave: baixo = meio + 1
     *       senão: alto = meio - 1
     *
     * Como pode haver valores repetidos, depois de achar um são recolhidos
     * também os vizinhos iguais à esquerda e à direita.
     */
    public static List<Integer> binaria(Requisicao[] vetor, CampoOrdenacao campo, String chave, long[] contador) {
        List<Integer> posicoes = new ArrayList<>();
        int baixo = 0;
        int alto = vetor.length - 1;
        int achado = -1;
        while (baixo <= alto) {
            int meio = (baixo + alto) >>> 1;
            contador[0]++;
            int resultado = campo.compararComChave(vetor[meio], chave);
            if (resultado == 0) {
                achado = meio;
                break;
            } else if (resultado < 0) {
                baixo = meio + 1;       // elemento do meio é menor: procurar na metade direita
            } else {
                alto = meio - 1;        // elemento do meio é maior: procurar na metade esquerda
            }
        }
        if (achado < 0) {
            return posicoes;
        }
        int inicio = achado;            // expande para a esquerda enquanto houver iguais
        while (inicio > 0 && comparaIgual(vetor[inicio - 1], campo, chave, contador)) {
            inicio--;
        }
        int fim = achado;               // expande para a direita enquanto houver iguais
        while (fim < vetor.length - 1 && comparaIgual(vetor[fim + 1], campo, chave, contador)) {
            fim++;
        }
        for (int i = inicio; i <= fim; i++) {
            posicoes.add(i);
        }
        return posicoes;
    }

    private static boolean comparaIgual(Requisicao r, CampoOrdenacao campo, String chave, long[] contador) {
        contador[0]++;
        return campo.compararComChave(r, chave) == 0;
    }
}
