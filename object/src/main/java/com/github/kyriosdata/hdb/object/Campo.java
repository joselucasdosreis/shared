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
    public static int INT32 = 0;

    public static int INT64 = 0;
    public static int REAL = 0;
    public static int DOUBLE = 0;

    public static int TRUE = 0;
    public static int FALSE = 0;

    /**
     * lower (INT32),
     * upper (INT32),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN)
     */
    public static int INTERVAL_INT = 0;

    /**
     * lower (INT64),
     * upper (INT64),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN)
     */
    public static int INTERVAL_INT64 = 0;

    /**
     * lower (REAL),
     * upper (REAL),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN)
     */
    public static int INTERVAL_REAL = 0;

    /**
     * lower (DOUBLE),
     * upper (DOUBLE),
     * lowerIncluded (BOOLEAN),
     * upperIncluded (BOOLEAN)
     */
    public static int INTERVAL_DOUBLE = 0;

    public static int CHAR = 0;

    public static int STRING = 0;
    public static int ARRAY = 0;

    public static int LIST = 0;
    public static int SET = 0;
    public static int HASH = 0;

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
