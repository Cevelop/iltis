package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import org.eclipse.core.runtime.Assert;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;

/**
 * The {@link CRefactoringWizard} ensures that the {@link CRefactoringContext}
 * will get disposed when the Wizard finishes.
 * <p>
 * It provides {@link #getCRefactoring()} and {@link #getCRefactoringContext()}
 * to access the refactoring and its context individually.
 * 
 * @author hpatzen
 * @since 1.1
 */
public abstract class CRefactoringWizard extends RefactoringWizard {

    private final CRefactoringContext fRefactoringContext;

    public CRefactoringWizard(CRefactoring refactoring, int flags) {
        super(refactoring, flags);
        fRefactoringContext = refactoring.getContext();
        Assert.isNotNull(fRefactoringContext);
    }

    public CRefactoringWizard(CRefactoringContext refactoringContext, int flags) {
        super(refactoringContext, flags);
        fRefactoringContext = refactoringContext;
    }

    /**
     * Get the {@link CRefactoringContext} from this Wizard.
     * 
     * Use this instead of {@link #getRefactoringContext()}!
     * 
     * @return the {@link CRefactoringContext}
     */
    public CRefactoringContext getCRefactoringContext() {
        return fRefactoringContext;
    }

    /**
     * Get the {@link CRefactoring} from this Wizard.
     * 
     * Use this instead of {@link #getRefactoring()}!
     * 
     * @return the {@link CRefactoring}
     */
    public CRefactoring getCRefactoring() {
        return fRefactoringContext.getRefactoring();
    }

    @Override
    public boolean performCancel() {
        fRefactoringContext.dispose();
        return super.performCancel();
    }
    
    @Override
    public boolean performFinish() {
        boolean finish = super.performFinish();
        fRefactoringContext.dispose();
        return finish;
    }
}
