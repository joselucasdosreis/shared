package com.github.kyriosdata.healthdb.log;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateFormatTest {

    @Test
    public void equivalencia() {
        long instante = System.currentTimeMillis();

        DateFormat fmt = new DateFormat();

        byte[] fullBytes = fmt.toBytes(instante);
        String strBytes = new String(fullBytes, 0, 24);
        System.out.println(strBytes);

        Instant instant = Instant.ofEpochMilli(instante);
        OffsetDateTime ofdt = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
        System.out.println(ofdt);

        assertEquals(ofdt.toString(), strBytes);
    }
}
