package com.github.kyriosdata.shared;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InstanteFormatterTest {

    @Test
    public void formataData() {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(dt1.format(d));
    }

    @Test
    public void equivalencia() {
        long instante = System.currentTimeMillis();

        InstanteFormatter fmt = new InstanteFormatter();

        byte[] fullBytes = fmt.template;
        fmt.formatToBytes(instante, fullBytes);
        String strBytes = new String(fullBytes, 0, 24);
        System.out.println(strBytes);

        Instant instant = Instant.ofEpochMilli(instante);
        OffsetDateTime ofdt = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
        System.out.println(ofdt);

        assertEquals(ofdt.toString(), strBytes);
    }

    @Test
    public void verificaDesempenhoBytes() {
        long ms = System.currentTimeMillis();

        InstanteFormatter fmt = new InstanteFormatter();
        fmt.updateMidnightMillis(ms);

        ms = fmt.millisSinceMidnight(ms);

        byte[] bytes = new byte[24];
        StopWatch soma = new StopWatch();
        soma.start();

        for(int i = 0; i < 1_000; i++) {
            fmt.writeTimeToBytes((int) ms, bytes);
        }

        long nano = soma.getNanoTime();

        System.out.println(nano);
    }

    @Test
    public void verificaDesempenhoSoma() {
        long ms = System.currentTimeMillis();

        InstanteFormatter fmt = new InstanteFormatter();
        fmt.updateMidnightMillis(ms);

        ms = fmt.millisSinceMidnight(ms);

        byte[] bytesSoma = new byte[24];
        StopWatch soma = new StopWatch();
        soma.start();

        for(int i = 0; i < 1_000; i++) {
            fmt.writeTimeToBytesSoma((int) ms, bytesSoma);
        }

        long nano = soma.getNanoTime();

        System.out.println(nano);
    }
}
