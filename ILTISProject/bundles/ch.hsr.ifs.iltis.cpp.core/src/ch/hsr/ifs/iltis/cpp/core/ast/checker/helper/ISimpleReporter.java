package ch.hsr.ifs.iltis.cpp.core.ast.checker.helper;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.VisitorReport;
import ch.hsr.ifs.iltis.cpp.core.resources.info.MarkerInfo;


public interface ISimpleReporter<ProblemId extends IProblemId<ProblemId>> {

    /**
     * Adds {@code node} to the list of nodes that will be reported
     *
     * @param problemId
     * The problem to be reported
     * @param node
     * The node for which to report the problem
     * 
     * @since 1.1
     */
    public default void addNodeForReporting(final ProblemId problemId, final IASTNode node) {
        addNodeForReporting(new VisitorReport<>(problemId, node));
    }

    /**
     * Adds {@code node} to the list of nodes that will be reported
     *
     * @param problemId
     * The problem to be reported
     * @param node
     * The node for which to report the problem
     * @param info
     * The marker info
     * 
     * @since 1.1
     */
    public default void addNodeForReporting(final ProblemId problemId, final IASTNode node, final MarkerInfo<?> info) {
        addNodeForReporting(new VisitorReport<>(problemId, node), info);
    }

    /**
     * Adds {@code node} to the list of nodes that will be reported
     *
     * @param result
     * The {@link Pair<IASTNode, ProblemId} that shall be reported
     */
    public default void addNodeForReporting(final VisitorReport<ProblemId> result) {
        addNodeForReporting(result, null);
    }

    /**
     * Adds {@code node} to the list of nodes that will be reported
     *
     * @param result
     * The {@link Pair<IASTNode, ProblemId} that shall be reported
     * @param info
     * The markerInfo for reporting. Can be {@code null}.
     * 
     * @since 1.1
     */
    public void addNodeForReporting(final VisitorReport<ProblemId> result, final MarkerInfo<?> info);

}
