/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.fabrica;

import java.io.Closeable;

/**
 * Identifica ponto de extensão do HealthDB, o que significa que a
 * implementação deve atender os seguintes requisitos:
 * <ul>
 *     <li>Possuir um construtor padrão (default).</li>
 *     <li>Após a criação de uma instância de um módulo o método
 *     {@link #start(Object...)} é chamado para deixar o objeto
 *     "pronto" para uso dos clientes de referência.</li>
 *     <li>Quando a instância do módulo não for mais necessária,
 *     possivelmente após requisição de <i>shutdown</i>, o método
 *     {@link Closeable#close()} será chamado, dando oportunidade
 *     para a instância liberar recursos (operações de finalização).</li>
 * </ul>
 */
public interface Modulo extends Closeable {

    /**
     * Inicia a operação do módulo.
     *
     * @param params Parâmetros exigidos para a correta inicialização
     *               da implementação em questão.
     */
    void start(Object... params);
}
