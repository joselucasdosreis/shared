package com.github.kyriosdata.hdb.object;

/**
 * Um objeto do Modelo de Referência do openEHR.
 * Executa operações de consulta sobre os campos.
 */
public class Objeto {

    /**
     * Tipo do objeto, por exemplo, OE_DVIDENTIFIER.
     */
    private int tipo;

    /**
     * Posição do header
     */
    int header = 0;

    /**
     * Posição dos dados
     */
    int dados = 0;

    /**
     * Consome informações básicas do objeto, como o tipo,
     * por exemplo. Exige carga do bloco correspondente.
     */
    public void start() {
        header = 0;
        dados = 30;
    }

    /**
     * Obtém o byte correspondente ao n-ésimo campo do objeto.
     *
     * @param ordem A ordem do campo do tipo byte (zero-based).
     *
     * @return O valor do tipo byte correspondente ao n-ésimo
     * campo do tipo byte.
     */
    byte getByte(int ordem) {
        // Assegura que requisição é compatível com o objeto
        assert Campo.BYTE == ClasseManager.tipo(tipo, ordem);

        // Offset negativo indica que deslocamento está no header
        // (trata-se de um campo de tamanho variável)
        int offset = ClasseManager.offset(tipo, ordem);

        return (byte)0;
    }

    String getString(int ordem) {
        // Tipo não primitivo, pode ser precedido por outro
        // não primitivo, ou seja, deslocamento deve vir do header.
        int offset = ClasseManager.offset(tipo, ordem);

        return "ok";
    }

    public int fragmentado(int offset, int size) {
        int restantes = restantes(dados, offset);
        return restantes >= size ? 0 :
    }

    /**
     * Retorna a quantidade de bytes existente no buffer a
     * partir da posição inicial dos dados de um objeto e
     * com um determinado deslocamento.
     *
     * @param d Posição inicial dos dados relativa ao buffer.
     *          Essa é a posição de referência. Se é o início do
     *          buffer, então o valor é zero. Pode ser um valor
     *          entre 0 e {@link Constantes#BUFFER_SIZE},
     *          inclusive.
     *
     * @param o Deslocamento do byte de interesse relativa à
     *          posição inicial (parâmetro {@code d}).
     *
     * @return Quantidade de bytes existente no buffer a partir
     * da posição inicial d com o deslocamento o.
     */
    public int restantes(int d, int o) {
        return Constantes.BUFFER_SIZE - o - d + 1;
    }
}
