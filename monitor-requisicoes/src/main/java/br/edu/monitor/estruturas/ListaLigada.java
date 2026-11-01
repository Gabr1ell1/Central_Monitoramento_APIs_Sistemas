package br.edu.monitor.estruturas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * LISTA DUPLAMENTE ENCADEADA, implementada do zero (sem usar java.util.LinkedList).
 *
 * Como mantém ponteiros para o primeiro e o último nó, ela também funciona
 * como um DEQUE (fila de duas pontas): permite inserir e remover tanto no
 * início quanto no fim, em tempo constante, O(1).
 *
 *   primeiro                                   ultimo
 *      |                                          |
 *      v                                          v
 *    [ A ] <--> [ B ] <--> [ C ] <--> [ D ] <--> [ E ]
 *
 * Complexidades:
 *   inserirInicio / inserirFim / removerInicio / removerFim / remover(no) -> O(1)
 *   buscarNo / inserirOrdenado                                           -> O(n)
 */
public class ListaLigada<T> implements Iterable<T> {

    private No<T> primeiro;
    private No<T> ultimo;
    private int tamanho;

    // ------------------------------------------------------------------
    // Inserção (as duas pontas do DEQUE)
    // ------------------------------------------------------------------

    /** Insere no INÍCIO da lista. */
    public No<T> inserirInicio(T dado) {
        No<T> novo = new No<>(dado);
        if (primeiro == null) {          // lista vazia: o novo nó é o primeiro e o último
            primeiro = novo;
            ultimo = novo;
        } else {
            novo.setProximo(primeiro);   // novo aponta para o antigo primeiro
            primeiro.setAnterior(novo);  // antigo primeiro aponta de volta para o novo
            primeiro = novo;
        }
        tamanho++;
        return novo;
    }

    /** Insere no FIM da lista. */
    public No<T> inserirFim(T dado) {
        No<T> novo = new No<>(dado);
        if (ultimo == null) {
            primeiro = novo;
            ultimo = novo;
        } else {
            novo.setAnterior(ultimo);
            ultimo.setProximo(novo);
            ultimo = novo;
        }
        tamanho++;
        return novo;
    }

    /** Insere o dado imediatamente ANTES do nó de referência (ou no fim, se a referência for nula). */
    public No<T> inserirAntes(No<T> referencia, T dado) {
        if (referencia == null) {
            return inserirFim(dado);
        }
        if (referencia == primeiro) {
            return inserirInicio(dado);
        }
        No<T> novo = new No<>(dado);
        No<T> anterior = referencia.getAnterior();
        anterior.setProximo(novo);
        novo.setAnterior(anterior);
        novo.setProximo(referencia);
        referencia.setAnterior(novo);
        tamanho++;
        return novo;
    }

    /**
     * Insere mantendo a lista sempre ordenada pelo comparador.
     * Devolve false (e não insere) se já existir um elemento igual.
     */
    public boolean inserirOrdenado(T dado, Comparator<T> comparador) {
        No<T> atual = primeiro;
        while (atual != null) {
            int resultado = comparador.compare(atual.getDado(), dado);
            if (resultado == 0) {
                return false;            // duplicado
            }
            if (resultado > 0) {
                break;                   // achou o primeiro maior: inserir antes dele
            }
            atual = atual.getProximo();
        }
        inserirAntes(atual, dado);
        return true;
    }

    // ------------------------------------------------------------------
    // Remoção
    // ------------------------------------------------------------------

    /** Remove e devolve o primeiro elemento (ou null se a lista estiver vazia). */
    public T removerInicio() {
        return primeiro == null ? null : remover(primeiro);
    }

    /** Remove e devolve o último elemento (ou null se a lista estiver vazia). */
    public T removerFim() {
        return ultimo == null ? null : remover(ultimo);
    }

    /** Remove um nó qualquer, "religando" os vizinhos um ao outro. */
    public T remover(No<T> no) {
        if (no.getAnterior() == null) {
            primeiro = no.getProximo();                 // era o primeiro
        } else {
            no.getAnterior().setProximo(no.getProximo());
        }
        if (no.getProximo() == null) {
            ultimo = no.getAnterior();                  // era o último
        } else {
            no.getProximo().setAnterior(no.getAnterior());
        }
        no.setAnterior(null);
        no.setProximo(null);
        tamanho--;
        return no.getDado();
    }

    // ------------------------------------------------------------------
    // Consulta
    // ------------------------------------------------------------------

    /** Percorre a lista do início ao fim e devolve o primeiro nó que atende ao critério. */
    public No<T> buscarNo(Predicate<T> criterio) {
        for (No<T> atual = primeiro; atual != null; atual = atual.getProximo()) {
            if (criterio.test(atual.getDado())) {
                return atual;
            }
        }
        return null;
    }

    /** Copia os elementos para uma lista comum (usado para montar vetores de ordenação). */
    public List<T> paraLista() {
        List<T> copia = new ArrayList<>(tamanho);
        for (T dado : this) {
            copia.add(dado);
        }
        return copia;
    }

    public No<T> getPrimeiro() { return primeiro; }
    public No<T> getUltimo() { return ultimo; }
    public int tamanho() { return tamanho; }
    public boolean estaVazia() { return tamanho == 0; }

    /** Permite usar a lista em um "for (T x : lista)". */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private No<T> atual = primeiro;

            @Override
            public boolean hasNext() {
                return atual != null;
            }

            @Override
            public T next() {
                if (atual == null) {
                    throw new NoSuchElementException();
                }
                T dado = atual.getDado();
                atual = atual.getProximo();
                return dado;
            }
        };
    }
}
