package com.github.kyriosdata.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoggingTest {

    @Test
    public void singleLog() {
        Logging log = new Logging();

        TaskManager tm = new TaskManager();
        tm.repita(log, 1000, 1000);

        log.fail("ThreadName: " + Thread.currentThread().getName());

        log.run();
    }

    @Test
    public void log4h() {
        Logging log = new Logging();

        // Gerenciador de tarefas
        // (para flush do conteúdo de log agendado)
        TaskManager tm = new TaskManager();
        tm.repita(log, 1000, 1000);

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
        byte[] saude = "saúde".getBytes(StandardCharsets.UTF_8);
        byte[] vida = "vida".getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(10);

        Logging.transferToBuffer(buffer, saude, false);
        Logging.transferToBuffer(buffer, vida, true);

        assertEquals("saúdevida", new String(buffer.array()));
    }
}

