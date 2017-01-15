/*
 * Copyright (c) 2016 Fábio Nogueira de Lucena
 * Fábrica de Software - Instituto de Informática (UFG)
 * Creative Commons Attribution 4.0 International License.
 */

package com.github.kyriosdata.shared;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Classe que encapsula operações de controle de concorrência
 * em cenário de vários produtores e um único consumidor.
 *
 * <p>Orientações para o uso correto. O método
 * {@link #aloca()} indica o desejo de produzir (algo), o retorno
 * é um inteiro no intervalo fechado [0, 31], que unicamente
 * identifica o que se deseja produzir. Em geral, o valor fornecido
 * é o índice de um <i>array</i> ou permite identificar, de fato,
 * o dado compartilhado, ou seja, esse valor funciona como um
 * <i>handle</i> para a real informação.
 *
 * <p>A alocação não é suficiente para o
 * consumo. Antes de ser consumido o valor alocado precisa ser
 * "produzido" pelo método {@link #produz(int)}. O valor fornecido
 * como argumento deve ser aquele obtido do método {@link #aloca()}.
 * Noutras palavras, uma chamada desse método com o argumento
 * 7, por exemplo, indica que a informação associada ao valor
 * 7 está disponível para consumo.
 *
 * <p>O consumo propriamente dito ocorre pelo método {@link #consome(int, boolean)}.
 * O usuário dessa classe deve sobrescrever esse método. Quando chamado
 * sabe-se que a faixa de valores indicada foi produzida e deve ser
 * consumida.
 */
public class Shared {

    // Tamanho da "lista circular" (ring buffer).
    // Necessariamente uma potência de 2.
    private final int SIZE = 32;

    // Máscara para "rotacionar" índices
    // Permite substituir "% SIZE" por "& MASCARA" (mais eficiente).
    private final int MASCARA = SIZE - 1;

    // Empregada para evitar reentrância do consumidor.
    private AtomicInteger working = new AtomicInteger(0);

    // Indica primeira entrada livre (first free).
    private AtomicInteger ff = new AtomicInteger(0);

    // Indica última entrada livre (las free)
    private int lf = SIZE - 1;

    // 32 bits (1 para cada valor da lista circular)
    private int produzidos = 0;

    /**
     * Consome a informação produzida e associada
     * ao valor indicado.
     *
     * <p>Após a execução desse método o valor
     * correspondente estará disponível para
     * reutilização pelo método {@link #aloca()}.
     *
     * @param v Valor a ser consumido.
     * @param ultimo {@code true} se e somente se
     *                           é o "último" evento
     *                           registrado até o momento
     *                           para consumo.
     */
    public void consome(int v, boolean ultimo) {}

    /**
     * Indica a produção de informação associada
     * ao valor fornecido.
     *
     * @param v O valor que identifica a produção.
     *           Esse valor necessariamente deve ser
     *           obtido do método {@link #aloca()}.
     *
     * @see #aloca()
     * @see #flush()
     */
    public void produz(int v) {
        assert v > -1 && v < 32;
        produzidos = set(produzidos, v);
        assert produzido(v) == true;
    }

    /**
     * Obtém o valor do n-ésimo bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo (zero-based) bit.
     * @return O valor do n-ésimo bit do inteiro.
     */
    public static int bitValue(int x, int n) {
        return (x & (1 << n)) >>> n;
    }

    /**
     * Define com o valor zero o n-ésimo (zero-based) bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo bit (zero-based).
     * @return O inteiro com o n-ésimo (zero-based) bit 0. Possivelmente
     * o valor retornado é o mesmo daquele fornecido, se o bit já era 0.
     */
    public static int cls(int x, int n) {
        return x & ~(1 << n);
    }

    /**
     * Define com o valor 1 o n-ésimo (zero-based) bit do inteiro.
     *
     * @param x O inteiro.
     * @param n O n-ésimo bit (zero-based).
     * @return O inteiro com o n-ésimo bit definido com o valor
     * 1 (possivelmente o mesmo valor fornecido).
     */
    public static int set(int x, int n) {
        return x | (1 << n);
    }

    /**
     * Quantidade de entradas ocupadas (já alocadas).
     *
     * <p>Nenhuma das entradas, necessariamente, foi
     * "produzida", mas seguramente já foi alocada.
     *
     * @return Número de entradas ocupadas.
     */
    public int totalAlocados() {
        return SIZE - totalLiberados();
    }

    /**
     * Quantidade de entradas disponíveis para alocação.
     *
     * @return Número de entradas (valores) disponíveis
     * para alocação imediata.
     */
    public int totalLiberados() {
        return lf - ff.get() + 1;
    }

    /**
     * Verifica se, no instante em questão, o valor está produzido.
     * @param v O valor sobre o qual a verificação é feita.
     * @return {@code true} se e somente se, no instante em que a
     * chamada é realizada, o valor fornecido está produzido.
     */
    public boolean produzido(int v) {
        return bitValue(produzidos, v) == 1;
    }

    /**
     * Realiza alocação de um valor.
     *
     * @return O identificador único, valor de 0 a 31, inclusive,
     * que deve ser "produzido" e posteriormente consumido.
     */
    public int aloca() {
        while (true) {
            int candidato = ff.get();
            if (candidato <= lf) {
                if (ff.compareAndSet(candidato, candidato + 1)) {
                    return candidato & MASCARA;
                }
            } else {
                flush();
            }
        }
    }

    private static int aqui = 0;

    /**
     * Processa valores já disponíveis. Ou seja,
     * todos os que já foram alocados e também
     * usados.
     */
    public void flush() {

        // Evita reentrância
        if (!working.compareAndSet(0, 1)) {
            return;
        }

        assert ++aqui < 2;

        int fa = lf + 1;
        int la = ff.get() - 1 + SIZE;

        // Obém o total de usados dentre os alocados
        int producao = totalDaProducao(fa, la);

        if (producao > 0) {
            // Alocados e usados é |[fa, lu]| = producao
            int lu = lf + producao;

            // Consome entradas (exceto o último)
            for (int i = fa; i < lu; i++) {
                int valor = i & MASCARA;
                try {
                    consome(valor, false);
                } catch (Exception exp) {
                    System.out.println(exp.toString());
                }

                produzidos = cls(produzidos, valor);
            }

            // Indica que se trata do ÚLTIMO
            consome(lu & MASCARA, true);
            produzidos = cls(produzidos, lu & MASCARA);

            // Disponibiliza valores para reutilização
            lf = lf + producao;
        }

        assert --aqui == 0;

        working.set(0);
    }

    public void showBits(int produzidos) {
        for (int i = 0; i < 32; i++) {
            System.out.print(Shared.bitValue(produzidos, i));
            if ((i+1) % 4 == 0) {
                System.out.print(" ");
            }
        }

        System.out.println();
    }

    private int totalDaProducao(int first, int last) {
        int produzidos = 0;
        int i = first;
        while (i <= last && produzido(i & MASCARA)) {
            i++;
            produzidos++;
        }

        return produzidos;
    }
}
