/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.log;

import com.github.kyriosdata.healthdb.api.Log;
import com.github.kyriosdata.healthdb.concurrency.RingBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Implementação de serviço de logging.
 */
public class Logging implements Log, Runnable {

    private FileManager fm = new FileManager("/Users/kyriosdata/tmp/localhost.log");
    private final int BUFFER_SIZE = 64 * 1024;
    private final int EVENTS_SIZE = 1024;

    private ScheduledThreadPoolExecutor agenda = new ScheduledThreadPoolExecutor(1);

    /**
     * Constante que indica nível INFO (informação).
     */
    private final int INFO = 0;

    /**
     * Constante que indica nível WARN (aviso).
     * Situação não necessariamente indesejável.
     */
    private final int WARN = 1;
    private final int FAIL = 2;

    /**
     * Bytes correspondentes à " INFO ", " WARN " e " FAIL ".
     * Observe que é empregado um espaço antes e após o nome
     * de cada nível.
     */
    private byte[][] level = {
            { 32, 73, 78, 70, 79, 32 },
            { 32, 87, 65, 82, 78, 32 },
            { 32, 70, 65, 73, 76, 32 } };

    // Cache Level 1
    private LogEvent[] eventos = new LogEvent[EVENTS_SIZE];

    // Cache Level 2
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    // Vetor de bytes correspondente ao caractere de "nova linha"
    private final byte[] NEWLINE = { 10 };

    private RingBuffer shared;

    public Logging() {
        for (int i = 0; i < EVENTS_SIZE; i++) {
            eventos[i] = new LogEvent();
        }

        fmt = new DateFormat();

        shared = new RingBuffer() {

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

                // Instante (24 bytes)
                byte[] timestamp = fmt.toBytes(eventos[v].instante);
                transferToBuffer(buffer, timestamp, timestamp.length - 1, false);

                // Nível (" INFO ", " WARN " ou " FAIL ") (6 bytes)
                transferToBuffer(buffer, level[eventos[v].level], level[eventos[v].level].length - 1, false);

                // Payload (tamanho variável)
                // TODO substituir getBytes por char[], wrap, CharsetEncoder.
                byte[] bytes = eventos[v].payload.getBytes(StandardCharsets.UTF_8);
                transferToBuffer(buffer, bytes, bytes.length - 1, false);

                // Newline
                transferToBuffer(buffer, NEWLINE, NEWLINE.length - 1, ultimo);
            }
        };
    }

    /**
     * Inicia o serviço de <i>logging</i>.
     *
     * @param filename O arquivo a ser empregado para registro das
     *                 informações.
     */
    @Override
    public void start(String filename) {
        agenda.scheduleWithFixedDelay(this, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Transfere para o buffer o conteúdo do vetor de bytes, desde o
     * primeiro byte do vetor até a posição final. Se durante a
     * cópia o buffer enche, então um "flush" é realizado. O "flush"
     * é realizado mesmo que o buffer não esteja cheio, mas pela
     * indicação do argumento.
     *
     * @param buffer Buffer para o qual bytes serão copiados.
     * @param bytes Vetor de bytes a ser copiado.
     * @param fim
     * @param flush Indica que flush do buffer deve ser realizado, mesmo
 *              que não esteja cheio.
     */
    public void transferToBuffer(ByteBuffer buffer, byte[] bytes, int fim, boolean flush) {
        int resto = Buffers.copyToBuffer(buffer, bytes, 0, fim);
        while (resto != 0) {

            // Buffer cheio
            flush(buffer);

            resto = Buffers.copyToBuffer(buffer, bytes, fim - resto + 1, fim);
        }

        if (flush) {
            flush(buffer);
        }
    }

    /**
     * Descarrega o conteúdo do buffer.
     *
     * @param buffer Buffer cujo conteúdo deve ser descarregado.
     */
    public void flush(ByteBuffer buffer) {
        buffer.flip();

        fm.acrescenta(buffer);

        buffer.clear();
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
    public void fail(String msg) {
        log(FAIL, msg);
    }

    /**
     * Produz evento de log carimbado com o instante de tempo
     * corrente (UTC).
     *
     * @param level Nível do log: INFO, WARN ou FAIL.
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

    private final DateFormat fmt;

    /**
     * Contêiner para um evento de log.
     */
    private class LogEvent {
        public long instante;
        public byte level;
        public String payload;
    }
}

