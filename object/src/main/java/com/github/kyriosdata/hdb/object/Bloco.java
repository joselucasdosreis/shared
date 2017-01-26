package com.github.kyriosdata.hdb.object;

/**
 * Encapsula relação entre um bloco ("pedaço" de um arquivo) e
 * a área em RAM na qual é depositado (buffer).
 */
public class Bloco {
    int bloco;
    int contador;

    /**
     * Ao contrário dos demais campos, o buffer não se
     * altera. Ou seja, uma instância de bloco sempre
     * está associado a um dado buffer. Contudo, tanto
     * o bloco de fato associado ao buffer (fixo) e,
     * naturalmente, o contador, variam.
     */
    public final Buffer buffer;

    public Bloco(Buffer buffer) {
        this.buffer = buffer;
    }
}
