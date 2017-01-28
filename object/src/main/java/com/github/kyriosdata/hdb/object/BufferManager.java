/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.hdb.object;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementação do serviço de gerência de buffers.
 *
 * <h3>Análise de cenários possíveis para as duas operações</h3>
 * <ul>
 * <li>Lock para bloco disponível no BM. Nesse caso, basta
 * acrescentar referência adicional para o bloco em questão e
 * simplesmente retorná-lo.
 * </li>
 * <li>Lock para bloco não disponível no BM. Há dois cenários
 * específicos:
 * <ul>
 * <li>Há buffer disponível. Reserve o uso do buffer (LRU).
 * Carregue o bloco para o mesmo e o retorne.
 * </li>
 * <li>Não há buffer disponível. Aqui deve se aguardar pela
 * liberação do buffer a ser utilizado, quando o cenário acima
 * se repete.
 * </li>
 * </ul>
 * </li>
 * <li>Unlock. Torna o buffer livre para reutilização.</li>
 * </ul>
 */
public class BufferManager implements BufferService {

    private ByteBuffer[] rawBuffers;
    private byte[][] rawBytes;
    private Buffer[] buffers;
    private LRU lru;

    /**
     * Conjunto de locked gerenciados pelo BM. Ou seja,
     * estão depositados em buffers e estão prontos para uso.
     * Inicialmente esse conjunto é vazio. Contudo, a capacidade
     * é limitada pelo total de buffers.
     *
     * <p>À medida em que o BM é utilizado esse conjunto é
     * acrescido de locked carregados. Quando um bloco é
     * substituído por outro, reutilização do buffer correspondente,
     * o bloco em questão é removido desse conjunto.
     */
    private Set<Integer> disponiveis;

    /**
     * Blocos gerenciados pelo BM (locked).
     * A capacidade do dicionário
     * não varia com o tempo e é
     * a mesma do total de buffers.
     */
    private Map<Integer, Bloco> locked;

    /**
     * Conjunto de buffers e os locked associados.
     */
    private Bloco[] bb;

    /**
     * Executa operações de inicialização do BM.
     *
     * @param totalBuffers Total de locked (<i>rawBuffers</i>) a serem
     *                     gerenciados pelo BM.
     */
    public void start(int totalBuffers) {

        locked = new HashMap<>(totalBuffers);

        // Inicialmente nenhum bloco está disponível.
        disponiveis = new HashSet<>(totalBuffers);

        lru = new LRU(totalBuffers);

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

            buffers[i] = new BufferByteBuffer();
        }
    }

    /**
     * Registra o arquivo do qual o conteúdo de blocos serão
     * requisitados.
     *
     * @param arquivo O nome (identificador) do arquivo.
     *
     * @return O handle do arquivo a ser empregado como argumento
     * nas demais operações.
     *
     * @see #lock(int, int)
     * @see #unlock(int, int)
     */
    @Override
    public int register(String arquivo) {
        return 0;
    }

    /**
     * Disponibiliza buffer contendo o bloco do arquivo.
     * Essa operação deve ser executada para cada bloco cujos
     * dados serão consultados.
     *
     * <p>Esse método irá aguardar, caso não esteja disponível,
     * buffer para que o conteúdo do bloco seja carregado. Por outro
     * lado, um buffer pode estar disponível, o que assegura o
     * início imediato da carga dos dados do bloco. Ainda é
     * possível que o bloco já esteja em uso por algum buffer e,
     * nesse caso, o retorno é imediato.
     * <p>
     * <p>A chamada a esse método para um dado bloco exige a
     * posterior chamada ao método {@link #unlock(int, int)} para o
     * mesmo bloco. Caso contrário o bloco estará indisponível para
     * reutilização. De fato, esse cenário persiste enquanto o
     * método {@link #unlock(int, int)}} não é chamado.
     * <p>
     * <p>Um uso eficiente, portanto, deve ser similar àquele
     * ilustrado abaixo.
     * <p>
     * <pre>
     * {@code
     * Buffer buffer = bm.lock(bloco);
     *
     * // Use o buffer!
     * // Período em que buffer não poderá ser reutilizado,
     * // ou seja, mantenha o mínimo possível o lock.
     *
     * bm.unlock(bloco);
     * }
     * </pre>
     *
     * @param fileId O identificador do arquivo.
     * @param blocoId O bloco que identifica o conteúdo do buffer.
     * @return Buffer disponível para uso.
     *
     * @see #unlock(int, int)
     * @see #register(String)
     */
    @Override
    public Buffer lock(int fileId, int blocoId) {

        // -------------------------------------------------------
        // CENARIO BLOCO LOCKED
        // Acrescente contador (referência adicional para o bloco)
        // Retorna o buffer correspondente.
        Bloco bloco = locked.get(blocoId);
        if (bloco != null) {
            bloco.contador++;
            return bloco.buffer;
        }

        // -------------------------------------------------------
        // CENARIO BLOCO UNLOCKED

        // REGIÃO CRÍTICA
        // 1. Recupere o buffer unlocked (LRU)
        // 2. Se não existe, aguarde.
        // 3. Remove da LRU o buffer recuperado.
        // END REGIÃO CRÍTICA

        bloco = lru.get();

        // 3. Carregue os dados do bloco no buffer (assíncrona).

        // 4. Define contador de referência com o valor 1.
        bloco.contador = 1;

        // 5. Acrescenta bloco entre os locked
        locked.put(blocoId, bloco);

        // 6. Aguarda carga dos dados no buffer.
        // 7. Retorna buffer liberado para uso.
        return bloco.buffer;
    }

    /**
     * Libera o bloco pela <i>thread</i> em questão.
     * Esse método deve ser chamado para cada chamada ao método
     * {@link #lock(int, int)}}.
     * <p>
     * <p>Não necessariamente o buffer utilizado pela <i>thread</i>
     * poderá ser reutilizado, dado que outras <i>threads</i> também
     * podem estar consultando o mesmo bloco.
     *
     * @param fileId O identificador do arquivo.
     * @param blocoId Identificador do bloco a ser liberado.
     *
     * @see #lock(int, int)
     * @see #register(String)
     */
    @Override
    public void unlock(int fileId, int blocoId) {

        Bloco bloco = locked.get(blocoId);
        if (bloco == null) {
            return;
        }

        // Decrementa contador de referência.
        bloco.contador--;

        // Se não houver mais referências ao bloco,
        // então sai de locked para unlocked.
        if (bloco.contador == 0) {
            lru.add(bloco);
            locked.remove(blocoId);
        }
    }

    @Override
    public void start(Object... params) {

    }

    @Override
    public void close() throws IOException {

    }
}
