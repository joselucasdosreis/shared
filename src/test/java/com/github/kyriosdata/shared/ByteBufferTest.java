package com.github.kyriosdata.shared;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ByteBufferTest {

    @Test
    public void quandoTudoCabeNoBuffer() {

        byte[] bytes = "teste".getBytes();

        ByteBuffer bb = ByteBuffer.allocate(bytes.length);

        int resto = copyToBuffer(bb, bytes, 0);

        assertEquals(0, resto);
        assertEquals(bytes.length, bb.position());
        assertTrue(bb.remaining() >= 0);
    }

    @Test
    public void quandoNemTudoCabeNoBuffer() {

        byte[] bytes = "teste".getBytes();

        // Buffer com 1 byte a menos
        ByteBuffer bb = ByteBuffer.allocate(bytes.length - 1);

        int resto = copyToBuffer(bb, bytes, 0);

        assertEquals(1, resto);
        assertEquals(bytes.length - 1, bb.position());
        assertTrue(bb.remaining() == 0);
    }

    @Test
    public void seNadaCabeNadaCopiado() {

        byte[] bytes = "casa".getBytes();

        // Buffer com 1 byte a menos
        ByteBuffer bb = ByteBuffer.allocate(0);

        int resto = copyToBuffer(bb, bytes, 0);

        assertEquals(bytes.length, resto);
        assertEquals(0, bb.position());
        assertTrue(bb.remaining() == 0);
    }

    @Test
    public void reutilizandoByteBuffer() {
        byte[] saude = "saúde".getBytes(StandardCharsets.UTF_8);
        byte[] vida = "vida".getBytes(StandardCharsets.UTF_8);

        ByteBuffer buffer = ByteBuffer.allocate(10);

        transferToBuffer(buffer, saude, false);
        transferToBuffer(buffer, vida, true);
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
        buffer.flip();

//        byte[] recuperados = new byte[1024];
//        buffer.get(recuperados, 0 , buffer.limit());
//        assertEquals(0, buffer.remaining());
//        System.out.println(new String(recuperados, 0, buffer.position(), StandardCharsets.UTF_8));

        FileManager fm = new FileManager();
        Path path = Paths.get("/Users/kyriosdata/tmp/localhost.log");
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
}
