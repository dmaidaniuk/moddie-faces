/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 ModdieFaces.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.moddiefaces.web.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * @since 0.1
 * @author Dmytro Maidaniuk
 */
public class StringUtils {

    public static final String BLANK = "";
    public static final String SPACE = " ";

    public static final String SEPARATOR_COMMA = ", ";

    public static String concatWithSpace(final String... styles) {
        final StringBuilder sb = new StringBuilder();

        for (final String style : styles) {
            if (style != null && !BLANK.equals(style)) {
                sb.append(style);
                sb.append(SPACE);
            }
        }

        return sb.toString().trim();
    }

    public static boolean isEmpty(final String value) {
        return value == null || BLANK.equals(value);
    }

    public static boolean isNotEmpty(final String value) {
        return !isEmpty(value);
    }

    public static String getNotNullValue(final String value, final String alternative) {
        return isNotEmpty(value) ? value : alternative;
    }

    public static String getNullSafeValue(final String value) {
        return getNotNullValue(value, "");
    }

    public static String joinWithCommaSeparator(final Collection<String> values) {
        return join(values, SEPARATOR_COMMA, false);
    }

    public static String joinWithCommaSeparator(final Collection<String> values, boolean escape) {
        return join(values, SEPARATOR_COMMA, escape);
    }

    public static String joinWithSpaceSeparator(final Collection<String> values) {
        return join(values, SPACE, false);
    }

    public static String join(final Collection<String> values, final String separator, boolean escape) {
        final StringBuilder value = new StringBuilder();
        final Iterator<String> iterator = values.iterator();

        while (iterator.hasNext()) {
            final String valueToAdd = iterator.next();
            if (escape) {
                value.append("'");
            }
            value.append(valueToAdd);
            if (escape) {
                value.append("'");
            }

            if (iterator.hasNext()) {
                value.append(separator);
            }
        }

        return value.toString();
    }
}
