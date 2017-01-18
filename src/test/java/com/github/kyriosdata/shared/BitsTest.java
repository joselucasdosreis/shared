package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void exibeBits() {
        String s0 = Bits.showBits2(0);
        assertEquals("0000 0000 0000 0000 0000 0000 0000 0000", s0);

        String s1 = Bits.showBits2(1);
        assertEquals("0000 0000 0000 0000 0000 0000 0000 0001", s1);

        String s31 = Bits.showBits2(1 << 31);
        assertEquals("1000 0000 0000 0000 0000 0000 0000 0000", s31);
    }

    @Test
    public void potenciaDeDois() {
        assertTrue(Bits.potenciaDois(1));
        assertTrue(Bits.potenciaDois(2));
        assertTrue(Bits.potenciaDois(4));
        assertTrue(Bits.potenciaDois(1024));

        assertFalse(Bits.potenciaDois(3));
        assertFalse(Bits.potenciaDois(1025));
    }
}
