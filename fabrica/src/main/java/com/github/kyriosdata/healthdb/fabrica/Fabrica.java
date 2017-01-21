package com.github.kyriosdata.healthdb.fabrica;

import java.util.ServiceLoader;

/**
 * Fábrica de objetos.
 */
public class Fabrica {

    public static <T> T loadService(Class<T> api) {

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
