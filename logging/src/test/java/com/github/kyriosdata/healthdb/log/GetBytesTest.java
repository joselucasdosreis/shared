package com.github.kyriosdata.healthdb.log;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class GetBytesTest {

    FileManager getBytes = new FileManager("getbytes.log");
    FileManager fencoder = new FileManager("encoder.log");

    private final String log = "linha fixa\n";

    public GetBytesTest() throws IOException {
    }

    @Test
    public void padraoGetBytes() throws Exception {
        for (int i = 0; i < 100; i++) {
            byte[] bytes = log.getBytes(StandardCharsets.UTF_8);
            getBytes.acrescenta(bytes, 0, bytes.length);
        }
    }

    final char[] charsArray = new char[4 * 1024];
    final byte[] bytesArray = new byte[8 * 1024];

    CharBuffer cb = CharBuffer.wrap(charsArray, 0, charsArray.length);

    final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();

    final ByteBuffer bb = ByteBuffer.wrap(bytesArray);

    @Test
    public void charsetEncoder() throws Exception {
        for (int i = 0; i < 10; i++) {
            getBytes(log);

            fencoder.acrescenta(bb);
        }
    }

    private void getBytes(String str) {

        int length = str.length();

        // Deposita string em char[]
        str.getChars(0, length, charsArray, 0);

        // CharBuffer é reutilizado (operações de inicialização)
        cb.position(0);
        cb.limit(length);

        // Passo 3
        bb.clear();
        encoder.encode(cb, bb, true);

        bb.flip();
    }
}
