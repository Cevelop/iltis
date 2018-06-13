package ch.hsr.ifs.iltis.cpp.core.ast.checker;

import java.util.Arrays;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


public interface ISimpleReporter<ProblemId extends IProblemId> {

   /**
    * Adds {@code node} to the list of nodes that will be reported
    *
    * @param result
    *        The {@link Pair<IASTNode, ProblemId} that shall be reported
    */
   public default void addNodeForReporting(final VisitorReport<ProblemId> result) {
      addNodeForReporting(result, (List<?>) null);
   }

   /**
    * Adds {@code node} to the list of nodes that will be reported
    *
    * @param result
    *        The {@link Pair<IASTNode, ProblemId} that shall be reported
    */
   public default void addNodeForReporting(final VisitorReport<ProblemId> result, final Object... args) {
      addNodeForReporting(result, Arrays.asList(args));
   }

   /**
    * Adds {@code node} to the list of nodes that will be reported
    * 
    * @param problemId
    *        The problem to be reported
    * @param node
    *        The node for which to report the problem
    * @param args
    *        arguments
    */
   public default void addNodeForReporting(ProblemId problemId, IASTNode node, final Object... args) {
      addNodeForReporting(new VisitorReport<>(problemId, node), Arrays.asList(args));
   }

   /**
    * Adds {@code node} to the list of nodes that will be reported
    *
    * @param result
    *        The {@link Pair<IASTNode, ProblemId} that shall be reported
    * @param args
    *        The arguments for the reporting. Can be null.
    */
   public void addNodeForReporting(final VisitorReport<ProblemId> result, final List<Object> args);

}
