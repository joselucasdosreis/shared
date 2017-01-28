/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.hdb.object;

import com.github.kyriosdata.healthdb.file.ArquivoService;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Classe que simula um arquivo contendo 100 valores inteiros,
 * de 1 até 100. Nesse caso, um buffer de tamanho 10 causará
 * várias "faltas", pois o terceiro inteiro, por exemplo, inicia-se
 * no bloco 0 e termina no bloco 1.
 *
 * APENAS PARA LEITURA, APENAS METODOS CARREGA.
 */
public class ArquivoServiceParaTeste implements ArquivoService {

    private int unicoArquivoHandle;

    // Mantém conteúdo simulado do arquivo
    // (cem inteiros de valores 1 até 100)
    private byte[] dados = new byte[4 * 100];
    private ByteBuffer bb = ByteBuffer.wrap(dados);

    /**
     * Cria conteúdo do único arquivo.
     */
    public ArquivoServiceParaTeste() {
        for (int i = 1; i <= 100; i++) {
            int posicao = (i - 1) * 4;
            bb.putInt(posicao, i);
        }
    }

    @Override
    public void start(Object... params) {
        // Nada para fazer, objeto de propósito específico para teste.
    }

    @Override
    public int register(String filename) {
        unicoArquivoHandle = new Random().nextInt(100_000);
        return unicoArquivoHandle;
    }

    @Override
    public void unregister(int handle) {
        if (handle != unicoArquivoHandle) {
            throw new IllegalArgumentException("handle");
        }
    }

    @Override
    public boolean existe(int handle) {
        return handle == unicoArquivoHandle;
    }

    @Override
    public boolean cria(int handle) {
        return handle == unicoArquivoHandle;
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
    public int carrega(int handle, ByteBuffer buffer, int posicao) {
        return 0;
    }

    @Override
    public int carrega(int handle, byte[] buffer, int posicao) {
        int tamanho  = buffer.length;

        for(int i = posicao; i < 100; i++) {

        }

        return 0;
    }

    @Override
    public int escreve(int handle, ByteBuffer buffer, int posicao) {
        return 0;
    }

    @Override
    public int escreve(int handle, byte[] buffer, int posicao) {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }
}
