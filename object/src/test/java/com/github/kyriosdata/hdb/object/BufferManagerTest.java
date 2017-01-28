package com.github.kyriosdata.hdb.object;

import com.github.kyriosdata.healthdb.file.ArquivoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BufferManagerTest {

    private String dir = getClass().getResource(".").getFile();

    // Parâmetros para o serviço de buffer
    private final int BUFFER_SIZE = 10;
    private final int TOTAL_BUFFERS = 3;
    private final ArquivoService as = new ArquivoServiceParaTeste();

    BufferManager bm;

    @BeforeEach
    public void beforeEach() {
        bm = new BufferManager();
        bm.start(BUFFER_SIZE, TOTAL_BUFFERS, as);
    }

    @Test
    public void blocoRecuperadoUsadoLiberado() {
        String fn = dir + "inteirosDeUmAteCem.dat";

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
