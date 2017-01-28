/**
 * Esse pacote define os serviços de manipulação de
 * arquivos. Um arquivo é tratado como uma sequência de bytes com um
 * identificador único.
 *
 * <p>Os serviços oferecidos devem ser usufruídos por meio da
 * interface {@link com.github.kyriosdata.healthdb.file.ArquivoService}.
 *
 * <p>Código que usa essa interface tem à disposição a associação de um nome
 * à uma sequência de bytes. As operações permitem que "trechos" sejam
 * lidos, acrescentados e/ou sobrescritos.
 */
package com.github.kyriosdata.healthdb.file;