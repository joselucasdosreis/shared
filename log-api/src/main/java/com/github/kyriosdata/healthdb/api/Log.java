/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.api;

/**
 * Definição de serviços de <i>logging</i>.
 *
 * <p>Orientações sobre separação da interface e da implementação correspondente.
 *     http://softwareengineering.stackexchange.com/questions/246620/in-java-what-are-some-good-ways-to-separate-apis-from-implementation-of-entire
 */
public interface Log {
    /**
     * Registra mensagem de log (informativa).
     *
     * @param msg Mensagem a ser registrada.
     */
    void info(String msg);

    /**
     * Registra mensagem de log (aviso). Um aviso
     * é entendido como uma situação excepcional,
     * embora não configure necessariamente algo
     * indesejável.
     *
     * @param msg Mensagem a ser registrada.
     */
    void warn(String msg);

    /**
     * Registra mensagem de pertinente à falha. Representa
     * situação indesejável.
     *
     * @param msg Mensagem a ser registrada.
     */
    void fail(String msg);
}


