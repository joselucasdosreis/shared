/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileSystemJava implements ArquivoManager, Closeable {

    @Override
    public Arquivo abre(String filename) {
        return null;
    }

    @Override
    public boolean existe(String filename) {
        return Files.exists(Paths.get(filename));
    }

    @Override
    public boolean cria(String filename) {
        try {
            Files.createFile(Paths.get(filename));
            return true;
        } catch (IOException exp) {
            return false;
        }
    }

    @Override
    public boolean remove(String filename) {
        try {
            Files.deleteIfExists(Paths.get(filename));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void start(Object[] params) {

    }

    @Override
    public int register(String filename) {
        return 0;
    }

    @Override
    public void unregister(int handle) {

    }

    @Override
    public void close() throws IOException {

    }
}
