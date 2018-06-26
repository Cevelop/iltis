package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.tests.cdttest.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.comparison.ASTComparison;



/**
 * @author tstauber
 *
 *         Tests for RTS normalizer to strip formatting differences
 *
 */
public class NormalizerTest {

   @Test
   public void leadingLineBreaks() {
      // @formatter:off
		final String input = "\n\n\n#include <cstdint>";

		final String should = "#include <cstdint>";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

   @Test
   public void trailingLineBreaks() {
      // @formatter:off
		final String input = "#include <cstdint>\n\n\n";

		final String should = "#include <cstdint>";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

   @Test
   public void shortenMultipleSpaces() {
      // @formatter:off
		final String input = "int      foo   {42};";

		final String should = "int foo {42};";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

   @Test
   public void removeLeadingSpaces() {
      // @formatter:off
		final String input = "    int foo {42};";

		final String should = "int foo {42};";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

   @Test
   public void removeTrailingSpaces() {
      // @formatter:off
		final String input = "int foo {42};    ";

		final String should = "int foo {42};";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

   @Test
   public void replaceLineBreaks() {
      // @formatter:off
		final String input = "#include <cstdint>\n" + "int main()  {\r\n" + "           int foo = 42; \n\n\n" + "}";

		final String should = "#include <cstdint>\n" + "int main() {\n" + "int foo = 42;\n" + "}";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

   @Test
   public void removeRTSComments() {
      // @formatter:off
		final String input = "#include <cstdint>\n" + "/*TODO write more tests */";

		final String should = "#include <cstdint>";
		// @formatter:on

      assertEquals(should, ASTComparison.normalizeCPP(input));
   }

}
