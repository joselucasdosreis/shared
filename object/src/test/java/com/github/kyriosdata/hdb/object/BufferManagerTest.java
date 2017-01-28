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

    // Oferece acesso a um arquivo de 100 inteiros (1, 2, ..., 100).
    private final ArquivoService as = new ArquivoServiceCemInteiros();

    // TOTAL DE INTEIROS: 100 * 4 = 400 bytes
    // TOTAL DE BLOCOS: 40 (de 10 bytes cada)
    // BLOCOS: 0..39

    BufferManager bm;

    @BeforeEach
    public void beforeEach() {
        bm = new BufferManager();
        bm.start(BUFFER_SIZE, TOTAL_BUFFERS, as);
    }

    @Test
    public void inteiroPodeSerFragmentadoAoMeio() {
        String fn = dir + "inteirosDeUmAteCem.dat";

        int handle = bm.register(fn);

        // Bloco 0
        // Contém: 1, 2 completamente nesse bloco, além de
        // 2 bytes do terceiro inteiro que, juntamente com 2
        // bytes do segundo bloco, perfazem o valor 3.

        Buffer buffer = bm.lock(handle, 0);

        assertEquals(1, buffer.int32(0));
        assertEquals(2, buffer.int32(4));
        assertEquals(3, buffer.int32(8));
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
