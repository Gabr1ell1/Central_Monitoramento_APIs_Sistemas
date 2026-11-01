package br.edu.monitor.estruturas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * TABELA HASH de cidades que MANTÉM A ORDEM ALFABÉTICA.
 *
 * Uma função hash comum espalha as chaves ao acaso, então percorrer a tabela
 * não devolve nada ordenado. Aqui a função hash() é MONÓTONA: se a cidade A vem
 * antes de B no alfabeto, então hash(A) <= hash(B). Assim:
 *
 *   - percorrer os baldes de 0 até o último já segue a ordem alfabética;
 *   - dentro de cada balde (cidades com as mesmas duas primeiras letras),
 *     a lista ligada é mantida ordenada a cada inserção.
 *
 * Índice do balde = (primeira letra) * 27 + (segunda letra)
 *   primeira letra: a=0 ... z=25
 *   segunda letra:  "não existe" = 0, a=1 ... z=26   (assim "a" vem antes de "aa")
 * Total: 26 * 27 = 702 baldes.
 *
 * Exemplo: "Curitiba" -> c=2, u=21 -> balde 2*27 + 21 = 75
 */
public class TabelaHashCidades {

    /** Representa uma cidade guardada e o balde em que está. */
    public static class CidadeHash {
        private final String nome;
        private final int balde;

        public CidadeHash(String nome, int balde) {
            this.nome = nome;
            this.balde = balde;
        }

        public String getNome() { return nome; }
        public int getBalde() { return balde; }
    }

    private static final int BASE = 27;
    public static final int CAPACIDADE = 26 * BASE;

    /** Ordem usada dentro de cada balde: alfabética, ignorando acentos e maiúsculas. */
    private static final Comparator<String> ORDEM_ALFABETICA = Comparator.comparing(Texto::normalizar);

    private final ListaLigada<String>[] baldes;
    private int quantidade;

    @SuppressWarnings("unchecked")
    public TabelaHashCidades() {
        baldes = (ListaLigada<String>[]) new ListaLigada[CAPACIDADE];
        for (int i = 0; i < CAPACIDADE; i++) {
            baldes[i] = new ListaLigada<>();
        }
    }

    /** FUNÇÃO HASH monótona (garante a classificação). */
    public int hash(String cidade) {
        String nome = Texto.normalizar(cidade);
        if (nome.isEmpty() || nome.charAt(0) < 'a' || nome.charAt(0) > 'z') {
            return 0;      // nomes que não começam com letra vão para o primeiro balde
        }
        int primeira = nome.charAt(0) - 'a';
        int segunda = 0;
        if (nome.length() > 1 && nome.charAt(1) >= 'a' && nome.charAt(1) <= 'z') {
            segunda = nome.charAt(1) - 'a' + 1;
        }
        return primeira * BASE + segunda;
    }

    /** Insere a cidade no balde correto, já na posição alfabética. Devolve false se for duplicada. */
    public boolean inserir(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return false;
        }
        boolean inseriu = baldes[hash(cidade)].inserirOrdenado(cidade.trim(), ORDEM_ALFABETICA);
        if (inseriu) {
            quantidade++;
        }
        return inseriu;
    }

    /** Devolve o número do balde onde a cidade está, ou -1 se não existir. */
    public int buscar(String cidade) {
        int indice = hash(cidade);
        String chave = Texto.normalizar(cidade);
        No<String> no = baldes[indice].buscarNo(c -> Texto.normalizar(c).equals(chave));
        return no == null ? -1 : indice;
    }

    /** Percorre os baldes em ordem: o resultado já sai em ordem alfabética, sem precisar ordenar. */
    public List<CidadeHash> listarEmOrdem() {
        List<CidadeHash> resultado = new ArrayList<>();
        for (int indice = 0; indice < baldes.length; indice++) {
            for (String cidade : baldes[indice]) {
                resultado.add(new CidadeHash(cidade, indice));
            }
        }
        return resultado;
    }

    public int getQuantidade() { return quantidade; }
}
