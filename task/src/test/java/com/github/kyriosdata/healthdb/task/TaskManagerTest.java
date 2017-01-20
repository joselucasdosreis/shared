package com.github.kyriosdata.healthdb.task;

import org.junit.jupiter.api.Test;

public class TaskManagerTest {

    @Test
    public void processo() throws Exception {
        TaskManager pm = new TaskManager();
        pm.repita(() -> System.out.println(System.currentTimeMillis()), 1000, 1000);

        Thread.sleep(1000);
    }
}
