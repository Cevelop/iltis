package ch.hsr.ifs.iltis.cpp.ast.visitor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTNode;

import ch.hsr.ifs.iltis.cpp.ast.checker.CheckerResult;
import ch.hsr.ifs.iltis.cpp.ast.checker.helper.IProblemId;
import ch.hsr.ifs.iltis.cpp.ast.visitor.helper.IVisitorArgument;


/**
 * A simpler visitor, which supports callbacks.
 *
 * @author tstauber
 *
 * @param <problemId>
 *        A class which implements IProblemId (It is recommended to use an enum for this)
 */
public abstract class SimpleVisitor<problemId extends IProblemId> extends ASTVisitor {

   protected final List<IVisitorArgument>             args;
   protected final Consumer<CheckerResult<problemId>> callback;

   public SimpleVisitor(final Consumer<CheckerResult<problemId>> callback, final IVisitorArgument... args) {
      this(callback, Arrays.asList(args));
   }

   public SimpleVisitor(final Consumer<CheckerResult<problemId>> callback, final List<IVisitorArgument> args) {
      this.callback = callback;
      this.args = args;
   }

   public SimpleVisitor(final Consumer<CheckerResult<problemId>> callback) {
      this(callback, (List<IVisitorArgument>) null);
   }

   /**
    * Reports the{@code IProblemId} - {@code IASTNode} combination via the callback.
    * Overriding not recommended
    */
   protected void report(final problemId id, final IASTNode node) {
      callback.accept(new CheckerResult<>(id, node));
   }
}
