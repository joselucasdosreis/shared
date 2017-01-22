/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 *
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ArquivoPadrao implements Arquivo, Closeable {

    private Path path;
    private SeekableByteChannel channel;

    public ArquivoPadrao(String filename) throws IOException {
        path = Paths.get(filename);
        channel = Files.newByteChannel(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
    }

    /**
     * Acrescenta, ao final do arquivo indicado, o conteúdo disponível no
     * buffer.
     *
     * @param buffer Conteúdo a ser inserido.
     */
    @Override
    public void acrescenta(ByteBuffer buffer) {

        try {

            // Posição corrente é o final do arquivo.
            channel.position(channel.size());

            buffer.flip();

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    @Override
    public void acrescenta(byte[] buffer, int i, int total) {

        acrescenta(ByteBuffer.wrap(buffer, i, total));
    }

    @Override
    public void carrega(ByteBuffer buffer, int posicao) {
        try {
            buffer.clear();
            channel.position(posicao);
            channel.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void carrega(byte[] buffer, int posicao) {
        carrega(ByteBuffer.wrap(buffer), posicao);
    }

    @Override
    public void escreve(ByteBuffer buffer, int posicao) {

    }

    @Override
    public void escreve(byte[] buffer, int posicao) {

    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
