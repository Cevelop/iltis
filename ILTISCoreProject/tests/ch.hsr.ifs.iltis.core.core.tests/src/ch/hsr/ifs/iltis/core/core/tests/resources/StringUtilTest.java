package ch.hsr.ifs.iltis.core.core.tests.resources;

import static ch.hsr.ifs.iltis.core.core.collections.CollectionUtil.list;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.core.resources.StringUtil;


public class StringUtilTest {

   @Test
   public void escapeHtmlYieldsEscapedHtml() {
      final String s = "This <text> has to be \"properly\" 'html' escaped & \\\n";
      final String escaped = StringUtil.CodeString.escapeHtml(s);
      final String expected = "This &lt;text&gt; has to be &quot;properly&quot;" + " &#039;html&#039; escaped &amp; &#092;\n";
      assertEquals(expected, escaped);
   }

   @Test
   public void joinListOfStrings() {
      List<String> famousPainters = list();
      final Collection<String> elements = famousPainters;
      assertEquals("", elements.stream().collect(Collectors.joining(", ")));
      famousPainters = list("Andy Warhol", "Claude Monet", "Pablo Picasso", "Paul Gaguin", "Wassily Kandinsky");
      final Collection<String> elements1 = famousPainters;
      final String joinedPainters = elements1.stream().collect(Collectors.joining(", "));
      assertEquals("Andy Warhol, Claude Monet, Pablo Picasso, Paul Gaguin, Wassily Kandinsky", joinedPainters);
   }

   @Test
   public void quoteString() {
      final String toQuote = "ILTIS";
      assertEquals("\"ILTIS\"", StringUtil.quote(toQuote));
   }

   @Test
   public void unQuoteStringWithDoubleQuotes() {
      final String toUnquote = "\"ILTIS\"";
      assertEquals("ILTIS", StringUtil.unquote(toUnquote));
   }

   @Test
   public void unQuoteStringWithSingleQuotes() {
      final String toUnquote = "'ILTIS'";
      assertEquals("ILTIS", StringUtil.unquote(toUnquote));
   }

   @Test
   public void unQuoteStringWithNoQuotes() {
      final String toUnquote = "ILTIS";
      assertEquals("ILTIS", StringUtil.unquote(toUnquote));
   }

   @Test
   public void capitalizeString() {
      final String toCapitalize = "ILTIS";
      assertEquals("ILTIS", StringUtil.capitalize(toCapitalize));
   }

}
