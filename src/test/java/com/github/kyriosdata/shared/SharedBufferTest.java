package com.github.kyriosdata.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class SharedBufferTest {

    @Test
    public void processo() throws Exception {
        TaskManager pm = new TaskManager();
        pm.novoAgendamento(() -> System.out.println(LocalDateTime.now()));

        Thread.sleep(1000);
    }

    @Test
    public void singleLog() {
        LogService log = new Log();

        TaskManager tm = new TaskManager();
        tm.novoAgendamento(log);

        log.fail("ThreadName: " + Thread.currentThread().getName());

        log.run();

        System.out.println(Log.contadorFlush);
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

        System.out.println(Log.contadorFlush);
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

class SharedJustForTeste extends Shared {
    private int[] contador = new int[1024];

    @Override
    public void consome(int i, boolean ultimo) {
        contador[i] = contador[i] + 1;
    }

    public int total() {
        int total = 0;
        for (int i = 0; i < 1024; i++) {
            total = total + contador[i];
        }

        return total;
    }
}
