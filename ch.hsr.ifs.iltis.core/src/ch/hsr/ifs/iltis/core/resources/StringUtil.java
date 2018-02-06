package ch.hsr.ifs.iltis.core.resources;

import java.util.LinkedHashMap;
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

}
