package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import java.util.Optional;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnitHolder;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.cdt.ui.ICEditor;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ltk.ui.refactoring.RefactoringWizardOpenOperation;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import ch.hsr.ifs.iltis.core.core.functional.OptionalUtil;
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
public abstract class WizardRefactoringStarterMenuHandler<WizardType extends RefactoringWizard, RefactoringType extends CRefactoring> extends
      AbstractHandler {

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
      Shell parent = HandlerUtil.getActiveShell(event);

      RefactoringSaveHelper saveHelper = new RefactoringSaveHelper(saveMode.saveModeConstant);
      if (!saveHelper.saveEditors(parent)) return null;

      IEditorPart editor = HandlerUtil.getActiveEditor(event);
      if (editor == null || !(editor instanceof ICEditor) || !(editor instanceof ITranslationUnitHolder)) return null;

      ITranslationUnit tu = ((ITranslationUnitHolder) editor).getTranslationUnit();

      if (tu != null && tu instanceof IWorkingCopy) {
         RefactoringType refactoring = getRefactoring((IWorkingCopy) tu, OptionalUtil.of(((ITextEditor) editor).getSelectionProvider()).map(
               ISelectionProvider::getSelection).mapAs(ITextSelection.class).get());

         if (refactoring.getContext() == null) getRefactoringContext(refactoring);

         try {
            final int result = getRefactoringOpenOperation(getRefactoringWizard(refactoring)).run(parent, refactoring.getName());
            switch (result) {
            case IDialogConstants.CANCEL_ID:
            case RefactoringWizardOpenOperation.INITIAL_CONDITION_CHECKING_FAILED:
               ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
            }
         } catch (InterruptedException | CoreException e) {
            e.printStackTrace();
         }
      }
      return null;
   }

   /**
    * Gets the refactoring. The project can be created by calling {@code element.getCProject()}.
    * 
    * @param element
    *        The ICElement on which to execute the refactoring.
    * @param selection
    *        The active selection while starting the refactoring if present. Else Optional.empty().
    * @return The refactoring.
    */
   protected abstract RefactoringType getRefactoring(ICElement element, Optional<ITextSelection> selection);

   /**
    * If a special refactoring context should be used, it can be created here.
    * 
    * @param refactoring
    *        The refactoring on which to set the context.
    * @return A CRefactoringContext with the refactoring already set.
    */
   protected CRefactoringContext getRefactoringContext(RefactoringType refactoring) {
      return new CRefactoringContext(refactoring);
   }

   /**
    * Creates the refactoring-wizard. Can return a {@link NoWizard}.
    * 
    * @param refactoring
    *        The refactoring to create the wizard for.
    * @return A refactoring wizard
    */
   protected abstract WizardType getRefactoringWizard(RefactoringType refactoring);

   /**
    * If the refactoring open operation should be modified, this can be done in this hook method.
    * 
    * @param wizard
    * @return
    */
   protected RefactoringWizardOpenOperation getRefactoringOpenOperation(WizardType wizard) {
      return new RefactoringWizardOpenOperation(wizard);
   }
}
