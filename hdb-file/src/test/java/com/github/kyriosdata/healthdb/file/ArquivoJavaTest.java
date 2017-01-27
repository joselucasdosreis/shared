package com.github.kyriosdata.healthdb.file;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ArquivoJavaTest {

    private String dir = getClass().getResource(".").getFile();

    @Test
    public void closeable() {
        String fn = dir + UUID.randomUUID().toString();
        cria(fn);

        final ArquivoJava refToA;
        try (ArquivoJava a = new ArquivoJava()) {
            a.filename(fn);
            assertTrue(a.abre());
            refToA = a;
        }

        assertNotNull(refToA);

        // Falha, pois já foi fechado pelo "try"
        assertFalse(refToA.fecha());

        assertThrows(NullPointerException.class, () -> refToA.filename());
    }

    @Test
    public void naoHaComoCarregarAcrescentarArquivoFechado() {
        ArquivoJava aj = new ArquivoJava();
        assertEquals(-1, aj.acrescenta(new byte[1], 0, 1));
        assertEquals(-1, aj.carrega(new byte[1], 0));
    }

    @Test
    public void recuperarNomeDoArquivo() {
        String name = UUID.randomUUID().toString();
        String fn = dir + name;
        cria(fn);

        ArquivoJava a = new ArquivoJava();
        a.filename(fn);

        assertTrue(a.abre());

        assertTrue(a.filename().startsWith(dir));
        assertTrue(a.filename().endsWith(name));
    }

    @Test
    public void arquivoInexistenteNaoPodeSerAberto() {
        String fn = dir + UUID.randomUUID().toString();
        ArquivoJava a = new ArquivoJava();
        a.filename(fn);

        assertFalse(a.abre());
    }

    @Test
    public void arquivoExistentePodeSerAberto() {
        String fn = dir + UUID.randomUUID().toString();
        cria(fn);

        ArquivoJava a = new ArquivoJava();
        a.filename(fn);

        assertTrue(a.abre());
    }

    @Test
    public void podeAbrirFecharVariasVezes() {
        String fn = dir + UUID.randomUUID().toString();
        cria(fn);

        ArquivoJava aj = new ArquivoJava();
        aj.filename(fn);

        for (int i = 0; i < 1000; i++) {
            aj.filename(fn);
            assertTrue(aj.abre());
            assertTrue(aj.fecha());
        }
    }

    @Test
    public void acrescentaRecupera() {
        String fn = dir + UUID.randomUUID().toString();
        cria(fn);

        ArquivoJava aj = new ArquivoJava();
        aj.filename(fn);

        aj.abre();

        byte[] bytes = "ok".getBytes(StandardCharsets.UTF_8);

        // Acrescenta e fecha
        aj.acrescenta(bytes, 0, bytes.length);
        assertTrue(aj.fecha());

        // Reutiliza instância
        aj.filename(fn);
        assertTrue(aj.abre());

        // Carrega conteúdo existente
        byte[] recuperado = new byte[1024];
        int total = aj.carrega(recuperado, 0);

        String ok = new String(recuperado, 0, total, StandardCharsets.UTF_8);
        assertEquals("ok", ok);
    }

    @Test
    public void naoHaComoCarregarEscreverPosicaoInvalida() {
        String fn = dir + UUID.randomUUID().toString();
        cria(fn);

        ArquivoJava aj = new ArquivoJava();
        aj.filename(fn);
        aj.abre();

        byte[] bytes = new byte[1];

        // Arquivo recém-criado, não existe posição 2).
        assertEquals(-1, aj.carrega(bytes, 2));
        assertEquals(-1, aj.escreve(bytes, -1));
    }

    private void cria(String fn) {
        try {
            Files.createFile(Paths.get(fn));
        } catch (IOException exp) {
        }
    }
}
