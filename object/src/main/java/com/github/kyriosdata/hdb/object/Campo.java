package com.github.kyriosdata.hdb.object;

/**
 * Informações sobre um campo de uma classe.
 */
public class Campo {

    public static int BYTE = 0;
    public static int INT = 0;
    public static int INT64 = 0;
    public static int REAL = 0;
    public static int DOUBLE = 0;

    public static int INTERVAL_INT = 0;
    public static int INTERVAL_INT64 = 0;
    public static int INTERVAL_REAL = 0;
    public static int INTERVAL_DOUBLE = 0;

    public static int CHAR = 0;
    public static int STRING = 0;

    public static int ARRAY = 0;
    public static int LIST = 0;
    public static int SET = 0;
    public static int HASH = 0;

    public String nome;
    public int tipo;
    public int offset;

    public Campo(String a, int INT, int offset) {

    }
}
