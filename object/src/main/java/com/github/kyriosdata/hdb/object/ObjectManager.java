/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.hdb.object;

/**
 * Encapsula operações sobre um registro baseadas em
 * um ou mais buffers.
 */
public class ObjectManager {

    public ObjectsPool pool;
    public BufferService bm;

    public ObjectManager(BufferService bufferService, ObjectsPool colecao) {
        bm = bufferService;
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
