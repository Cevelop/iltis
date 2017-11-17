package ch.hsr.ifs.iltis.core.tests.resources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.resources.StringUtil;


public class StringUtilTest {

   @Test
   public void escapeHtmlYieldsEscapedHtml() {
      final String s = "This <text> has to be \"properly\" 'html' escaped & \\\n";
      final String escaped = StringUtil.CodeString.escapeHtml(s);
      final String expected = "This &lt;text&gt; has to be &quot;properly&quot;" + " &#039;html&#039; escaped &amp; &#092;\n";
      assertEquals(expected, escaped);
   }
}
