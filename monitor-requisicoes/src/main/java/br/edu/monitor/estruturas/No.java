package br.edu.monitor.estruturas;

/**
 * Nó de uma lista duplamente encadeada.
 *
 *   anterior <-- [ dado ] --> proximo
 *
 * Cada nó guarda um dado e a referência (ponteiro) para o nó vizinho
 * de cada lado. É isso que permite inserir e remover nas duas pontas
 * sem deslocar elementos, como seria necessário em um vetor.
 */
public class No<T> {

    private T dado;
    private No<T> anterior;
    private No<T> proximo;

    public No(T dado) {
        this.dado = dado;
    }

    public T getDado() { return dado; }
    public void setDado(T dado) { this.dado = dado; }

    public No<T> getAnterior() { return anterior; }
    public void setAnterior(No<T> anterior) { this.anterior = anterior; }

    public No<T> getProximo() { return proximo; }
    public void setProximo(No<T> proximo) { this.proximo = proximo; }
}
