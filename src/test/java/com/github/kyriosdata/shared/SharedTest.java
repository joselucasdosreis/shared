package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SharedTest {

    @Test
    public void alocaSequencialmenteZeroToTrintaUm() {
        Shared s = new Shared();

        int anterior = s.aloca();
        s.produz(anterior);
        for (int i = 0; i < 1_000; i++) {
            int proximo = s.aloca();
            s.produz(proximo);
            assertTrue(proximo > -1 && proximo < 32);
            if (anterior == 31) {
                assertEquals(0, proximo);
            } else {
                assertEquals(anterior, proximo - 1);
            }
            anterior = proximo;
        }
    }

    @Test
    public void consumidorNaoReentrante() {
        Shared s = new Shared();

        for(int i = 0; i < 1_000; i++) {
            s.flush();
        }
    }

    @Test
    public void verificaEstadoInicial() {
        Shared s = new Shared();
        assertEquals(32, s.totalLiberados());
        assertEquals(0, s.totalAlocados());
    }

    @Test
    public void estadoAposDuasAlocacoes() {
        Shared s = new Shared();
        assertEquals(0, s.aloca());
        assertEquals(1, s.aloca());

        assertEquals(30, s.totalLiberados());
        assertEquals(2, s.totalAlocados());
    }

    @Test
    public void alocaUsaLibera() {
        Shared s = new Shared();
        assertEquals(0, s.aloca());

        s.produz(0);
        s.flush();

        assertEquals(32, s.totalLiberados());
        assertEquals(0, s.totalAlocados());
        assertFalse(s.produzido(0));
    }

    @Test
    public void alocaUsaLiberaTodos() {
        Shared s = new Shared();

        for (int i = 0; i < 32; i++) {
            assertEquals(i, s.aloca());
            s.produz(i);
        }

        assertEquals(32, s.totalAlocados());
        assertEquals(0, s.totalLiberados());

        s.flush();

        assertEquals(0, s.totalAlocados());
        assertEquals(32, s.totalLiberados());
    }

    @Test
    public void alocaSemUsarLiberacaoNaoLimpa() {
        Shared s = new Shared();

        for (int i = 0; i < 32; i++) {
            assertEquals(i, s.aloca());
        }

        s.flush();

        assertEquals(32, s.totalAlocados());
        assertEquals(0, s.totalLiberados());
    }

    @Test
    public void alocaUsaLiberaForcandoCircularidade() {
        Shared shared = new Shared();

        // 32 alocados
        for (int i = 0; i < 32; i++) {
            assertEquals(i, shared.aloca());
        }

        assertEquals(32, shared.totalAlocados());
        assertEquals(0, shared.totalLiberados());

        // 1 usado
        shared.produz(0);
        assertEquals(32, shared.totalAlocados());
        assertEquals(0, shared.totalLiberados());

        // Único usado é limpado
        shared.flush();

        assertEquals(31, shared.totalAlocados());
        assertEquals(1, shared.totalLiberados());

        // Aloca o único liberado
        // 32 alocados, 0 liberados
        int unicoLiberado = shared.aloca();
        assertEquals(0, unicoLiberado);
        assertEquals(32, shared.totalAlocados());
        assertEquals(0, shared.totalLiberados());

        // 32 usados
        for (int i = 0; i < 32; i++) {
            shared.produz(i);
        }

        // TODOS ALOCADOS E USADOS
        // Ao chamar aloca() -> chamar flush() e alocar o primeiro 0.
        int alocado = shared.aloca();
        assertEquals(1, alocado);
        assertEquals(1, shared.totalAlocados());
        assertEquals(31, shared.totalLiberados());
    }

    @Test
    public void circularidadeSemModulo() {
        int size = 32; // 2^5
        int mask = size - 1;

        for(int i = 0; i < 10_000_000; i++) {
            int indice = i & mask;
            int modulo = i % 32;
            assertEquals(modulo, indice);
        }
    }

    /**
     * Executa 36.000 alocações, produções e "liberações".
     * Ou seja, o esforço equivalente ao de 10 logs/s
     * durante 1 hora.
     *
     * @throws Exception Possivelmente gerada.
     */
    @Test
    public void comVariasThreads() throws Exception {
        SharedJustForTeste shared = new SharedJustForTeste();

        executaThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                int k = shared.aloca();
                shared.produz(k);
            }
        });

        shared.flush();

        assertEquals(72_000, shared.total());
    }

    @Test
    public void verificaNaoReentranciaFlush() throws Exception {
        SharedJustForTeste shared = new SharedJustForTeste();

        executaThreads(() -> {
            for (int i = 0; i < 100_000; i++) {
                shared.flush();
            }
        });
    }

    private void executaThreads(Runnable tarefa) throws InterruptedException {
        int TOTAL_THREADS = 20;

        Thread[] threads = new Thread[TOTAL_THREADS];

        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i] = new Thread(tarefa);
        }

        // INICIA
        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i].start();
        }

        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i].join();
        }
    }
}

class SharedJustForTeste extends Shared {
    private int[] contador = new int[32];

    @Override
    public void consome(int i, boolean ultimo) {
        contador[i] = contador[i] + 1;
    }

    public void output() {
        for(int i = 0; i < 32; i++) {
            System.out.println(i + ": " + contador[i]);
        }
    }

    public int total() {
        int total = 0;
        for (int i = 0; i < 32; i++) {
            total = total + contador[i];
        }

        return total;
    }
}

