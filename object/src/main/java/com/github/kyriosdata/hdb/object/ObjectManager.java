package com.github.kyriosdata.hdb.object;

/**
 * Encapsula operações sobre um registro baseadas em
 * um ou mais buffers.
 */
public class ObjectManager {

    public ObjectsPool pool;
    public BufferManager bm;

    public ObjectManager(BufferManager bufferManager, ObjectsPool colecao) {
        bm = bufferManager;
        pool = colecao;
    }

    Objeto get(int arquivo, int endereco) {
        // Requisita carga "antecipada" do
        // bloco onde se inicia o objeto.

        Objeto objeto = pool.get();

        // Configurar aqui o objeto


        return objeto;
    }
}
