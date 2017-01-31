/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.fabrica;

import java.util.ServiceLoader;

/**
 * Fábrica de objetos que implementam "pontos de extensão".
 *
 * <p>A presente classe é recomendada para permitir a criação de
 * instâncias de {@link Modulo}. Um módulo é a implementação de
 * um ponto de extensão, desconhecido do cliente que o usa por
 * meio de uma interface de interesse. Ao contrário da implementação,
 * a interface é conhecida pela presente classe.
 */
public class Fabrica {

    /**
     * Cria uma instância do tipo indicado. A primeira classe encontrada
     * que implementa o tipo é utilizada.
     *
     * @param classe Classe do tipo de interesse.
     *
     * @param <T> Interface cuja implementação é requisitada. Não é a
     *            interface {@link Modulo}, mas a interface de interesse
     *            do domínio em questão.
     * @return
     */
    public static <T> T newInstance(Class<T> classe) {

        T instancia = null;

        ServiceLoader<T> fornecedores = ServiceLoader.load(classe);

        for (T fornecedor : fornecedores) {
            if (fornecedor != null) {
                instancia = fornecedor;
                break;
            }
        }

        if (instancia == null) {
            throw new RuntimeException("class not found for " + classe);
        }

        return instancia;
    }
}
