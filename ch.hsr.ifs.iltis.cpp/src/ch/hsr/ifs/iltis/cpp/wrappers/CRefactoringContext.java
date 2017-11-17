package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;


@SuppressWarnings("restriction")
public class CRefactoringContext extends org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext {

   public CRefactoringContext(final CRefactoring refactoring) {
      super(refactoring);
   }

   public CRefactoringContext(final org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext ctx) {
      super((CRefactoring) ctx.getRefactoring());
   }

   @Override
   public IASTTranslationUnit getAST(final ITranslationUnit tu, final IProgressMonitor pm) throws OperationCanceledException, CoreException {
      return super.getAST(tu, pm);
   }

   @Override
   public IIndex getIndex() throws CoreException, OperationCanceledException {
      return super.getIndex();
   }

   @Override
   public void dispose() {
      super.dispose();
   }

   @Override
   protected void finalize() throws Throwable {
      super.finalize();
   }

}
