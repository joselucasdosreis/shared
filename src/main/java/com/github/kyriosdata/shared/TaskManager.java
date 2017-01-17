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

    public void novoAgendamento(Runnable r) {
        agenda.scheduleWithFixedDelay(r, 1000, 1000, TimeUnit.MILLISECONDS);
    }
}
