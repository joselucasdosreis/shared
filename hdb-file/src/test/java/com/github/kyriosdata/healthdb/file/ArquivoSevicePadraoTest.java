/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

public class ArquivoSevicePadraoTest {

    @Test
    public void iniciarPararInumerasVezes() {
        for (int i = 0; i < 100; i++) {
            try (ArquivoServicePadrao asj = new ArquivoServicePadrao()) {
                asj.start();
            }
        }
    }
}
