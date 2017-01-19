/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

/**
 * Implementação de serviço que recupera instante corrente.
 */
public class SystemClock implements Clock {

    /**
     * Milissegundos transcorridos desde a "epoch"
     * (UTC).
     *
     * @return Total de milissegundos desde "epoch" (UTC).
     * Consulte {@link System#currentTimeMillis()} para
     * detalhes.
     *
     * @see System#currentTimeMillis()
     */
    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}


