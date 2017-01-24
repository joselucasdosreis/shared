package com.github.kyriosdata.hdb.object;

/**
 * Mantém informações sobre as classes
 * do Modelo de Referência do openEHR.
 *
 * <p>Cada classe é definida por uma sequência bem definida de
 * campos. Cada um deles com o seu identificador (nome), tipo e
 * deslocamento em número de bytes a partir do início do registro.
 *
 * <p>O deslocamento está definido para todos os tipos "primitivos"
 * e para o primeiro campo de tamanho variável. Nos demais casos o
 * valor indicado é -1. Nesse contexto, indica que o deslocamento
 * só pode ser obtido por meio do <i>header</i> do registro.
 *
 */
public class ClasseManager {

    /**
     * Constante que identifica objeto da classe DV_IDENTIFIER.
     */
    private static int OE_DVIDENTIFIER = 0;

    /**
     * Cada classe do Modelo de Referência do openEHR possui
     * uma entrada correspondente nesse atributo, em conformidade
     * com a ordem estabelecida pelos valores das constantes acima.
     */
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
