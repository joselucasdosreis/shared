package com.github.kyriosdata.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

public class LogTest {

    @Test
    public void singleLog() {
        LogService log = new Log();

        TaskManager tm = new TaskManager();
        tm.novoAgendamento(log);

        log.fail("ThreadName: " + Thread.currentThread().getName());

        log.run();
    }

    @Test
    public void log4h() {
        LogService log = new Log();

        // Gerenciador de tarefas
        // (para flush do conteúdo de log agendado)
        TaskManager tm = new TaskManager();
        tm.novoAgendamento(log);

        geraLogsEmVariasThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                log.fail("I: " + i + " ThreadName: " + Thread.currentThread().getName());
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
}

