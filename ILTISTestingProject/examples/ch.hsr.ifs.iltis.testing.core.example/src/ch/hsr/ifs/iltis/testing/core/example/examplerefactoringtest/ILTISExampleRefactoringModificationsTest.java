package ch.hsr.ifs.iltis.testing.core.example.examplerefactoringtest;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.junit.Test;

import ch.hsr.ifs.iltis.testing.core.cdttest.CDTTestingRefactoringTest;
import ch.hsr.ifs.iltis.testing.core.example.examplerefactoringtest.refactorings.ILTISDummyRenameRefactoring;


public class ILTISExampleRefactoringModificationsTest extends CDTTestingRefactoringTest {

   @Override
   protected Refactoring createRefactoring() {
      return new ILTISDummyRenameRefactoring(getPrimaryCElementFromCurrentProject().get(), getSelectionOfPrimaryTestFile(),
            getCurrentCProject());
   }

   @Test
   public void runTest() throws Throwable {
      openPrimaryTestFileInEditor();
      runRefactoringAndAssertSuccess();
   }
}
