package ch.hsr.ifs.iltis.cpp.wrappers;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringContext;


@SuppressWarnings("restriction")
public class CRefactoringContext extends org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext {

   public CRefactoringContext(final CRefactoring refactoring) {
      super(refactoring);
   }

   public CRefactoringContext(final org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext ctx) {
      super((CRefactoring) ctx.getRefactoring());
   }

   public static CRefactoringContext wrap(RefactoringContext ctx) {
      if (ctx instanceof CRefactoringContext) {
         return (CRefactoringContext) ctx;
      } else if (ctx instanceof org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext) {
         return new CRefactoringContext((org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext) ctx);
      } else {
         return null;
      }
   }

   public static boolean isUnwrappedType(RefactoringContext ctx) {
      return ctx instanceof org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext;
   }

   public static Class<org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext> getUnwrappedType() {
      return org.eclipse.cdt.internal.ui.refactoring.CRefactoringContext.class;
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
