package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SharedTest {

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

    @Test
    public void reservasExecutadasCorretamente() throws Exception {
        Shared shared = new Shared();

        System.out.println(shared.status());

        Runnable facaReservas = () -> {
            for (int i = 0; i < 16; i++) {
                int k = shared.reserve();
                shared.used(k);
            }
        };

        Thread t1 = new Thread(facaReservas);
        t1.start();

        Thread t2 = new Thread(facaReservas);
        t2.start();

        t1.join();
        t2.join();

        System.out.println(shared.status());
    }

    @Test
    public void semChamadasConcorrentes() throws Exception {
        Shared shared = new Shared();

        Runnable runnable = () -> {
            for (int i = 0; i < 10_250_000; i++) {
                int k = shared.reserve();
                shared.used(k);
            }

            System.out.println("Reached the end...");
        };

        Thread t1 = new Thread(runnable);
        t1.start();

        Thread t2 = new Thread(runnable);
        t2.start();

        t1.join();

        shared.consume();
        shared.consume();
        shared.consume();

        System.out.println(shared.status());
    }

    @Test
    public void comChamadasConcorrentes() throws Exception {
        Shared shared = new Shared();

        Runnable decimo = () -> {
            for (int i = 0; i < 1_250_000; i++) {
                int k = shared.reserve();
                shared.used(k);
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
            System.out.println(threads[i].isAlive());
        }

        shared.consume();

        System.out.println("Iniciando laco consume...");
        for (int i = 0; i < 10; i++) {
            Thread.sleep(500);
            shared.consume();
        }

        System.out.println(shared.status());
    }

}

