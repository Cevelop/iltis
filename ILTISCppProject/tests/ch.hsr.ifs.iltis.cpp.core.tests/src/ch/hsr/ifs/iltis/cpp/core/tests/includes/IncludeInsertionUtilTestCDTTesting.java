package ch.hsr.ifs.iltis.cpp.core.tests.includes;
//package ch.hsr.ifs.iltis.cpp.tests.includes;
//
//import java.util.EnumSet;
//import java.util.Properties;
//
//import org.eclipse.cdt.core.model.CoreModelUtil;
//import org.eclipse.cdt.core.model.ITranslationUnit;
//import org.eclipse.ltk.core.refactoring.TextFileChange;
//import org.junit.Assert;
//import org.junit.Test;
//
//import ch.hsr.ifs.iltis.cpp.includes.IncludeInsertionUtil;
//
//import ch.hsr.ifs.cdttesting.cdttest.base.CDTTestingUITest;
//import ch.hsr.ifs.cdttesting.cdttest.comparison.ASTComparison.ComparisonArg;
//
////TODO remove this and its associated rts file after successful testing with IncludeInsertionUtilTest
//
//public class IncludeInsertionUtilTestCDTTesting extends CDTTestingUITest {
//
//   private String  headerName;
//   private boolean isSystemInclude;
//
//   @Override
//   protected void configureTest(final Properties properties) {
//      headerName = properties.getProperty("headerName");
//      isSystemInclude = Boolean.valueOf(properties.getProperty("systemInclude"));
//      super.configureTest(properties);
//   }
//
//   @Test
//   public void runTest() throws Throwable {
//      ITranslationUnit currentTu = CoreModelUtil.findTranslationUnit(getCurrentIFile(getNameOfPrimaryTestFile()));
//      IncludeInsertionUtil.includeIfNotJetIncluded(currentTu.getAST(), headerName, isSystemInclude, TextFileChange.FORCE_SAVE);
//      /* Doing both comparisons to be sure */
//      fastAssertEquals(getNameOfPrimaryTestFile(), EnumSet.of(ComparisonArg.COMPARE_INCLUDE_DIRECTIVES));
//      Assert.assertEquals(expectedProjectHolder.getDocumentFromRelativePath(getNameOfPrimaryTestFile()).get(), currentProjectHolder
//            .getDocumentFromRelativePath(getNameOfPrimaryTestFile()).get());
//   }
//
//}
