/**
 * Pacote que inclui interfaces e classes necessárias para a
 * implementação da noção de "módulo".
 *
 * <p>Em tempo, módulo é a implementação de uma interface de interesse
 * que representa um ponto de extensão de um software. O módulo também
 * deve implementar a interface
 * {@link com.github.kyriosdata.healthdb.fabrica.Modulo}.
 *
 * <p>A interface {@link com.github.kyriosdata.healthdb.fabrica.Modulo} é
 * empregada para gerir o ciclo de vida de um módulo.
 *
 * <p>Adicionalmente, a implementação do módulo deve ser fornecida por
 * meio de um arquivo .jar, que necessariamente deve conter o diretório
 * <b>META-INF</b>, no qual deve conter o diretório <b>resources</b> que no
 * qual deve estar presente o arquivo com o mesmo nome da interface
 * de interesse implementada pelo módulo. Detalhes dessa exigência podem ser
 * consultados na documentação da classe {@link java.util.ServiceLoader}.
 */
package com.github.kyriosdata.healthdb.fabrica;