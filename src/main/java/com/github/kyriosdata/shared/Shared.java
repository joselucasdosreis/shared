/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe empregada para gerir valores (índices) empregados
 * para identificar registros de log.
 * <p>
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

    /**
     * Cria controle de concorrência para uso de 32 valores, 0 a 31 inclusive.
     * Primeiro deve-se reservar o valor a ser utilizado
     * ({@link #aloca()} ()}), indicar que está disponível para consumo
     * ({@link #used(int)}) e, finalmente, consumido ({@link #limpa()} ()}).
     */
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

    private AtomicInteger ff = new AtomicInteger(0);
    private AtomicInteger lf = new AtomicInteger(31);

    public int totalAlocados() {
        return 32 - totalLiberados();
    }

    public int totalLiberados() {
        return lf.get() - ff.get() + 1;
    }

    public String liberados() {
        return "Liberados: [" + (ff.get() & MASCARA) + ", " + (lf.get() & MASCARA) + "]";
    }

    public int aloca() {
        while (true) {
            int candidato = ff.get();
            if (candidato <= lf.get()) {
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
        int totalLiberados = lf.get() - ff.get() + 1;
        int totalOcupados = 32 - totalLiberados;

        if (totalOcupados == 0) {
            return;
        }

        int first = lf.get() + 1;
        int last = first + totalOcupados - 1;

        // Percorre alocados (antes e após os liberados)
        // enquanto forem usados.
        int totalUsados = getTotalUsados(first, last);

        // Alocados usados é |[first, ...]| = totalUsados
        // (consuma-os e os retorne para a lista de livres)

        // < CONSOME AQUI >

        // Libere para reutilização
        // Primeiro. Indique que não estão mais usados
        int lastUsed = first + totalUsados - 1;
        for (int i = first; i <= lastUsed; i++) {
            cls(valoresUsados, i & MASCARA);
        }

        // Segundo. Atualiza último liberado
        lf.addAndGet(totalUsados);
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

    /**
     * Sinaliza que valor anteriormente recuperado pelo método
     * {@link #reserve()} já foi utilizado e, portanto, o
     * "consumidor" poderá fazer uso do mesmo.
     *
     * @param valor Valor anteriormente reservado e já empregado
     *              pelo produtor.
     * @see #aloca()
     * @see #limpa()
     */
    public void used(int valor) {
        // Identifica índice correspondente ao valor
        int indice = valor & MASCARA;
        valoresUsados = set(valoresUsados, indice);
    }
}
