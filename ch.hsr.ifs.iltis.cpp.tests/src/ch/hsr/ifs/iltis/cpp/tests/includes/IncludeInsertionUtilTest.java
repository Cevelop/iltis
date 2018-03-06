package ch.hsr.ifs.iltis.cpp.tests.includes;

import java.util.EnumSet;
import java.util.Properties;

import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.junit.Assert;
import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.includes.IncludeInsertionUtil;

import ch.hsr.ifs.cdttesting.cdttest.CDTTestingTest;
import ch.hsr.ifs.cdttesting.cdttest.comparison.ASTComparison.ComparisonArg;


public class IncludeInsertionUtilTest extends CDTTestingTest {

   private String  headerName;
   private boolean isSystemInclude;

   @Override
   protected void configureTest(final Properties properties) {
      headerName = properties.getProperty("headerName");
      isSystemInclude = Boolean.valueOf(properties.getProperty("systemInclude"));
      super.configureTest(properties);
   }

   @Test
   public void runTest() throws Throwable {
      IncludeInsertionUtil.includeIfNotJetIncluded(getCurrentASTOfActiveFile(), headerName, isSystemInclude, TextFileChange.FORCE_SAVE);
      /* Doing both comparisons to be sure */
      fastAssertEquals(activeFileName, EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES));
      Assert.assertEquals(getExpectedSource(), getCurrentSource());
   }

}
