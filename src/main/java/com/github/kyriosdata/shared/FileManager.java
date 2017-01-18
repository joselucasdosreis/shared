package com.github.kyriosdata.shared;

import java.io.IOException;
import java.nio.ByteBuffer;
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
 * Serviços de manipulação de arquivo.
 */
public class FileManager {

    private static final Set<OpenOption> appendOptions;

    static {
        appendOptions = new HashSet<>();
        appendOptions.add(APPEND);
        appendOptions.add(CREATE);
    }

    private final Path path;

    /**
     * Cria instância que oferece acesso a serviços sobre arquivos.
     * @param filename
     */
    public FileManager(String filename) {
       path = Paths.get(filename);
    }

    public void acrescenta(byte[] payload, int i, int size) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(payload, i, size);

        acrescenta(path, buffer);
    }

    /**
     * Acrescenta o conteúdo do buffer ao arquivo.
     * @param buffer
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
}
