package ch.hsr.ifs.iltis.testing.highlevel.example.examplecodantest;

import java.util.EnumSet;

import org.eclipse.ui.IMarkerResolution;
import org.junit.Test;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;

import ch.hsr.ifs.iltis.testing.highlevel.cdttest.CDTTestingQuickfixTest;
import ch.hsr.ifs.iltis.testing.highlevel.cdttest.comparison.ASTComparison.ComparisonArg;
import ch.hsr.ifs.iltis.testing.highlevel.example.examplecodantest.MyCodanChecker.MyProblemId;


public class ExampleCodanQuickFixTest extends CDTTestingQuickfixTest {

   @Override
   protected IProblemId getProblemId() {
      return MyProblemId.EXAMPLE_ID;
   }

   @Test
   public void runTest() throws Throwable {
      runQuickfixForAllMarkersAndAssertAllEqual();
   }

   @Override
   protected EnumSet<ComparisonArg> makeComparisonArguments() {
      return EnumSet.of(ComparisonArg.USE_SOURCE_COMPARISON);
   }

   @Override
   protected IMarkerResolution createMarkerResolution() {
      return new MyQuickFix();
   }
}
