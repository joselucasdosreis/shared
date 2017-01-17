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
 * Serviços de manipulação de arquivos.
 */
public class FileManager {

    private final Set<OpenOption> appendOptions;

    public FileManager() {
        appendOptions = new HashSet<>();
        appendOptions.add(APPEND);
        appendOptions.add(CREATE);
    }

    public void acrescenta(byte[] payload, int i, int size) throws Exception {
        Path path = Paths.get("/Users/Kyriosdata/tmp", "localhost.log");
        ByteBuffer buffer = ByteBuffer.wrap(payload, i, size);

        acrescenta(path, buffer);
    }

    public void acrescenta(Path path, ByteBuffer buffer) {

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
