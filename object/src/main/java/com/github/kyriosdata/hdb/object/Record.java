package com.github.kyriosdata.hdb.object;

/**
 * Encapsula operações sobre um registro baseadas em
 * um ou mais buffers.
 */
public class Record {

    public ObjectsPool pool;
    public BufferManager bm;

    public Record(BufferManager bufferManager, ObjectsPool colecao) {
        bm = bufferManager;
        pool = colecao;
    }

    Objeto get(int arquivo, int endereco) {
        // Requisita carga "antecipada" do
        // bloco onde se inicia o objeto.
        bm.load(arquivo, endereco);

        Objeto objeto = pool.get();

        // Configurar aqui o objeto


        return objeto;
    }
}
