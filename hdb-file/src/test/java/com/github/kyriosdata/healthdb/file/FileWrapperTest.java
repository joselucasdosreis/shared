package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileWrapperTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void createFile() throws Exception {

        String filename = dir + "x.txt";
        Path path = Paths.get(filename);

        System.out.println("File: " + filename);
        System.out.println("Existe: " + exists(filename));

        FileChannel f = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);

        for (int i = 0; i < 5; i++) {
            long pos = f.size();
            f.position(pos);
            String msg = "i: " + i + "\n";
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            f.write(byteBuffer);
        }

        f.close();
    }

    public static boolean exists(String filename) {
        return Files.exists(Paths.get(filename));
    }
}
