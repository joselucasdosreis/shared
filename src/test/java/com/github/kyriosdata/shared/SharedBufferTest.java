package com.github.kyriosdata.shared;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public void log4h() {
        ILog log = new Log();

        // Gerenciador de tarefas
        // (para flush do conteúdo de log agendado)
        TaskManager tm = new TaskManager();
        tm.novoAgendamento(log);

        geraLogsEmVariasThreads(() -> {
            for (int i = 0; i < 3_600; i++) {
                log.error("I: " + i + " ThreadName: " + Thread.currentThread().getName());
            }
        });

        log.run();

        System.out.println(ByteBufferTest.contadorFlush);
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

/**
 * Definição dos serviços de <i>logging</i>.
 */
interface ILog extends Runnable {
    /**
     * Registra mensagem de log (informativa).
     *
     * @param msg Mensagem a ser registrada.
     */
    void info(String msg);

    /**
     * Registra mensagem de log (aviso). Um aviso
     * é entendido como uma situação excepcional,
     * embora não configure necessariamente algo
     * indesejável.
     *
     * @param msg Mensagem a ser registrada.
     */
    void warn(String msg);

    /**
     * Registra mensagem de log (erro). Representa
     * situação indesejável.
     *
     * @param msg Mensagem a ser registrada.
     */
    void error(String msg);
}

class Log implements ILog {

    private final int BUFFER_SIZE = 64 * 1024;
    private final int EVENTS_SIZE = 1024;
    private final int INFO = 0;
    private final int WARN = 1;
    private final int ERROR = 2;

    private String[] levelNames = {"INFO", "WARN", "ERROR"};

    // Cache Level 1
    private LogEvent[] eventos = new LogEvent[EVENTS_SIZE];

    // Cache Level 2
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private Shared shared;

    public Log() {
        for (int i = 0; i < EVENTS_SIZE; i++) {
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
             * no buffer já liberado.
             *
             * <p>Adicionalmente às observações acima, o "flush" em
             * disco é evitado enquanto há espaço no buffer e ocorre
             * quando o elemento consumido é o último (flag true).
             *
             * @param v Valor que identifica o log a ser consumido.
             * @param ultimo Caso verdadeiro, então persiste o conteúdo
             *               disponível no buffer.
             */
            @Override
            public void consome(int v, boolean ultimo) {
                byte[] bytes = packLogEvent(eventos[v]);

                // ByteBufferTest.transferToBuffer(buffer, bytes, ultimo);
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
        // Reserva logevent
        int v = shared.aloca();

        // Produz o evento
        eventos[v].instante = System.currentTimeMillis();
        eventos[v].level = (byte) level;
        eventos[v].payload = msg;

        // Disponibiliza o evento para consumo.
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
     * @return Sequência de bytes correspondente ao evento.
     */
    private byte[] packLogEvent(LogEvent event) {
        ZonedDateTime now = Instant.ofEpochMilli(event.instante).atZone(ZoneOffset.UTC);
        String level = levelNames[event.level];

        String log = String.format("%s %s %s\n", now, level, event.payload);

        return log.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Contêiner para um registro de log.
     */
    private class LogEvent {
        public long instante;
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

        acrescenta(path, buffer);
    }

    public void acrescenta(Path path, ByteBuffer buffer) {
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

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
