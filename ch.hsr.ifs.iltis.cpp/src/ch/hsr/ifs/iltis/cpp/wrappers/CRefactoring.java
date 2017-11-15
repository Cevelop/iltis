package ch.hsr.ifs.iltis.cpp.wrappers;

import java.lang.reflect.Field;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.ui.refactoring.changes.CCompositeChange;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.mapping.IResourceChangeDescriptionFactory;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;

/**
 * @author tstauber
 */
@SuppressWarnings("restriction")
public abstract class CRefactoring extends org.eclipse.cdt.internal.ui.refactoring.CRefactoring {
   protected ModificationCollector modificationCollectorWrapper;

   /**
    * {@inheritDoc}
    */
   public CRefactoring(final ICElement element, final ISelection selection, final ICProject project) {
      super(element, selection, project);
   }


   /**
    * Wrapper method which uses a ILTIS CRefactoringContext
    */
   public void setContext(final CRefactoringContext refactoringContext) {
      Assert.isNotNull(refactoringContext);
      this.refactoringContext = refactoringContext;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected RefactoringStatus checkFinalConditions(final IProgressMonitor subProgressMonitor, final CheckConditionsContext checkContext)
            throws CoreException, OperationCanceledException {
      return super.checkFinalConditions(subProgressMonitor, checkContext);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public RefactoringStatus checkInitialConditions(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
      return super.checkInitialConditions(pm);
   }

   /**
    * Uses reflection to
    *
    * @return
    */
   protected ModificationCollector getModificationCollector() {
      return modificationCollectorWrapper;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Change createChange(final IProgressMonitor pm) throws CoreException, OperationCanceledException {
      final SubMonitor subMonitor = SubMonitor.convert(pm, "Creating change", 10);
      final CCompositeChange finalChange = modificationCollectorWrapper.createFinalChange();
      subMonitor.worked(3);
      finalChange.setDescription(new RefactoringChangeDescriptor(getRefactoringDescriptor()));
      subMonitor.worked(7);
      return finalChange;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return super.getName();
   }

   /**
    * {@inheritDoc}
    */
   public IRegion getSelectedRegion() {
      return super.selectedRegion;
   }

   /**
    * {@inheritDoc}
    */
   public ICProject getProject() {
      return super.project;
   }

   /**
    * Convenience method to get the {@code IProject} associated with this refactorings {@code ICProject}
    */
   public IProject getIProject() {
      return super.project.getProject();
   }

   /**
    * Wrapper method to get the {@code RefactoringStatus} of the {@code CRefactoring}.
    */
   public RefactoringStatus initStatus() {
      return super.initStatus;
   }

   /**
    * Wrapper method to get the {@code RefactoringContext} on which the {@code CRefactoring} operates.
    */
   public CRefactoringContext refactoringContext() {
      if (!(super.refactoringContext instanceof CRefactoringContext)) {
         super.refactoringContext = new CRefactoringContext(super.refactoringContext);
      }
      return (CRefactoringContext) super.refactoringContext;
   }

   /**
    * Wrapper method to get the {@code Region} on which the {@code CRefactoring} operates.
    */
   public IRegion selectedRegion() {
      return super.selectedRegion;
   }

   /**
    * Wrapper method to get the {@code TranslationUnit} on which the {@code CRefactoring} operates.
    */
   public ITranslationUnit tu() {
      return getTranslationUnit();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ITranslationUnit getTranslationUnit() {
      return super.getTranslationUnit();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected IASTTranslationUnit getAST(final ITranslationUnit tu, final IProgressMonitor pm) throws CoreException, OperationCanceledException {
      return super.getAST(tu, pm);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected IIndex getIndex() throws OperationCanceledException, CoreException {
      return super.getIndex();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected boolean checkAST(final IASTTranslationUnit ast) {
      return super.checkAST(ast);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected List<IASTName> findAllMarkedNames(final IASTTranslationUnit ast) {
      return super.findAllMarkedNames(ast);
   }

   /**
    * Do not call directly. Delegates the collectModifications call.
    */
   @Override
   protected void collectModifications(final IProgressMonitor pm, final org.eclipse.cdt.internal.ui.refactoring.ModificationCollector collector)
            throws CoreException, OperationCanceledException {
      modificationCollectorWrapper = new ModificationCollector(getDeltaFactory(collector));
      collectModifications(pm, modificationCollectorWrapper);
   }

   abstract protected void collectModifications(IProgressMonitor pm, ModificationCollector collector) throws CoreException, OperationCanceledException;

   private IResourceChangeDescriptionFactory getDeltaFactory(final org.eclipse.cdt.internal.ui.refactoring.ModificationCollector collector) {
      try {
         final Field factory = org.eclipse.cdt.internal.ui.refactoring.ModificationCollector.class.getDeclaredField("deltaFactory");
         factory.setAccessible(true);
         return (IResourceChangeDescriptionFactory) factory.get(collector);
      } catch (IllegalAccessException | NoSuchFieldException | SecurityException e) {
         throw new RuntimeErrorException(null);
      }
   }
}
