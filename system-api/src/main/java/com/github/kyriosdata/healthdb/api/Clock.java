/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.api;

/**
 * Serviço do qual se obtém o instante corrente.
 */
public interface Clock {

    /**
     * Obtém a quantidade de milissegundos transcorridos desde a "epoch" (UTC).
     *
     * @return Total de milissegundos desde "epoch" (UTC).
     */
    long currentTimeMillis();
}


