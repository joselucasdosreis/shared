package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

public class SharedTest {

    @Test
    public void semChamadasConcorrentes() throws Exception {
        Shared shared = new Shared(1024);

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

        shared.consume();

        System.out.println(shared.status());
    }

    @Test
    public void comChamadasConcorrentes() throws Exception {
        Shared shared = new Shared(1024);

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

        System.out.println(shared.status());
    }

}

