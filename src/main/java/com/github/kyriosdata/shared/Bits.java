/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

/**
 * Implementação de operações sobre bits.
 *
 */
public class Bits {

    // Tamanho da "lista circular" (ring buffer).
    // Necessariamente uma potência de 2.
    private final int SIZE = 32;

    /**
     * Obtém o valor do n-ésimo bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo (zero-based) bit.
     * @return O valor do n-ésimo bit do inteiro.
     */
    public static int bitValue(int x, int n) {
        return (x & (1 << n)) >>> n;
    }

    /**
     * Define com o valor zero o n-ésimo (zero-based) bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo bit (zero-based).
     * @return O inteiro com o n-ésimo (zero-based) bit 0. Possivelmente
     * o valor retornado é o mesmo daquele fornecido, se o bit já era 0.
     */
    public static int cls(int x, int n) {
        return x & ~(1 << n);
    }

    /**
     * Define com o valor 1 o n-ésimo (zero-based) bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo bit (zero-based).
     * @return O inteiro com o n-ésimo bit definido com o valor
     * 1 (possivelmente o mesmo valor fornecido).
     */
    public static int set(int x, int n) {
        return x | (1 << n);
    }

    /**
     * Produz sequência correspondente à representação binária do inteiro fornecido.
     * @param produzidos
     */
    public static String showBits(int produzidos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(Bits.bitValue(produzidos, i));
            if ((i+1) % 4 == 0) {
                sb.append(" ");
            }
        }

        return sb.toString();
    }
}
