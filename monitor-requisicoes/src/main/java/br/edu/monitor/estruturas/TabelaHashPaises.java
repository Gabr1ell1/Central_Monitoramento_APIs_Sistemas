package br.edu.monitor.estruturas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TABELA HASH de países: chave = nome do país, valor = lista de países que fazem fronteira.
 *
 * Como funciona:
 *   1. A função hash() transforma o nome em um número de 0 a (capacidade - 1).
 *   2. Esse número é o índice do "balde" (posição do vetor) onde o país fica.
 *   3. Países diferentes podem cair no mesmo balde (COLISÃO). Resolvemos com
 *      ENCADEAMENTO: cada balde é uma lista ligada, e os países colidindo ficam na mesma lista.
 *
 *   baldes[0]  -> (vazio)
 *   baldes[1]  -> [Brasil] -> [Chile]      <- colisão resolvida por encadeamento
 *   baldes[2]  -> [Peru]
 *   ...
 *
 * Busca e inserção custam O(1) em média, pois só olham um balde.
 */
public class TabelaHashPaises {

    /** Um país armazenado na tabela, com o balde em que foi parar. */
    public static class EntradaPais {
        private final String nome;
        private final List<String> fronteiras;
        private final int balde;

        public EntradaPais(String nome, List<String> fronteiras, int balde) {
            this.nome = nome;
            this.fronteiras = fronteiras;
            this.balde = balde;
        }

        public String getNome() { return nome; }
        public List<String> getFronteiras() { return fronteiras; }
        public int getBalde() { return balde; }
    }

    private final ListaLigada<EntradaPais>[] baldes;
    private int quantidade;

    @SuppressWarnings("unchecked")
    public TabelaHashPaises(int capacidade) {
        baldes = (ListaLigada<EntradaPais>[]) new ListaLigada[capacidade];
        for (int i = 0; i < capacidade; i++) {
            baldes[i] = new ListaLigada<>();
        }
    }

    /**
     * FUNÇÃO HASH (método polinomial): h = h * 31 + código_do_caractere, para cada letra.
     * O resto da divisão pela capacidade mantém o resultado dentro do vetor.
     * Usa o nome normalizado, então "Brasil" e "brasil" caem no mesmo balde.
     */
    public int hash(String pais) {
        int h = 0;
        for (char letra : Texto.normalizar(pais).toCharArray()) {
            h = (h * 31 + letra) % baldes.length;
        }
        return h;
    }

    /** Insere o país; se já existir, substitui a lista de fronteiras. */
    public void inserir(String pais, List<String> fronteiras) {
        int indice = hash(pais);
        String chave = Texto.normalizar(pais);
        EntradaPais nova = new EntradaPais(pais.trim(), new ArrayList<>(fronteiras), indice);
        No<EntradaPais> existente = baldes[indice].buscarNo(e -> Texto.normalizar(e.getNome()).equals(chave));
        if (existente != null) {
            existente.setDado(nova);
        } else {
            baldes[indice].inserirFim(nova);
            quantidade++;
        }
    }

    /** Busca o país: calcula o balde com hash() e procura só nele. Devolve null se não achar. */
    public EntradaPais buscar(String pais) {
        String chave = Texto.normalizar(pais);
        No<EntradaPais> no = baldes[hash(pais)].buscarNo(e -> Texto.normalizar(e.getNome()).equals(chave));
        return no == null ? null : no.getDado();
    }

    /** Lista todos os países em ordem alfabética (apenas para exibição). */
    public List<EntradaPais> listar() {
        List<EntradaPais> todos = new ArrayList<>();
        for (ListaLigada<EntradaPais> balde : baldes) {
            for (EntradaPais entrada : balde) {
                todos.add(entrada);
            }
        }
        todos.sort(Comparator.comparing(e -> Texto.normalizar(e.getNome())));
        return todos;
    }

    public int getQuantidade() { return quantidade; }
    public int getCapacidade() { return baldes.length; }
}
