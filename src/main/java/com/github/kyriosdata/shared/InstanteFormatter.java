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
 */
public class InstanteFormatter {

    private volatile long midnightToday = 0;
    private volatile long midnightTomorrow = 0;
    private byte[] cachedDayBytes;
    private String cachedDayString;

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 30 bytes (allows immediate JVM inlining: <= -XX:MaxInlineSize=35 bytes)
    public long millisSinceMidnight(final long now) {
        if (now >= midnightTomorrow || now < midnightToday) {
            updateMidnightMillis(now);
        }
        return now - midnightToday;
    }

    public void updateMidnightMillis(final long now) {

        updateCachedDate(now);

        midnightToday = calcMidnightMillis(now, 0);
        midnightTomorrow = calcMidnightMillis(now, 1);
    }

    public long calcMidnightMillis(final long time, final int addDays) {
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(time);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, addDays);
        return cal.getTimeInMillis();
    }

    private void updateCachedDate(final long now) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        cachedDayString = sdf.format(new Date(now));
        cachedDayBytes = cachedDayString.getBytes();
        System.arraycopy(cachedDayBytes, 0, template, 0, 10);
    }

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 31 bytes (allows immediate JVM inlining: <= -XX:MaxInlineSize=35 bytes)
    public void formatToBytes(final long time, final byte[] buffer) {
        final int ms = (int) (millisSinceMidnight(time));
        writeTimeToBytes(ms, buffer);
    }

    /**
     * Template para
     * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public final byte[] template = {0, 0, 0, 0, 45, 0, 0, 45, 0, 0, 84, 0, 0, 58, 0, 0, 58, 0, 0, 46, 0, 0, 0, 90, 32, 0, 0, 0, 0, 32};

    public final byte[] digitos = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57};

    // Profiling showed this method is important to log4j performance. Modify with care!
    // 262 bytes (will be inlined when hot enough: <= -XX:FreqInlineSize=325 bytes on Linux)
    public void writeTimeToBytes(int ms, final byte[] buffer) {
        final int hours = ms / 3600000;
        ms -= 3600000 * hours;

        final int minutes = ms / 60000;
        ms -= 60000 * minutes;

        final int seconds = ms / 1000;
        ms -= 1000 * seconds;

        // Hour
        int temp = hours / 10;
        buffer[11] = digitos[temp];
        buffer[12] = digitos[hours - 10 * temp];

        // Minute
        temp = minutes / 10;
        buffer[14] = digitos[temp];
        buffer[15] = digitos[minutes - 10 * temp];

        // Second
        temp = seconds / 10;
        buffer[17] = digitos[temp];
        buffer[18] = digitos[seconds - 10 * temp];

        // Millisecond
        temp = ms / 100;
        buffer[20] = digitos[temp];

        ms -= 100 * temp;
        temp = ms / 10;
        buffer[21] = digitos[temp];

        buffer[22] = digitos[ms - 10 * temp];
    }

    public void writeTimeToBytesSoma(int ms, final byte[] buffer) {
        final int hours = ms / 3600000;
        ms -= 3600000 * hours;

        final int minutes = ms / 60000;
        ms -= 60000 * minutes;

        final int seconds = ms / 1000;
        ms -= 1000 * seconds;

        // Hour
        int temp = hours / 10;
        buffer[11] = (byte) (temp + '0');
        buffer[12] = (byte) (hours - 10 * temp + '0');

        // Minute
        temp = minutes / 10;
        buffer[14] = (byte) (temp + '0');
        buffer[15] = (byte) (minutes - 10 * temp + '0');

        // Second
        temp = seconds / 10;
        buffer[17] = (byte) (temp + '0');
        buffer[18] = (byte) (seconds - 10 * temp + '0');

        // Millisecond
        temp = ms / 100;
        buffer[20] = (byte) (temp + '0');

        ms -= 100 * temp;
        temp = ms / 10;
        buffer[21] = (byte) (temp + '0');

        buffer[22] = (byte) (ms - 10 * temp + '0');
    }
}
