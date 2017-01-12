package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

public class SharedTest {

    @Test
    public void semChamadasConcorrentes() {
        Shared shared = new Shared(1024);
        for (int i = 0; i < 10_250_000; i++) {
            int k = shared.reserve();
            shared.used(k);
        }

        shared.consume();

        System.out.println(shared.status());
    }

    @Test
    public void comChamadasConcorrentes() {
        Shared shared = new Shared(1024);

        Runnable decimo = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10_250_000; i++) {
                    int k = shared.reserve();
                    shared.used(k);
                }
            }
        };

        for (int i = 0; i < 10; i++) {
            new Thread(decimo).start();
        }

        shared.consume();

        System.out.println(shared.status());
    }

}

