package ch.hsr.ifs.iltis.testing.core.example.examplecodantest;

import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.core.cdttest.CDTTestingCheckerTest;
import ch.hsr.ifs.iltis.testing.core.example.examplecodantest.MyCodanChecker.MyProblemId;


public class ExampleCodanCheckerTest extends CDTTestingCheckerTest {

   @Override
   protected IProblemId getProblemId() {
      return MyProblemId.EXAMPLE_ID;
   }

   @Test
   public void runTest() throws Throwable {
      int markerExpectedOnLine = 1;
      assertMarkerLines(markerExpectedOnLine);
      assertMarkerMessages(new String[] { "Declaration 'main' is wrong." });
   }
}
