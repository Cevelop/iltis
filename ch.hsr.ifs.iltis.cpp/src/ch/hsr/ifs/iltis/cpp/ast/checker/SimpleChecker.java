package ch.hsr.ifs.iltis.cpp.ast.checker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.codan.core.cxx.model.AbstractIndexAstChecker;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblemLocation;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.runtime.CoreException;

import ch.hsr.ifs.iltis.core.data.AbstractPair;
import ch.hsr.ifs.iltis.core.exception.ILTISException;
import ch.hsr.ifs.iltis.cpp.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.resources.CProjectUtil;


public abstract class SimpleChecker<problemId extends IProblemId> extends AbstractIndexAstChecker implements IChecker {

   protected ASTVisitor                           visitor;
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
    *        The {@link Pair<IASTNode, ProblemId} that shall be reported
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
    * 
    * @param node
    * @return
    */
   protected IProblemLocation locationHook(final IASTNode node) {
      return getProblemLocation(node);
   }

   //DOC missing
   protected Object[] argsHook(final AbstractPair<problemId, IASTNode> pair) {
      return null;
   }

   //DOC missing
   protected IIndex getIndex() {
      try {
         return getModelCache().getIndex();
      } catch (final CoreException e) {
         throw new ILTISException(e).rethrowUnchecked();
      }
   }

   //DOC missing
   protected IASTTranslationUnit getAst() {
      try {
         return getModelCache().getAST();
      } catch (final CoreException e) {
         throw new ILTISException(e).rethrowUnchecked();
      }
   }

   //DOC missing
   protected ICProject getCProject() {
      return CProjectUtil.getCProject(getFile());
   }

}
