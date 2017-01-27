/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.file;

import java.io.Closeable;

/**
 * Define serviços básicos de gerência do
 * ciclo de vida de um arquivo ({@link Arquivo}).
 *
 * <p>O ponto de entrada do ciclo de vida de um
 * arquivo não é a criação dele, mas o registro
 * correspondente, o que é realizado pela operação
 * {@link #register(String)}. O fim é definido
 * pela operação {@link #unregister(int)}. Um mesmo
 * arquivo pode ser registrado e ter o registro
 * removido inúmeras vezes.
 *
 * <p>Após o registro as demais operações estão
 * disponíveis: criação do arquivo ({@link #cria(int)}),
 * remoção ({@link #remove(int)}), verificação da
 * existência ({@link #existe(int)})
 *
 * @see Arquivo
 */
public interface ArquivoManager extends Closeable {

    /**
     * Prepara o administrador (gerente) de arquivos.
     * Argumentos devem variar conforme a implementação.
     * Por exemplo, se Azure Blobs são empregados, então
     * entre os parâmetros é razoável a presença de credencial
     * de acesso.
     *
     * @param params Parâmetros que configuram o objeto.
     *               Esse método deve ser chamado antes que
     *               qualquer serviço oferecido seja executado.
     */
    void start(Object... params);

    /**
     * Recupera o nome do arquivo associado ao handle.
     *
     * @param handle O handle do arquivo.
     *
     * @return Nome do arquivo associado ao handle ou o valor
     * {@code null}, caso não esteja registrado.
     *
     * @see #register(String)
     */
    String filename(int handle);

    /**
     * Registra o nome de arquivo, cujo handle correspondente,
     * empregado pelas demais operações oferecidas, é retornado.
     *
     * @param filename Nome do arquivo (identificador) para o quel
     *                 o handle deve ser criado.
     *
     * @return O handle, valor inteiro único correspondente ao
     * arquivo.
     *
     * @see #filename(int)
     */
    int register(String filename);

    /**
     * Elimina o registro do arquivo cujo handle é fornecido.
     * O arquivo é fechado nesse processo.
     *
     * @param handle O handle do arquivo cujo registro deve
     *               ser removido.
     */
    void unregister(int handle);

    /**
     * Verifica se o arquivo existe.
     *
     * @param handle O nome do arquivo.
     *
     * @return {@code true} se o arquivo existe e
     * {@code false}, caso contrário.
     *
     * @see #remove(int)
     * @see #register(String)
     */
    boolean existe(int handle);

    /**
     * Cria o arquivo.
     *
     * @param handle O nome do arquivo a ser criado.
     *
     * @return {@code true} se o arquivo já existe ou foi
     * criado pela operação, caso contrário, retorna {@code false}.
     *
     * @see #register(String)
     */
    boolean cria(int handle);

    /**
     * Remove o arquivo, caso exista.
     *
     * @param handle O nome do arquivo.
     *
     * @return {@code true} se o arquivo foi
     * removido ou não existe. Retorna {@code false}
     * apenas se o arquivo existe, mesmo após a
     * tentativa de removê-lo.
     */
    boolean remove(int handle);

    /**
     * Abre o arquivo, tornando-o disponível para operações
     * de leitura e escrita.
     *
     * @param handle O handle do arquivo.
     *
     * @return {@code true} se o arquivo foi aberto ou
     * {@code false}, caso contrário.
     *
     * @see #fecha(int)
     */
    boolean abre(int handle);

    /**
     * Fecha o arquivo, liberando toda e qualquer memória
     * correspondente associada ao arquivo. Após a execução dessa
     * operação não é possível ler ou escrever no arquivo.
     *
     * @param handle O handle do arquivo.
     *
     * @return {@code true} se o arquivo foi fechado ou
     * {@code false}, caso contrário.
     *
     * @see #abre(int)
     */
    boolean fecha(int handle);

    /**
     * Recupera a instância que representa um arquivo por meio da
     * qual operações de leitura e escrita podem ser executadas.
     *
     * @param handle O handle do arquivo.
     *
     * @return Instância de {@link Arquivo} que representa o arquivo
     * associado ao handle.
     */
    Arquivo get(int handle);
}
