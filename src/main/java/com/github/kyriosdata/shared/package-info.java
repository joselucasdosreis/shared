/**
 * Serviços de controle de concorrência.
 *
 * <p>Implementação com ausência de lock, usando
 * apenas CAS (compare-and-set), de estrutura
 * produtor/consumidor com vários produtores e
 * um único consumidor.
 */
package com.github.kyriosdata.shared;
