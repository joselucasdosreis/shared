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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Uma base de dados pode estar organizada em um ou mais
 * arquivos. Um arquivos pode ser suficientemente extenso o
 * para inviabilizar a sua transferência do seu conteúdo para
 * a memória RAM. A transferência é necessária para processar
 * o conteúdo da base de dados. A solução é transferir um bloco,
 * uma parte do conteúdo.
 * <p>
 * <p>Dado que não é assegurada a possibilidade de manter em RAM
 * o conteúdo completo de um arquivo, cada arquivo é dividido em
 * locked de tamanho fixo ({@link Constantes#BUFFER_SIZE}). Ao
 * contrário do conteúdo do qual é obtido, um bloco pode ser
 * transferido para a memória e, no sentido inverso, persistido.
 * <p>
 * <p>O <i>Buffer Manager</i> (BM) é responsável por transferir para a
 * memória RAM um bloco requisitado e, no sentido inverso, persistir
 * um bloco presente em RAM. Um bloco é mantido em RAM em um <i>buffer</i>.
 * Naturalmente, o número de rawBuffers empregados pelo BM é limitado.
 * Cabe ao BM, quando necessário, identificar um buffer (e o bloco
 * correspondente) cujo conteúdo será substituído pelo conteúdo de
 * outro bloco, cujo acesso é requisitado ao BM. A estratégia do
 * bloco de uso mais antigo é empregada nesse caso ({@link LRU}).
 * <p>
 * <p>A requisição de um bloco (<i>buffer</i>) decorre das operações
 * de consultas e atualizações de dados requisitadas por clientes. As
 * operações básicas são: (a) lock e (b) unlock. A primeira carrega um
 * bloco, caso já não esteja disponível e a segunda "libera" o buffer
 * do bloco correspondente para ser eventualmente reutilizado em
 * posterior operação de lock.
 * <p>
 * <p>O cliente do BM é resonsável por executar a operação unlock para
 * cada operação de lock realizada. Convém destacar que, enquanto a
 * operação de unlock não é realizada, o BM tenderá a manter o bloco
 * correspondente em uso.
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
public class BufferManager {

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
     * Disponibiliza buffer para uso contendo um dado bloco.
     * Essa operação deve ser executada para cada bloco cujos
     * dados serão consultados.
     * <p>
     * <p>Esse método irá aguardar, caso não esteja disponível,
     * buffer para que o conteúdo do bloco seja carregado. Por outro
     * lado, um buffer pode estar disponível, o que assegura o
     * início imediato da carga dos dados do bloco. Ainda é
     * possível que o bloco já esteja em uso por algum buffer e,
     * nesse caso, o retorno é imediato.
     * <p>
     * <p>A chamada a esse método para um dado bloco exige a
     * posterior chamada ao método {@link #unlock(int)} para o
     * mesmo bloco. Caso contrário o bloco estará indisponível para
     * reutilização. De fato, esse cenário persiste enquanto o
     * método {@link #unlock(int)}} não é chamado.
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
     * @param blocoId O bloco que identifica o conteúdo do buffer.
     * @return Buffer disponível para uso.
     * @see #unlock(int)
     */
    public Buffer lock(int blocoId) {

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
     * {@link #lock(int)}}.
     * <p>
     * <p>Não necessariamente o buffer utilizado pela <i>thread</i>
     * poderá ser reutilizado, dado que outras <i>threads</i> também
     * podem estar consultando o mesmo bloco.
     *
     * @param blocoId Identificador do bloco a ser liberado.
     */
    public void unlock(int blocoId) {

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
}
