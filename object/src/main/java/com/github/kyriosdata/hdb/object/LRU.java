package com.github.kyriosdata.hdb.object;

import java.util.HashMap;

/**
 * Implementação de Least Recently Used (LRU) para permitir que
 * o bloco usado há mais tempo possa ser removido para ceder
 * espaço para outro, que ocupará o <i>buffer</i> correspondente.
 *
 * <p>Essa implementação assume que o bloco é identificado por
 * um inteiro único e o <i>buffer</i> por ele empregado também
 * por um inteiro único. O bloco é a chave e o <i>buffer</i> é o
 * valor no dicionário empregado na implementação.
 *
 * <p>Essa classe não é <i>thread safe</i>. Ou seja, proteção
 * deverá ser oferecida, se for o caso, para que a funcionalidade
 * seja assegurada.
 *
 * <p>A implementação faz uso de um Map para localizar o <i>buffer</i>
 * empregado por um bloco e de uma lista duplamente encadeada para
 * permitir que o <i>buffer</i> localizado possa ir para o início
 * da lista, mais recente, com número constante de operações.
 *
 * <p>Quando uma instância é criada a lista é preenchida totalmente.
 * Ou seja, não são mais permitidas inserções. Restando uma única operação
 * relevante para a implementação do LRU: "trazer" o nó que irá referenciar
 * o bloco requisitado para a frente da lista ({@link #bringToFront(No)}).
 */
public class LRU {

    private HashMap<Integer, No> usados;
    private No head;
    private No tail;

    public LRU(int capacity) {
        usados = new HashMap<>(capacity);

        head = new No(Integer.MIN_VALUE, 0);
        tail = new No(Integer.MIN_VALUE, 1);

        head.next = tail;
        tail.prev = head;

        for (int i = 2; i < capacity; i++) {
            No n = new No(Integer.MIN_VALUE, i);
            tail.next = n;
            n.prev = tail;
            tail = n;
        }
    }

    public int use(int blocoId) {

        No old = usados.get(blocoId);

        if (old == null) {
            old = tail;
            usados.remove(old.key);
            usados.put(blocoId, old);
            old.key = blocoId;
        }

        bringToFront(old);

        return old.value;
    }

    private void bringToFront(No no) {
        if (head == no) {
            return;
        }

        no.prev.next = no.next;

        if (no != tail) {
            no.next.prev = no.prev;
        } else {
            tail = no.prev;
        }

        head.prev = no;
        no.next = head;
        no.prev = null;
        head = no;
    }

    private class No {
        int key;
        int value;
        No prev;
        No next;

        public No(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
}