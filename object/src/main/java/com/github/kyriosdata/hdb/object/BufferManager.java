package com.github.kyriosdata.hdb.object;

/**
 * Uma base de dados pode estar organizada em um ou mais
 * arquivos, geralmente extensos o suficiente para ser inviável
 * a transferência deles para a memória RAM. A transferência é
 * necessária para processar o conteúdo da base de dados.
 *
 * <p>Dado que não é assegurada a possibilidade de manter em RAM
 * o conteúdo completo de um arquivo, cada arquivo é dividido em
 * blocos de tamanho fixo ({@link Constantes#BUFFER_SIZE}). Um
 * bloco, por sua vez, pode ser transferido para a memória e,
 * no sentido inverso, persistido se alguma alteração foi realizada.
 *
 * <p>O <i>Buffer Manager</i> é responsável por transferir para a
 * memória RAM um bloco requisitado e, no sentido inverso, persistir
 * um bloco que passou por alteração. Tudo isso com a restrição de
 * um limite de blocos que podem ser mantidos em memória em um
 * dado instante de tempo. Ou seja, é necessário gerenciar o conjunto
 * de blocos em RAM (<i>buffers</i>) e, quando necessário, "descartar"
 * um bloco cuja memória (<i>buffer</i>) será liberada para uso por
 * outro bloco.
 *
 * <p>A requisição de um bloco (<i>buffer</i>) decorre das operações
 * de consultas e atualizações de dados requisitadas por clientes.
 */
public class BufferManager {

    public int
}
