/*
 * Copyright (c) 2016
 *
 * Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 *
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.healthdb.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Encapsula valores (propriedades) obtidas de pares (chave/valor)
 * depositadas em um arquivo texto. Um arquivo texto é a única forma
 * de criação de uma instância dessa classs.
 *
 * <p>Valores de uma configuração não podem ser alterados,
 * são apenas para leitura (<i>read only</i>).
 *
 * <p>Propriedades são pares definidos por uma chave separada do valor
 * pelo símbolo '='. Por exemplo, <code>port=80</code> defina a
 * propriedade 'port' com o valor '80'. Uma única propriedade é
 * fornecida em cada linha. Além de um par, uma linha pode ser
 * vazia (o que não produz nenhum efeito) ou fornecer algum comentário,
 * o que é indicado pelo primeiro caractere da linha que deve ser ';'.
 *
 * <p>O caminho completo de um arquivo de propriedade deve ser
 * fornecido quando uma instância é criada. Caso contrário, o arquivo
 * indicado é procurado no <i>classpath</i>. A criação de uma instância
 * não deve falhar e, se nenhuma das opções acima conduz ao arquivo
 * desejado, então a configuração padrão é fornecida. A configuração
 * padrão não possui nenhuma propriedade (o que pode ser verificado
 * facilmente).
 */
public class Configuracao {

    /**
     * Tamanho máximo de um arquivo de configuração.
     */
    public final int MAX_SIZE = 4096;

    /**
     * Mantém conjunto de pares (chave/valor) da configuração.
     */
    private Map<String, String> propriedades =
            Collections.unmodifiableMap(new HashMap<>(0));

    /**
     * Cria instância de configuração baseada no arquivo fornecido.
     * Deve ser fornecido o nome (<i>path</i>) completo do arquivo
     * ou, caso contrário, a busca será realizada pelo nome no
     * 'classpath' da aplicação.
     * <p>
     * <p>O arquivo de configuração deve possuir, no máximo,
     * {@link #MAX_SIZE} bytes. Um arquivo com tamanho superior
     * será ignorado. Ou seja, nenhuma propriedade será disponibilizada
     * por meio da instância criada.
     * <p>
     * <p>Caso ocorra algum erro de leitura do arquivo, nenhuma
     * propriedade estará disponível por meio da instância criada.
     *
     * @param filename Nome do arquivo a ser procurado no 'classpath' ou,
     *                 o arquivo conforme o nome completo fornecido.
     */
    public Configuracao(String filename) {

        try {
            getPropriedades(filename);
        } catch (Exception exp) {
            // Do nothing.
        } finally {
            propriedades = Collections.unmodifiableMap(propriedades);
        }
    }

    private void getPropriedades(String filename) throws IOException, URISyntaxException {

        Path path = getConfigFilePath(filename);
        if (path == null) {
            return;
        }

        if (Files.size(path) > MAX_SIZE) {
            return;
        }

        List<String> linhas = new ArrayList();
        try (BufferedReader reader = Files.newBufferedReader(path)) {

            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isEmpty() || linha.startsWith(";")) {
                    continue;
                }

                linhas.add(linha);
            }
        } catch (IOException x) {
            return;
        }

        propriedades = new HashMap<>(linhas.size());

        for (String linha : linhas) {
            int igual = linha.indexOf('=');
            if (igual <= 0) {
                continue;
            }

            String chave = linha.substring(0, igual).trim();
            if (chave.isEmpty()) {
                continue;
            }

            String valor = linha.substring(igual + 1).trim();

            propriedades.put(chave, valor);
        }
    }

    /**
     * Recupera o valor da propriedade fornecida.
     *
     * @param propriedade Nome da propriedade ou chave.
     *
     * @return O valor da propriedade ou {@code null},
     * caso a propriedade não esteja definida.
     */
    public String valor(String propriedade) {
        return propriedades.get(propriedade);
    }

    /**
     * Obtém conjunto de chaves.
     *
     * @return Conjunto de chaves obtido do arquivo de configuração.
     */
    public Set<String> chaves() {
        return propriedades.keySet();
    }

    /**
     * Obtem o {@link Path} do arquivo de configuração baseado no
     * arquivo fornecido. A prioridade é para arquivo no 'classpath'.
     * Caso não encontrado, então é tratado como o 'path' do arquivo
     * a ser carregado. Caso não encontrado, retorna {@code null}.
     *
     * @param filename Nome do arquivo no 'classpath' contendo a
     *                 configuração. Alternativamente pode ser fornecido
     *                 o caminho completo do arquivo, em lugar distinto
     *                 do 'classpath'.
     * @return {@code null} caso não encontrado ou o {@link Path} para o
     * arquivo contendo a configuração a ser carregada.
     * @throws NullPointerException Se o argumento é {@code null}.
     */
    private static Path getConfigFilePath(String filename) throws URISyntaxException {

        URL url = ClassLoader.getSystemResource(filename);

        Path path = url != null ? Paths.get(url.toURI()) : Paths.get(filename);

        return Files.exists(path) ? path : null;
    }
}
