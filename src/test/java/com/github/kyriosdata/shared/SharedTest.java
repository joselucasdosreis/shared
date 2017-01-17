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
            assertTrue(proximo > -1 && proximo < Shared.SIZE);
            if (anterior == Shared.SIZE - 1) {
                assertEquals(0, proximo);
            } else {
                assertEquals(anterior, proximo - 1);
            }
            anterior = proximo;
        }
    }

    @Test
    public void flushCalledWithoutWorkToDo() {
        Shared s = new Shared();

        for(int i = 0; i < 1_000; i++) {
            s.flush();
        }
    }

    @Test
    public void verificaEstadoInicial() {
        Shared s = new Shared();
        assertEquals(Shared.SIZE, s.totalLiberados());
        assertEquals(0, s.totalAlocados());
    }

    @Test
    public void estadoAposDuasAlocacoes() {
        Shared s = new Shared();
        assertEquals(0, s.aloca());
        assertEquals(1, s.aloca());

        assertEquals(Shared.SIZE - 2, s.totalLiberados());
        assertEquals(2, s.totalAlocados());
    }

    @Test
    public void alocaUsaLibera() {
        Shared s = new Shared();
        assertEquals(0, s.aloca());

        s.produz(0);
        s.flush();

        assertEquals(Shared.SIZE, s.totalLiberados());
        assertEquals(0, s.totalAlocados());
        assertFalse(s.produzido(0));
    }

    @Test
    public void alocaUsaLiberaTodos() {
        Shared s = new Shared();

        for (int i = 0; i < Shared.SIZE; i++) {
            assertEquals(i, s.aloca());
            s.produz(i);
        }

        assertEquals(Shared.SIZE, s.totalAlocados());
        assertEquals(0, s.totalLiberados());

        s.flush();

        assertEquals(0, s.totalAlocados());
        assertEquals(Shared.SIZE, s.totalLiberados());
    }

    @Test
    public void alocaSemUsarLiberacaoNaoLimpa() {
        Shared s = new Shared();

        for (int i = 0; i < Shared.SIZE; i++) {
            assertEquals(i, s.aloca());
        }

        s.flush();

        assertEquals(Shared.SIZE, s.totalAlocados());
        assertEquals(0, s.totalLiberados());
    }

    @Test
    public void alocaUsaLiberaForcandoCircularidade() {
        Shared shared = new Shared();

        // 32 alocados
        for (int i = 0; i < Shared.SIZE; i++) {
            assertEquals(i, shared.aloca());
        }

        assertEquals(Shared.SIZE, shared.totalAlocados());
        assertEquals(0, shared.totalLiberados());

        // 1 usado
        shared.produz(0);
        assertEquals(Shared.SIZE, shared.totalAlocados());
        assertEquals(0, shared.totalLiberados());

        // Único usado é limpado
        shared.flush();

        assertEquals(Shared.SIZE - 1, shared.totalAlocados());
        assertEquals(1, shared.totalLiberados());

        // Aloca o único liberado
        // 32 alocados, 0 liberados
        int unicoLiberado = shared.aloca();
        assertEquals(0, unicoLiberado);
        assertEquals(Shared.SIZE, shared.totalAlocados());
        assertEquals(0, shared.totalLiberados());

        // 32 usados
        for (int i = 0; i < Shared.SIZE; i++) {
            shared.produz(i);
        }

        // TODOS ALOCADOS E USADOS
        // Ao chamar aloca() -> chamar flush() e alocar o primeiro 0.
        int alocado = shared.aloca();
        assertEquals(1, alocado);
        assertEquals(1, shared.totalAlocados());
        assertEquals(Shared.SIZE - 1, shared.totalLiberados());
    }

    @Test
    public void circularidadeSemModulo() {
        int size = Shared.SIZE; // 2^5
        int mask = size - 1;

        for(int i = 0; i < 10_000_000; i++) {
            int indice = i & mask;
            int modulo = i % Shared.SIZE;
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
            for (int i = 0; i < 10_000; i++) {
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

