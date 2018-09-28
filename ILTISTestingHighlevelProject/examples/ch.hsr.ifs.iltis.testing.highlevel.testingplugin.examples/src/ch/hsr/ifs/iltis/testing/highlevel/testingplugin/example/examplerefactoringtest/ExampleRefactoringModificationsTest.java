package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplerefactoringtest;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.cdttest.CDTTestingRefactoringTest;
import ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplerefactoringtest.refactorings.DummyRenameRefactoring;


public class ExampleRefactoringModificationsTest extends CDTTestingRefactoringTest {

   private String testSourceFileName = "main.cpp";

   @Override
   protected Refactoring createRefactoring() {
      return new DummyRenameRefactoring(getCurrentCElement(getCurrentIFile(testSourceFileName)).get(), getSelection(testSourceFileName),
            getCurrentCProject());
   }

   @Test
   public void runTest() throws Throwable {
      openTestFileInEditor(testSourceFileName);
      runRefactoringAndAssertSuccess();
   }

}
