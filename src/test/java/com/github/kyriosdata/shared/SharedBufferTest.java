package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
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
    public void processo() throws Exception {
        TaskManager pm = new TaskManager();
        pm.novoAgendamento(() -> System.out.println(LocalDateTime.now()));

        Thread.sleep(10000);
    }

    @Test
    public void logFuncionamento() {
        ILog log = new Log();

        // Gerenciador de tarefas
        // (para flush do conteúdo de log agendado)
        TaskManager tm = new TaskManager();
        tm.novoAgendamento(log);

        log.info("ok");
        log.warn("alerta");
        log.error("erro");

        try {
            Thread.sleep(2000);
        } catch (Exception exp) {}
    }
}

/**
 * Definição dos serviços de <i>logging</i>.
 */
interface ILog extends Runnable {
    void info(String msg);
    void warn(String msg);
    void error(String msg);
}

class Log implements ILog {

    private final int SIZE = 32;
    private final int INFO = 0;
    private final int WARN = 1;
    private final int ERROR = 2;

    private String[] levelNames = { "INFO", "WARN", "ERROR" };

    private LogEvent[] eventos = new LogEvent[SIZE];

    private Shared shared;

    public Log() {
        for (int i = 0; i < SIZE; i++) {
            eventos[i] = new LogEvent();
        }

        shared = new Shared() {
            @Override
            public void consome(int v) {
                byte[] bytes = packLogEvent(eventos[v]);
                System.out.println(new String(bytes, StandardCharsets.UTF_8));
            }
        };
    }

    @Override
    public void info(String msg) {
        log(INFO, msg);
    }

    @Override
    public void warn(String msg) {
        log(WARN, msg);
    }

    @Override
    public void error(String msg) {
        log(ERROR, msg);
    }

    private void log(int level, String msg) {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);

        // Reserva logevent
        int v = shared.aloca();

        // Produz o evento
        eventos[v].instante = nowUTC;
        eventos[v].level = (byte) level;
        eventos[v].payload = msg;

        shared.produz(v);
    }

    @Override
    public void run() {
        shared.flush();
    }

    /**
     * Empacota um evento de log em uma sequência de bytes.
     *
     * @param event Evento a ser convertido em sequência de bytes.
     *
     * @return Sequência de bytes correspondente ao evento.
     */
    private byte[] packLogEvent(LogEvent event) {
        String now = event.instante.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String level = levelNames[event.level];

        String log = String.format("%s %s %s", now, level, event.payload);

        return log.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Contêiner para um registro de log.
     */
    private class LogEvent {
        public ZonedDateTime instante;
        public byte level;
        public String payload;
    }
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

class FileManager {

}
