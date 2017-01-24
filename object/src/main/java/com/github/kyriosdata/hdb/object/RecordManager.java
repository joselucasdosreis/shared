package com.github.kyriosdata.hdb.object;

/**
 * Mantém informações sobre as classes
 * do Modelo de Referência do openEHR.
 * <p>
 * <p>Cada classe é definida por uma sequência de campos.
 */
public class RecordManager {

    private static int OE_DVIDENTIFIER = 0;

    private static Campo fields;

    private static final Campo[][] classes = {

            // OE_DVIDENTIFIER
            {new Campo("a", Campo.INT, 0),
                    new Campo("b", Campo.STRING, 4),
                    new Campo("b", Campo.STRING, -1)
            }

    };

    public static int tipo(int classe, int ordem) {
        return classes[classe][ordem].tipo;
    }

    /**
     * Identifica, para uma determinada classe, o deslocamento
     * a partir do início de um objeto correspondente, do primeiro
     * byte do valor do campo de ordem indicada.
     *
     * @param classe O identificador único da classe.
     * @param ordem  A ordem do campo, ou seja, o n-ésimo campo da classe.
     * @return O deslocamento do primeiro byte, relativo ao início do
     * objeto, do campo indicado.
     */
    public static int offset(int classe, int ordem) {
        return classes[classe][ordem].offset;
    }
}
