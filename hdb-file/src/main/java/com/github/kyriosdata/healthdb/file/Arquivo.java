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
 * Abstração para uma sequência de bytes geralmente
 * armazenada em meio secundário, juntamente com operações
 * pertinentes.
 *
 * <p>Uma instância dessa classe deve ser projetada de tal
 * forma a favorecer a reutilização da mesma para uma sequência
 * distinta daquela inicialmente utilizada (referenciada).
 * Dessa forma, abre-se a possibilidade de redução da pressão sobre
 * o GC.
 *
 * <p>As operações são baseadas na perspectiva do Cliente
 * (código que faz uso dessa interface), sem referência explícita
 * ou dependência para o real meio empregado para armazenar ou
 * consultar tais sequências de bytes.
 */
public interface Arquivo {

    /**
     * Abre o arquivo, tanto para leitura quanto para escrita.
     *
     * @param nome O nome do arquivo.
     *
     * @return {@code true} se o arquivo foi aberto corretamente, ou
     * {@code false}, caso contrário.
     */
    boolean abre(String nome);

    /**
     * Fecha o arquivo, cujas operações de leitura/escrita correspondentes
     * tornam-se indisponíveis.
     *
     * @return {@code true} se o arquivo foi fechado e {@code false},
     * caso contrário.
     */
    boolean fecha();

    /**
     * Acrescenta o conteúdo do buffer, desde o primeiro byte
     * até o último, ao final do arquivo.
     *
     * @param buffer Buffer cujo conteúdo será acrescido ao arquivo.
     *
     * @return A posição no arquivo a partir da qual o buffer será
     * inserido.
     *
     * @see #acrescenta(byte[], int, int)
     */
    int acrescenta(ByteBuffer buffer);

    /**
     * Acrescenta o total de bytes do buffer, a partir da posição
     * indicada, ao final do arquivo.
     *
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
     * @see #acrescenta(ByteBuffer)
     */
    int acrescenta(byte[] buffer, int i, int total);

    /**
     * Obtém do arquivo, a partir de determinada posição, um total de bytes
     * a ser depositado no buffer.
     *
     * <p>Espera-se que o buffer possua espaço suficiente para
     * carregar o total de bytes a ser carregado.
     *
     * @param buffer Buffer onde bytes lidos serão depositados.
     *
     * @param posicao Posição inicial do arquivo a partir da qual bytes
     *                serão lidos.
     *
     * @return Quantidade de bytes carregados.
     *
     * @see #carrega(byte[], int)
     */
    int carrega(ByteBuffer buffer, int posicao);

    /**
     * Obtém do arquivo o total de bytes do buffer, a partir de determinada
     * posição, e os deposita no buffer.
     *
     * @param buffer Vetor de bytes no qual o conteúdo lido do arquivo será
     *               depositado.
     *
     * @param posicao Posição inicial no arquivo a partir da qual bytes serão
     *                lidos.
     *
     * @return Quantidade de bytes carregados. Pode ser valor inferior ao
     * tamanho do buffer (em caso de falha).
     *
     * @see #carrega(ByteBuffer, int)
     */
    int carrega(byte[] buffer, int posicao);

    /**
     * Deposita no arquivo, a partir da posição indicada, o total de
     * bytes, obtidos a partir da primeira posição do buffer.
     *
     * @param buffer Buffer do qual bytes serão consultados para a
     *               inserção no arquivo.
     *
     * @param posicao Posição inicial no arquivo a partir da qual
     *                bytes serão escritos.
     *
     * @return Quantidade de bytes escritos, em caso de falha, pode
     * ser inferior ao total requisitado.
     *
     * @see #escreve(byte[], int)
     */
    int escreve(ByteBuffer buffer, int posicao);

    /**
     * Deposita no arquivo, a partir da posição indicada, o total de
     * bytes do buffer, obtidos a partir da primeira posição do buffer.
     *
     * @param buffer Buffer do qual bytes serão consultados para a
     *               inserção no arquivo.
     *
     * @param posicao Posição inicial no arquivo a partir da qual
     *                bytes serão escritos.
     *
     * @return Quantidade de bytes escritos, em caso de falha, pode
     * ser inferior ao total requisitado.
     *
     * @see #escreve(ByteBuffer, int)
     */
    int escreve(byte[] buffer, int posicao);

    /**
     * Recupera o nome (identificador) do arquivo.
     *
     * @return O nome (identificador) único do arquivo.
     */
    String filename();
}
