/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.io.Closeable;
import java.nio.ByteBuffer;

/**
 * Define serviços de gerência ({@link ArquivoManager}) e de
 * manipulação (leitura e escrita) de arquivos, aqui abstratamente
 * considerados como sequências de bytes divididas em blocos de
 * tamanho fixo.
 *
 * <p>A identificação de um arquivo é feita pelo nome (String)
 * fornecido ao método {@link #register(String)}, que retorna o
 * handle para o arquivo em questão. Esse handle é empregado em
 * todas as demais operações.
 *
 * <p>Quando o acesso ao arquivo não for mais necessário é
 * aconselhável fazer uso do método {@link #unregister(int)}.
 * Dessa forma, eventuais recursos empregados podem ser liberados.
 *
 * <p>As operações que consultam ou alteram o conteúdo de um
 * arquivo devem ser precedidas pela chamada ao método
 * {@link #abre(int)}, que recebe como argumento o handle do
 * arquivo a ser aberto. Adicionalmente, após a chamada do método
 * {@link #fecha(int)}, os serviços de leitura/escrita no arquivo
 * tornam-se indisponíveis.
 */
public interface ArquivoService extends Closeable {

    /**
     * Prepara o administrador (gerente) de arquivos.
     * Argumentos devem variar conforme a implementação.
     * Por exemplo, se Azure Blobs são empregados, então
     * entre os parâmetros é razoável a presença de credencial
     * de acesso.
     *
     * @param params Parâmetros que configuram o objeto.
     *               Esse método deve ser chamado antes que
     *               qualquer serviço oferecido seja executado.
     */
    void start(Object[] params);

    /**
     * Registra o nome de arquivo, cujo handle correspondente,
     * empregado pelas demais operações oferecidas, é retornado.
     *
     * @param filename Nome do arquivo (identificador) para o quel
     *                 o handle deve ser criado.
     *
     * @return O handle, valor inteiro único correspondente ao
     * arquivo.
     */
    int register(String filename);

    /**
     * Elimina o registro do arquivo cujo handle é fornecido.
     *
     * @param handle O handle do arquivo cujo registro deve
     *               ser removido.
     */
    void unregister(int handle);

    /**
     * Verifica se o arquivo existe.
     *
     * @param handle O nome do arquivo.
     *
     * @return {@code true} se o arquivo existe e
     * {@code false}, caso contrário.
     *
     * @see #remove(int)
     * @see #register(String)
     */
    boolean existe(int handle);

    /**
     * Cria o arquivo.
     *
     * @param handle O nome do arquivo a ser criado.
     *
     * @return {@code true} se o arquivo já existe ou foi
     * criado pela operação, caso contrário, retorna {@code false}.
     *
     * @see #register(String)
     */
    boolean cria(int handle);

    /**
     * Remove o arquivo, caso exista.
     *
     * @param handle O nome do arquivo.
     *
     * @return {@code true} se o arquivo foi
     * removido ou não existe. Retorna {@code false}
     * apenas se o arquivo existe, mesmo após a
     * tentativa de removê-lo.
     */
    boolean remove(int handle);

    /**
     * Abre o arquivo, tornando-o disponível para operações
     * de leitura e escrita.
     *
     * @param handle O handle do arquivo.
     *
     * @return {@code true} se o arquivo foi aberto ou
     * {@code false}, caso contrário.
     *
     * @see #fecha(int)
     */
    boolean abre(int handle);

    /**
     * Fecha o arquivo, liberando toda e qualquer memória
     * correspondente associada ao arquivo. Após a execução dessa
     * operação não é possível ler ou escrever no arquivo.
     *
     * @param handle O handle do arquivo.
     *
     * @return {@code true} se o arquivo foi fechado ou
     * {@code false}, caso contrário.
     *
     * @see #abre(int)
     */
    boolean fecha(int handle);

    /**
     * Acrescenta o conteúdo do buffer, desde o primeiro byte
     * até o último, ao final do arquivo.
     *
     * @param handle O handle do arquivo.
     * @param buffer Buffer cujo conteúdo será acrescido ao arquivo.
     *
     * @return A posição no arquivo a partir da qual o buffer será
     * inserido.
     *
     * @see #acrescenta(int, byte[], int, int)
     */
    int acrescenta(int handle, ByteBuffer buffer);

    /**
     * Acrescenta o total de bytes do buffer, a partir da posição
     * indicada, ao final do arquivo.
     *
     * @param handle O handle do arquivo.
     * @param buffer Buffer contendo a origem dos bytes a serem inseridos.
     *
     * @param i Posição inicial do buffer a partir do qual bytes serão copiados.
     *
     * @param total Total de bytes a serem inseridos no arquivo, a partir da
     *              posição indicada, ao final do arquivo.
     *
     * @return A posição no arquivo a partir da qual o buffer será
     * inserido.
     *
     * @see #acrescenta(int, ByteBuffer)
     */
    int acrescenta(int handle, byte[] buffer, int i, int total);

    /**
     * Obtém do arquivo, a partir de determinada posição, um total de bytes
     * a ser depositado no buffer.
     *
     * <p>Espera-se que o buffer possua espaço suficiente para
     * carregar o total de bytes a ser carregado.
     *
     *
     * @param handle O handle do arquivo.
     * @param buffer Buffer onde bytes lidos serão depositados.
     *
     * @param posicao Posição inicial do arquivo a partir da qual bytes
     *                serão lidos.
     *
     * @see #carrega(int, byte[], int)
     */
    void carrega(int handle, ByteBuffer buffer, int posicao);

    /**
     * Obtém do arquivo o total de bytes, a partir de determinada posição, e
     * os deposita em um buffer.
     *
     * <p>Espera-se que o buffer possua espaço suficiente para
     * carregar o total de bytes a ser carregado.
     *
     *
     * @param handle O handle do arquivo.
     * @param buffer Vetor de bytes no qual o conteúdo lido do arquivo será
     *               depositado.
     *
     * @param posicao Posição inicial no arquivo a partir da qual bytes serão
     *                lidos.
     *
     * @see #carrega(int, ByteBuffer, int)
     */
    void carrega(int handle, byte[] buffer, int posicao);

    /**
     * Deposita no arquivo, a partir da posição indicada, o total de
     * bytes, obtidos a partir da primeira posição do buffer.
     *
     *
     * @param handle O handle do arquivo.
     * @param buffer Buffer do qual bytes serão consultados para a
     *               inserção no arquivo.
     *
     * @param posicao Posição inicial no arquivo a partir da qual
     *                bytes serão escritos.
     *
     * @see #escreve(int, byte[], int)
     */
    void escreve(int handle, ByteBuffer buffer, int posicao);

    /**
     * Deposita no arquivo, a partir da posição indicada, o total de
     * bytes do buffer, obtidos a partir da primeira posição do buffer.
     *
     *
     * @param handle O handle do arquivo.
     * @param buffer Buffer do qual bytes serão consultados para a
     *               inserção no arquivo.
     *
     * @param posicao Posição inicial no arquivo a partir da qual
     *                bytes serão escritos.
     *
     * @see #escreve(int, ByteBuffer, int)
     */
    void escreve(int handle, byte[] buffer, int posicao);
}
