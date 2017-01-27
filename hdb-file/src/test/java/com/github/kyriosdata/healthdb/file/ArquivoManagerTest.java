package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ArquivoManagerTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void mesmoArquivoMesmoHandle() throws Exception {
        ArquivoManager am = new ArquivoManagerJava();

        int h1 = am.register("1.txt");
        int h2 = am.register("2.txt");
        int h3 = am.register("1.txt");

        assertNotEquals(h1, h2);
        assertEquals(h1, h3);
    }

    @Test
    public void registerUnregisterSameFileManyTimes() throws Exception {
        ArquivoManager am = new ArquivoManagerJava();

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
