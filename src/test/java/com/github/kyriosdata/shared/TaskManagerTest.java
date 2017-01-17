package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

public class TaskManagerTest {

    @Test
    public void processo() throws Exception {
        TaskManager pm = new TaskManager();
        pm.novoAgendamento(() -> System.out.println(System.currentTimeMillis()));

        Thread.sleep(1000);
    }
}
