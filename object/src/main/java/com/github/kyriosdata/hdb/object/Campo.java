/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.hdb.object;

/**
 * Retém informações sobre um campo em um determinado
 * tipo de registro. Ou seja, não existe a definição
 * de um campo sem a existência do formato (tipo) do
 * registro em questão.
 */
public class Campo {

    /**
     * Indica valor numérico inteiro de 8 bits.
     */
    public static int BYTE = 0;

    /**
     * Indica valor numérico inteiro de 32 bits.
     */
    public static int INT32 = 1;

    /**
     * Indica valor numérico inteiro de 64 bits.
     */
    public static int INT64 = 2;

    /**
     * Indica valor real (ponto flutuante) de 32 bits.
     */
    public static int REAL = 3;

    /**
     * Indica valor real (ponto flutuante) de 64 bits.
     */
    public static int DOUBLE = 4;

    /**
     * Indica valor lógico (true ou false) de 8 bits.
     */
    public static int BOOLEAN = 5;

    /**
     * lower (INT32),
     * upper (INT32),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN)
     * Tamanho: 10 bytes
     */
    public static int INTERVAL_INT = 6;

    /**
     * lower (INT64),
     * upper (INT64),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN).
     * Tamanho: 18 bytes.
     */
    public static int INTERVAL_INT64 = 7;

    /**
     * lower (REAL),
     * upper (REAL),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN).
     * Tamanho: 10 bytes
     */
    public static int INTERVAL_REAL = 8;

    /**
     * lower (DOUBLE),
     * upper (DOUBLE),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN).
     * Tamanho: 18 bytes.
     */
    public static int INTERVAL_DOUBLE = 9;

    /**
     * Caractere de 8 bits.
     */
    public static int CHAR = 10;

    /**
     * Indica texto (UTF-8). Tamanho variável.
     * Se o tamanho é -1, indica não definido.
     * Se o tamanho é 0, indica que é vazia.
     */
    public static int STRING = 11;

    /**
     * Vetor de bytes. Tamanho variável.
     * Se o tamanho é 0, indica vetor vazio (sem elementos).
     */
    public static int ARRAY = 12;

    /**
     * Indica sequência de elementos (admite repetição).
     * Se o tamanho é -1, então não definida.
     * Se o tamanho é 0, então lista vazia.
     */
    public static int LIST = 13;

    /**
     * Indica conjunto de elementos (não admite repetição).
     * Se o tamanho é -1, então não definida.
     * Se o tamanho é 0, então conjunto vazio.
     */
    public static int SET = 14;

    /**
     * Indica dicionário (pares do chave/valor).
     * Se o tamanho é -1, então não definico.
     * Se o tamanho é 0, então nenhuma entrada.
     */
    public static int HASH = 15;

    public final String nome;
    public final int tipo;
    public final int offset;

    /**
     * Cria instância de campo.
     *
     * @param identificador O identificador (nome) do campo.
     *
     * @param tipo O valor único que identifica o tipo do campo.
     *
     * @param offset O deslocamento do campo, em bytes, a partir do
     *               início do registro no qual está inserido. O valor
     *               {@link Constantes#INDEFINIDO} indica que o deslocamento
     *               depende dos valores do registro e, portanto, devem ser
     *               obtidos do <i>header</i> correspondente.
     */
    public Campo(String identificador, int tipo, int offset) {
        this.nome = identificador;
        this.tipo = tipo;
        this.offset = offset;
    }
}
