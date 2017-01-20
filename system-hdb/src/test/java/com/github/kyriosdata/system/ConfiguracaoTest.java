package com.github.kyriosdata.system;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConfiguracaoTest {

    @Test
    public void nullFilenameNullPointerException() {
        assertThrows(NullPointerException.class, () -> Configuracao.getConfigFilePath(null));
    }

    @Test
    public void encontraArquivoDepositadoEmResources() {
        assertNotNull(Configuracao.getConfigFilePath("log4j2.xml"));
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
        assertEquals("", cfg.getValor("porta"));
    }
}
