package ch.hsr.ifs.iltis.cpp.core.ast.checker;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.core.core.data.AbstractPair;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;


/**
 * A type to return a {@code IASTNode} coupled with its corresponding {@code IProblemId}
 *
 * @author tstauber
 *
 * @param <ProblemId>
 *        A class which implements IProblemId (It is recommended to use an enum for this)
 */
public class VisitorReport<ProblemId extends IProblemId> extends AbstractPair<ProblemId, IASTNode> {

   public VisitorReport(final ProblemId first, final IASTNode second) {
      super(first, second);
   }

   /**
    * @return The problem id which was reported by the checker
    */
   public ProblemId getProblemId() {
      return first;
   }

   /**
    * Convenience method for {@code getProblemId().getId()}
    */
   public String getIdString() {
      return first.getId();
   }

   /**
    * @return The node on which the checker hit
    */
   public IASTNode getNode() {
      return second;
   }
}
