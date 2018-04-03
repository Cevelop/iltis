package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.ui.refactoring.RefactoringStarter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


/**
 * A wrapper class for the cdt RefactoringRunner. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class RefactoringRunner {

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

   protected final ISelection     selection;
   protected final ICElement      element;
   protected final ICProject      project;
   protected final IShellProvider shellProvider;

   public RefactoringRunner(ICElement element, ISelection selection, IShellProvider shellProvider, ICProject cProject) {
      this.selection = selection;
      this.element = element;
      this.project = cProject;
      this.shellProvider = shellProvider;
   }

   public abstract void run();

   protected void run(RefactoringWizard wizard, CRefactoring refactoring, int saveMode) {
      CRefactoringContext context = new CRefactoringContext(refactoring);
      try {
         RefactoringStarter starter = new RefactoringStarter();
         starter.activate(wizard, shellProvider.getShell(), refactoring.getName(), saveMode);
      } finally {
         context.dispose();
      }
   }
   
}
