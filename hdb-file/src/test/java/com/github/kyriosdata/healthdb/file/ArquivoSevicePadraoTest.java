/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ArquivoSevicePadraoTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void iniciarPararInumerasVezes() {
        for (int i = 0; i < 100; i++) {
            try (ArquivoServicePadrao asj = new ArquivoServicePadrao()) {
                asj.start();
            }
        }
    }

    @Test
    public void mesmoArquivoMesmoHandle() {
        ArquivoServicePadrao asp = new ArquivoServicePadrao();
        asp.start();

        int h1 = asp.register("a");
        int h2 = asp.register("a");

        assertEquals(h1, h2);

        asp.unregister(h1);
    }

    @Test
    public void naoTemEfeitoUnregisterParaInvalidHandle() {
        ArquivoServicePadrao asp = new ArquivoServicePadrao();
        asp.start();

        asp.unregister(Integer.MAX_VALUE);
    }

    @Test
    public void criaVerificaExistenciaRemove() {
        ArquivoServicePadrao asp = new ArquivoServicePadrao();
        asp.start();

        String fn = dir + UUID.randomUUID().toString();
        int handle = asp.register(fn);

        assertFalse(asp.existe(handle));
        assertTrue(asp.cria(handle));
        assertTrue(asp.existe(handle));
        assertTrue(asp.remove(handle));
        assertFalse(asp.existe(handle));
    }

    @Test
    public void abreArquivoDepoisFechaConfirmaFechado() {
        ArquivoServicePadrao asp = new ArquivoServicePadrao();
        asp.start();

        String fn = dir + UUID.randomUUID().toString();
        int handle = asp.register(fn);

        assertTrue(asp.cria(handle));
        assertTrue(asp.abre(handle));
        assertTrue(asp.fecha(handle));

        // Arquivo já está fechado (falha nova tentativa)
        assertFalse(asp.fecha(handle));
    }

    @Test
    public void criaEscreveBlocosCarregaVerifica() {
        ArquivoServicePadrao asp = new ArquivoServicePadrao();
        asp.start();

        String fn = dir + UUID.randomUUID().toString();
        int handle = asp.register(fn);

        assertTrue(asp.cria(handle));
        assertTrue(asp.abre(handle));

        // ------------------------------------------
        // Nesse ponto, arquivo está criado e aberto.
        // Ou seja, pronto para leitura/escrita.
        // ------------------------------------------

        // GERAR 4096 BYTES ARBITRÁRIOS
        // Assegura que são diferentes
        byte[] b1 = getBytes();
        b1[0] = 1;
        byte[] b2 = getBytes();
        b2[0] = 2;

        asp.acrescenta(handle, b1, 0, b1.length);
        asp.acrescenta(handle, b2, 0, b1.length);

        // Verifica os acréscimos dos blocos
        byte[] buffer = new byte[4096];
        asp.carrega(handle, buffer, 0);
        assertArrayEquals(b1, buffer);
        assertEquals(1, buffer[0]);

        asp.carrega(handle, buffer, 4096);
        assertArrayEquals(b2, buffer);
        assertEquals(2, buffer[0]);

        // "Troca" conteúdo dos blocos usando ByteBuffer
        ByteBuffer bb1 = ByteBuffer.wrap(b1);
        ByteBuffer bb2 = ByteBuffer.wrap(b2);

        asp.escreve(handle, bb2, 0);
        asp.escreve(handle, bb1, 4096);

        // Verifica com ByteBuffer
        ByteBuffer bb = ByteBuffer.allocate(4096);

        int tbb = asp.carrega(handle, bb, 0);
        assertEquals(4096, tbb);
        assertArrayEquals(b2, bb.array());

        bb.clear();
        tbb = asp.carrega(handle, bb, 4096);
        assertEquals(4096, tbb);
        assertArrayEquals(b1, bb.array());

        // Escreve apenas zeros
        byte[] zeros = new byte[4096];
        asp.escreve(handle, zeros, 0);
        asp.escreve(handle, zeros, 4096);

        byte[] tudo = new byte[4096 * 2];
        int total = asp.carrega(handle, tudo, 0);
        assertEquals(4096 * 2, total);

        // Acrescenta com ByteBuffer
        bb.flip();
        int posicao = asp.acrescenta(handle, bb);
        assertEquals(8192, posicao);

        byte[] outro = new byte[4096];
        total = asp.carrega(handle, outro, 8192);
        assertEquals(4096, total);
        assertArrayEquals(bb.array(), outro);
    }

    private byte[] getBytes() {
        byte[] b1 = new byte[4096];
        Random random = new Random();
        for(int i = 0; i < 4096; i++) {
            b1[i] = (byte)random.nextInt(250);
        }

        return b1;
    }
}
