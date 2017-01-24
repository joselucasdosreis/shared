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
     * Limite do buffer
     */
    int limite = 2048;

    /**
     * Consome informações básicas do objeto, como o tipo,
     * por exemplo. Exige carga do bloco correspondente.
     */
    public void start() {
        header = 0;
        dados = 30;
        limite = 2048;
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
}