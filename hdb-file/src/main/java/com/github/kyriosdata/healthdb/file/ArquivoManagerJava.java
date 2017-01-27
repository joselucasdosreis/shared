/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementação de {@link ArquivoService} baseada no sistema de
 * nomeToHandle encapsulado por Java. Ou seja, essa implementação deve
 * funcionar nos ambientes Windows, Linux e MacOS.
 */
public class ArquivoManagerJava implements ArquivoManager {

    /**
     * Capacidade inicial prevista para o gerente.
     * TODO criar lista circular e limitar a 128 (GC)
     */
    private final int INITIAL_CAPACITY = 128;

    /**
     * Gerador de handles únicos para arquivos.
     */
    private AtomicInteger handleGenerator;

    /**
     * Mantém relação entre nomes (identificadores) de nomeToHandle
     * e o handle único correspondente.
     */
    private Map<String, Integer> nomeToHandle;

    /**
     * Mantém a relação entre um handle e o arquivo
     * correspondente.
     */
    private Map<Integer, Arquivo> handleToArquivo;

    @Override
    public void start(Object... params) {
        nomeToHandle = new HashMap<>(INITIAL_CAPACITY);
        handleToArquivo = new HashMap<>(INITIAL_CAPACITY);
        handleGenerator = new AtomicInteger(-1);
    }

    @Override
    public void close() {

        // Fechar os arquivos gerenciados
        for (Arquivo arquivo : handleToArquivo.values()) {
            arquivo.fecha();
        }

        handleToArquivo.clear();
        handleToArquivo = null;

        nomeToHandle.clear();
        nomeToHandle = null;

        handleGenerator = null;
    }

    @Override
    public boolean abre(int handle) {
        Arquivo arquivo = handleToArquivo.get(handle);
        return arquivo != null && arquivo.abre();
    }

    @Override
    public boolean fecha(int handle) {
        Arquivo arquivo = handleToArquivo.get(handle);
        return arquivo != null && arquivo.fecha();
    }

    @Override
    public Arquivo get(int handle) {
        return handleToArquivo.get(handle);
    }

    @Override
    public boolean existe(int handle) {
        return Files.exists(Paths.get(filename(handle)));
    }

    @Override
    public String filename(int handle) {
        Arquivo arquivo = handleToArquivo.get(handle);
        return arquivo == null ? null : arquivo.filename();
    }

    @Override
    public boolean cria(int handle) {
        try {
            Files.createFile(Paths.get(filename(handle)));
            return true;
        } catch (Exception exp) {
            return false;
        }
    }

    @Override
    public boolean remove(int handle) {
        try {
            Files.deleteIfExists(Paths.get(filename(handle)));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int register(String filename) {
        Integer handle = nomeToHandle.get(filename);
        if (handle == null) {
            int newHandle = handleGenerator.decrementAndGet();
            nomeToHandle.put(filename, newHandle);

            Arquivo arquivo = new ArquivoJava();
            arquivo.filename(filename);
            handleToArquivo.put(newHandle, arquivo);

            handle = newHandle;
        }

        return handle;
    }

    @Override
    public void unregister(int handle) {
        Arquivo arquivo = handleToArquivo.get(handle);
        if (arquivo == null) {
            return;
        }

        handleToArquivo.remove(handle);
        nomeToHandle.remove(arquivo.filename());

        // Fecha o arquivo
        arquivo.fecha();
    }
}
