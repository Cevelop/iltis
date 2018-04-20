package ch.hsr.ifs.iltis.core.resources;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.hsr.ifs.iltis.core.functional.functions.Function;


/**
 * A utility class which offers convenience methods for Strings
 * 
 * @author tstauber
 *
 */
public abstract class StringUtil {

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
       *        unescaped html string
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

   public static boolean equalsOneOf(CharSequence input, List<String> compareTo) {
      return compareTo.stream().anyMatch(str -> str.equals(input));
   }

   public static boolean equalsNoneOf(CharSequence input, List<String> compareTo) {
      return !equalsOneOf(input, compareTo);
   }

   public static String capitalize(final String word) {
      final char[] charArray = word.toCharArray();
      charArray[0] = Character.toUpperCase(charArray[0]);
      return String.valueOf(charArray);
   }

   public static String quote(final CharSequence s) {
      return String.format("\"%s\"", s);
   }

   public static String unquote(CharSequence s) {
      if (s != null && StringUtil.startsAndEndsWithQuotes(s)) {
         s = s.subSequence(1, s.length() - 1);
      }
      return s.toString();
   }

   public static <O> String toString(Iterable<O> it, String prefix, String separator, String postfix, Function<O, String> fun) {
      StringBuffer buff = new StringBuffer(prefix);
      boolean isFirst = true;
      for (O o : it) {
         if (!isFirst) buff.append(separator);
         buff.append(fun.apply(o));
         isFirst = false;
      }
      buff.append(postfix);
      return buff.toString();
   }

   public static <O> String toString(Iterable<O> it) {
      return toString(it, "[", ", ", "]", O::toString);
   }

   public static <O> String toString(O[] it, String prefix, String separator, String postfix, Function<O, String> fun) {
      StringBuffer buff = new StringBuffer(prefix);
      boolean isFirst = true;
      for (O o : it) {
         if (!isFirst) buff.append(separator);
         buff.append(fun.apply(o));
         isFirst = false;
      }
      buff.append(postfix);
      return buff.toString();
   }

   public static <O> String toString(O[] it) {
      return toString(it, "[", ", ", "]", O::toString);
   }

   public static boolean startsAndEndsWithQuotes(final CharSequence s) {
      return s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' || s.charAt(0) == '\'' && s.charAt(s.length() - 1) == '\'';
   }

}
