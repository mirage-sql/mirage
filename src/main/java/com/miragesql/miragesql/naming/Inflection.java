/*
 * Copyright (c) rogueweb - Port of Rails to Java Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.miragesql.miragesql.naming;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Handles singularization and pluralization.
 *
 * @since 1.1.4
 * @author Anthony Eden
 * @see <a href="http://code.google.com/p/rogueweb/">rogueweb</a>
 */
public class Inflection {

    private static final List<Inflection> PLURAL = new ArrayList<>();

    private static final List<Inflection> SINGULAR = new ArrayList<>();

    private static final List<String> UNCOUNTABLE = new ArrayList<>();

    // CHECKSTYLE:OFF
    static {
        // plural is "singular to plural form"
        // singular is "plural to singular form"
        plural("$", "s");
        plural("s$", "s");
        plural("(ax|test)is$", "$1es");
        plural("(octop|vir)us$", "$1i");
        plural("(alias|status)$", "$1es");
        plural("(bu)s$", "$1ses");
        plural("(buffal|tomat)o$", "$1oes");
        plural("([ti])um$", "$1a");
        plural("sis$", "ses");
        plural("(?:([^f])fe|([lr])f)$", "$1$2ves");
        plural("(hive)$", "$1s");
        plural("([^aeiouy]|qu)y$", "$1ies");
        //plural("([^aeiouy]|qu)ies$", "$1y");
        plural("(x|ch|ss|sh)$", "$1es");
        plural("(matr|vert|ind)ix|ex$", "$1ices");
        plural("([m|l])ouse$", "$1ice");
        plural("^(ox)$", "$1en");
        plural("(quiz)$", "$1zes");

        singular("s$", "");
        singular("(n)ews$", "$1ews");
        singular("([ti])a$", "$1um");
        singular("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$", "$1$2sis");
        singular("(^analy)ses$", "$1sis");
        singular("([^f])ves$", "$1fe");
        singular("(hive)s$", "$1");
        singular("(tive)s$", "$1");
        singular("([lr])ves$", "$1f");
        singular("([^aeiouy]|qu)ies$", "$1y");
        singular("(s)eries$", "$1eries");
        singular("(m)ovies$", "$1ovie");
        singular("(x|ch|ss|sh)es$", "$1");
        singular("([m|l])ice$", "$1ouse");
        singular("(bus)es$", "$1");
        singular("(o)es$", "$1");
        singular("(shoe)s$", "$1");
        singular("(cris|ax|test)es$", "$1is");
        singular("(octop|vir)i$", "$1us");
        singular("(alias|status)es$", "$1");
        singular("^(ox)en", "$1");
        singular("(vert|ind)ices$", "$1ex");
        singular("(matr)ices$", "$1ix");
        singular("(quiz)zes$", "$1");

        // irregular
        irregular("person", "people");
        irregular("man", "men");
        irregular("child", "children");
        irregular("sex", "sexes");
        irregular("move", "moves");

        uncountable("equipment");
        uncountable("information");
        uncountable("rice");
        uncountable("money");
        uncountable("species");
        uncountable("series");
        uncountable("fish");
        uncountable("sheep");

        //Collections.reverse(singular);
        //Collections.reverse(plural);
    }


    // CHECKSTYLE:ON

    /**
     * Return true if the word is uncountable.
     *
     * @param word The word
     * @return True if it is uncountable
     */
    public static boolean isUncountable(String word) {
        for (String w : UNCOUNTABLE) {
            if (w.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return the pluralized version of a word.
     *
     * @param word The word
     * @return The pluralized word
     */
    public static String pluralize(String word) {
        if (isUncountable(word)) {
            return word;
        } else {
            for (Inflection inflection : PLURAL) {
                if (inflection.match(word)) {
                    return inflection.replace(word);
                }
            }
            return word;
        }
    }

    /**
     * Return the singularized version of a word.
     *
     * @param word The word
     * @return The singularized word
     */
    public static String singularize(String word) {
        if (isUncountable(word)) {
            return word;
        } else {
            for (Inflection inflection : SINGULAR) {
                if (inflection.match(word)) {
                    return inflection.replace(word);
                }
            }
        }
        return word;
    }

    private static void irregular(String s, String p) {
        plural("(" + s.substring(0, 1) + ")" + s.substring(1) + "$", "$1" + p.substring(1));
        singular("(" + p.substring(0, 1) + ")" + p.substring(1) + "$", "$1" + s.substring(1));
    }

    private static void plural(String pattern, String replacement) {
        PLURAL.add(0, new Inflection(pattern, replacement));
    }

    private static void singular(String pattern, String replacement) {
        SINGULAR.add(0, new Inflection(pattern, replacement));
    }

    private static void uncountable(String word) {
        UNCOUNTABLE.add(word);
    }


    private String pattern;

    private String replacement;

    private boolean ignoreCase;


    Inflection(String pattern) {
        this(pattern, null, true);
    }

    Inflection(String pattern, String replacement) {
        this(pattern, replacement, true);
    }

    Inflection(String pattern, String replacement, boolean ignoreCase) {
        this.pattern = pattern;
        this.replacement = replacement;
        this.ignoreCase = ignoreCase;
    }

    /**
     * Does the given word match?
     *
     * @param word The word
     * @return True if it matches the inflection pattern
     */
    public boolean match(String word) {
        int flags = 0;
        if (ignoreCase) {
            flags = flags | Pattern.CASE_INSENSITIVE;
        }
        return Pattern.compile(pattern, flags).matcher(word).find();
    }

    /**
     * Replace the word with its pattern.
     *
     * @param word The word
     * @return The result
     */
    public String replace(String word) {
        int flags = 0;
        if (ignoreCase) {
            flags = flags | Pattern.CASE_INSENSITIVE;
        }
        return Pattern.compile(pattern, flags).matcher(word).replaceAll(replacement);
    }
}
