package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplerefactoringtest;

import static org.junit.Assert.assertTrue;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplerefactoringtest.refactorings.TestRefactoring;


public class ExampleRefactoringTest extends CDTTestingRefactoringTest {

   private TestRefactoring testRefactoring;

   @Override
   protected Refactoring createRefactoring() {
      testRefactoring = new TestRefactoring(getCurrentCElement("XY.cpp").get(), getSelection("XY.cpp").get(), getCurrentCProject());
      return testRefactoring;
   }

   @Test
   public void runTest() throws Throwable {
      openTestFileInEditor("XY.cpp");
      runRefactoringAndAssertSuccess();
      assertTrue(testRefactoring.wasRefactoringSuccessful());
      // calling the following instead of assertRefactoringSuccess() will/would fail
      // this test (because the TestRefactoring does not fail/throws exception etc.)
      // assertRefactoringFailure();
   }

}
