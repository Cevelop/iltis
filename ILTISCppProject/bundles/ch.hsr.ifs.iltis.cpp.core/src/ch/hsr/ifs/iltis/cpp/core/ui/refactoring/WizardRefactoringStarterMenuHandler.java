package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.ITranslationUnitHolder;
import org.eclipse.cdt.core.model.IWorkingCopy;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.cdt.ui.ICEditor;
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


/**
 * A MenuHandler allowing to execute a refactoring with a wizard directly.
 * 
 * @author tstauber
 *
 * @param <WizardType>
 * @param <RefactoringType>
 */
public abstract class WizardRefactoringStarterMenuHandler<WizardType extends RefactoringWizard, RefactoringType extends CRefactoring> extends
      RefactoringStarterMenuHandler<RefactoringType> {

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
         if (true /* TODO implement this refactoring.getRefactoringContext() == null */) getRefactoringContext(refactoring);

         int result;
         try {
            result = new RefactoringWizardOpenOperation(getRefactoringWizard(refactoring)).run(parent, refactoring.getName());
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

   protected abstract WizardType getRefactoringWizard(RefactoringType refactoring);

   protected RefactoringWizardOpenOperation getRefactoringOpenOperation(WizardType wizard) {
      return new RefactoringWizardOpenOperation(wizard);
   }

}
