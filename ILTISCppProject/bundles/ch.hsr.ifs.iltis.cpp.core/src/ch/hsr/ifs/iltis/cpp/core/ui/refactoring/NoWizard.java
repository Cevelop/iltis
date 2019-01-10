package ch.hsr.ifs.iltis.cpp.core.ui.refactoring;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoringContext;


/**
 * Fake wizard instance. Use this if the refactoring does not need a wizard.
 *
 * @author tstauber
 *
 */
class NoWizard extends CRefactoringWizard {

    public NoWizard(final CRefactoring refactoring) {
        super(refactoring, CHECK_INITIAL_CONDITIONS_ON_OPEN);
    }

    public NoWizard(final CRefactoringContext context) {
        super(context, CHECK_INITIAL_CONDITIONS_ON_OPEN);
    }

    @Override
    protected void addUserInputPages() {
        /* Do nothing to avoid showing */
    }
}
