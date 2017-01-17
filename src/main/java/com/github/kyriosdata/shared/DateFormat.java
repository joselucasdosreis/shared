/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */

package com.github.kyriosdata.shared;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Classe inspirada em FixedDateFormat, fornecida pelo projeto Log4j2.
 *
 * <p>As opções da JVM -XX:FreqInlineSize e -XX:MaxInlineSize podem ser
 * investigadas para identificar possíveis ganhos de desempenho.
 */
public class DateFormat {

    private long midnightToday = 0;
    private long midnightTomorrow = 0;

    /**
     * Template para yyyy-MM-dd'T'HH:mm:ss.SSS'Z' formado por 24 bytes.
     * Vetor que é empregado para manter o formato e a data (que é alterada
     * apenas uma vez a cada dia).
     */
    private final byte[] cachedTemplate = {
            0, 0, 0, 0, 45, 0, 0, 45, 0, 0, 84, 0, 0, 58, 0, 0, 58, 0, 0, 46, 0, 0, 0, 90 };

    /**
     * Formata o instante UTC no padrão "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'".
     * Importante: esse método retorna o mesmo vetor reiteradamente.
     * O vetor é reutilizado. O conteúdo dele deve ser copiado.
     * Em consequência, esse método <b>não é thread-safe</b>.
     *
     * @param time O instante em milissegundos UTC (epoch).
     *
     * @return Vetor de bytes correspondente ao instante.
     */
    public byte[] toBytes(final long time) {
        writeTimeToBytes(millisSinceMidnight(time));
        return cachedTemplate;
    }

    /**
     * Quantidade de milissegundos do instante desde a meia-noite.
     *
     * @param now O instante.
     * @return O total de milissegundos transcorridos desde a meia-note.
     */
    public int millisSinceMidnight(final long now) {

        // Observe que relógio pode ser "atrasado", o
        // que justifica segunda parte da condição.
        if (now >= midnightTomorrow || now < midnightToday) {
            updateMidnightMillis(now);
        }

        return (int) (now - midnightToday);
    }

    /**
     * Atualiza total de milissegundos.
     * @param now
     */
    public void updateMidnightMillis(final long now) {

        updateCache(now);

        midnightToday = addDaysToMidnightMillis(now, 0);
        midnightTomorrow = addDaysToMidnightMillis(now, 1);
    }

    private void updateCache(final long now) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(now));

        // Transfere "yyyy-MM-dd" para buffer (10 primeiros bytes)
        System.arraycopy(date.getBytes(), 0, cachedTemplate, 0, 10);
    }

    private long addDaysToMidnightMillis(final long time, final int addDays) {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, addDays);
        return cal.getTimeInMillis();
    }

    /**
     * Transforma o tempo em milissegundos, após a última meia-noite, na
     * sequência correspondentes de bytes, depositados no buffer, conforme o
     * formato padrão. Especificamente, produz no buffer a definição dos
     * valores das horas, minutos, segundos e milissegundos.
     *
     * @param ms Quantidade de milissegundos após a última meia-noite.
     */
    private void writeTimeToBytes(int ms) {
        final int hours = ms / 3600000;
        ms -= 3600000 * hours;

        final int minutes = ms / 60000;
        ms -= 60000 * minutes;

        final int seconds = ms / 1000;
        ms -= 1000 * seconds;

        // Hour
        int temp = hours / 10;
        cachedTemplate[11] = (byte) (temp + '0');
        cachedTemplate[12] = (byte) (hours - 10 * temp + '0');

        // Minute
        temp = minutes / 10;
        cachedTemplate[14] = (byte) (temp + '0');
        cachedTemplate[15] = (byte) (minutes - 10 * temp + '0');

        // Second
        temp = seconds / 10;
        cachedTemplate[17] = (byte) (temp + '0');
        cachedTemplate[18] = (byte) (seconds - 10 * temp + '0');

        // Millisecond
        temp = ms / 100;
        cachedTemplate[20] = (byte) (temp + '0');

        ms -= 100 * temp;
        temp = ms / 10;
        cachedTemplate[21] = (byte) (temp + '0');

        cachedTemplate[22] = (byte) (ms - 10 * temp + '0');
    }
}
