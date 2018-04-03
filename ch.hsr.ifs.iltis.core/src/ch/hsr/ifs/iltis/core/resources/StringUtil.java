package ch.hsr.ifs.iltis.core.resources;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


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
      public final static String escapeHtml(final String html) {
         String escaped = new String(html);
         for (final String s : LOOKUP_TABLE.keySet()) {
            escaped = escaped.replace(s, LOOKUP_TABLE.get(s));
         }

         return escaped;
      }
   }

   public static boolean equalsOneOf(String input, List<String> compareTo) {
      return compareTo.stream().anyMatch(str -> str.equals(input));
   }

   public static boolean equalsNoneOf(String input, List<String> compareTo) {
      return !equalsOneOf(input, compareTo);
   }

   public static String capitalize(final String word) {
      final char[] charArray = word.toCharArray();
      charArray[0] = Character.toUpperCase(charArray[0]);
      return String.valueOf(charArray);
   }

   public static String quote(final String s) {
      return String.format("\"%s\"", s);
   }

   public static String unquote(String s) {
      if (s != null && StringUtil.startsAndEndsWithQuotes(s)) {
         s = s.substring(1, s.length() - 1);
      }
      return s;
   }

   public static String toString(Iterable<?> it) {
      StringBuffer buff = new StringBuffer("[");
      boolean isFirst = true;
      for (Object o : it) {
         if (isFirst) buff.append(", ");
         buff.append(o.toString());
         isFirst = false;
      }
      buff.append("]");
      return buff.toString();
   }

   public static boolean startsAndEndsWithQuotes(final String s) {
      return s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") && s.endsWith("'");
   }

}
