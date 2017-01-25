/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.hdb.object;

import java.nio.ByteBuffer;

/**
 * Uma base de dados pode estar organizada em um ou mais
 * arquivos, geralmente extensos o suficiente para ser inviável
 * a transferência deles para a memória RAM. A transferência é
 * necessária para processar o conteúdo da base de dados.
 *
 * <p>Dado que não é assegurada a possibilidade de manter em RAM
 * o conteúdo completo de um arquivo, cada arquivo é dividido em
 * blocos de tamanho fixo ({@link Constantes#BUFFER_SIZE}). Um
 * bloco, por sua vez, pode ser transferido para a memória e,
 * no sentido inverso, persistido se alguma alteração foi realizada.
 *
 * <p>O <i>Buffer Manager</i> é responsável por transferir para a
 * memória RAM um bloco requisitado e, no sentido inverso, persistir
 * um bloco que passou por alteração. Tudo isso com a restrição de
 * um limite de blocos que podem ser mantidos em memória em um
 * dado instante de tempo. Ou seja, é necessário gerenciar o conjunto
 * de blocos em RAM (<i>buffers</i>) e, quando necessário, "descartar"
 * um bloco cuja memória (<i>buffer</i>) será liberada para uso por
 * outro bloco.
 *
 * <p>A requisição de um bloco (<i>buffer</i>) decorre das operações
 * de consultas e atualizações de dados requisitadas por clientes.
 *
 * <p>O <i>Buffer Manager</i>, por simplicidade apenas BM, é
 * configurado com a quantidade máxima de <i>buffers</i> a serem
 * gerenciados e o <i>File Manager</i>, responsável por gerir os
 * arquivos empregados, além de oferecer uma abstração sobre o real
 * mecanismo de armazenamento empregado, em geral, o sistema de
 * arquivos do sistema operacional em questão.
 *
 * <p>
 */
public class BufferManager {

    private ByteBuffer[] buffers;
    private byte[][] bytes;

    /**
     * Inicia o BM com a quantidade de blocos (<i>buffers</i>)
     * indicada.
     *
     * @param totalBuffers Total de blocos (<i>buffers</i>) a serem
     *                     gerenciados pelo BM.
     */
    public void start(int totalBuffers) {

        buffers = new ByteBuffer[totalBuffers];
        bytes = new byte[totalBuffers][];

        for (int i = 0; i < totalBuffers; i++) {
            bytes[i] = new byte[Constantes.BUFFER_SIZE];
            buffers[i] = ByteBuffer.wrap(bytes[i]);
        }
    }

    // Versao 0 - assume um único arquivo predefinido

    public int int32(int bloco, int offset) {
        return 0;
    }
}
