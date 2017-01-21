/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 *
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Encapsula serviços básicos de acesso e manipulação de arquivo.
 * A instância mantém em 'cache' objetos que agilizam o acesso ao
 * conteúdo do arquivo após a chamada do método {@link #open()} e são
 * liberados apenas quando o método {@link #close()} for executado.
 *
 * <p>Os serviços incluem: (a) criação/remoção; (b) acréscimo (append) e
 * (c) leitura/escrita.
 */
public class FileWrapper {

    private static final Set<OpenOption> appendOptions;

    static {
        appendOptions = new HashSet<>();
        appendOptions.add(APPEND);
        appendOptions.add(CREATE);
    }

    private final Path path;
    private FileChannel channel;

    /**
     * Cria instância associada ao arquivo.
     *
     * @param filename Arquivo associado à instância.
     */
    public FileWrapper(String filename) {
        path = Paths.get(filename);
    }

    /**
     * Acrescenta ao arquivo os bytes do vetor.
     *
     * @param payload Conteúdo a ser inserido no arquivo.
     * @param i Posição inicial do vetor a partir da qual os bytes
     *          serão acrescentados ao arquivo.
     * @param size Quantidade de bytes do vetor, a partir da posição
     *             inicial, que será inserida no arquivo.
     *
     * @throws Exception
     */
    public void acrescenta(byte[] payload, int i, int size) {
        ByteBuffer buffer = ByteBuffer.wrap(payload, i, size);

        acrescenta(path, buffer);
    }

    /**
     * Acrescenta o conteúdo do buffer ao final do arquivo.
     *
     * @param buffer Buffer cujos bytes serão acrescentados ao
     *               final do arquivo.
     */
    public void acrescenta(ByteBuffer buffer) {
        acrescenta(path, buffer);
    }

    /**
     * Acrescenta, ao final do arquivo indicado, o conteúdo disponível no
     * buffer.
     *
     * @param path Arquivo no qual será feita a inserção.
     *
     * @param buffer Conteúdo a ser inserido.
     */
    public static void acrescenta(Path path, ByteBuffer buffer) {

        try (SeekableByteChannel seekableByteChannel = (Files.newByteChannel(path,
                appendOptions))) {

            //append some text at the end
            seekableByteChannel.position(seekableByteChannel.size());

            while (buffer.hasRemaining()) {
                seekableByteChannel.write(buffer);
            }

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void open() {
        try {
            channel = FileChannel.open(path, appendOptions);
        } catch (Exception exp) {

        }
    }

    public void close() {
        try {
            channel.close();
        } catch (Exception exp) {
        }
    }
}
