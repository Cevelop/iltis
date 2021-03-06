package ch.hsr.ifs.iltis.cpp.core.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblemTypeId;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.util.CModelUtil;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.ltk.core.refactoring.participants.ValidateEditChecker;

import ch.hsr.ifs.iltis.core.ILTIS;
import ch.hsr.ifs.iltis.core.functional.OptionalUtil;


/**
 * A wrapper class for the cdt CRefactoring. Using this wrapper reduces the amount of warnings respectively the amount of {@code @SuppressWarnings}
 * tags
 * <p>
 * Mostly copied from the cdt CRefactorinContext
 *
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public abstract class CRefactoring extends Refactoring {

    protected String                   name = "Refactoring";
    protected final ICProject          project;
    protected final ITranslationUnit   tu;
    protected final RefactoringStatus  initStatus;
    protected Region                   selectedRegion;
    protected Optional<ITextSelection> selection;
    protected CRefactoringContext      refactoringContext;
    protected ModificationCollector    modificationCollector;

    /**
     * Constructor for the abstract CRefactoring. Replaces the constructor {@link #CRefactoring(ICElement, Optional, ICProject)}.
     * 
     * @since 1.1
     */
    public CRefactoring(final ICElement element, final Optional<ITextSelection> selection) {
        this(element, selection, element.getCProject());
    }

    /**
     * @deprecated use {@link #CRefactoring(ICElement, Optional)} instead!
     * TODO remove after full transition
     * @param element
     * @param selection
     * @param project
     */
    @Deprecated
    public CRefactoring(final ICElement element, final Optional<ITextSelection> selection, final ICProject project) {
        this.project = project;
        initStatus = new RefactoringStatus();
        if (!(element instanceof ISourceReference)) {
            tu = null;
            initStatus.addFatalError("Selection is not valid");
            return;
        }

        final ISourceReference sourceRef = (ISourceReference) element;
        tu = CModelUtil.toWorkingCopy(sourceRef.getTranslationUnit());

        this.selection = selection;
        initSelectedRegion(selection, sourceRef);

    }

    private void initSelectedRegion(final Optional<ITextSelection> selection, final ISourceReference sourceRef) {
        OptionalUtil.of(selection).ifPresent(sel -> {
            selectedRegion = new Region(sel.getOffset(), sel.getLength());
        }).ifNotPresent(() -> {
            try {
                final ISourceRange sourceRange = sourceRef.getSourceRange();
                selectedRegion = new Region(sourceRange.getIdStartPos(), sourceRange.getIdLength());
            } catch (final CModelException e) {
                ILTIS.log(e);
            }
        });
    }

    public void setContext(final CRefactoringContext refactoringContext) {
        Assert.isNotNull(refactoringContext);
        this.refactoringContext = refactoringContext;
    }

    /**
     * Returns the context in which this refactoring is executed.
     * 
     * @since 1.1
     */
    public CRefactoringContext getContext() {
        return refactoringContext;
    }

    @Override
    public RefactoringStatus checkFinalConditions(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
        final SubMonitor progress = SubMonitor.convert(pm, "Checking preconditions...", 12);

        try {
            final CheckConditionsContext context = new CheckConditionsContext();
            context.add(new ValidateEditChecker(getValidationContext()));
            final ResourceChangeChecker resourceChecker = new ResourceChangeChecker();
            final IResourceChangeDescriptionFactory deltaFactory = resourceChecker.getDeltaFactory();
            context.add(resourceChecker);

            final RefactoringStatus result = checkFinalConditions(progress.split(8), context);
            if (result.hasFatalError()) return result;

            modificationCollector = new ModificationCollector(deltaFactory);
            collectModifications(progress.split(2), modificationCollector);

            result.merge(context.check(progress.split(2)));
            return result;
        } finally {
            progress.done();
        }
    }

    protected RefactoringStatus checkFinalConditions(final IProgressMonitor subProgressMonitor, final CheckConditionsContext checkContext)
            throws CoreException, OperationCanceledException {
        return new RefactoringStatus();
    }

    @Override
    public RefactoringStatus checkInitialConditions(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
        final SubMonitor sm = SubMonitor.convert(pm, 10);
        if (isProgressMonitorCanceled(sm, initStatus)) {
            return initStatus;
        }
        sm.subTask("Load Translation Unit");
        final IASTTranslationUnit ast = getAST(tu, sm);
        if (ast == null) {
            initStatus.addError(String.format("Unable to parse %s.", tu.getPath()));
            return initStatus;
        }
        if (isProgressMonitorCanceled(sm, initStatus)) {
            return initStatus;
        }
        sm.subTask("Check Translation Unit");
        checkAST(ast);
        sm.worked(2);
        sm.subTask("Initialize Refactoring");
        sm.done();
        return initStatus;
    }

    protected static boolean isProgressMonitorCanceled(final IProgressMonitor sm, final RefactoringStatus status) {
        if (sm.isCanceled()) {
            status.addFatalError("Refactoring canceled by user.");
            return true;
        }
        return false;
    }

    @Override
    public Change createChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
        final CCompositeChange finalChange = modificationCollector.createFinalChange();
        finalChange.setDescription(new RefactoringChangeDescriptor(getRefactoringDescriptor()));
        return finalChange;
    }

    abstract protected RefactoringDescriptor getRefactoringDescriptor();

    abstract protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException,
            OperationCanceledException;

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the translation unit where the refactoring started.
     */
    public ITranslationUnit getTranslationUnit() {
        return tu;
    }

    protected IASTTranslationUnit getAST(final ITranslationUnit tu, final IProgressMonitor pm) throws CoreException, OperationCanceledException {
        return refactoringContext.getAST(tu, pm);
    }

    protected IFile getIFile() {
        return tu.getFile();
    }

    protected IIndex getIndex() throws OperationCanceledException, CoreException {
        return refactoringContext.getIndex();
    }

    protected boolean checkAST(final IASTTranslationUnit ast) {
        final ProblemFinder problemFinder = new ProblemFinder(initStatus);
        ast.accept(problemFinder);
        return problemFinder.hasProblem();
    }

    protected List<IASTName> findAllMarkedNames(final IASTTranslationUnit ast) {
        final List<IASTName> names = new ArrayList<>();

        ast.accept(new ASTVisitor() {

            {
                shouldVisitNames = true;
            }

            @Override
            public int visit(final IASTName name) {
                if (name.isPartOfTranslationUnitFile() && SelectionHelper.doesNodeOverlapWithRegion(name, selectedRegion) &&
                    !(name instanceof ICPPASTQualifiedName)) {
                    names.add(name);
                }
                return super.visit(name);
            }
        });
        return names;
    }

    private class ProblemFinder extends ASTVisitor {

        private boolean                 problemFound = false;
        private final RefactoringStatus status;

        public ProblemFinder(final RefactoringStatus status) {
            this.status = status;
        }

        {
            shouldVisitProblems = true;
            shouldVisitDeclarations = true;
            shouldVisitExpressions = true;
            shouldVisitStatements = true;
            shouldVisitTypeIds = true;
        }

        @Override
        public int visit(final IASTProblem problem) {
            addWarningToState();
            return ASTVisitor.PROCESS_CONTINUE;
        }

        @Override
        public int visit(final IASTDeclaration declaration) {
            if (declaration instanceof IASTProblemDeclaration) {
                addWarningToState();
            }
            return ASTVisitor.PROCESS_CONTINUE;
        }

        @Override
        public int visit(final IASTExpression expression) {
            if (expression instanceof IASTProblemExpression) {
                addWarningToState();
            }
            return ASTVisitor.PROCESS_CONTINUE;
        }

        @Override
        public int visit(final IASTStatement statement) {
            if (statement instanceof IASTProblemStatement) {
                addWarningToState();
            }
            return ASTVisitor.PROCESS_CONTINUE;
        }

        @Override
        public int visit(final IASTTypeId typeId) {
            if (typeId instanceof IASTProblemTypeId) {
                addWarningToState();
            }
            return ASTVisitor.PROCESS_CONTINUE;
        }

        public boolean hasProblem() {
            return problemFound;
        }

        private void addWarningToState() {
            if (!problemFound) {
                status.addWarning(
                        "The translation unit contains one or several problems. This can be caused by a syntax error in the code or a parser flaw. The refactoring will possibly fail.");
                problemFound = true;
            }
        }
    }

    /* V Utility Methods V */

    protected Optional<IASTNode> findSelectedNodeExactly(final Optional<ITextSelection> selection) {
        return OptionalUtil.of(selection).ifPresent(sel -> getAST(tu, null).getNodeSelector(null).findNode(sel.getOffset(), sel.getLength()),
                it -> null).get();
    }

    protected Optional<IASTName> findSelectedNameExactly(final Optional<ITextSelection> selection) {
        return OptionalUtil.of(selection).ifPresent(sel -> getAST(tu, null).getNodeSelector(null).findName(sel.getOffset(), sel.getLength()),
                it -> null).get();
    }

    protected Optional<IASTName> findFirstEnclosingName(final Optional<ITextSelection> selection) {
        return OptionalUtil.of(selection).ifPresent(sel -> getAST(tu, null).getNodeSelector(null).findEnclosingName(sel.getOffset(), sel.getLength()),
                it -> null).get();
    }

    protected Optional<IASTNode> findFirstEnclosingNode(final Optional<ITextSelection> selection) {
        return OptionalUtil.of(selection).ifPresent(sel -> getAST(tu, null).getNodeSelector(null).findEnclosingNode(sel.getOffset(), sel.getLength()),
                it -> null).get();
    }

    protected Optional<IASTName> findFirstEnclosedName(final Optional<ITextSelection> selection) {
        return OptionalUtil.of(selection).ifPresent(sel -> getAST(tu, null).getNodeSelector(null).findFirstContainedName(sel.getOffset(), sel
                .getLength()), it -> null).get();
    }

    protected Optional<IASTNode> findFirstEnclosedNode(final Optional<ITextSelection> selection) {
        return OptionalUtil.of(selection).ifPresent(sel -> getAST(tu, null).getNodeSelector(null).findFirstContainedNode(sel.getOffset(), sel
                .getLength()), it -> null).get();
    }

    public Optional<ICPPASTCompositeTypeSpecifier> findFirstEnclosingClass(final Optional<ITextSelection> selection) {
        return findFirstEnclosingNode(selection).flatMap(n -> CPPVisitor.findAncestorWithType(n, ICPPASTCompositeTypeSpecifier.class));
    }

    public Optional<ICPPASTCompositeTypeSpecifier> findFirstEnclosedClass(final Optional<ITextSelection> selection) {
        return findFirstEnclosingNode(selection).flatMap(n -> CPPVisitor.findChildWithType(n, ICPPASTCompositeTypeSpecifier.class));
    }

    public ICProject getProject() {
        return project;
    }

    public CRefactoringContext getRefactoringContext() {
        return refactoringContext;
    }

    /**
     * Convenience method to get the {@code IProject} associated with this refactorings {@code ICProject}
     */
    public IProject getIProject() {
        return project.getProject();
    }
}
