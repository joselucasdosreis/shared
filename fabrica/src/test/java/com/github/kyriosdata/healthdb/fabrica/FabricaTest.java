package com.github.kyriosdata.healthdb.fabrica;

import com.github.kyriosdata.healthdb.api.Log;
import org.junit.jupiter.api.Test;

public class FabricaTest {

    @Test
    public void casoTrivial() {
        Log log = Fabrica.newInstance(Log.class);
        log.info("testando apenas...");
    }
}
