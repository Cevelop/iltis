package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;


/**
 * A MenuHandler allowing to execute a refactoring with a wizard directly.
 * 
 * @author tstauber
 *
 * @param <WizardType>
 * @param <RefactoringType>
 */
public abstract class RefactoringStarterMenuHandler<RefactoringType extends CRefactoring> extends AbstractHandler {

   public enum RefactoringSaveMode {
      /**
       * Save mode to save all dirty editors (always ask).
       */
      SAVE_ALL_ALWAYS_ASK(1),
      /**
       * Save mode to save all dirty editors.
       */
      SAVE_ALL(2),
      /**
       * Save mode to not save any editors.
       */
      SAVE_NOTHING(3),
      /**
       * Save mode to save all editors that are known to cause trouble for C refactorings, e.g.
       * editors on compilation units that are not in working copy mode.
       */
      SAVE_REFACTORING(4);

      public final int saveModeConstant;

      RefactoringSaveMode(int bridgeConstant) {
         this.saveModeConstant = bridgeConstant;
      }
   }

   protected RefactoringSaveMode saveMode = RefactoringSaveMode.SAVE_REFACTORING;

   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      //TODO implement execution of refactoring using 
      return null;
   }

   protected abstract RefactoringType getRefactoring(ICElement element, Optional<ITextSelection> selection);

   protected CRefactoringContext getRefactoringContext(RefactoringType refactoring) {
      return new CRefactoringContext(refactoring);
   }
}
