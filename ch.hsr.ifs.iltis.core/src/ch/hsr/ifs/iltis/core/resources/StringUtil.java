package ch.hsr.ifs.iltis.core.resources;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class StringUtil {

   public static class CodeString {

      /**
       * Normalizes the passed {@link String} by removing all testeditor-comments,
       * removing leading/trailing whitespace and line-breaks, replacing all
       * remaining line-breaks by ↵ and reducing all groups of whitespace to a
       * single space.
       *
       * @author tstauber
       *
       * @param in
       *            The {@link String} that should be normalized.
       *
       * @return A normalized copy of the parameter in.
       **/
      public static String normalize(final String in) {
         //@formatter:off
         return in.replaceAll("/\\*.*\\*/", "")								//Remove all test-editor-comments
                  .replaceAll("(^((\\r?\\n)|\\s)*|((\\r?\\n)|\\s)*$)", "")	//Remove all leading and trailing linebreaks/whitespace
                  .replaceAll("\\s*(\\r?\\n)+\\s*", "↵")						//Replace all linebreaks with linebreak-symbol
                  .replaceAll("\\s+", " ");									//Reduce all groups of whitespace to a single space
         //@formatter:on
      }

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
