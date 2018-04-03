package ch.hsr.ifs.iltis.cpp.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblemTypeId;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.util.CModelUtil;
import org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange;
import org.eclipse.cdt.internal.ui.refactoring.utils.SelectionHelper;
import org.eclipse.cdt.ui.CUIPlugin;
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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ResourceChangeChecker;
import org.eclipse.ltk.core.refactoring.participants.ValidateEditChecker;


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

   protected String                  name = "Refactoring";
   protected final ICProject         project;
   protected final ITranslationUnit  tu;
   protected final RefactoringStatus initStatus;
   protected Region                  selectedRegion;
   protected CRefactoringContext     refactoringContext;
   protected ModificationCollector   modificationCollector;

   public CRefactoring(ICElement element, ISelection selection, ICProject project) {
      this.project = project;
      this.initStatus = new RefactoringStatus();
      if (!(element instanceof ISourceReference)) {
         tu = null;
         initStatus.addFatalError("Selection is not valid");
         return;
      }

      ISourceReference sourceRef = (ISourceReference) element;
      tu = CModelUtil.toWorkingCopy(sourceRef.getTranslationUnit());

      if (selection instanceof ITextSelection) {
         selectedRegion = SelectionHelper.getRegion(selection);
      } else {
         try {
            ISourceRange sourceRange = sourceRef.getSourceRange();
            selectedRegion = new Region(sourceRange.getIdStartPos(), sourceRange.getIdLength());
         } catch (CModelException e) {
            CUIPlugin.log(e);
         }
      }
   }

   public ICProject getProject() {
      return project;
   }

   /**
    * Convenience method to get the {@code IProject} associated with this refactorings {@code ICProject}
    */
   public IProject getIProject() {
      return project.getProject();
   }

   public void setContext(CRefactoringContext refactoringContext) {
      Assert.isNotNull(refactoringContext);
      this.refactoringContext = refactoringContext;
   }

   @Override
   public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
      SubMonitor progress = SubMonitor.convert(pm, "Checking preconditions...", 12);

      try {
         CheckConditionsContext context = new CheckConditionsContext();
         context.add(new ValidateEditChecker(getValidationContext()));
         ResourceChangeChecker resourceChecker = new ResourceChangeChecker();
         IResourceChangeDescriptionFactory deltaFactory = resourceChecker.getDeltaFactory();
         context.add(resourceChecker);

         RefactoringStatus result = checkFinalConditions(progress.split(8), context);
         if (result.hasFatalError()) return result;

         modificationCollector = new ModificationCollector(deltaFactory);
         collectModifications(progress.split(2), modificationCollector);

         result.merge(context.check(progress.split(2)));
         return result;
      } finally {
         progress.done();
      }
   }

   protected RefactoringStatus checkFinalConditions(IProgressMonitor subProgressMonitor, CheckConditionsContext checkContext) throws CoreException,
         OperationCanceledException {
      return new RefactoringStatus();
   }

   @Override
   public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
      SubMonitor sm = SubMonitor.convert(pm, 10);
      if (isProgressMonitorCanceled(sm, initStatus)) { return initStatus; }
      sm.subTask("Load Translation Unit");
      IASTTranslationUnit ast = getAST(tu, sm);
      if (ast == null) {
         initStatus.addError(String.format("Unable to parse %s.", tu.getPath()));
         return initStatus;
      }
      if (isProgressMonitorCanceled(sm, initStatus)) { return initStatus; }
      sm.subTask("Check Translation Unit");
      checkAST(ast);
      sm.worked(2);
      sm.subTask("Initialize Refactoring");
      sm.done();
      return initStatus;
   }

   protected static boolean isProgressMonitorCanceled(IProgressMonitor sm, RefactoringStatus status) {
      if (sm.isCanceled()) {
         status.addFatalError("Refactoring canceled by user.");
         return true;
      }
      return false;
   }

   @Override
   public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
      CCompositeChange finalChange = modificationCollector.createFinalChange();
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

   protected IASTTranslationUnit getAST(ITranslationUnit tu, IProgressMonitor pm) throws CoreException, OperationCanceledException {
      return refactoringContext.getAST(tu, pm);
   }

   protected IFile getIFile() {
      return tu.getFile();
   }

   protected IIndex getIndex() throws OperationCanceledException, CoreException {
      return refactoringContext.getIndex();
   }

   protected boolean checkAST(IASTTranslationUnit ast) {
      ProblemFinder problemFinder = new ProblemFinder(initStatus);
      ast.accept(problemFinder);
      return problemFinder.hasProblem();
   }

   protected List<IASTName> findAllMarkedNames(IASTTranslationUnit ast) {
      final List<IASTName> names = new ArrayList<IASTName>();

      ast.accept(new ASTVisitor() {

         {
            shouldVisitNames = true;
         }

         @Override
         public int visit(IASTName name) {
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

      public ProblemFinder(RefactoringStatus status) {
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
      public int visit(IASTProblem problem) {
         addWarningToState();
         return ASTVisitor.PROCESS_CONTINUE;
      }

      @Override
      public int visit(IASTDeclaration declaration) {
         if (declaration instanceof IASTProblemDeclaration) {
            addWarningToState();
         }
         return ASTVisitor.PROCESS_CONTINUE;
      }

      @Override
      public int visit(IASTExpression expression) {
         if (expression instanceof IASTProblemExpression) {
            addWarningToState();
         }
         return ASTVisitor.PROCESS_CONTINUE;
      }

      @Override
      public int visit(IASTStatement statement) {
         if (statement instanceof IASTProblemStatement) {
            addWarningToState();
         }
         return ASTVisitor.PROCESS_CONTINUE;
      }

      @Override
      public int visit(IASTTypeId typeId) {
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

}
