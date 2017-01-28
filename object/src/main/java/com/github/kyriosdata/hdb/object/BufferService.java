/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.hdb.object;

import com.github.kyriosdata.healthdb.fabrica.Modulo;

/**
 * Uma base de dados pode estar organizada em um ou mais
 * arquivos. Um arquivo pode ser suficientemente extenso
 * para inviabilizar a transferência completa do seu conteúdo para
 * a memória RAM. A transferência é necessária para processar de forma
 * adequada (leia-se "rápida"), o conteúdo da base de dados.
 * Nesse caso, a solução é transferir um bloco, uma parte do conteúdo
 * por vez. Observe que esse é o cenário geral, dado que, se houver
 * espaço suficiente, o conjunto dos "pedaços" seria o arquivo completo.
 *
 * <p>Dado que não é assegurada a possibilidade de manter em RAM
 * o conteúdo completo de um arquivo, cada arquivo é dividido em
 * blocos de tamanho fixo ({@link Constantes#BUFFER_SIZE}). Serviços
 * de manipulação de buffers incluem a transferência de um bloco para
 * um buffer (o que exige gerenciar qual buffer será utilizado) e,
 * no sentido inverso, persistir o conteúdo de um buffer no arquivo
 * em questão.
 *
 * <p>O <i>Buffer Manager</i> (BM) é responsável por transferir para a
 * memória RAM um bloco requisitado e, no sentido inverso, persistir
 * um bloco presente em RAM. Um bloco é mantido em RAM em um <i>buffer</i>.
 * Em consequência, dado um buffer "ocupado" existe necessariamente um
 * bloco correspondente. Contudo, dado um bloco, não necessariamente
 * tem-se um buffer com o seu conteúdo. Convém ressaltar que, em geral,
 * o número de blocos é bem superior ao número de buffers.
 *
 * <p>Naturalmente, o número de buffers empregados pelo BM é limitado.
 * Cabe ao BM, quando necessário, identificar um buffer (e o bloco
 * correspondente) cujo conteúdo será substituído pelo conteúdo de
 * outro bloco, cujo acesso é requisitado ao BM. Uma estratégia comum
 * é substituir o bloco que foi usado há mais tempo. O algoritmo
 * correspondente é conhecido em inglês pela sigla
 * <a href="https://en.wikipedia.org/wiki/Cache_replacement_policies#LRU">
 * LRU</a> (uma estratégia de substituição de cache).
 *
 * <p>A requisição de um bloco (<i>buffer</i>) decorre das operações
 * de consultas e atualizações de dados requisitadas por clientes. As
 * operações básicas são: (a) lock e (b) unlock. A primeira carrega um
 * bloco, caso já não esteja disponível e a segunda "libera" o buffer
 * do bloco correspondente para ser eventualmente reutilizado em
 * posterior operação de lock. Observe que liberar não necessariamente
 * significa que o conteúdo do buffer é "limpado", o que inviabilizaria
 * a reutilização imediata dele em seguida. A substituição só ocorre
 * por meio da política adotada pelo algoritmo de substituição de cache.
 *
 * <p>O cliente do BM é resonsável por executar a operação unlock para
 * cada operação de lock realizada. Convém destacar que, enquanto a
 * operação de unlock não é realizada, o BM tenderá a manter o bloco
 * correspondente em uso.
 *
 */
public interface BufferService extends Modulo {

    /**
     * Registra o arquivo do qual os conteúdos de blocos serão
     * requisitados. O registro é obrigatório, pois a requisição
     * de blocos é feita por meio do handle do arquivo, retornado
     * pelo presente método.
     *
     * @param arquivo O nome (identificador) do arquivo.
     *
     * @return O handle do arquivo a ser empregado como argumento
     * nas demais operações.
     *
     * @see #lock(int, int)
     * @see #unlock(int, int)
     */
    int register(String arquivo);

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
     * Buffer buffer = bm.lock(fid, bloco);
     *
     * // Use o buffer!
     * // Período em que buffer não poderá ser reutilizado,
     * // ou seja, mantenha o mínimo possível o lock.
     *
     * bm.unlock(fid, bloco);
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
    Buffer lock(int fileId, int blocoId);

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
    void unlock(int fileId, int blocoId);
}
