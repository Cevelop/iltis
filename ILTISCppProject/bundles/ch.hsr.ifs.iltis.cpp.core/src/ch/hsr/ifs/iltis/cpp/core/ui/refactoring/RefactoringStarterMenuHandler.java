package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


/**
 * A MenuHandler allowing to execute a refactoring witout a wizard directly.
 * 
 * @author tstauber
 *
 * @param <RefactoringType>
 */
public abstract class RefactoringStarterMenuHandler<RefactoringType extends CRefactoring> extends
      WizardRefactoringStarterMenuHandler<NoWizard, RefactoringType> {

   /**
    * This method gets finalized here. There is no need to override in a subtype, as it can't use a wizard.
    */
   @Override
   protected final NoWizard getRefactoringWizard(RefactoringType refactoring) {
      return new NoWizard(refactoring);
   }

   /**
    * This method gets finalized here. There is no need to override in a subtype, as it can't use a wizard.
    */
   @Override
   protected RefactoringWizardOpenOperation getRefactoringOpenOperation(NoWizard wizard) {
      return new RefactoringWizardOpenOperation(wizard);
   }
}
