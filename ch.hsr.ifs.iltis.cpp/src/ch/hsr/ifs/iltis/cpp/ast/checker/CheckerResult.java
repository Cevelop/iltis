package ch.hsr.ifs.iltis.cpp.ast.checker;

import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.core.data.AbstractPair;
import ch.hsr.ifs.iltis.cpp.ast.checker.helper.IProblemId;

public class CheckerResult<problemId extends IProblemId> extends AbstractPair<problemId, IASTNode> {

  public CheckerResult(problemId first, IASTNode second) {
    super(first, second);
  }

  public problemId getProblemId() {
    return first;
  }
  
  public String getIdString() {
    return first.getId();
  }

  public IASTNode getNode() {
    return second;
  }
}
