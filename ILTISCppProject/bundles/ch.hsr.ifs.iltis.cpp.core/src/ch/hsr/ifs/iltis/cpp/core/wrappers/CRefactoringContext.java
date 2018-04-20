package ch.hsr.ifs.iltis.cpp.core.wrappers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.corext.util.CModelUtil;
import org.eclipse.cdt.internal.ui.editor.ASTProvider;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringContext;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


/**
 * A wrapper class for the cdt CRefactoringContext. Using this wrapper reduces the amount of warnings respectively the amount of
 * {@code @SuppressWarnings} tags
 * 
 * <p>
 * Mostly copied from the cdt CRefactorinContext
 * 
 * @author tstauber
 *
 */
@SuppressWarnings("restriction")
public class CRefactoringContext extends RefactoringContext {

   protected final Map<ITranslationUnit, IASTTranslationUnit> fASTCache = new ConcurrentHashMap<>();
   protected IIndex                                           fIndex;
   protected IASTTranslationUnit                              fSharedAST;

   public CRefactoringContext(CRefactoring refactoring) {
      super(refactoring);
      refactoring.setContext(this);
   }

   protected int getParseMode() {
      return ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_CONFIGURE_USING_SOURCE_CONTEXT | ITranslationUnit.AST_PARSE_INACTIVE_CODE;
   }

   protected int getIndexOptions() {
      return IIndexManager.ADD_DEPENDENCIES | IIndexManager.ADD_DEPENDENT;
   }

   /**
    * Returns an AST for the given translation unit. The AST is built for the working
    * copy of the translation unit if such working copy exists. The returned AST is
    * a shared one whenever possible.
    * <p>
    * An AST returned by this method should not be accessed concurrently by multiple threads.
    * <p>
    * <b>NOTE</b>: No references to the AST or its nodes can be kept after calling
    * the {@link #dispose()} method.
    *
    * @param tu
    *        The translation unit.
    * @param pm
    *        A progress monitor.
    * @return An AST, or <code>null</code> if the AST cannot be obtained.
    */
   public IASTTranslationUnit getAST(ITranslationUnit tu, IProgressMonitor pm) throws CoreException, OperationCanceledException {
      if (isDisposed()) throw new IllegalStateException("CRefactoringContext is already disposed."); //$NON-NLS-1$
      getIndex(); /* Make sure the index is locked. */
      assertNotCanceled(pm);

      ITranslationUnit wc = CModelUtil.toWorkingCopy(tu);

      if (fASTCache.get(wc) != null) {
         done(pm);
         return fASTCache.get(wc);
      } else if (fSharedAST != null && wc.equals(fSharedAST.getOriginatingTranslationUnit())) {
         done(pm);
         return fSharedAST;
      }

      IASTTranslationUnit sharedAST = ASTProvider.getASTProvider().acquireSharedAST(wc, fIndex, ASTProvider.WAIT_ACTIVE_ONLY, pm);
      if (sharedAST != null && !sharedAST.hasNodesOmitted()) {
         updateSharedAST(sharedAST);
         done(pm);
         return sharedAST;
      } else {
         releaseSharedAST(sharedAST); /* AST had omitted nodes */
         assertNotCanceled(pm);
         fASTCache.put(wc, wc.getAST(fIndex, getParseMode())); //TODO confirm: Was put(tu, tu.getAST(...))
         done(pm);
         return fASTCache.get(wc);
      }
   }

   private void releaseSharedAST(IASTTranslationUnit sharedAST) {
      if (sharedAST != null) {
         ASTProvider.getASTProvider().releaseSharedAST(sharedAST);
      }
   }

   private void done(IProgressMonitor pm) {
      if (pm != null) {
         pm.done();
      }
   }

   private void updateSharedAST(IASTTranslationUnit ast) {
      if (fSharedAST != null) {
         ASTProvider.getASTProvider().releaseSharedAST(fSharedAST);
      }
      fSharedAST = ast;
   }

   private void assertNotCanceled(IProgressMonitor pm) {
      if (pm != null && pm.isCanceled()) throw new OperationCanceledException();
   }

   /**
    * Returns the index that can be safely used for reading until the cache is disposed.
    * 
    * @return The index.
    */
   public IIndex getIndex() throws CoreException, OperationCanceledException {
      if (isDisposed()) throw new IllegalStateException("CRefactoringContext is already disposed."); //$NON-NLS-1$
      if (fIndex == null) {
         IIndex index = acquireIndex();
         lockAndUpdateIndex(index);
      }
      return fIndex;
   }

   protected IIndex acquireIndex() throws CoreException {
      return CCorePlugin.getIndexManager().getIndex(((CRefactoring) getRefactoring()).getProject(), getIndexOptions());
   }

   private void lockAndUpdateIndex(IIndex index) {
      try {
         index.acquireReadLock();
      } catch (InterruptedException e) {
         throw new OperationCanceledException();
      }
      fIndex = index;
   }

   @Override
   public void dispose() {
      if (isDisposed()) throw new IllegalStateException("CRefactoringContext.dispose() called more than once."); //$NON-NLS-1$
      releaseSharedAST(fSharedAST);
      if (fIndex != null) fIndex.releaseReadLock();
      super.dispose();
   }

   protected boolean isDisposed() {
      return getRefactoring() == null;
   }

   @Override
   protected void finalize() throws Throwable {
      if (!isDisposed()) CUIPlugin.logError("CRefactoringContext was not disposed"); //$NON-NLS-1$
      super.finalize();
   }

}
