/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Gerência da execução de tarefas.
 *
 * <p>Entre os serviços está o
 * agendamento de execução em intervalos de tempo específico
 * e a manutenção de um <i>thread pool</i> por meio do qual
 * tarefas submetidas são executadas.
 */
public class TaskManager {

    // Threads para tarefas agendadas.
    private ScheduledThreadPoolExecutor agenda;
    private List<ScheduledFuture> agendados;

    // Threads de uso geral
    private ExecutorService taskExecutor;

    public TaskManager() {
        agenda = new ScheduledThreadPoolExecutor(2);
        agendados = new ArrayList<>();

        taskExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Cria agendamento a ser realizado indefinidamente, de tempos em
     * tempos, com retardo inicial.
     *
     * @param r Tarefa a ser executada.
     * @param initialDelay Atraso inicial para início da execução.
     *
     * @param delay Intervalo entre as execuções.
     */
    public void repita(Runnable r, int initialDelay, int delay) {

        agenda.scheduleWithFixedDelay(r, initialDelay, delay, TimeUnit.MILLISECONDS);
    }
}
