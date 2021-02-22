package ch.hsr.ifs.iltis.cpp.core.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.codan.core.cxx.model.ICAstChecker;
import org.eclipse.cdt.codan.core.cxx.model.ICodanCommentMap;
import org.eclipse.cdt.codan.core.model.AbstractCheckerWithProblemPreferences;
import org.eclipse.cdt.codan.core.model.ICheckerInvocationContext;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.codan.core.model.IRunnableInEditorChecker;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTMacroExpansionLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNodeLocation;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.semantics.CPPSemantics;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;

import ch.hsr.ifs.iltis.core.ILTIS;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;


/**
 * Convenience implementation of checker that works on index-based AST of a
 * C/C++
 * program.
 *
 * Clients may extend this class.
 *
 * FIXME remove as soon as my changes are in the cdt
 */
public abstract class AbstractIndexAstChecker extends AbstractCheckerWithProblemPreferences implements ICAstChecker, IRunnableInEditorChecker {

    private CxxModelsCache modelCache;

    @Override
    public synchronized boolean processResource(final IResource resource) throws OperationCanceledException {
        if (!shouldProduceProblems(resource)) return false;
        if (!(resource instanceof IFile)) return true;
        processFile((IFile) resource);
        return false;
    }

    private void processFile(final IFile file) throws OperationCanceledException {
        final ICheckerInvocationContext context = getContext();
        synchronized (context) {
            modelCache = context.get(CxxModelsCache.class);
            if (modelCache == null) {
                final ICElement celement = CoreModel.getDefault().create(file);
                if (!(celement instanceof ITranslationUnit)) {
                    return;
                }
                modelCache = acquireModelCache((ITranslationUnit) celement);
                context.add(modelCache);
            }
        }
        try {
            // Run the checker only if the index is fully initialized. Otherwise it may produce
            // false positives.
            if (modelCache.getIndex().isFullyInitialized()) {
                final IASTTranslationUnit ast = modelCache.getAST();
                if (ast != null) {
                    synchronized (ast) {
                        processAst(ast);
                    }
                }
            }
        } catch (final CoreException e) {
            ILTIS.log(e);
        } finally {
            modelCache = null;
        }
    }

    protected CxxModelsCache acquireModelCache(final ITranslationUnit tu) {
        return new CxxModelsCache(tu);
    }

    /*
     * (non-Javadoc)
     * @see IRunnableInEditorChecker#processModel(Object,
     * ICheckerInvocationContext)
     */
    @SuppressWarnings("restriction")
    @Override
    public synchronized void processModel(final Object model, final ICheckerInvocationContext context) {
        if (model instanceof IASTTranslationUnit) {
            final IASTTranslationUnit ast = (IASTTranslationUnit) model;
            // Run the checker only if the index was fully initialized when the file was parsed.
            // Otherwise the checker may produce false positives.
            if (ast.isBasedOnIncompleteIndex()) return;
            setContext(context);
            synchronized (context) {
                modelCache = context.get(CxxModelsCache.class);
                if (modelCache == null) {
                    modelCache = new CxxModelsCache(ast);
                    context.add(modelCache);
                }
            }
            CPPSemantics.pushLookupPoint(ast);
            try {
                processAst(ast);
            } finally {
                modelCache = null;
                setContext(null);
                CPPSemantics.popLookupPoint();
            }
        }
    }

    @Override
    public boolean runInEditor() {
        return true;
    }

    public void reportProblem(final IProblemId<?> id, final IASTNode astNode, final MarkerInfo<?> info) {
        final IProblemLocation loc = getProblemLocation(astNode);
        if (loc != null) reportProblem(id.getId(), loc, info.toCodanProblemArgsArray());
    }

    public void reportProblem(final IProblem problem, final IASTNode astNode, final MarkerInfo<?> info) {
        final IProblemLocation loc = getProblemLocation(astNode);
        if (loc != null) reportProblem(problem, loc, info.toCodanProblemArgsArray());
    }

    public void reportProblem(final IProblemId<?> id, final IASTNode astNode) {
        reportProblem(id.getId(), astNode);
    }

    public void reportProblem(final IProblemId<?> problemId, final IProblemLocation loc) {
        reportProblem(problemId.getId(), loc);
    }

    public void reportProblem(final IProblemId<?> problemId, final IProblemLocation loc, final MarkerInfo<?> info) {
        reportProblem(problemId.getId(), loc, info.toCodanProblemArgsArray());
    }

    /**
     * @deprecated Use {@link #reportProblem(IProblemId, IProblemLocation, MarkerInfo))}.
     */
    @Deprecated
    @Override
    public final void reportProblem(final String problemId, final IProblemLocation loc, final Object... args) {
        reportProblem(getProblemById(problemId, loc.getFile()), loc, args);
    }

