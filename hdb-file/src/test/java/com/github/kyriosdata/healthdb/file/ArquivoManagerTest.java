package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ArquivoManagerTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void mesmoArquivoMesmoHandle() throws Exception {
        ArquivoManagerJava am = new ArquivoManagerJava();
        am.start();

        int h1 = am.register("1.txt");
        int h2 = am.register("1.txt");

        assertEquals(h1, h2);
    }

    @Test
    public void arquivosDistintosHandlesDistintos() throws Exception {
        ArquivoManagerJava am = new ArquivoManagerJava();
        am.start();

        int h1 = am.register("a");
        int h2 = am.register("b");

        assertNotEquals(h1, h2);
    }

    @Test
    public void registerUnregisterSameFileManyTimes() throws Exception {
        ArquivoManagerJava am = new ArquivoManagerJava();
        am.start();

        String inexistente = UUID.randomUUID().toString();
        for(int i = 0; i < 1000; i++) {
            int handle = am.register(inexistente);
            assertFalse(am.existe(handle));
            assertEquals(inexistente, am.filename(handle));

            am.unregister(handle);
            assertNull(am.filename(handle));
        }
    }

}
