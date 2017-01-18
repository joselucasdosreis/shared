package com.github.kyriosdata.shared;

import java.nio.ByteBuffer;

/**
 * Serviço do qual se obtém o instante corrente.
 */
public class Buffers {

    /**
     * Copia bytes do vetor de bytes, a partir do deslocamento indicado para
     * o buffer.
     *
     * @param buffer Buffer para o qual bytes serão copiados.
     * @param payload Vetor do qual bytes serão copiados.
     * @param inicio Posição inicial no vetor a partir da qual os
     *               bytes serão copiados.
     *
     * @param fim Posição final no vetor do último byte a ser copiado.
     * @return 0 se todos os bytes foram copiados ou a quantidade
     * de bytes restantes no vetor de bytes que não foram copiados.
     * Um valor diferente de zero indica que a capacidade do buffer
     * foi atingida.
     */
    public static int copyToBuffer(ByteBuffer buffer, byte[] payload, int inicio, int fim) {

        int restante = buffer.remaining();
        int pretendido = payload.length - inicio;

        int quantos = pretendido > restante ? restante : pretendido;

        buffer.put(payload, inicio, quantos);

        return pretendido - quantos;
    }
}


