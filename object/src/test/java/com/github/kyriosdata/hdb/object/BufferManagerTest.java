package com.github.kyriosdata.hdb.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BufferManagerTest {

    @Test
    public void blocoRecuperadoUsadoLiberado() {
        BufferManager bm = new BufferManager();
        bm.start(1);

        Buffer buffer = bm.lock(1234);
    }

    @Test
    public void blocoLockedRetornado() {
        BufferManager bm = new BufferManager();
        bm.start(1);

        Buffer b1 = bm.lock(987);
        assertNotNull(b1);

        Buffer b2 = bm.lock(987);
        assertNotNull(b2);

        assertEquals(b1, b2);

        bm.unlock(987);
        bm.unlock(987);
    }
}
