/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.system;

/**
 * Implementação de operações sobre bits.
 *
 */
public class Bits {

    /**
     * Obtém o valor do n-ésimo bit do inteiro, contado do
     * menos significativo (direita) para o mais significativo (esquerda).
     *
     * @param x O inteiro.
     * @param n O n-ésimo (zero-based) bit.
     * @return O valor do n-ésimo bit do inteiro.
     */
    public static int value(int x, int n) {
        return (x & (1 << n)) >>> n;
    }

    /**
     * Define com o valor zero o n-ésimo (zero-based) bit do inteiro,
     * contado do menos significativo (direita) para o mais significativo
     * (esquerda).
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
     * Define com o valor 1 o n-ésimo (zero-based) bit do inteiro, contado do
     * menos significativo (direita) para o mais significativo (esquerda).
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
     * Verifica se o valor fornecido é uma potência de dois.
     *
     * @param valor O valor a ser verificado.
     *
     * @return {@code true} se e somente se o valor é uma potência
     * de dois.
     */
    public static boolean isPowerOfTwo(int valor) {
        return valor > 0 && ((valor & (valor - 1)) == 0);
    }

    /**
     * Representação em sequência de caracteres dos bits do
     * valor.
     * @param valor O valor.
     * @return Sequência de bits do valor, do mais significativo para
     * o menos significativo. Um espaço em branco separa cada 4
     * bits exibidos.
     */
    public static String bitsToString(int valor) {
        char[] bits = new char[39];

        // Última posição da saída
        int indexSaida = 0;

        // Percorre todos os bits
        for (int i = 31; i > 0; i--) {
            bits[indexSaida] = (char) (Bits.value(valor, i) + '0');

            if ((i & 3) == 0) {
                bits[++indexSaida] = ' ';
            }

            indexSaida++;
        }

        bits[indexSaida] = (char) (Bits.value(valor, 0) + '0');

        return new String(bits);
    }
}
