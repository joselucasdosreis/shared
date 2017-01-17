package com.github.kyriosdata.shared;

/**
 * Serviço do qual se obtém o instante corrente.
 */
interface ClockService {

    /**
     * Obtém a quantidade de milissegundos transcorridos desde a "epoch" (UTC).
     *
     * @return Total de milissegundos desde "epoch" (UTC).
     */
    long currentTimeMillis();
}


