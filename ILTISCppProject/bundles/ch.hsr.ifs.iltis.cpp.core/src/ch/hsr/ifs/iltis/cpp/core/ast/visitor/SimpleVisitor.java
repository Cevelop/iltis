package ch.hsr.ifs.iltis.cpp.core.ast.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.cdt.codan.core.CodanRuntime;
import org.eclipse.cdt.codan.core.model.IChecker;
import org.eclipse.cdt.codan.core.model.IProblem;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;

import ch.hsr.ifs.iltis.cpp.core.ast.checker.SimpleChecker;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.core.ast.checker.helper.ISimpleReporter;
import ch.hsr.ifs.iltis.cpp.core.wrappers.CRefactoring;


/**
 * A simpler visitor, which supports callbacks.
 *
 * @author tstauber
 *
 * @param <ProblemId>
 *        An enum which implements IProblemId
 */
public abstract class SimpleVisitor<ProblemId extends Enum<ProblemId> & IProblemId, ArgType> extends ASTVisitor {

   protected final List<ArgType>              arguments;
   protected final ISimpleReporter<ProblemId> reporter;

   
   public ISimpleReporter<ProblemId> getReporter() {
      return reporter;
   }

   protected boolean isInCompositeSkipMode = false;

   /**
    * Constructs a new SimpleVisitor
    * 
    * @param reporter
    *        The reporter. Mostly this would be a {@link SimpleChecker} or a {@link CRefactoring} implementing {@link ISimpleReporter}
    * @param args
    *        Optional arguments
    */
   @SafeVarargs
   public SimpleVisitor(ISimpleReporter<ProblemId> reporter, final ArgType... args) {
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
   public SimpleVisitor(ISimpleReporter<ProblemId> reporter, final List<ArgType> args) {
      this.reporter = reporter;
      this.arguments = args;
   }

   /**
    * Constructs a new SimpleVisitor
    * 
    * @param reporter
    *        The reporter. Mostly this would be a {@link SimpleChecker} or a {@link CRefactoring} implementing {@link ISimpleReporter}
    */
   public SimpleVisitor(ISimpleReporter<ProblemId> reporter) {
      this(reporter, new ArrayList<ArgType>(10));
   }

   /**
    * Adds a collection of arguments to this visitors. This method returns the visitors itself to allow chaining.
    * 
    * @param args
    *        The arguments to add
    * @return Itself to allow chaining
    */
   public SimpleVisitor<ProblemId, ArgType> addArguments(Collection<ArgType> args) {
      arguments.addAll(args);
      return this;
   }

   /**
    * Adds a collection of arguments to this visitors. This method returns the visitors itself to allow chaining.
    * 
    * @param args
    *        The arguments to add
    * @return Itself to allow chaining
    */
   public SimpleVisitor<ProblemId, ArgType> addArguments(ArgType[] args) {
      return addArguments(Arrays.asList(args));
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
      if (reporter instanceof IChecker && CodanRuntime.getInstance() != null) {
         Collection<? extends IProblemId> enabledProblemIds = getEnabledProblemIds();
         return getProblemIds().stream().anyMatch(enabledProblemIds::contains);
      }
      return true;
   }

   private Set<? extends IProblemId> getEnabledProblemIds() {
      Set<String> codanProblems = CodanRuntime.getInstance().getCheckersRegistry().getRefProblems((IChecker) reporter).parallelStream().filter(
            IProblem::isEnabled).map(IProblem::getId).collect(Collectors.toSet());
      return getProblemIds().stream().filter(ipid -> codanProblems.contains(ipid.getId())).collect(Collectors.toSet());
   }

}
