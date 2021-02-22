package ch.hsr.ifs.iltis.cpp.core.preprocessor;

import static ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorStatementUtil.compareIncludes;

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
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.stack.MutableStack;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Stacks;
import org.eclipse.collections.impl.utility.ArrayIterate;

import ch.hsr.ifs.iltis.core.functional.functions.Consumer;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective.IncludeType;


public class PreprocessorScope {

    public PreprocessorScope                            parent;
    public final IASTPreprocessorStatement              start;
    public IASTPreprocessorStatement                    end             = null;
    public final MutableList<IASTPreprocessorStatement> nonScopeContent = Lists.mutable.empty();
    public final MutableList<PreprocessorScope>         subScopes       = Lists.mutable.empty();

    protected PreprocessorScope(final PreprocessorScope parent, final IASTPreprocessorStatement start) {
        this.parent = parent;
        this.start = start;
    }

    public static PreprocessorScope createFrom(final IASTTranslationUnit ast) {
        final MutableList<IASTPreprocessorStatement> statements = Lists.mutable.of(ast.getAllPreprocessorStatements());
        final MutableStack<PreprocessorScope> scopeStack = Stacks.mutable.of(new PreprocessorScope(null, null));
        for (final IASTPreprocessorStatement stmt : statements) {
            if (stmt instanceof IASTPreprocessorIfStatement || stmt instanceof IASTPreprocessorIfdefStatement ||
                stmt instanceof IASTPreprocessorIfndefStatement) {
                final PreprocessorScope newScope = new PreprocessorScope(scopeStack.peek(), stmt);
                scopeStack.peek().subScopes.add(newScope);
                scopeStack.push(newScope);
            } else if (stmt instanceof IASTPreprocessorElifStatement || stmt instanceof IASTPreprocessorElseStatement) {
                scopeStack.pop().end = stmt;
                final PreprocessorScope newScope = new PreprocessorScope(scopeStack.peek(), stmt);
                scopeStack.peek().subScopes.add(newScope);
                scopeStack.push(newScope);
            } else if (stmt instanceof IASTPreprocessorEndifStatement) {
                scopeStack.pop().end = stmt;
            } else {
                scopeStack.peek().nonScopeContent.add(stmt);
            }
        }
        PreprocessorScope root = scopeStack.pop();
        if (root.nonScopeContent.isEmpty()) {
            if (root.subScopes.size() == 1 && ArrayIterate.allSatisfyWith(ast.getDeclarations(true), (d, rs) -> {
                final IASTFileLocation loc = d.getFileLocation();
                return rs.subScopes.get(0).contains(loc.getNodeOffset(), loc.getNodeOffset() + loc.getNodeLength());
            }, root)) {
                root = root.subScopes.get(0);
                root.parent = null;
            }
        }
        return root;
    }

    @Deprecated
    public Optional<? extends IASTPreprocessorStatement> findStmtAfterWhichToAddInclude(final String name, final boolean isSystemInclude) {
        return findStmtAfterWhichToAddInclude(new IncludeDirective(name, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER));
    }

    public Optional<? extends IASTPreprocessorStatement> findStmtAfterWhichToAddInclude(final IncludeDirective include) {
        final MutableList<? extends IASTPreprocessorIncludeStatement> includeStatements = getIncludeDirectives();
        if (!includeStatements.isEmpty()) {
            final IASTPreprocessorStatement stmt = includeStatements.toReversed().detect(st -> compareIncludes(st, include) < 0);
            if (stmt != null) return Optional.of(stmt);
        }
        return findLastNonScopeNonIncludeStatementBeforeSubScopes();
    }

    //TODO needs to treat only local nodes??
    public MutableList<IASTPreprocessorIncludeStatement> getIncludeDirectives() {
        return Lists.adapt(nonScopeContent).selectInstancesOf(IASTPreprocessorIncludeStatement.class);
    }

    public Optional<IASTPreprocessorStatement> findLastNonScopeNonIncludeStatementBeforeSubScopes() {
        final MutableList<IASTPreprocessorStatement> nonIncludeStmts = nonScopeContent.select(s -> !(s instanceof IASTPreprocessorIncludeStatement));
        if (nonIncludeStmts.isEmpty()) {
            return Optional.ofNullable(start);
        } else {
            return Lists.adapt(nonIncludeStmts).select(s -> {
                final IASTFileLocation loc = s.getFileLocation();
                return Lists.adapt(subScopes).collect(sc -> sc.start).noneSatisfyWith((scope, endOffset) -> scope.getFileLocation()
                        .getNodeOffset() < endOffset, loc.getNodeOffset() + loc.getNodeLength());
            }).getLastOptional();
        }
    }

    public PreprocessorScope getRootScope() {
        if (parent == null) return this;
        return parent.getRootScope();
    }

    public PreprocessorScope findNarrowestScope(final IASTNode node) {
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

    private PreprocessorScope findScopeForContainedNode(final int nodeStart, final int nodeEnd) {
        if (Lists.adapt(subScopes).anySatisfy(s -> s.contains(nodeStart, nodeEnd))) {
            return findScopeForContainedNode(nodeStart, nodeEnd);
        } else {
            return this;
        }
    }

    public boolean contains(final int startOffset, final int endOffset) {
        if (start == null && end == null) return true;
        final IASTFileLocation startFileLocation = start.getFileLocation();
        final IASTFileLocation endFileLocation = end.getFileLocation();
        return startOffset >= startFileLocation.getNodeOffset() && endOffset <= endFileLocation.getNodeOffset() + endFileLocation.getNodeLength();
    }

    public Stream<PreprocessorScope> streamUp() {
        final Builder<PreprocessorScope> builder = Stream.builder();
        forEachUp(scope -> builder.add(scope));
        return builder.build();
    }

    public Stream<PreprocessorScope> streamDown() {
        final Builder<PreprocessorScope> builder = Stream.builder();
        forEachDown(scope -> builder.add(scope));
        return builder.build();
    }

    public void forEachDown(final Consumer<PreprocessorScope> task) {
        task.accept(this);
        Lists.adapt(subScopes).forEach(s -> s.forEachDown(task));
    }

    public <T> void forEachDownWith(final BiConsumer<PreprocessorScope, T> task, final T with) {
        task.accept(this, with);
        Lists.adapt(subScopes).forEach(s -> s.forEachDownWith(task, with));
    }

    public void forEachUp(final Consumer<PreprocessorScope> task) {
        task.accept(this);
        if (parent != null) parent.forEachUp(task);
    }

    public <T> void forEachUpWith(final BiConsumer<PreprocessorScope, T> task, final T with) {
        task.accept(this, with);
        if (parent != null) parent.forEachUpWith(task, with);
    }

}
