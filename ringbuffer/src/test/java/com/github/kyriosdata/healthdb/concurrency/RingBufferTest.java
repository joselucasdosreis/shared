package com.github.kyriosdata.healthdb.concurrency;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RingBufferTest {

    static int valor = 0;

    @Test
    public void consumidorQueGeraExcecaoNaoInterrompeDemais() {

        RingBuffer s = new RingBuffer() {
            @Override
            public void consome(int i, boolean u) {
                if (i == 0) {
                    throw new RuntimeException();
                } else {
                    valor++;
                }
            }
        };

        // Três produções, primeira falha, ao
        // contrários das demais, que acrescenta valor.
        s.produz(s.aloca());
        s.produz(s.aloca());
        s.produz(s.aloca());

        s.flush();

        assertEquals(2, valor);
    }

    @Test
    public void potenciaDeDoisObrigatoriaParaTamanho() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer(-1));
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer(3));
    }

    @Test
    public void alocaSequencialmenteZeroToTrintaUm() {
        RingBuffer s = new RingBuffer();

        int anterior = s.aloca();
        s.produz(anterior);
        for (int i = 0; i < 1_000; i++) {
            int proximo = s.aloca();
            s.produz(proximo);
            assertTrue(proximo > -1 && proximo < RingBuffer.SIZE);
            if (anterior == RingBuffer.SIZE - 1) {
                assertEquals(0, proximo);
            } else {
                assertEquals(anterior, proximo - 1);
            }
            anterior = proximo;
        }
    }

    @Test
    public void flushCalledWithoutWorkToDo() {
        RingBuffer s = new RingBuffer();

        for(int i = 0; i < 1_000; i++) {
            s.flush();
        }
    }

    @Test
    public void verificaEstadoInicial() {
        RingBuffer s = new RingBuffer();
        assertEquals(RingBuffer.SIZE, s.entradasDisponiveis());
        assertEquals(0, RingBuffer.SIZE - s.entradasDisponiveis());
    }

    @Test
    public void estadoAposDuasAlocacoes() {
        RingBuffer s = new RingBuffer();
        assertEquals(0, s.aloca());
        assertEquals(1, s.aloca());

        assertEquals(RingBuffer.SIZE - 2, s.entradasDisponiveis());
        assertEquals(2, RingBuffer.SIZE - s.entradasDisponiveis());
    }

    @Test
    public void alocaUsaLibera() {
        RingBuffer s = new RingBuffer();
        assertEquals(0, s.aloca());

        s.produz(0);
        s.flush();

        assertEquals(RingBuffer.SIZE, s.entradasDisponiveis());
        assertEquals(0, RingBuffer.SIZE - s.entradasDisponiveis());
        assertFalse(s.produzido(0));
    }

    @Test
    public void alocaUsaLiberaTodos() {
        RingBuffer s = new RingBuffer();

        for (int i = 0; i < RingBuffer.SIZE; i++) {
            assertEquals(i, s.aloca());
            s.produz(i);
        }

        assertEquals(RingBuffer.SIZE, RingBuffer.SIZE - s.entradasDisponiveis());
        assertEquals(0, s.entradasDisponiveis());

        s.flush();

        assertEquals(0, RingBuffer.SIZE - s.entradasDisponiveis());
        assertEquals(RingBuffer.SIZE, s.entradasDisponiveis());
    }

    @Test
    public void alocaSemUsarLiberacaoNaoLimpa() {
        RingBuffer s = new RingBuffer();

        for (int i = 0; i < RingBuffer.SIZE; i++) {
            int alocado = s.aloca();
            assertFalse(s.produzido(alocado));
            assertEquals(i, alocado);
        }

        s.flush();

        assertEquals(RingBuffer.SIZE, RingBuffer.SIZE - s.entradasDisponiveis());
        assertEquals(0, s.entradasDisponiveis());
    }

    @Test
    public void alocaUsaLiberaForcandoCircularidade() {
        RingBuffer ringBuffer = new RingBuffer();

        // 32 alocados
        for (int i = 0; i < RingBuffer.SIZE; i++) {
            assertEquals(i, ringBuffer.aloca());
        }

        assertEquals(RingBuffer.SIZE, RingBuffer.SIZE - ringBuffer.entradasDisponiveis());
        assertEquals(0, ringBuffer.entradasDisponiveis());

        // 1 usado
        ringBuffer.produz(0);
        assertEquals(RingBuffer.SIZE, RingBuffer.SIZE - ringBuffer.entradasDisponiveis());
        assertEquals(0, ringBuffer.entradasDisponiveis());

        // Único usado é limpado
        ringBuffer.flush();

        assertEquals(RingBuffer.SIZE - 1, RingBuffer.SIZE - ringBuffer.entradasDisponiveis());
        assertEquals(1, ringBuffer.entradasDisponiveis());

        // Aloca o único liberado
        // 32 alocados, 0 liberados
        int unicoLiberado = ringBuffer.aloca();
        assertEquals(0, unicoLiberado);
        assertEquals(RingBuffer.SIZE, RingBuffer.SIZE - ringBuffer.entradasDisponiveis());
        assertEquals(0, ringBuffer.entradasDisponiveis());

        // 32 usados
        for (int i = 0; i < RingBuffer.SIZE; i++) {
            ringBuffer.produz(i);
        }

        // TODOS ALOCADOS E USADOS
        // Ao chamar aloca() -> chamar flush() e alocar o primeiro 0.
        int alocado = ringBuffer.aloca();
        assertEquals(1, alocado);
        assertEquals(1, RingBuffer.SIZE - ringBuffer.entradasDisponiveis());
        assertEquals(RingBuffer.SIZE - 1, ringBuffer.entradasDisponiveis());
    }

    @Test
    public void circularidadeSemModulo() {
        int size = RingBuffer.SIZE; // 2^5
        int mask = size - 1;

        for(int i = 0; i < 10_000_000; i++) {
            int indice = i & mask;
            int modulo = i % RingBuffer.SIZE;
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
        RingBufferJustForTeste shared = new RingBufferJustForTeste();

        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(2);
        scheduler.scheduleWithFixedDelay(shared, 10, 50, TimeUnit.MILLISECONDS);

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
        RingBufferJustForTeste shared = new RingBufferJustForTeste();

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

class RingBufferJustForTeste extends RingBuffer implements Runnable {
    private int[] contador = new int[1024];

    @Override
    public void consome(int i, boolean ultimo) {
        contador[i] = contador[i] + 1;
    }

    public int total() {
        int total = 0;
        for (int i = 0; i < 1024; i++) {
            total = total + contador[i];
        }

        return total;
    }

    @Override
    public void run() {
        flush();
    }
}