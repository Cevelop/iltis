package ch.hsr.ifs.iltis.cpp.ast.checker;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.core.data.AbstractPair;
import ch.hsr.ifs.iltis.cpp.ast.checker.helper.IProblemId;


/**
 * A type to return a {@code IASTNode} coupled with its corresponding {@code IProblemId}
 *
 * @author tstauber
 *
 * @param <problemId>
 *        A class which implements IProblemId (It is recommended to use an enum for this)
 */
public class CheckerResult<problemId extends IProblemId> extends AbstractPair<problemId, IASTNode> {

   public CheckerResult(final problemId first, final IASTNode second) {
      super(first, second);
   }

   public problemId getProblemId() {
      return first;
   }

   /**
    * Convenience method for {@code getProblemId().getId()}
    */
   public String getIdString() {
      return first.getId();
   }

   public IASTNode getNode() {
      return second;
   }
}
