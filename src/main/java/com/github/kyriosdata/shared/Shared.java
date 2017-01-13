/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * Classe empregada para gerir valores (índices) empregados
 * para identificar registros de log.
 *
 * <p>Oferece serviço para controle de memória em três
 * fases: (a) reserva; (b) indicação de uso e (c)
 * consumo. A reserva simplesmente assegura que o
 * valor obtido não será empregado por outro. A
 * indicação de uso informa que o uso do valor está
 * liberado e, consequentemente, pode ser consumido.
 */
public class Shared {
    // Quantidade de entradas vazias
    private AtomicInteger totalFree;

    // Primeira posição livre no buffer (ring)
    private AtomicInteger firstFree;

    // Indica se reader está trabalhando
    private AtomicBoolean working;

    // Total de espaços vazios (tamanho do ring)
    private final int SIZE = 32;

    // Máscara para "rotacionar" índices
    private final int MASCARA = SIZE - 1;

    private int primeiroParaTratar;

    // 32 bits (1 para cada valor do buffer)
    private int valoresUsados;

    private int consumeCalled = 0;

    public Shared() {
        totalFree = new AtomicInteger(SIZE);
        firstFree = new AtomicInteger(0);

        // Inicialmente nenhuma entrada está usada.
        valoresUsados = 0;

        working = new AtomicBoolean(false);
        primeiroParaTratar = 0;
    }

    /**
     * Obtém o valor do n-ésimo bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo (zero-based) bit.
     *
     * @return O valor do n-ésimo bit do inteiro.
     */
    public static int bitValue(int x, int n) {
        return (x & (1 << n)) >>> n;
    }

    /**
     * Define com o valor zero o n-ésimo (zero-based) bit do inteiro.
     *
     * @param x O inteiro.
     *
     * @param n O n-ésimo bit (zero-based).
     *
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
     *
     * @return O inteiro com o n-ésimo bit definido com o valor
     * 1 (possivelmente o mesmo valor fornecido).
     */
    public static int set(int x, int n) {
        return x | (1 << n);
    }

    /**
     * Informação empregada exclusivamente para depuração.
     *
     * @return Informações para uso de depuração.
     */
    public String status() {
        int totalUsados = 0;

        for(int i = 0; i < SIZE; i++) {
            if ((valoresUsados & i) != 1) {
                totalUsados++;
            }
        }

        return "\nWorking: " + working.get() + "\nUsados: " + totalUsados +
                "\nTotal free: " + totalFree.get() + "\nFirst free: " +
                firstFree.get();
    }

    /**
     * Obtém valor reservado para uso. Nenhuma outra chamada
     * irá recuperar esse valor, senão após ter sido usado e
     * disponibilizado para uso novamente.
     *
     * <p>Após reservado, o valor poderá ser usado e, após tal,
     * indique isso pelo método {@link #used(int)}.
     *
     * @return Identificador reservado para uso.
     *
     * @see #used(int)
     * @see #consume()
     */
    public int reserve() {
        while (true) {
            int free = totalFree.get();
            if (free > 0) {
                if (totalFree.compareAndSet(free, free - 1)) {
                    return firstFree.getAndIncrement() & MASCARA;
                }
            } else {
                //consume();
            }
        }
    }

    /**
     * Sinaliza que valor anteriormente recuperado pelo método
     * {@link #reserve()} já foi utilizado e, portanto, o
     * "consumidor" poderá fazer uso do mesmo.
     *
     * @param valor Valor anteriormente reservado e já empregado
     *              pelo produtor.
     *
     * @see #reserve()
     * @see #consume()
     */
    public void used(int valor) {
        valoresUsados = valoresUsados | valor;
    }

    /**
     * Valores reservados e também já utilizados são consumidos
     * pelo presente método e, imediatamente após, liberados para
     * novo ciclo (reserva, uso, consumo).
     *
     * @see #reserve()
     * @see #used(int)
     */
    public void consume() {

        // Não inicia se há trabalho em andamento
        if (working.getAndSet(true)) {
            return;
        }

        int free = totalFree.get();

        while (true) {

            // Retorne se não há o que tratar
            if (free == SIZE) {
                working.set(false);
                return;
            }

            int totalParaTratar = SIZE - free;

            // Faixa "candidata" para ser tratada
            int first = primeiroParaTratar & MASCARA;
            int last = (first + totalParaTratar - 1) & MASCARA;

            //System.out.println("first: " + first + " Last: " + last + " Tratar: " + totalParaTratar + " Free: " + free);

            // Percorre as "reservadas" de first até last
            // nessa ordem, enquanto estiverem "valoresUsados"
            int lu = -1;
            int i = first;
            while (i <= last && (valoresUsados & i) != 0) {
               lu = i;
               i++;
            }

            // Não há o que fazer se não houver "usada"
            if (lu < 0) {
                return;
            }

            // NESSE PONTO SABE-SE
            // (a) há pelo menos uma usada
            // (b) faixa das valoresUsados é de first até lu, inclusive

            // Atualiza total da lista "final"
            totalParaTratar = lu - first + 1;

            // Repasse <first, lu> para tratamento, nessa ordem.

            // Limpa faixa de valoresUsados (<first,lu>)
            for (int k = first; k <= lu; k++) {
                valoresUsados = valoresUsados & (~k);
            }

            // Atualiza próximo a ser tratado
            primeiroParaTratar = primeiroParaTratar + totalParaTratar;

            //System.out.println("first: " + first + " lu: " + lu + " Tratar: " + totalParaTratar + " Free: " + free);

            // Liberar tratados
            free = totalFree.addAndGet(totalParaTratar);

            System.out.println(++consumeCalled);
        }
    }
}
