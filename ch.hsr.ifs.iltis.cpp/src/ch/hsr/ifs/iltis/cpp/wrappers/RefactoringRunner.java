package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.refactoring.CRefactoring;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


/**
 * A wrapper class for the cdt RefactoringRunner. Using this wrapper reduces the amount of warnings respectively the amount of {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class RefactoringRunner extends org.eclipse.cdt.internal.ui.refactoring.RefactoringRunner {

   /* Copied from RefactoringSaveHelper to avoid API problems */

   /**
    * Save mode to save all dirty editors (always ask).
    */
   public static final int SAVE_ALL_ALWAYS_ASK = 1;

   /**
    * Save mode to save all dirty editors.
    */
   public static final int SAVE_ALL = 2;

   /**
    * Save mode to not save any editors.
    */
   public static final int SAVE_NOTHING = 3;

   /**
    * Save mode to save all editors that are known to cause trouble for C
    * refactorings, e.g. editors on compilation units that are not in working
    * copy mode.
    */
   public static final int SAVE_REFACTORING = 4;

   public RefactoringRunner(final ICElement element, final ISelection selection, final IShellProvider shellProvider, final ICProject cProject) {
      super(element, selection, shellProvider, cProject);
   }

   protected ICElement getElement() {
      return element;
   }

   protected ISelection getSelection() {
      return selection;
   }

   protected ICProject getProject() {
      return project;
   }

   protected IShellProvider getShellProvider() {
      return shellProvider;
   }

   /* Wraps the final run method. */
   protected void runWithContext(final RefactoringWizard wizard, final CRefactoring refactoring, final int saveMode) {
      run(wizard, refactoring, saveMode);
   }
}