    /**
     * @deprecated Use {@link #reportProblem(IProblemId, IASTNode, MarkerInfo)}.
     */
    @Deprecated
    public final void reportProblem(final IProblemId<?> id, final IASTNode astNode, final Object... args) {
        reportProblem(id.getId(), astNode, args);
    }

    /**
     * @deprecated Use {@link #reportProblem(IProblemId, IASTNode, MarkerInfo)}.
     */
    @Deprecated
    public final void reportProblem(final String id, final IASTNode astNode, final Object... args) {
        final IProblemLocation loc = getProblemLocation(astNode);
        if (loc != null) reportProblem(id, loc, args);
    }

    /**
     * @deprecated Use {@link #reportProblem(IProblem, IASTNode, MarkerInfo)}
     */
    @Deprecated
    public final void reportProblem(final IProblem problem, final IASTNode astNode, final Object... args) {
        final IProblemLocation loc = getProblemLocation(astNode);
        if (loc != null) reportProblem(problem, loc, args);
    }

    /**
     * Checks if problem should be reported, in this case it will check line
     * comments, later can add filters or what not.
     *
     * @param problem
     * - problem kind
     * @param loc
     * - location
     * @param args
     * - arguments
     * @since 3.4
     */
    @Override
    protected boolean shouldProduceProblem(final IProblem problem, final IProblemLocation loc, final Object... args) {
        final String suppressionComment = (String) getSuppressionCommentPreference(problem).getValue();
        if (suppressionComment.isEmpty()) return super.shouldProduceProblem(problem, loc, args);
        final List<IASTComment> lineComments = getLineCommentsForLocation(loc);
        for (final IASTComment astComment : lineComments) {
            if (astComment.getRawSignature().contains(suppressionComment)) return false;
        }
        return super.shouldProduceProblem(problem, loc, args);
    }

    protected List<IASTComment> getLineCommentsForLocation(final IProblemLocation loc) {
        final ArrayList<IASTComment> lineComments = new ArrayList<>();
        try {
            final IASTComment[] commentsArray = modelCache.getAST().getComments();
            for (final IASTComment comm : commentsArray) {
                final IASTFileLocation fileLocation = comm.getFileLocation();
                if (fileLocation.getStartingLineNumber() == loc.getLineNumber()) {
                    final String problemFile = loc.getFile().getLocation().toOSString();
                    final String commentFile = fileLocation.getFileName();
                    if (problemFile.equals(commentFile)) {
                        lineComments.add(comm);
                    }
                }
            }
        } catch (OperationCanceledException | CoreException e) {
            ILTIS.log(e);
        }
        return lineComments;
    }

    protected IProblemLocation getProblemLocation(final IASTNode astNode) {
        final IASTFileLocation astLocation = astNode.getFileLocation();
        return astLocation == null ? null : getProblemLocation(astNode, astLocation);
    }

    private IProblemLocation getProblemLocation(final IASTNode astNode, final IASTFileLocation astLocation) {
        final int line = astLocation.getStartingLineNumber();
        final IProblemLocationFactory locFactory = getRuntime().getProblemLocationFactory();
        if (enclosedInMacroExpansion(astNode) && astNode instanceof IASTName) {
            final IASTImageLocation imageLocation = ((IASTName) astNode).getImageLocation();
            if (imageLocation != null) {
                final int start = imageLocation.getNodeOffset();
                final int end = start + imageLocation.getNodeLength();
                return locFactory.createProblemLocation(getFile(), start, end, line);
            }
        }
        // If the raw signature has more than one line, we highlight only the code
        // related to the problem. However, if the problem is associated with a
        // node representing a class definition, do not highlight the entire class
        // definition, because that can result in many lines being highlighted.
        if (astNode instanceof IASTCompositeTypeSpecifier) {
            return locFactory.createProblemLocation(getFile(), line);
        }
        final int start = astLocation.getNodeOffset();
        final int end = start + astLocation.getNodeLength();
        return locFactory.createProblemLocation(getFile(), start, end, line);
    }

    protected static boolean enclosedInMacroExpansion(final IASTNode node) {
        final IASTNodeLocation[] nodeLocations = node.getNodeLocations();
        return nodeLocations.length == 1 && nodeLocations[0] instanceof IASTMacroExpansionLocation;
    }

    protected static boolean includesMacroExpansion(final IASTNode node) {
        for (final IASTNodeLocation nodeLocation : node.getNodeLocations()) {
            if (nodeLocation instanceof IASTMacroExpansionLocation) return true;
        }
        return false;
    }

    protected IFile getFile() {
        return modelCache.getFile();
    }

    protected IProject getProject() {
        final IFile file = getFile();
        return file == null ? null : file.getProject();
    }

    protected CxxModelsCache getModelCache() {
        return modelCache;
    }

    protected ICodanCommentMap getCommentMap() {
        return modelCache.getCommentedNodeMap();
    }
}
