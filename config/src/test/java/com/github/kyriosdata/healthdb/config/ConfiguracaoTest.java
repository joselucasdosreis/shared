package com.github.kyriosdata.healthdb.config;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracaoTest {

    @Test
    public void propriedadeNullValorNull() {
        Configuracao cfg = new Configuracao(null);
        assertNull(cfg.valor(null));
    }

    @Test
    public void nullFilenameNaoGeraExcecaoPropriedadesVazias() {
        assertEquals(0, new Configuracao(null).chaves().size());
    }

    @Test
    public void arquivoInexistenteNenhumaPropriedadeDefinida() {
        String filename = UUID.randomUUID().toString();
        assertEquals(0, new Configuracao(filename).chaves().size());
    }

    @Test
    public void umArquivoVazio() {
        Configuracao cfg = new Configuracao("vazio.cfg");
        assertEquals(0, cfg.chaves().size());
    }

    @Test
    public void unicaLinhaErrada() {
        Configuracao cfg = new Configuracao("errado.cfg");
        assertEquals(1, cfg.chaves().size());
        assertEquals("", cfg.valor("porta"));
    }

    @Test
    public void readOnly() {
        Configuracao c = new Configuracao("propriedadeUnica.cfg");
        assertNull(c.valor("x"));
        assertEquals(1, c.chaves().size());

        Set<String> chaves = c.chaves();
        assertThrows(UnsupportedOperationException.class, () -> chaves.add("x"));

        assertThrows(UnsupportedOperationException.class, () -> chaves.remove("chave"));
        assertEquals(1, c.chaves().size());
    }

    @Test
    public void tamanhoMaiorQuePermitidoNenhumaPropriedadeLida() {
        Configuracao c = new Configuracao("4097.cfg");
        assertEquals(0, c.chaves().size());
    }

    @Test
    public void arquivoTipico() {
        Configuracao c = new Configuracao("tipico.cfg");
        assertEquals("80", c.valor("port"));
        assertEquals("inf.ufg.br", c.valor("server"));
    }

    @Test
    public void propriedadeSobrescrita() {
        Configuracao c = new Configuracao("tipico.cfg");
        assertNotEquals("ok", c.valor("original"));
        assertEquals("sobrescrito", c.valor("original"));
    }
}
