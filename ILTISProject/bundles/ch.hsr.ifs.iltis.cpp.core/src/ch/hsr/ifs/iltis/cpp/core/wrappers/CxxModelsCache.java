package ch.hsr.ifs.iltis.cpp.core.wrappers;

import java.util.WeakHashMap;

import org.eclipse.cdt.codan.core.cxx.internal.model.CodanCommentMap;
import org.eclipse.cdt.codan.core.cxx.internal.model.cfg.CxxControlFlowGraph;
import org.eclipse.cdt.codan.core.cxx.model.ICodanCommentMap;
import org.eclipse.cdt.codan.core.model.ICodanDisposable;
import org.eclipse.cdt.codan.core.model.cfg.IControlFlowGraph;
import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.rewrite.commenthandler.ASTCommenter;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ui.services.IDisposable;


/**
 * Cache data models for resource so checkers can share it
 *
 */
@SuppressWarnings("restriction")
public class CxxModelsCache implements ICodanDisposable {

    private final IFile                                                  file;
    private final ITranslationUnit                                       tu;
    private IASTTranslationUnit                                          ast;
    private IIndex                                                       fIndex;
    private final WeakHashMap<IASTFunctionDefinition, IControlFlowGraph> cfgmap;
    private ICodanCommentMap                                             commentMap;
    private boolean                                                      disposed;

    protected CxxModelsCache(final ITranslationUnit tu) {
        this.tu = tu;
        file = tu != null ? (IFile) tu.getResource() : null;
        cfgmap = new WeakHashMap<>(0);
    }

    protected CxxModelsCache(final IASTTranslationUnit ast) {
        this(ast.getOriginatingTranslationUnit());
        this.ast = ast;
    }

    protected int getParseMode() {
        return ITranslationUnit.AST_SKIP_ALL_HEADERS | ITranslationUnit.AST_CONFIGURE_USING_SOURCE_CONTEXT |
               ITranslationUnit.AST_SKIP_TRIVIAL_EXPRESSIONS_IN_AGGREGATE_INITIALIZERS | ITranslationUnit.AST_PARSE_INACTIVE_CODE;
    }

    protected int getIndexOptions() {
        return IIndexManager.ADD_DEPENDENCIES | IIndexManager.ADD_DEPENDENT;
    }

    public IASTTranslationUnit getAST() throws CoreException {
        if (ast == null) {
            getIndex();
            ast = tu.getAST(fIndex, getParseMode());
        }
        return ast;
    }

    public ITranslationUnit getTranslationUnit() {
        return tu;
    }

    public IFile getFile() {
        return file;
    }

    public synchronized IControlFlowGraph getControlFlowGraph(final IASTFunctionDefinition func) {
        IControlFlowGraph cfg = cfgmap.get(func);
        if (cfg != null) return cfg;
        cfg = CxxControlFlowGraph.build(func);

        // TODO(Alena Laskavaia): Change to LRU.
        if (cfgmap.size() > 20) { // if too many function better drop the cache
            cfgmap.clear();
        }
        cfgmap.put(func, cfg);
        return cfg;
    }

    public synchronized ICodanCommentMap getCommentedNodeMap() {
        if (commentMap == null) {
            if (ast == null) throw new IllegalStateException("getCommentedNodeMap called before getAST"); //$NON-NLS-1$
            commentMap = new CodanCommentMap(ASTCommenter.getCommentedNodeMap(ast));
        }
        return commentMap;
    }

    /**
     * Returns the index that can be safely used for reading until the cache is disposed.
     *
     * @return The index.
     * @throws CoreException
     */
    public synchronized IIndex getIndex() throws CoreException {
        Assert.isTrue(!disposed, "CxxASTCache is already disposed."); //$NON-NLS-1$
        if (fIndex == null) {
            final IIndex index = acquireIndex();
            lockAndUpdateIndex(index);
            fIndex = index;
        }
        return fIndex;
    }

    protected IIndex acquireIndex() throws CoreException {
        return CCorePlugin.getIndexManager().getIndex(tu.getCProject(), getIndexOptions());
    }

    private void lockAndUpdateIndex(final IIndex index) {
        try {
            index.acquireReadLock();
        } catch (final InterruptedException e) {
            throw new OperationCanceledException();
        }
        fIndex = index;
    }

    /**
     * @see IDisposable#dispose()
     * This method should not be called concurrently with any other method.
     */
    @Override
    public void dispose() {
        Assert.isTrue(!disposed, "CxxASTCache.dispose() called more than once."); //$NON-NLS-1$
        disposed = true;
        if (fIndex != null) {
            fIndex.releaseReadLock();
        }
    }

}
