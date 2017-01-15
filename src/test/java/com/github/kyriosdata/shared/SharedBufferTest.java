package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

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

        gerarLogsParaTeste(log);
    }

    private void gerarLogsParaTeste(ILog log) {
        Runnable decimo = () -> {
            for (int i = 0; i < 100; i++) {
                String msg = "I: " + i + " ThreadName: " + Thread.currentThread().getName();
                System.out.println(msg);
                log.error(msg);
            }
        };

        // CRIA
        int TOTAL_THREADS = 2;

        Thread[] threads = new Thread[TOTAL_THREADS];

        for (int i = 0; i < TOTAL_THREADS; i++) {
            threads[i] = new Thread(decimo);
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

    private String[] levelNames = {"INFO", "WARN", "ERROR"};

    private LogEvent[] eventos = new LogEvent[SIZE];

    private Shared shared;

    public Log() {
        for (int i = 0; i < SIZE; i++) {
            eventos[i] = new LogEvent();
        }

        shared = new Shared() {

            /**
             * Consome o evento de log associado ao valor.
             * Aparentemente é o "melhor lugar" para empacotar
             * o evento em questão e depositá-lo no buffer.
             *
             * <p>Caso o buffer não comporte o registro completo
             * do evento, então o espaço disponível é preenchido e
             * o buffer persistido no arquivo em questão. Na
             * sequência, a parte restante do evento é depositada
             * no buffer.
             *
             * @param v Valor que identifica o log a ser consumido.
             * @param ultimo Caso verdadeiro, então persiste o conteúdo
             *               disponível no buffer.
             */
            @Override
            public void consome(int v, boolean ultimo) {
                FileManager fm = new FileManager();
                byte[] bytes = packLogEvent(eventos[v]);
                try {
                    fm.acrescenta(bytes, 0, bytes.length);
                } catch (Exception exp) {
                    System.out.println(exp.toString());
                }
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

    /**
     * Produz evento de log carimbado com o instante de tempo
     * corrente (UTC).
     *
     * @param level Nível do log: INFO, WARN ou ERROR.
     * @param msg   Mensagem associada ao evento.
     */
    private void log(int level, String msg) {
        ZonedDateTime nowUTC = ZonedDateTime.now(ZoneOffset.UTC);

        // Reserva logevent
        int v = shared.aloca();

        // Produz o evento
        eventos[v].instante = nowUTC;
        eventos[v].level = (byte) level;
        eventos[v].payload = msg;

        // Disponibiliza o evento para consumo.
        shared.produz(v);

        assert shared.produzido(v);
    }

    @Override
    public void run() {
        shared.flush();
    }

    /**
     * Empacota um evento de log em uma sequência de bytes.
     *
     * @param event Evento a ser convertido em sequência de bytes.
     * @return Sequência de bytes correspondente ao evento.
     */
    private byte[] packLogEvent(LogEvent event) {
        String now = event.instante.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
        String level = levelNames[event.level];

        String log = String.format("%s %s %s\n", now, level, event.payload);

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

    // Threads de uso geral
    private ExecutorService taskExecutor;

    public TaskManager() {
        agenda = new ScheduledThreadPoolExecutor(2);
        agendados = new ArrayList<>();

        taskExecutor = Executors.newFixedThreadPool(2);
    }

    public void novoAgendamento(Runnable r) {
        agenda.scheduleWithFixedDelay(r, 1000, 1000, TimeUnit.MILLISECONDS);
    }
}

class FileManager {

    public void acrescenta(byte[] payload, int i, int size) throws Exception {
        Path path = Paths.get("/Users/Kyriosdata/tmp", "localhost.log");
        ByteBuffer buffer = ByteBuffer.wrap(payload, i, size);

        Set<OpenOption> options = new HashSet<>();
        options.add(APPEND);
        options.add(CREATE);

        try (SeekableByteChannel seekableByteChannel = (Files.newByteChannel(path,
                options))) {

            //append some text at the end
            seekableByteChannel.position(seekableByteChannel.size());

            while (buffer.hasRemaining()) {
                seekableByteChannel.write(buffer);
            }

            buffer.clear();

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
