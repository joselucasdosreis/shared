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
 * é um inteiro no intervalo fechado [0, 31], que unicamente
 * identifica o que se deseja produzir. Em geral, o valor fornecido
 * é o índice de um <i>array</i> ou permite identificar, de fato,
 * o dado compartilhado, ou seja, esse valor funciona como um
 * <i>handle</i> para a real informação.
 *
 * <p>A alocação não é suficiente para o
 * consumo. Antes de ser consumido o valor alocado precisa ser
 * "produzido" pelo método {@link #produz(int)}. O valor fornecido
 * como argumento deve ser aquele obtido do método {@link #aloca()}.
 * Noutras palavras, uma chamada desse método com o argumento
 * 7, por exemplo, indica que a informação associada ao valor
 * 7 está disponível para consumo.
 *
 * <p>O consumo propriamente dito ocorre pelo método {@link #consome(int)}.
 * O usuário dessa classe deve sobrescrever esse método. Quando chamado
 * sabe-se que a faixa de valores indicada foi produzida e deve ser
 * consumida.
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
     * Consome a informação produzida e associada
     * ao valor indicado.
     *
     * <p>Após a execução desse método o valor
     * correspondente estará disponível para
     * reutilização pelo método {@link #aloca()}.
     *
     * @param v Valor a ser consumido.
     */
    public void consome(int v) {}

    /**
     * Indica a produção de informação associada
     * ao valor fornecido.
     *
     * @param v O valor que identifica a produção.
     *           Esse valor necessariamente deve ser
     *           obtido do método {@link #aloca()}.
     *
     * @see #aloca()
     * @see #flush()
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
                flush();
            }
        }
    }

    /**
     * Processa valores já disponíveis. Ou seja,
     * todos os que já foram alocados e também
     * usados.
     */
    public void flush() {

        // Evita reentrância
        if (working.getAndSet(true)) {
            return;
        }

        int fa = lf + 1;
        int la = ff.get() - 1 + SIZE;

        // Percorre faixa de alocados pelo total de usados
        int totalUsados = usadosConsecutivosNaFaixa(fa, la);

        // Alocados e usados é |[fa, lu]| = totalUsados
        int lu = fa + totalUsados - 1;

        // Consome entrada
        for (int i = fa; i <= lu; i++) {
            consome(i & MASCARA);
            cls(valoresUsados, i & MASCARA);
        }

        // Disponibiliza valores para reutilização
        lf = lf + totalUsados;

        working.set(false);
    }

    private int usadosConsecutivosNaFaixa(int first, int last) {
        int totalUsados = 0;
        int i = first;
        while (i <= last && bitValue(valoresUsados, i & MASCARA) == 1) {
            i++;
            totalUsados++;
        }

        return totalUsados;
    }
}
