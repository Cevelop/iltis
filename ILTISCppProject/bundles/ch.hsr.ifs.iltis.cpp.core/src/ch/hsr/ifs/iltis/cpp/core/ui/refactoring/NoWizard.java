package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringContext;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;


/**
 * Fake wizard instance. Use this if the refactoring does not need a wizard.
 *
 * @author tstauber
 *
 */
class NoWizard extends RefactoringWizard {

    public NoWizard(final Refactoring refactoring) {
        super(refactoring, CHECK_INITIAL_CONDITIONS_ON_OPEN);
    }

    public NoWizard(final RefactoringContext context) {
        super(context, CHECK_INITIAL_CONDITIONS_ON_OPEN);
    }

    @Override
    protected void addUserInputPages() {
        /* Do nothing to avoid showing */
    }

}
