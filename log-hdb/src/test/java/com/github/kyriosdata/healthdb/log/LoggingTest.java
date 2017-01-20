package com.github.kyriosdata.healthdb.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingTest {

    private ScheduledThreadPoolExecutor agenda = new ScheduledThreadPoolExecutor(2);

    @Test
    public void singleLog() {
        Logging log = new Logging();

        agenda.scheduleWithFixedDelay(log, 1000, 1000, TimeUnit.MILLISECONDS);

        log.fail("ThreadName: " + Thread.currentThread().getName());

        log.run();
    }

    @Test
    public void log4h() {
        Logging log = new Logging();

        agenda.scheduleWithFixedDelay(log, 1000, 1000, TimeUnit.MILLISECONDS);

        String msg = "ThreadName: " + Thread.currentThread().getName();

        geraLogsEmVariasThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                log.fail(msg);
            }
        });

        log.run();
    }

    @Test
    public void log4j() {
        Logger logger = LogManager.getLogger(this.getClass());
        geraLogsEmVariasThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                logger.info("I: " + i + " ThreadName: " + Thread.currentThread().getName());
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

        System.out.println("Threads criadas.");

        // INICIA
        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i].start();
        }

        System.out.println("Threads iniciadas.");

        try {
            for (int i = 0; i < TOTAL_THREADS; i++) {
                threads[i].join();
            }
        } catch (Exception exp) {
            System.out.println(exp.toString());
        }

        System.out.println("Threads concluídas.");
    }

    @Test
    public void reutilizandoByteBuffer() {
        Logging log = new Logging();

        byte[] saude = "saúde".getBytes(StandardCharsets.UTF_8);
        byte[] vida = "vida".getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(10);

        log.transferToBuffer(buffer, saude, saude.length - 1, false);
        log.transferToBuffer(buffer, vida, vida.length - 1, true);

        assertEquals("saúdevida", new String(buffer.array()));
    }
}

