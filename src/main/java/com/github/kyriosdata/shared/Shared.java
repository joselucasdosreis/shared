/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe que encapsula operações de controle de concorrência
 * em cenário de vários produtores e um único consumidor.
 *
 * <p>Orientações para o uso correto. O método
 * {@link #aloca()} indica o desejo de produzir (algo), o retorno
 * é um inteiro, valor de 0 a 31, inclusive, que unicamente identifica
 * o que se deseja produzir.
 *
 * <p>A alocação não é suficiente para o
 * consumo. Antes de ser consumido o valor alocado precisa ser
 * "produzido" pelo método {@link #produz(int)}. O valor fornecido
 * como argumento deve ser aquele obtido do método {@link #aloca()}.
 *
 * <p>O consumo propriamente dito ocorre pelo método {@link #consome(int, int)}.
 * O usuário dessa classe deve sobrescrever esse método. Quando chamado
 * sabe-se que a faixa de valores indicada foi produzida e pode ser
 * consumida, independente do que isso significa para a aplciação
 * em questão. Uma possibilidade é empregar os inteiros de 0 a 31 como
 * índices de um <i>buffer pool</i>.
 */
public class Shared {

    // Tamanho da "lista circular" (ring buffer).
    // Necessariamente uma potência de 2.
    private final int SIZE = 32;

    // Máscara para "rotacionar" índices
    // Permite substituir "% SIZE" por "& MASCARA" (mais eficiente).
    private final int MASCARA = SIZE - 1;

    // Empregada para evitar reentrância do consumidor.
    private AtomicBoolean working = new AtomicBoolean(false);

    // Indica primeira entrada livre (first free).
    private AtomicInteger ff = new AtomicInteger(0);

    // Indica última entrada livre (las free)
    private int lf = SIZE - 1;

    // 32 bits (1 para cada valor da lista circular)
    private int valoresUsados = 0;

    /**
     * Consome a faixa de valores consecutivos indicada.
     * Método deve ser sobrescrito com implementação
     * relevante para o uso pretendido.
     *
     * <p>Após a execução desse métodos os valores
     * na faixa fornecida estarão disponíveis para
     * reutilização pelo método {@link #aloca()}.
     *
     * @param i Primeiro valor da faixa.
     * @param f Último valor da faixa.
     */
    public void consome(int i, int f) {}

    /**
     * Indica a produção de informação associada
     * ao valor fornecido.
     *
     * @param v O valor que identifica a produção.
     *           Esse valor necessariamente deve ser
     *           obtido do método {@link #aloca()}.
     *
     * @see #aloca()
     * @see #limpa()
     */
    public void produz(int v) {
        valoresUsados = set(valoresUsados, v);
    }

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
     * Quantidade de entradas ocupadas (já alocadas).
     *
     * <p>Nenhuma das entradas, necessariamente, foi
     * "produzida", mas seguramente já foi alocada.
     *
     * @return Número de entradas ocupadas.
     */
    public int totalAlocados() {
        return SIZE - totalLiberados();
    }

    /**
     * Quantidade de entradas disponíveis para alocação.
     *
     * @return Número de entradas (valores) disponíveis
     * para alocação imediata.
     */
    public int totalLiberados() {
        return lf - ff.get() + 1;
    }

    /**
     * Empregada exclusivamente para teste.
     *
     * @return Primeiro valor livre para ser alocado.
     */
    public int getFirstFree() {
        return ff.get();
    }

    /**
     * Realiza alocação de um valor.
     *
     * @return O identificador único, valor de 0 a 31, inclusive,
     * que deve ser "produzido" e posteriormente consumido.
     */
    public int aloca() {
        while (true) {
            int candidato = ff.get();
            if (candidato <= lf) {
                if (ff.compareAndSet(candidato, candidato + 1)) {
                    return candidato & MASCARA;
                }
            } else {
                limpa();
            }
        }
    }

    /**
     * Processa valores já disponíveis. Ou seja,
     * todos os que já foram alocados e também
     * usados.
     */
    public void limpa() {

        // Evita reentrância
        if (working.getAndSet(true)) {
            return;
        }

        int fa = lf + 1;
        int la = SIZE + ff.get() - 1;

        // Percorre faixa de alocados pelo total de usados
        int totalUsados = getTotalUsados(fa, la);

        // Alocados e usados é |[fa, lu]| = totalUsados
        int lu = fa + totalUsados - 1;

        // Consome entrada
        consome(fa, lu);

        // Limpa indicação de uso
        for (int i = fa; i <= lu; i++) {
            cls(valoresUsados, i & MASCARA);
        }

        // Disponibiliza valores para reutilização
        lf = lf + totalUsados;

        working.set(false);
    }

    private int getTotalUsados(int first, int last) {
        int totalUsados = 0;
        for (int i = first; i <= last; i++) {
            int indice = i & MASCARA;
            if (bitValue(valoresUsados, indice) == 1) {
                totalUsados++;
            } else {
                break;
            }
        }
        return totalUsados;
    }
}
