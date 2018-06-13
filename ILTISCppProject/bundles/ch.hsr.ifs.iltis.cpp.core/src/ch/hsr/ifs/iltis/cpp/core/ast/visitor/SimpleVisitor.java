package ch.hsr.ifs.iltis.cpp.core.ast.visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.ISimpleReporter;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.SimpleChecker;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ast.visitor.helper.IVisitorArgument;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


/**
 * A simpler visitor, which supports callbacks.
 *
 * @author tstauber
 *
 * @param <ProblemId>
 *        An enum which implements IProblemId
 */
public abstract class SimpleVisitor<ProblemId extends Enum<ProblemId> & IProblemId> extends ASTVisitor {

   protected final List<IVisitorArgument>     args;
   protected final ISimpleReporter<ProblemId> reporter;

   protected boolean isInCompositeSkipMode = false;

   /**
    * Constructs a new SimpleVisitor
    * 
    * @param reporter
    *        The reporter. Mostly this would be a {@link SimpleChecker} or a {@link CRefactoring} implementing {@link ISimpleReporter}
    * @param args
    *        Optional arguments
    */
   public SimpleVisitor(ISimpleReporter<ProblemId> reporter, final IVisitorArgument... args) {
      this(reporter, Arrays.asList(args));
   }

   /**
    * Constructs a new SimpleVisitor
    * 
    * @param reporter
    *        The reporter. Mostly this would be a {@link SimpleChecker} or a {@link CRefactoring} implementing {@link ISimpleReporter}
    * @param args
    *        Optional arguments
    */
   public SimpleVisitor(ISimpleReporter<ProblemId> reporter, final List<IVisitorArgument> args) {
      this.reporter = reporter;
      this.args = args;
   }

   /**
    * Constructs a new SimpleVisitor
    * 
    * @param reporter
    *        The reporter. Mostly this would be a {@link SimpleChecker} or a {@link CRefactoring} implementing {@link ISimpleReporter}
    */
   public SimpleVisitor(ISimpleReporter<ProblemId> reporter) {
      this(reporter, Collections.emptyList());
   }

   /**
    * @return The problem Id's for this visitor. This must not be empty or null.
    */
   public abstract Set<? extends IProblemId> getProblemIds();

   public void enterCompositeSkipMode() {
      isInCompositeSkipMode = true;
   }

   public void exitCompositeSkipMode() {
      isInCompositeSkipMode = false;
   }

   public boolean isInCompositeSkipMode() {
      return isInCompositeSkipMode;
   }

   /**
    * This method can be overridden for custom enabling. By default if the reporter happens to be a IChecker, this visitor is considered enabled, if
    * any of the {@link #getProblemIds()} is enabled for it.
    * 
    * @return If this visitor shall be enabled
    */
   public boolean isEnabled() {
      if (reporter instanceof IChecker) {
         Collection<? extends IProblemId> enabledProblemIds = getEnabledProblemIds();
         return getProblemIds().stream().anyMatch(enabledProblemIds::contains);
      }
      return false;
   }

   private Set<? extends IProblemId> getEnabledProblemIds() {
      Set<String> codanProblems = CodanRuntime.getInstance().getCheckersRegistry().getRefProblems((IChecker) reporter).parallelStream().filter(
            IProblem::isEnabled).map(IProblem::getId).collect(Collectors.toSet());
      return getProblemIds().parallelStream().filter(ipid -> codanProblems.contains(ipid.getId())).collect(Collectors.toSet());
   }

}
