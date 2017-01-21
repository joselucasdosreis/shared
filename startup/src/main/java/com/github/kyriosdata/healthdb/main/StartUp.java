package com.github.kyriosdata.healthdb.main;

/**
 * Encapsulamento de inicialização de programas
 * complexos. A inicialização é definida por um conjunto
 * de procedimentos ordenados. Cada um dos procedimentos
 * é executado e produz um resultado, que pode interromper
 * a sequência de execução. A entrada é fornecida por um
 * arquivo texto, e a saída pelo serviço de <i>logging</i>
 * empregado.
 *
 * <p>A inicialização (a sequência de procedimentos) é dividida
 * em fases.
 *
 * <p><b>Recursos físicos (fase 0)</b>. Responsável pela verificação
 * de disponibilidade de memória, CPU, e outros relevantes para o
 * programa, como permissões de acesso aos recursos. Se não for
 * possível concluir a fase 0, então tem-se pelo menos uma falha,
 * que é enviada para {@link System#err}.
 *
 * <p><b></b>
 *
 *
 * <p>Nesse caso, nenhum processo ou <i>thread</i>
 * permanece em execução após o término do programa.
 *
 */
public class StartUp {

    /**
     * FASE 0 - Serviços oferecidos pelo StartUp
     * Get tempo corrente -> inicio
     * Start logging
     * Log início
     * Get configuração
     * Log configuração
     *
     * FASE 1
     * Check RAM
     * Check CPU
     * Check permissions
     * Log resultados de check
     *
     * FASE 2 - fabricar componentes
     *
     * FASE 4 - iniciar componentes
     *
     * FASE 5 - verificar operação de componentes
     */
    public static void main(String[] args) {

        // FASE 0 - Serviços oferecidos pelo StartUp
        long startTime = System.currentTimeMillis();
    }
}
