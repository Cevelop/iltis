package ch.hsr.ifs.iltis.cpp.wrappers;

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

   private static final int PARSE_MODE = ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_CONFIGURE_USING_SOURCE_CONTEXT |
                                         ITranslationUnit.AST_PARSE_INACTIVE_CODE;

   private final Map<ITranslationUnit, IASTTranslationUnit> fASTCache = new ConcurrentHashMap<>();
   private IIndex                                           fIndex;
   private IASTTranslationUnit                              fSharedAST;

   public CRefactoringContext(CRefactoring refactoring) {
      super(refactoring);
      refactoring.setContext(this);
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
      getIndex(); // Make sure the index is locked.
      if (pm != null && pm.isCanceled()) throw new OperationCanceledException();

      ITranslationUnit wc = CModelUtil.toWorkingCopy(tu);
      IASTTranslationUnit ast = fASTCache.get(wc);
      if (ast == null) {
         if (fSharedAST != null && wc.equals(fSharedAST.getOriginatingTranslationUnit())) {
            ast = fSharedAST;
         } else {
            ast = ASTProvider.getASTProvider().acquireSharedAST(wc, fIndex, ASTProvider.WAIT_ACTIVE_ONLY, pm);
            if (ast != null && ast.hasNodesOmitted()) {
               // Don't use an incomplete AST.
               ASTProvider.getASTProvider().releaseSharedAST(ast);
               ast = null;
            }
            if (ast == null) {
               if (pm != null && pm.isCanceled()) throw new OperationCanceledException();
               ast = wc.getAST(fIndex, PARSE_MODE);
               fASTCache.put(wc, ast);
            } else {
               if (fSharedAST != null) {
                  ASTProvider.getASTProvider().releaseSharedAST(fSharedAST);
               }
               fSharedAST = ast;
            }
         }
      }
      if (pm != null) {
         pm.done();
      }
      return ast;
   }

   /**
    * Returns the index that can be safely used for reading until the cache is disposed.
    * 
    * @return The index.
    */
   public IIndex getIndex() throws CoreException, OperationCanceledException {
      if (isDisposed()) throw new IllegalStateException("CRefactoringContext is already disposed."); //$NON-NLS-1$
      if (fIndex == null) {
         IIndex index = CCorePlugin.getIndexManager().getIndex(((CRefactoring) getRefactoring()).getProject(), IIndexManager.ADD_DEPENDENCIES &
                                                                                                               IIndexManager.ADD_DEPENDENT);
         try {
            index.acquireReadLock();
         } catch (InterruptedException e) {
            throw new OperationCanceledException();
         }
         fIndex = index;
      }
      return fIndex;
   }

   @Override
   public void dispose() {
      if (isDisposed()) throw new IllegalStateException("CRefactoringContext.dispose() called more than once."); //$NON-NLS-1$
      if (fSharedAST != null) {
         ASTProvider.getASTProvider().releaseSharedAST(fSharedAST);
      }
      if (fIndex != null) {
         fIndex.releaseReadLock();
      }
      super.dispose();
   }

   private boolean isDisposed() {
      return getRefactoring() == null;
   }

   @Override
   protected void finalize() throws Throwable {
      if (!isDisposed()) CUIPlugin.logError("CRefactoringContext was not disposed"); //$NON-NLS-1$
      super.finalize();
   }
}
