package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    public void setUnsetBits() {
        int v = 0;

        // Um inteiro qualquer
        int random = (int)(Math.random() * Integer.MAX_VALUE);

        for (int i = 0; i < 32; i++) {
            int bit = Shared.bitValue(random, i);

            int afterSet = Shared.set(random, i);
            assertEquals(1, Shared.bitValue(afterSet, i), "Valor: " + random + " i: " + i);

            int afterCls = Shared.cls(random, i);
            assertEquals(0, Shared.bitValue(afterCls, i), "Valor: " + random + " i: " + i);

            int calculado = bit == 0 ? afterCls : afterSet;
            assertEquals(random, calculado, "Valor: " + random + " i: " + i);
        }
    }

    @Test
    public void circularidadeSemModulo() {
        int size = 32; // 2^5
        int mask = size - 1;

        int[] contadores = new int[size];

        for(int i = 0; i < 3200; i++) {
            int indice = i & mask;
            int corrente = contadores[indice];
            contadores[indice] = corrente + 1;
        }

        for (int i = 0; i < size; i++) {
            assertEquals(100, contadores[i]);
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

        Runnable decimo = () -> {
            for (int i = 0; i < 3_600; i++) {
                int k = shared.aloca();
                shared.produz(k);
            }
        };

        // CRIA
        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(decimo);
        }

        // INICIA
        for (int i = 0; i < 10; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 10; i++) {
            threads[i].join();
        }

        shared.flush();

        assertEquals(36_000, shared.total());
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

