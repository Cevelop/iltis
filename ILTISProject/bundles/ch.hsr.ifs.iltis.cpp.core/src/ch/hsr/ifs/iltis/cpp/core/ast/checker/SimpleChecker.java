package ch.hsr.ifs.iltis.cpp.core.ast.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.codan.core.model.IProblemLocationFactory;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTImageLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.iltis.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter;
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.SimpleVisitor;
import ch.hsr.ifs.iltis.cpp.core.resources.CProjectUtil;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;
import ch.hsr.ifs.iltis.cpp.core.wrappers.AbstractIndexAstChecker;


/**
 * A checker which provides a certain infrastructure. Can easily be extended to use ASTRewriteCache.
 *
 * @author tstauber
 *
 * @param <ProblemId>
 * A class which implements IProblemId (It is recommended to use an enum for this)
 */
public abstract class SimpleChecker<ProblemId extends IProblemId<ProblemId>> extends AbstractIndexAstChecker implements IChecker,
        ISimpleReporter<ProblemId> {

    protected SimpleVisitor<?, ?> visitor = createVisitor();

    protected final List<VisitorReport<ProblemId>> nodesToReport = new ArrayList<>();

    protected final HashMap<VisitorReport<ProblemId>, MarkerInfo<?>> infosToReport = new HashMap<>();

    @Override
    public void processAst(final IASTTranslationUnit ast) {
        nodesToReport.clear();
        ast.accept(visitor);
        report();
    }

    /**
     * Returns the {@code ASTVisitor} which should be used. If the visitor is a SimpleVisitor,
     * {@code this::addNodeForReporting} can be used as callback.
     *
     * This method should only create a visitor. The created visitor will be stored in {@link #visitor}.
     */
    protected abstract SimpleVisitor<?, ?> createVisitor();

    @Override
    public void addNodeForReporting(final VisitorReport<ProblemId> result, final MarkerInfo<?> info) {
        nodesToReport.add(result);
        if (info != null) infosToReport.put(result, info);
    }

    /**
     * Reports all the nodes in {@code nodesToReport}
     */
    protected void report() {
        nodesToReport.stream().forEach((checkerResult) -> {
            MarkerInfo<?> info = infoHook(checkerResult);
            if (info != null) {
                reportProblem(checkerResult.getProblemId(), locationHook(checkerResult.getNode()), info);
            } else {
                reportProblem(checkerResult.getProblemId(), locationHook(checkerResult.getNode()));
            }
        });
        nodesToReport.clear();
    }

    /**
     * Per default this method highlights the whole code of the {@code IASTNode}.
     * Can be overridden.
     */
    protected IProblemLocation locationHook(final IASTNode node) {
        return getProblemLocation(node);
    }

    /**
     * Per default this method returns the arguments for each result. These arguments are then used to report the problem.
     * Can be overridden.
     * 
     * @since 1.1
     */
    protected MarkerInfo<?> infoHook(final VisitorReport<? extends IProblemId<ProblemId>> result) {
        return infosToReport.getOrDefault(result, null);
    }

    /**
     * Returns the {@code IIndex} for the AST on which this checker operates
     */
    protected IIndex getIndex() {
        try {
            return getModelCache().getIndex();
        } catch (final CoreException e) {
            throw new ILTISException(e).rethrowUnchecked();
        }
    }

    /**
     * Returns the AST on which this checker operates
     */
    protected IASTTranslationUnit getAst() {
        try {
            return getModelCache().getAST();
        } catch (final CoreException e) {
            throw new ILTISException(e).rethrowUnchecked();
        }
    }

    /**
     * If this AST is based on a file, then the {@code ICProject} to which said file belongs will be returned.
     */
    protected ICProject getCProject() {
        return CProjectUtil.getCProject(getFile());
    }

    @Override
    protected IProblemLocation getProblemLocation(final IASTNode astNode) {
        final IASTFileLocation astLocation = astNode.getFileLocation();
        return getProblemLocation(astNode, astLocation);
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
            final int start = astLocation.getNodeOffset();
            final int end = start + astNode.toString().length();
            return locFactory.createProblemLocation(getFile(), start, end, line);
        }
        final int start = astLocation.getNodeOffset();
        final int end = start + astLocation.getNodeLength();
        return locFactory.createProblemLocation(getFile(), start, end, line);
    }

}
