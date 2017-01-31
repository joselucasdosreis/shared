package com.github.kyriosdata.healthdb.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingTest {

    private static final boolean WINDOWS = System.getProperty("os.name").contains("indow");
    private String classDir = getClass().getResource(".").getFile();
    private String dir = WINDOWS ? classDir.substring(1) : classDir;
    private String file = dir + "teste.log";

    @Test
    public void cicloDeVida() throws Exception {
        Logging log = new Logging();
        log.start(file);

        log.info("uma mensagem qualquer 4");
        log.close();
    }

    @Test
    public void singleLog() throws Exception {
        Logging log = new Logging();
        log.start(file);

        log.info("info");
        log.warn("warn");
        log.fail("fail");

        log.close();
    }

    @Test
    public void log4h() throws Exception {
        Logging log = new Logging();
        log.start(file);

        String msg = "ThreadName: " + Thread.currentThread().getName();

        geraLogsEmVariasThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                log.fail(msg);
            }
        });

        log.close();
    }

    @Test
    public void log4j() {
        System.setProperty("async.log", dir + "async.log");
        Logger logger = LogManager.getLogger(this.getClass());

        String msg = "ThreadName: " + Thread.currentThread().getName();

        geraLogsEmVariasThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                logger.info(msg);
            }
        });
    }

    private void geraLogsEmVariasThreads(Runnable tarefa) {

        // CRIA
        int TOTAL_THREADS = 20;

        Thread[] threads = new Thread[TOTAL_THREADS];

        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i] = new Thread(tarefa);
        }

        // INICIA
        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i].start();
        }

        try {
            for (int i = 0; i < TOTAL_THREADS; i++) {
                threads[i].join();
            }
        } catch (Exception exp) {
            System.out.println(exp.toString());
        }
    }

    @Test
    public void reutilizandoByteBuffer() {
        Logging log = new Logging();

        byte[] saude = "saúde".getBytes(StandardCharsets.UTF_8);
        byte[] vida = "vida".getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(10);

        log.transferToBuffer(buffer, saude, saude.length - 1);
        log.transferToBuffer(buffer, vida, vida.length - 1);

        assertEquals("saúdevida", new String(buffer.array()));
    }
}

