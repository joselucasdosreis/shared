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
 * Fábrica de objetos que são "pontos de extensão".
 *
 * <p>A presente classe é recomendada para permitir a criação de
 * instâncias a partir de classes "desconhecidas". Por exemplo,
 * um classe que implementa uma determinada interface, mas que
 * é fornecida em arquivo .jar específico, desconhecido em
 * tempo de compilação ou cuja dependência para a classe que
 * implementa a interface não deve existir.
 */
public class Fabrica {

    /**
     * Carrega o serviço do tipo fornecido.
     * @param api
     * @param <T>
     * @return
     */
    public static <T> T newInstance(Class<T> api) {

        T result = null;

        ServiceLoader<T> impl = ServiceLoader.load(api);

        for (T loadedImpl : impl) {
            result = loadedImpl;
            if ( result != null ) break;
        }

        if ( result == null ) {
            throw new RuntimeException(
                    "Cannot find implementation for: " + api);
        }

        return result;
    }
}
