/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.nio.ByteBuffer;

/**
 * Implementação dos serviços de acesso a arquivo
 * baseados na biblioteca Java para os sistemas de
 * arquivos das plataformas Windows, Linux e MacOS.
 */
public class ArquivoServicePadrao implements ArquivoService {

    private ArquivoManager am;

    @Override
    public void start(Object... params) {
        am = new ArquivoManagerJava();
        am.start();
    }

    /**
     * Força chamada do método {@link #unregister(int)} para os arquivos
     * gerenciados pelo serviço.
     *
     */
    @Override
    public void close() {
        try {
            am.close();
        } catch (Exception exp) {
            // Não faz nada.
        }
    }

    @Override
    public int register(String filename) {
        return am.register(filename);
    }

    @Override
    public void unregister(int handle) {
        am.unregister(handle);
    }

    @Override
    public boolean existe(int handle) {
        return am.existe(handle);
    }

    @Override
    public boolean cria(int handle) {
        return am.cria(handle);
    }

    @Override
    public boolean remove(int handle) {
        return am.remove(handle);
    }

    @Override
    public boolean abre(int handle) {
        return am.abre(handle);
    }

    @Override
    public boolean fecha(int handle) {
        return am.fecha(handle);
    }

    @Override
    public int acrescenta(int handle, ByteBuffer buffer) {
        Arquivo arquivo = am.get(handle);
        return arquivo == null ? -1 : arquivo.acrescenta(buffer);
    }

    @Override
    public int acrescenta(int handle, byte[] buffer, int i, int total) {
        Arquivo arquivo = am.get(handle);
        return arquivo == null ? -1 : arquivo.acrescenta(buffer, i, total);
    }

    @Override
    public int carrega(int handle, ByteBuffer buffer, int posicao) {
        Arquivo arquivo = am.get(handle);
        return arquivo == null ? -1 : arquivo.carrega(buffer, posicao);
    }

    @Override
    public int carrega(int handle, byte[] buffer, int posicao) {
        Arquivo arquivo = am.get(handle);
        return arquivo == null ? -1 : arquivo.carrega(buffer, posicao);
    }

    @Override
    public int escreve(int handle, ByteBuffer buffer, int posicao) {
        Arquivo arquivo = am.get(handle);
        return arquivo == null ? -1 : arquivo.escreve(buffer, posicao);
    }

    @Override
    public int escreve(int handle, byte[] buffer, int posicao) {
        Arquivo arquivo = am.get(handle);
        return arquivo == null ? -1 : arquivo.escreve(buffer, posicao);
    }
}
