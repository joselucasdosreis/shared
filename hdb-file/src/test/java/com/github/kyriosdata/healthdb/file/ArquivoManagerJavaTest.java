package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ArquivoManagerJavaTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void mesmoArquivoMesmoHandle() throws Exception {
        ArquivoManagerJava am = new ArquivoManagerJava();
        am.start();

        int h1 = am.register("1.txt");
        int h2 = am.register("1.txt");

        assertEquals(h1, h2);
    }

    @Test
    public void arquivosDistintosHandlesDistintos() throws Exception {
        ArquivoManagerJava am = new ArquivoManagerJava();
        am.start();

        int h1 = am.register("a");
        int h2 = am.register("b");

        assertNotEquals(h1, h2);
    }

    @Test
    public void registerUnregisterSameFileManyTimes() throws Exception {
        ArquivoManagerJava am = new ArquivoManagerJava();
        am.start();

        String inexistente = UUID.randomUUID().toString();
        for(int i = 0; i < 1000; i++) {
            int handle = am.register(inexistente);
            assertFalse(am.existe(handle));
            assertEquals(inexistente, am.filename(handle));

            am.unregister(handle);
            assertNull(am.filename(handle));
        }
    }

    @Test
    public void semEfeitoUnregisterDeArquivoNaoRegistrado() {
        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        amj.unregister(Integer.MAX_VALUE);
    }

    @Test
    public void naoHaComoCriarArquivoHandleInvalido() {
        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        assertFalse(amj.cria(Integer.MAX_VALUE));
    }

    @Test
    public void naoHaComoRemoverArquivoHandleInvalido() {
        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        assertFalse(amj.remove(Integer.MAX_VALUE));
    }

    @Test
    public void handleInvalidoArquivoNull() {
        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        assertNull(amj.get(Integer.MAX_VALUE));
    }

    @Test
    public void arquivoNomeUuidNaoExiste() {
        String nome = UUID.randomUUID().toString();

        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        int handle = amj.register(nome);
        assertFalse(amj.existe(handle));
    }

    @Test
    public void aposCriacaoArquivoExiste() {
        String nome = dir + UUID.randomUUID().toString();
        cria(nome);

        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        int handle = amj.register(nome);
        assertTrue(amj.existe(handle));
    }

    @Test
    public void criaRemoveArquivo() {
        String nome = dir + UUID.randomUUID().toString();

        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        int handle = amj.register(nome);
        assertFalse(amj.existe(handle));

        assertTrue(amj.cria(handle));
        assertTrue(amj.existe(handle));
        assertTrue(amj.remove(handle));
        assertFalse(amj.existe(handle));
    }

    @Test
    public void arquivoInexistenteNaoPodeSerAberto() {
        String nome = dir + UUID.randomUUID().toString();

        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        int handle = amj.register(nome);
        assertFalse(amj.existe(handle));

        assertFalse(amj.abre(handle));
    }

    @Test
    public void arquivoNaoAbertoNaoPodeSerFechado() {
        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        int handle = amj.register("a");
        assertFalse(amj.fecha(handle));
    }

    @Test
    public void arquivoCriadoPodeSerAberto() {
        String nome = dir + UUID.randomUUID().toString();
        cria(nome);

        ArquivoManagerJava amj = new ArquivoManagerJava();
        amj.start();

        int handle = amj.register(nome);
        assertTrue(amj.abre(handle));
        assertTrue(amj.fecha(handle));
    }



    private void cria(String fn) {
        try {
            Files.createFile(Paths.get(fn));
        } catch (IOException exp) {
        }
    }
}
