package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

public class SharedTest {

    @Test
    public void semChamadasConcorrentes() {
        Shared shared = new Shared(1024);
        for (int i = 0; i < 1025; i++) {
            int k = shared.reserve();
            shared.used(k);
        }

        shared.consume();

        System.out.println(shared.status());
    }

}

