package ch.hsr.ifs.iltis.testing.highlevel.testingplugin.example.examplerefactoringtest.refactorings;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;
import ch.hsr.ifs.iltis.cpp.core.wrappers.ModificationCollector;


public class ILTISTestRefactoring extends CRefactoring {

    private boolean collectModificationsCalled;
    private boolean checkInitialConditionsCalled;
    private boolean checkFinalConditionsCalled;

    public ILTISTestRefactoring(ICElement element, Optional<ITextSelection> selection, ICProject project) {
        super(element, selection, project);
    }

    @Override
    protected RefactoringDescriptor getRefactoringDescriptor() {
        return null;
    }

    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
        RefactoringStatus result = super.checkInitialConditions(pm);
        checkInitialConditionsCalled = true;
        return result;
    }

    @Override
    protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException {
        IASTTranslationUnit ast = refactoringContext.getAST(tu, pm);
        collectModificationsCalled = ast != null;
    }

    @Override
    protected RefactoringStatus checkFinalConditions(IProgressMonitor subProgressMonitor, CheckConditionsContext checkContext) throws CoreException,
            OperationCanceledException {
        RefactoringStatus result = super.checkFinalConditions(subProgressMonitor, checkContext);
        checkFinalConditionsCalled = true;
        return result;
    }

    public boolean wasRefactoringSuccessful() {
        return checkInitialConditionsCalled && checkFinalConditionsCalled && collectModificationsCalled;
    }
}
