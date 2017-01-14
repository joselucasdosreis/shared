package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SharedBufferTest {

    @Test
    public void formatarInstante() {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);
        String now = nowUTC.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        System.out.println(now);
    }

    @Test
    public void verificaChamada() {
        SharedBuffer sb = new SharedBuffer();

        sb.produz(sb.aloca());
        sb.produz(sb.aloca());

        // força chamada do consumidor.
        sb.limpa();
    }

    @Test
    public void processo() throws Exception {
        TaskManager pm = new TaskManager();
        pm.novoAgendamento(new Runnable() {
            @Override
            public void run() {
                System.out.println(LocalDateTime.now());
            }
        });

        Thread.sleep(10000);
    }

}

class SharedBuffer extends Shared {
    @Override
    public void consome(int v) {

        System.out.println(LocalDateTime.now(Clock.systemUTC()));
    }
}

/**
 * Definição dos serviços de <i>logging</i>.
 */
interface ILog {
    void info(String msg);
    void warn(String msg);
    void error(String msg);
}

class Log implements ILog, Runnable {

    private final int SIZE = 32;

    private LogEvent[] eventos = new LogEvent[SIZE];

    private SharedBuffer shared = new SharedBuffer();

    public Log() {
        for(int i = 0; i < SIZE; i++) {
            eventos[i] = new LogEvent();
        }


    }

    @Override
    public void info(String msg) {
        log(0, msg);
    }

    @Override
    public void warn(String msg) {
        log(1, msg);
    }

    @Override
    public void error(String msg) {
        log(2, msg);
    }

    private void log(int level, String msg) {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);

        // Reserva logevent
        int v = shared.aloca();

        // Produz o evento
        eventos[v].instante = nowUTC;
        eventos[v].level = (byte)level;
        eventos[v].payload = msg;

        shared.produz(v);
    }

    @Override
    public void run() {
        shared.limpa();
    }
}

/**
 * Contêiner para um registro de log.
 */
class LogEvent {
    public ZonedDateTime instante;
    public byte level;
    public String payload;
}

/**
 * Gerencia a execução de tarefas. Entre os serviços está o
 * agendamento de execução em intervalos de tempo específico
 * e a manutenção de um <i>thread pool</i> por meio do qual
 * tarefas submetidas são executadas.
 */
class TaskManager {

    // Threads para tarefas agendadas.
    private ScheduledThreadPoolExecutor agenda;
    private List<ScheduledFuture> agendados;

    public TaskManager() {
        agenda = new ScheduledThreadPoolExecutor(2);
        agendados = new ArrayList<>();
    }

    public void novoAgendamento(Runnable r) {
        agenda.scheduleWithFixedDelay(r, 1000, 1000, TimeUnit.MILLISECONDS);
    }
}
