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
 * arquivos. Um arquivos pode ser suficientemente extenso o
 * para inviabilizar a sua transferência do seu conteúdo para
 * a memória RAM. A transferência é necessária para processar
 * o conteúdo da base de dados. A solução é transferir um bloco,
 * uma parte do conteúdo.
 *
 * <p>Dado que não é assegurada a possibilidade de manter em RAM
 * o conteúdo completo de um arquivo, cada arquivo é dividido em
 * blocos de tamanho fixo ({@link Constantes#BUFFER_SIZE}). Ao
 * contrário do conteúdo do qual é obtido, um bloco pode ser
 * transferido para a memória e, no sentido inverso, persistido.
 *
 * <p>O <i>Buffer Manager</i> (BM) é responsável por transferir para a
 * memória RAM um bloco requisitado e, no sentido inverso, persistir
 * um bloco presente em RAM. Um bloco é mantido em RAM em um <i>buffer</i>.
 * Naturalmente, o número de rawBuffers empregados pelo BM é limitado.
 * Cabe ao BM, quando necessário, identificar um buffer (e o bloco
 * correspondente) cujo conteúdo será substituído pelo conteúdo de
 * outro bloco, cujo acesso é requisitado ao BM. A estratégia do
 * bloco de uso mais antigo é empregada nesse caso ({@link LRU}).
 *
 * <p>A requisição de um bloco (<i>buffer</i>) decorre das operações
 * de consultas e atualizações de dados requisitadas por clientes. As
 * operações básicas são: (a) load e (b) unload. A primeira carrega um
 * bloco, caso já não esteja disponível e a segunda "libera" o buffer
 * do bloco correspondente para ser eventualmente reutilizado em
 * posterior operação de load.
 *
 */
public class BufferManager {

    private ByteBuffer[] rawBuffers;
    private byte[][] rawBytes;
    private Buffer[] buffers;

    /**
     * Inicia o BM com a quantidade de blocos (<i>rawBuffers</i>)
     * indicada.
     *
     * @param totalBuffers Total de blocos (<i>rawBuffers</i>) a serem
     *                     gerenciados pelo BM.
     */
    public void start(int totalBuffers) {

        // Aloca espaço propriamente dito que será empregado pelos rawBuffers.
        // TODO ByteBuffer.allocate, ByteBuffer.directAllocate
        // TODO Heap (objetos), Off heap (unsafe)
        rawBuffers = new ByteBuffer[totalBuffers];
        rawBytes = new byte[totalBuffers][];
        buffers = new Buffer[totalBuffers];

        for (int i = 0; i < totalBuffers; i++) {
            // Ou o contrário, ByteBuffer.allocate and ByteBuffer.array()?
            rawBytes[i] = new byte[Constantes.BUFFER_SIZE];
            rawBuffers[i] = ByteBuffer.wrap(rawBytes[i]);

            buffers[i] = new Buffer();
        }
    }
}
