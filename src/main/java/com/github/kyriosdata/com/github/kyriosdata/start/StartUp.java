package com.github.kyriosdata.com.github.kyriosdata.start;

/**
 * Encapsulamento de procedimentos de inicialização de programas
 * complexos.
 *
 * <p>A execução dos procedimentos definidos retorna
 * o valor 0 apenas quando são executados satisfatoriamente,
 * sem exceção. Caso contrário, um valor diferente de zero é
 * retornado, conforme a situação excepcional.
 *
 * <p>A inicialização é dividida em fases.
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

    public static void main(String[] args) {

        /**
         * FASE 0 (configuração disponível)
         * Get tempo corrente (início)
         * Get configuracao
         *
         * FASE 1 (inicia serviço de logging)
         * Start process manager
         * Start logging
         * Log tempo de início dos procedimentos
         * Log configuração recuperada
         *
         * FASE 2 (recursos mínimos)
         * Check RAM
         * Check CPU
         * Check permissions
         * Log resultados de check
         *
         * FASE 3 - iniciar componentes
         *
         * FASE 4 - verificar operação de componentes
         */

    }
}
