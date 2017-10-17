package com.cevelop.iltis.cpp.ast.checker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.codan.core.cxx.model.AbstractIndexAstChecker;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.cevelop.iltis.core.data.AbstractPair;
import com.cevelop.iltis.cpp.ast.checker.helper.IProblemId;

public abstract class SimpleChecker<problemId extends IProblemId> extends AbstractIndexAstChecker implements IChecker {
  protected ASTVisitor visitor;
  protected final List<CheckerResult<problemId>> nodesToReport = new ArrayList<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void processAst(final IASTTranslationUnit ast) {
    nodesToReport.clear();
    ast.accept(getVisitor());
    report();
  }

  /**
   * Returns the {@code ASTVisitor} which should be used
   * 
   * @return
   */
  protected abstract ASTVisitor getVisitor();

  /**
   * Adds {@code node} to the list of nodes that will be reported
   *
   * @author tstauber
   * @param pair
   *          The {@link Pair<IASTNode, ProblemId} that shall be reported
   */
  public void addNodeForReporting(final CheckerResult<problemId> pair) {
    nodesToReport.add(pair);
  }

  /**
   * Reports all the nodes in {@code nodesToReport}
   *
   * @author tstauber
   */
  protected void report() {
    nodesToReport.stream().forEach((checkerResult) -> {
      reportProblem(checkerResult.getProblemId().getId(), locationHook(checkerResult.getNode()), argsHook(checkerResult));
    });
    nodesToReport.clear();
  }

  /**
   * This hook can be overridden. Per default it highlights the whole {@code IASTNode}.
   * @param node
   * @return
   */
  protected IProblemLocation locationHook(final IASTNode node) {
    return getProblemLocation(node);
  }

  protected Object[] argsHook(AbstractPair<problemId, IASTNode> pair) {
    return null;
  }
}
