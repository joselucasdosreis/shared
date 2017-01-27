/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Implementação dos serviços de acesso a arquivo
 * baseados na biblioteca Java para os sistemas de
 * arquivos das plataformas Windows, Linux e MacOS.
 */
public class ArquivoServiceJava implements ArquivoService {
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
    public boolean existe(int handle) {
        return false;
    }

    @Override
    public boolean cria(int handle) {
        return false;
    }

    @Override
    public boolean remove(int handle) {
        return false;
    }

    @Override
    public boolean abre(int handle) {
        return false;
    }

    @Override
    public boolean fecha(int handle) {
        return false;
    }

    @Override
    public int acrescenta(int handle, ByteBuffer buffer) {
        return 0;
    }

    @Override
    public int acrescenta(int handle, byte[] buffer, int i, int total) {
        return 0;
    }

    @Override
    public void carrega(int handle, ByteBuffer buffer, int posicao) {

    }

    @Override
    public void carrega(int handle, byte[] buffer, int posicao) {

    }

    @Override
    public void escreve(int handle, ByteBuffer buffer, int posicao) {

    }

    @Override
    public void escreve(int handle, byte[] buffer, int posicao) {

    }

    /**
     * Força chamada do método {@link #unregister(int)} para os arquivos
     * gerenciados pelo serviço.
     * 
     * @throws IOException
     */
    @Override
    public void close() throws IOException {

    }
}
