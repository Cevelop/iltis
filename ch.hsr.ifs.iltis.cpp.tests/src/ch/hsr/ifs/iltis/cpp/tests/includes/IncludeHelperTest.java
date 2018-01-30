package ch.hsr.ifs.iltis.cpp.tests.includes;

import java.util.Properties;

import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.includes.IncludeHelper;

import ch.hsr.ifs.cdttesting.cdttest.CDTTestingTest;


public class IncludeHelperTest extends CDTTestingTest {

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
      IncludeHelper.createIncludeIfNotJetIncluded(getCurrentAST(), headerName, isSystemInclude);
      assertEqualsAST(getExpectedAST(), getCurrentAST());
   }

}
