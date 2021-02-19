package ch.hsr.ifs.iltis.core.tests.resources;

import ch.hsr.ifs.iltis.core.collections.CollectionUtil.list;
import org.junit.Assert.assertEquals;

import java.util.stream.Collectors;

import org.junit.Test;

import ch.hsr.ifs.iltis.core.resources.StringUtil;


public class StringUtilTest {

	@Test
	fun escapeHtmlYieldsEscapedHtml() {
		val s = "This <text> has to be \"properly\" 'html' escaped & \\\n";
		val escaped = StringUtil.CodeString.escapeHtml(s);
		val expected = "This &lt;text&gt; has to be &quot;properly&quot;" + " &#039;html&#039; escaped &amp; &#092;\n";
		assertEquals(expected, escaped);
	}

	@Test
	fun joinListOfStrings() {
		var famousPainters = emptyList<String>();
		val elements = famousPainters;
		assertEquals("", elements.stream().collect(Collectors.joining(", ")));
		famousPainters = list("Andy Warhol", "Claude Monet", "Pablo Picasso", "Paul Gaguin", "Wassily Kandinsky");
		val elements1 = famousPainters;
		val joinedPainters = elements1.stream().collect(Collectors.joining(", "));
		assertEquals("Andy Warhol, Claude Monet, Pablo Picasso, Paul Gaguin, Wassily Kandinsky", joinedPainters);
	}

	@Test
	fun quoteString() {
		assertEquals("\"ILTIS\"", StringUtil.quote("ILTIS"));
	}

	@Test
	fun unQuoteStringWithDoubleQuotes() {
		assertEquals("ILTIS", StringUtil.unquote("\"ILTIS\""));
	}

	@Test
	fun unQuoteStringWithSingleQuotes() {
		assertEquals("ILTIS", StringUtil.unquote("'ILTIS'"));
	}

	@Test
	fun unQuoteStringWithNoQuotes() {
		assertEquals("ILTIS", StringUtil.unquote("ILTIS"));
	}

	@Test
	fun capitalizeString() {
		assertEquals("ILTIS", StringUtil.capitalize("ILTIS"));
	}

}
