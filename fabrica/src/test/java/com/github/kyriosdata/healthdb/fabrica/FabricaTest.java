package com.github.kyriosdata.healthdb.fabrica;

import com.github.kyriosdata.healthdb.api.Log;
import org.junit.jupiter.api.Test;

public class FabricaTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void casoTrivial() throws Exception {
        Log log = Fabrica.newInstance(Log.class);
        log.start(dir + "teste.log");

        log.info("testando apenas...");

        log.close();
    }
}
