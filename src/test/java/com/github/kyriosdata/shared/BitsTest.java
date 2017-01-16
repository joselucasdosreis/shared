package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BitsTest {

    @Test
    public void setUnsetBits() {
        int v = 0;

        // Um inteiro qualquer
        int random = (int)(Math.random() * Integer.MAX_VALUE);

        for (int i = 0; i < 32; i++) {
            int bit = Bits.bitValue(random, i);

            int afterSet = Bits.set(random, i);
            assertEquals(1, Bits.bitValue(afterSet, i), "Valor: " + random + " i: " + i);

            int afterCls = Bits.cls(random, i);
            assertEquals(0, Bits.bitValue(afterCls, i), "Valor: " + random + " i: " + i);

            int calculado = bit == 0 ? afterCls : afterSet;
            assertEquals(random, calculado, "Valor: " + random + " i: " + i);
        }
    }
}
