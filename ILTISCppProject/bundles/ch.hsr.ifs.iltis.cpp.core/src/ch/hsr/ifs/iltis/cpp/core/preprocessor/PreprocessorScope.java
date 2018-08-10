package ch.hsr.ifs.iltis.cpp.core.preprocessor;

import static ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorStatementUtil.compareIncludes;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Stacks;

import ch.hsr.ifs.iltis.core.core.functional.functions.Consumer;


public class PreprocessorScope {

   public final PreprocessorScope               parent;
   public final IASTPreprocessorStatement       start;
   public IASTPreprocessorStatement             end             = null;
   public final List<IASTPreprocessorStatement> nonScopeContent = Lists.mutable.empty();
   public final List<PreprocessorScope>         subScopes       = Lists.mutable.empty();

   protected PreprocessorScope(PreprocessorScope parent, IASTPreprocessorStatement start) {
      this.parent = parent;
      this.start = start;
   }

   public static PreprocessorScope createFrom(IASTPreprocessorStatement[] statements) {
      final MutableStack<PreprocessorScope> scopeStack = Stacks.mutable.of(new PreprocessorScope(null, null));
      for (IASTPreprocessorStatement stmt : statements) {
         if (stmt instanceof IASTPreprocessorIfStatement || stmt instanceof IASTPreprocessorIfdefStatement) {
            PreprocessorScope newScope = new PreprocessorScope(scopeStack.peek(), stmt);
            scopeStack.peek().subScopes.add(newScope);
            scopeStack.push(newScope);
         } else if (stmt instanceof IASTPreprocessorElifStatement || stmt instanceof IASTPreprocessorElseStatement) {
            scopeStack.pop().end = stmt;
            PreprocessorScope newScope = new PreprocessorScope(scopeStack.peek(), stmt);
            scopeStack.peek().subScopes.add(newScope);
            scopeStack.push(newScope);
         } else if (stmt instanceof IASTPreprocessorEndifStatement) {
            scopeStack.pop().end = stmt;
         } else {
            scopeStack.peek().nonScopeContent.add(stmt);
         }
      }
      return scopeStack.pop();
   }

   public Optional<? extends IASTPreprocessorStatement> findStmtAfterWhichToAddInclude(String name, boolean isSystemInclude,
         IASTTranslationUnit ast) {
      MutableList<IASTPreprocessorIncludeStatement> includeStatements = getIncludeDirectives();
      if (!includeStatements.isEmpty()) {
         return includeStatements.reduce((last, current) -> compareIncludes(current, name, isSystemInclude) >= 0 ? last : current);
      } else {
         /* has no includes */
         return findLastNonScopeStatementBeforeSubScopes();
      }
   }

   //TODO needs to treat only local nodes??
   public MutableList<IASTPreprocessorIncludeStatement> getIncludeDirectives() {
      return Lists.adapt(nonScopeContent).selectInstancesOf(IASTPreprocessorIncludeStatement.class);
   }

   public Optional<IASTPreprocessorStatement> findLastNonScopeStatementBeforeSubScopes() {
      if (nonScopeContent.isEmpty()) {
         return Optional.ofNullable(start);
      } else {
         return Lists.adapt(nonScopeContent).select(s -> {
            IASTFileLocation loc = s.getFileLocation();
            return Lists.adapt(subScopes).collect(sc -> sc.start).noneSatisfyWith((scope, endOffset) -> scope.getFileLocation()
                  .getNodeOffset() < endOffset, loc.getNodeOffset() + loc.getNodeLength());
         }).getLastOptional();
      }
   }

   public PreprocessorScope getRootScope() {
      if (parent == null) return this;
      return parent.getRootScope();
   }

   public PreprocessorScope findNarrowestScope(IASTNode node) {
      final int nodeStart = node.getFileLocation().getNodeOffset();
      final int nodeEnd = nodeStart + node.getFileLocation().getNodeLength();

      if (contains(nodeStart, nodeEnd)) {
         return findScopeForContainedNode(nodeStart, nodeEnd);
      } else if (parent != null) {
         return parent.findNarrowestScope(node);
      } else {
         return null;
      }
   }

   private PreprocessorScope findScopeForContainedNode(int nodeStart, int nodeEnd) {
      if (Lists.adapt(subScopes).anySatisfy(s -> s.contains(nodeStart, nodeEnd))) {
         return findScopeForContainedNode(nodeStart, nodeEnd);
      } else {
         return this;
      }
   }

   public boolean contains(int startOffset, int endOffset) {
      if (start == null && end == null) return true;
      IASTFileLocation startFileLocation = start.getFileLocation();
      IASTFileLocation endFileLocation = end.getFileLocation();
      return startOffset >= startFileLocation.getNodeOffset() && endOffset <= endFileLocation.getNodeOffset() + endFileLocation.getNodeLength();
   }

   public Stream<PreprocessorScope> streamUp() {
      Builder<PreprocessorScope> builder = Stream.builder();
      forEachUp(scope -> builder.add(scope));
      return builder.build();
   }

   public Stream<PreprocessorScope> streamDown() {
      Builder<PreprocessorScope> builder = Stream.builder();
      forEachDown(scope -> builder.add(scope));
      return builder.build();
   }

   public void forEachDown(Consumer<PreprocessorScope> task) {
      task.accept(this);
      Lists.adapt(subScopes).forEach(s -> s.forEachDown(task));
   }

   public <T> void forEachDownWith(BiConsumer<PreprocessorScope, T> task, T with) {
      task.accept(this, with);
      Lists.adapt(subScopes).forEach(s -> s.forEachDownWith(task, with));
   }

   public void forEachUp(Consumer<PreprocessorScope> task) {
      task.accept(this);
      parent.forEachUp(task);
   }

   public <T> void forEachUpWith(BiConsumer<PreprocessorScope, T> task, T with) {
      task.accept(this, with);
      parent.forEachUpWith(task, with);
   }

}
