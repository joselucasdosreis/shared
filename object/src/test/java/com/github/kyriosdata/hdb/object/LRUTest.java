package com.github.kyriosdata.hdb.object;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LRUTest {

    @Test
    public void cadaBlocoBufferDistinto() {
        LRU lru = new LRU(3);

        int b1 = lru.use(1);
        int b2 = lru.use(2);
        int b3 = lru.use(3);

        assertNotEquals(b1, b2);
        assertNotEquals(b2, b3);
        assertNotEquals(b1, b3);
    }

    @Test
    public void usoMaisRemotoCedeEspaco() {
        LRU lru = new LRU(3);

        // Configuracao inicial
        // Primeiro b1 usado, depois b2 e por último b3.
        // b1 é o de uso mais remoto, b3 o mais recente.
        int b1 = lru.use(1);
        int b2 = lru.use(2);
        int b3 = lru.use(3);

        // Requisição de uso para bloco não disponível
        int buffer = lru.use(4);

        // Bloco de uso mais remoto, 1 usando buffer b1 (cede)
        assertEquals(b1, buffer);
    }

    @Test
    public void usoMaisRecenteReutilizado() {
        LRU lru = new LRU(3);

        // bloco 3, usando buffer b3 é o mais recente.
        int b1 = lru.use(1);
        int b2 = lru.use(2);
        int b3 = lru.use(3);

        // Usa o mais recente
        int buffer = lru.use(3);

        // buffer é o mesmo (reutilização)
        assertEquals(b3, buffer);

        // Permanece o mais recente
        int primeiroQueCede = lru.use(4);
        assertEquals(b1, primeiroQueCede);

        int segundoQueCede = lru.use(5);
        assertEquals(b2, segundoQueCede);

        int terceiroQueCede = lru.use(6);
        assertEquals(b3, terceiroQueCede);
    }

    @Test
    public void nemPrimeiroNemUltimoReutilizado() {
        LRU lru = new LRU(3);

        // Vamos usar os buffers disponíveis
        int b1 = lru.use(1);
        int b2 = lru.use(2);
        int b3 = lru.use(3);

        int bloco = lru.use(2);
        assertEquals(b2, bloco);

        // Do uso mais remoto para o mais recente
        // 1 (b1), 3 (b3) e 2 (usando o buffer b2).

        // Primeiros a ceder, na ordem, são 1 e 3.
        lru.use(4);
        lru.use(5);

        // Próximo a ceder é 2
        bloco = lru.use(6);
        assertEquals(b2, bloco);
    }

    @Test
    public void usoMaisRemotoReutilizado() {
        LRU lru = new LRU(3);

        // Vamos usar os buffers disponíveis
        int b1 = lru.use(1);
        int b2 = lru.use(2);
        int b3 = lru.use(3);

        int bloco = lru.use(1);
        assertEquals(b1, bloco);

        // Do uso mais remoto para o mais recente
        // 2 (b2), 3 (b3) e 1 (b1).

        // Primeiros a ceder, na ordem, são 2 e 3.
        lru.use(4);
        lru.use(5);

        // Próximo a ceder é 1
        bloco = lru.use(6);
        assertEquals(b1, bloco);
    }
}
