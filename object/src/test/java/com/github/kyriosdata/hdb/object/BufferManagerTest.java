package com.github.kyriosdata.hdb.object;

import org.junit.jupiter.api.Test;

public class BufferManagerTest {

    @Test
    public void simplesCriacao() {
        BufferManager bm = new BufferManager();
        bm.start(1000);
    }
}
