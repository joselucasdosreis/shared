package com.github.kyriosdata.shared;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Log implements LogService {

    public static int contadorFlush = 0;
    private static FileManager fm = new FileManager();
    private static Path path = Paths.get("/Users/kyriosdata/tmp/localhost.log");
    private final int BUFFER_SIZE = 64 * 1024;
    private final int EVENTS_SIZE = 1024;
    private final int INFO = 0;
    private final int WARN = 1;
    private final int ERROR = 2;

    private String[] levelNames = {"INFO", "WARN", "FAIL"};
    private byte[][] level = { { 73, 78, 70, 79 }, { 87, 65, 82, 78 }, {70, 65, 73, 76 } };

    // Cache Level 1
    private LogEvent[] eventos = new LogEvent[EVENTS_SIZE];

    // Cache Level 2
    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private Shared shared;

    public Log() {
        for (int i = 0; i < EVENTS_SIZE; i++) {
            eventos[i] = new LogEvent();
        }

        fmt = new InstanteFormatter();

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

                // Instante
                fmt.formatToBytes(eventos[v].instante, fmt.template);

                // Nível (INFO, WARN ou FAIL)
                System.arraycopy(level[eventos[v].level], 0, fmt.template, 25, 4);

                // System.out.println(new String(fmt.template, 0, 28, StandardCharsets.UTF_8));
                transferToBuffer(buffer, fmt.template, false);

                // Payload
                byte[] bytes = eventos[v].payload.getBytes(StandardCharsets.UTF_8);
                transferToBuffer(buffer, bytes, false);

                // Newline
                transferToBuffer(buffer, new byte[] { 10 }, ultimo);
            }
        };
    }

    /**
     * Transfere para buffer o conteúdo do vetor de bytes. Se durante a
     * cópia o buffer enche, então um "flush" é realizado. O "flush"
     * é realizado mesmo que o buffer não esteja cheio, mas pela
     * indicação do argumento.
     *
     * @param buffer Buffer para o qual bytes serão copiados.
     * @param bytes Vetor de bytes a ser copiado.
     *
     * @param flush Indica que flush do buffer deve ser realizado, mesmo
     *              que não esteja cheio.
     */
    public static void transferToBuffer(ByteBuffer buffer, byte[] bytes, boolean flush) {
        int resto = copyToBuffer(buffer, bytes, 0);
        while (resto != 0) {

            // Buffer cheio
            flush(buffer);

            resto = copyToBuffer(buffer, bytes, bytes.length - resto);
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
    public static void flush(ByteBuffer buffer) {
        contadorFlush++;
        buffer.flip();

        fm.acrescenta(path, buffer);

        buffer.clear();
    }

    /**
     * Copia bytes do vetor de bytes, a partir do deslocamento indicado para
     * o buffer.
     *
     * @param bb Buffer para o qual bytes serão copiados.
     * @param payload Buffer do qual bytes serão copiados.
     * @param offset Posição inicial no buffer a partir da qual os
     *               bytes serão copiados.
     *
     * @return 0 se todos os bytes foram copiados ou a quantidade
     * de bytes restantes no vetor de bytes que não foram copiados.
     * Um valor diferente de zero indica que a capacidade do buffer
     * foi atingida.
     */
    public static int copyToBuffer(ByteBuffer bb, byte[] payload, int offset) {

        int restante = bb.remaining();
        int pretendido = payload.length - offset;

        int copiado = pretendido > restante ? restante : pretendido;

        bb.put(payload, offset, copiado);

        return pretendido - copiado;
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

    private final InstanteFormatter fmt;

    /**
     * Contêiner para um registro de log.
     */
    private class LogEvent {
        public long instante;
        public byte level;
        public String payload;
    }
}

