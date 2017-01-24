package com.github.kyriosdata.hdb.object;

import java.util.ArrayList;
import java.util.List;

/**
 * Coleção de objetos "reutilizáveis". Evita
 * operação do GC.
 */
public class ObjectsPool {

    // Coleção dos objetos, 0..TOTAL-1.
    private List<Objeto> objetos;

    public ObjectsPool(int total) {

        // Cria coleção de objetos reutilizáveis.
        // Inicialmente todos estão disponíveis.
        objetos = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            objetos.add(new Objeto());
        }
    }

    public Objeto get() {
        Objeto referencia;
        synchronized (this) {
            referencia = objetos.get(0);
            objetos.remove(0);
        }

        return referencia;
    }

    public void free(Objeto objeto) {

        synchronized (this) {
            objetos.add(objeto);
        }
    }
}
