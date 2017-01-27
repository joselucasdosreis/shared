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

/**
 * Implementação de {@link Arquivo} usando biblioteca Java NIO.
 */
public class ArquivoJava implements Arquivo, Closeable {

    private Path path;
    private SeekableByteChannel channel;

    @Override
    public boolean abre(String nome) {
        path = Paths.get(nome);
        try {
            channel = Files.newByteChannel(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
        } catch (IOException exp) {
            return false;
        }

        return true;
    }

    @Override
    public boolean fecha() {
        try {
            channel.close();
        } catch (Exception exp) {
            return false;
        } finally {
            path = null;
            channel = null;
        }

        return true;
    }

    /**
     * Acrescenta, ao final do arquivo indicado, o conteúdo disponível no
     * buffer. Observe que não necessariamente é o conteúdo completo do
     * buffer, mas aquele disponível a partir da posição corrente desse
     * buffer. Ou seja, {@link ByteBuffer#remaining()} bytes.
     *
     * @param buffer Conteúdo a ser inserido.
     *
     * @return A posição no arquivo a partir da qual o conteúdo do
     * buffer foi inserido, ou o valor -1, em caso de falha.
     *
     * */
    @Override
    public int acrescenta(ByteBuffer buffer) {

        try {
            int posicao = (int) channel.position();
            escreve(buffer, posicao);
            return posicao;
        } catch (Exception exp) {
            return -1;
        }
    }

    @Override
    public int acrescenta(byte[] buffer, int i, int total) {
        return acrescenta(ByteBuffer.wrap(buffer, i, total));
    }

    @Override
    public int carrega(ByteBuffer buffer, int posicao) {
        try {
            buffer.clear();
            channel.position(posicao);
            return channel.read(buffer);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public int carrega(byte[] buffer, int posicao) {
        return carrega(ByteBuffer.wrap(buffer), posicao);
    }

    @Override
    public int escreve(ByteBuffer buffer, int posicao) {
        try {
            // Posição inicial de escrita
            channel.position(posicao);

            while (buffer.hasRemaining()) {
                channel.write(buffer);
            }

            return (int) (channel.position() - posicao);
        } catch (Exception ex) {
            return -1;
        }
    }

    @Override
    public int escreve(byte[] buffer, int posicao) {
        return escreve(ByteBuffer.wrap(buffer), posicao);
    }

    @Override
    public String filename() {
        return path.toString();
    }

    @Override
    public void close() {
        fecha();
    }
}
