/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Serviços de manipulação de arquivo.
 */
public class FileManager {

    private final Path path;
    private final SeekableByteChannel channel;

    /**
     * Cria instância que oferece acesso a serviços sobre arquivos.
     * @param filename
     */
    public FileManager(String filename) throws IOException {
       path = Paths.get(filename);
       channel = Files.newByteChannel(path, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public void acrescenta(byte[] payload, int i, int size) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(payload, i, size);

        acrescenta(buffer);
    }

    public void acrescenta(ByteBuffer buffer) {

        try {

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void close() {
        try {
            channel.close();
        } catch (Exception exp) {}
    }
}
