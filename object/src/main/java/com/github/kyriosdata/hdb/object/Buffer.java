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
 * Serviços disponíveis para acesso ao conteúdo de um buffer.
 *
 * <p>Os métodos recuperam as informações que se iniciam no
 * buffer, mas que não necessariamente estão contidas em sua
 * totalidade no buffer. Por exemplo, apenas o primeiro byte
 * de um inteiro pode estar no presente buffer, o que exige
 * desse método que o bloco seguinte seja carregado em outro
 * buffer do qual os demais três bytes serão recuperados.
 */
public interface Buffer {

    /**
     * Recupera o inteiro em uma posição no buffer.
     *
     * @param offset Deslocamento referente ao início do buffer no
     *               qual o inteiro se inicia.
     *
     * @return O inteiro que se inicia no buffer a partir do
     * deslocamento indicado.
     */
    int int32(int offset);

    /**
     * Recupera a sequência de caracteres em uma posição no buffer.
     *
     * @param offset Deslocamento a partir do início do buffer no qual
     *               se inicia a sequência de caracteres.
     *
     * @return Sequência de caracteres que se inicia na posição indicada.
     */
    String string(int offset);
}
