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
     * Consome informações básicas do objeto, como o tipo,
     * por exemplo. Exige carga do bloco correspondente.
     */
    public void start() {
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
        // Assegura que requisição é compatível com a classe
        assert Campo.BYTE == RecordManager.tipo(tipo, ordem);

        // Offset negativo indica que deslocamento está no header
        // (trata-se de um campo de tamanho variável)
        int offset = RecordManager.offset(tipo, ordem);
        return (byte)0;
    }

    String getString(int ordem) {
        // Tipo não primitivo, pode ser precedido por outro
        // não primitivo, ou seja, deslocamento deve vir do header.
        int offset = RecordManager.offset(tipo, ordem);
    }
}
