package com.github.kyriosdata.hdb.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BufferManagerTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void blocoRecuperadoUsadoLiberado() {
        String fn = dir + "blocorecuperado.dat";

        BufferManager bm = new BufferManager();
        bm.start(1);

        int handle = bm.register(fn);
        Buffer buffer = bm.lock(handle, 1234);
    }

    @Test
    public void blocoLockedRetornado() {
        BufferManager bm = new BufferManager();
        bm.start(1);

        Buffer b1 = bm.lock(0, 987);
        assertNotNull(b1);

        Buffer b2 = bm.lock(0, 987);
        assertNotNull(b2);

        assertEquals(b1, b2);

        bm.unlock(0, 987);
        bm.unlock(0, 987);
    }
}
