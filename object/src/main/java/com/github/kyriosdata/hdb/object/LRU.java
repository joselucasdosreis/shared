package com.github.kyriosdata.hdb.object;

import java.util.HashMap;

/**
 * Implementação de Least Recently Used (LRU) para permitir que
 * o bloco usado há mais tempo possa ser removido para ceder
 * espaço para outro, que ocupará o <i>buffer</i> correspondente.
 * 
 * <p>Essa implementação assume que o bloco é identificado por 
 * um inteiro único e o <i>buffer</i> por ele empregado também
 * por um inteiro. O bloco é a chave e o <i>buffer</i> é o valor
 * no dicionário empregado na implementação.
 * 
 * <p>Essa classe não é <i>thread safe</i>. Ou seja, proteção
 * deverá ser oferecida, se for o caso, para que a funcionalidade
 * seja assegurada. 
 */
public class LRU {
    int capacity;
    HashMap<Integer, Node> map;
    Node head = null;
    Node end = null;

    public LRU(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>(capacity);
    }

    public int get(int key) {
        if (map.containsKey(key)) {
            Node n = map.get(key);
            remove(n);
            setHead(n);
            return n.value;
        }

        return -1;
    }

    public void remove(Node n) {
        if (n.pre != null) {
            n.pre.next = n.next;
        } else {
            head = n.next;
        }

        if (n.next != null) {
            n.next.pre = n.pre;
        } else {
            end = n.pre;
        }

    }

    public void setHead(Node n) {
        n.next = head;
        n.pre = null;

        if (head != null)
            head.pre = n;

        head = n;

        if (end == null)
            end = head;
    }

    public void set(int key, int value) {
        if (map.containsKey(key)) {
            Node old = map.get(key);
            old.value = value;
            remove(old);
            setHead(old);
        } else {
            Node created = new Node(key, value);
            if (map.size() >= capacity) {
                map.remove(end.key);
                remove(end);
                setHead(created);

            } else {
                setHead(created);
            }

            map.put(key, created);
        }
    }
}

class Node {
    int key;
    int value;
    Node pre;
    Node next;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}