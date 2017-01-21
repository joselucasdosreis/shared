package com.github.kyriosdata.healthdb.fabrica;

import java.util.ServiceLoader;

/**
 * Fábrica de objetos.
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

        if ( result == null ) throw new RuntimeException(
                "Cannot find implementation for: " + api);

        return result;
    }
}
