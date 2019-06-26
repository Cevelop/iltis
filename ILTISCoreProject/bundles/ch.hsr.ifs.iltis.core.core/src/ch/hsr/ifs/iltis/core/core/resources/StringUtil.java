package ch.hsr.ifs.iltis.core.core.resources;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.utility.internal.primitive.CharIterableIterate;
import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;

import ch.hsr.ifs.iltis.core.core.functional.functions.Function;


/**
 * A utility class which offers convenience methods for Strings
 *
 * @author tstauber
 */
@NoExtend
@NoInstantiate
public class StringUtil {

    /**
     * A utility class which offers convenience methods for Strings which resemble code
     *
     * @author tstauber
     *
     */
    public static class CodeString {

        /* HTML */

        private static final Map<String, String> LOOKUP_TABLE = new LinkedHashMap<>();

        static {
            LOOKUP_TABLE.put("&", "&amp;");
            LOOKUP_TABLE.put(">", "&gt;");
            LOOKUP_TABLE.put("<", "&lt;");
            LOOKUP_TABLE.put("\"", "&quot;");
            LOOKUP_TABLE.put("'", "&#039;");
            LOOKUP_TABLE.put("\\", "&#092;");
        }

        /**
         * Escapes html-characters in String
         *
         * @author tstauber
         * @param html
         * unescaped html string
         * @return escaped html string
         */
        public final static String escapeHtml(final CharSequence html) {
            String escaped = html.toString();
            for (final String s : LOOKUP_TABLE.keySet()) {
                escaped = escaped.replace(s, LOOKUP_TABLE.get(s));
            }

            return escaped;
        }
    }

    public static boolean equalsOneOf(final CharSequence input, final List<String> compareTo) {
        return compareTo.stream().anyMatch(str -> str.equals(input));
    }

    public static boolean equalsNoneOf(final CharSequence input, final List<String> compareTo) {
        return !equalsOneOf(input, compareTo);
    }

    public static String capitalize(final String word) {
        final char[] charArray = word.toCharArray();
        charArray[0] = Character.toUpperCase(charArray[0]);
        return String.valueOf(charArray);
    }

    /**
     * Encloses the given CharSequence with double-quotes.
     * 
     * @param s
     * The CharSequence to enclose in quotes
     * @return A String representing the given "CharSequence"
     */
    public static String quote(final CharSequence s) {
        return String.format("\"%s\"", s);
    }

    public static String unquote(CharSequence s) {
        if (s != null && StringUtil.startsAndEndsWithQuotes(s)) {
            s = s.subSequence(1, s.length() - 1);
        }
        return s.toString();
    }

    public static <O> String toString(final Iterable<O> it, final String prefix, final String separator, final String postfix,
            final Function<O, String> fun) {
        final StringBuffer buff = new StringBuffer(prefix);
        boolean isFirst = true;
        for (final O o : it) {
            if (!isFirst) buff.append(separator);
            buff.append(fun.apply(o));
            isFirst = false;
        }
        buff.append(postfix);
        return buff.toString();
    }

    /**
     * Creates a formatted string from an iterable.
     * 
     * @param <O>
     * The element-type.
     * @param it
     * The iterable.
     * @param prefix
     * The prefix .
     * @param separator
     * The separator to place between the element-strings.
     * @param postfix
     * The postfix.
     * @param stringGenerator
     * A generator creating a string from an element.
     * @param separatorLinePadding
     * Used for indentation if the stringGenerator produces multi-line strings whose lines should be indented the same as the separator.
     * @return
     * 
     * @since 2.1
     */
    public static <O> String toString(final Iterable<O> it, final String prefix, final String separator, final String postfix,
            final Function<O, String> stringGenerator, final Function<String, String> separatorLinePadding) {
        final StringBuffer buff = new StringBuffer(prefix);
        boolean isFirst = true;
        for (final O o : it) {
            if (!isFirst) buff.append(separator);
            buff.append(preprendPaddingPerLine(stringGenerator.apply(o), l -> "", separatorLinePadding, separatorLinePadding));
            isFirst = false;
        }
        buff.append(postfix);
        return buff.toString();
    }

    public static <O> String toString(final Iterable<O> it) {
        return toString(it, "[", ", ", "]", O::toString);
    }

    public static <O> String toString(final O[] it, final String prefix, final String separator, final String postfix,
            final Function<O, String> fun) {
        final StringBuffer buff = new StringBuffer(prefix);
        boolean isFirst = true;
        for (final O o : it) {
            if (!isFirst) buff.append(separator);
            buff.append(fun.apply(o));
            isFirst = false;
        }
        buff.append(postfix);
        return buff.toString();
    }

    public static <O> String toString(final O[] it) {
        return toString(it, "[", ", ", "]", O::toString);
    }

    public static boolean startsAndEndsWithQuotes(final CharSequence s) {
        return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' || s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'';
    }

    /**
     * Detects if the passed char array contains only whitespace
     * 
     * @param chars
     * The chars to test
     * @return {@code true} iff every char in chars tests positively on {@link Character#isWhitespace(char)}.
     * 
     * @since 1.1
     */
    public static boolean containsOnlyWhitespace(final char[] chars) {
        return CharIterableIterate.allSatisfy(CharLists.immutable.of(chars), c -> Character.isWhitespace(c));
    }

    /**
     * Converts an integer to its English ordinal.
     * 
     * @param i
     * the integer
     * @return The correct English ordinal.
     * 
     * @since 2.1
     */
    public static String toOrdinal(int i) {
        String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
        switch (i % 100) {
        case 11:
        case 12:
        case 13:
            return i + "th";
        default:
            return i + sufixes[i % 10];
        }
    }

    /**
     * Prepends each line in the given input with the padding generated for this line
     * 
     * @param input
     * The input string.
     * 
     * @param firstLinePaddingGenerator
     * The generator used to create the padding for the first line
     * (takes precedence over the suffixPaddingGenerator if only one line is present)
     * @param paddingGenerator
     * The generator to create the padding for this line.
     * @param lastLinePaddingGenerator
     * The generator used to create the padding for the last line
     * (takes precedence over the paddingGenerator if only two lines are present)
     * 
     * @since 2.1
     */
    public static String preprendPaddingPerLine(String input, Function<String, String> firstLinePaddingGenerator,
            Function<String, String> paddingGenerator, Function<String, String> lastLinePaddingGenerator) {
        final StringBuffer buf = new StringBuffer(input.length() * 2);
        final Matcher matcher = Pattern.compile("(?<line>.+)(?<sep>\\R?)").matcher(input);
        boolean first = true;
        do {
            if (matcher.find()) {
                final String line = matcher.group("line");
                final String sep = matcher.group("sep");
                if (first) {
                    /* First (and/or only) line */
                    first = false;
                    buf.append(firstLinePaddingGenerator.apply(line));
                    buf.append(line);
                    buf.append(sep);
                } else if (sep.isEmpty()) {
                    /* Last line */
                    buf.append(lastLinePaddingGenerator.apply(line));
                    buf.append(line);
                } else {
                    /* Intermediate line */
                    buf.append(paddingGenerator.apply(line));
                    buf.append(line);
                    buf.append(sep);
                }
            }
        } while (!matcher.hitEnd());
        return buf.toString();
    }

}
