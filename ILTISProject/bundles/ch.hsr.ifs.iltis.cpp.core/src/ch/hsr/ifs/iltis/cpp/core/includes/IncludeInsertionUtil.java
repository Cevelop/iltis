package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.List;
import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.partition.list.PartitionMutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import ch.hsr.ifs.iltis.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.ASTTranslationUnitUtil;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective.IncludeType;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorScope;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorStatementUtil;


/**
 * A utility class which provides static methods for adding includes.
 *
 * @author tstauber
 */
public class IncludeInsertionUtil {

    /**
     * Creates and performs a change which inserts an user include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     */
    public static void insertUserIncludeIfNeeded(final IASTTranslationUnit ast, final String headerName) {
        includeIfNotYetIncluded(ast, new IncludeDirective(headerName, IncludeType.USER));
    }

    /**
     * Creates and performs a change which inserts a system include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     */
    public static void insertSystemIncludeIfNeeded(final IASTTranslationUnit ast, final String headerName) {
        includeIfNotYetIncluded(ast, new IncludeDirective(headerName, IncludeType.SYSTEM));
    }

    /**
     * @use {@link #includeIfNotYetIncluded(IASTTranslationUnit, IncludeDirective)}
     * 
     * @since 1.1
     */
    @Deprecated
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude) {
        includeIfNotYetIncluded(ast, new IncludeDirective(headerName, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER),
                TextFileChange.KEEP_SAVE_STATE);
    }

    /**
     * Creates and performs a change which inserts an include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     * 
     * @since 1.2
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final IncludeDirective include) {
        includeIfNotYetIncluded(ast, include, TextFileChange.KEEP_SAVE_STATE);
    }

    /**
     * @use {@link #includeIfNotYetIncluded(IASTTranslationUnit, IncludeDirective, int)}
     * 
     * @since 1.1
     */
    @Deprecated
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude,
            final int textChangeSaveState) {
        includeIfNotYetIncluded(ast, new IncludeDirective(headerName, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER), textChangeSaveState,
                new NullProgressMonitor());
    }

    /**
     * Creates and performs a change which inserts an include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @param textChangeSaveState
     * Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
     * {@code TextFileChange.LEAVE_DIRTY}
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     * 
     * @since 1.2
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final MutableList<IncludeDirective> includes,
            final int textChangeSaveState) {
        includeIfNotYetIncluded(ast, includes, textChangeSaveState, new NullProgressMonitor());
    }

    /**
     * Creates and performs a change which inserts an include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @param textChangeSaveState
     * Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
     * {@code TextFileChange.LEAVE_DIRTY}
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     * 
     * @since 1.2
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final IncludeDirective include, final int textChangeSaveState) {
        includeIfNotYetIncluded(ast, include, textChangeSaveState, new NullProgressMonitor());
    }

    /**
     * @use {@link #includeIfNotYetIncluded(IASTTranslationUnit, IncludeDirective, int, IProgressMonitor)}
     * 
     * @since 1.1
     */
    @Deprecated
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude,
            final int textChangeSaveState, final IProgressMonitor pm) {
        includeIfNotYetIncluded(ast, new IncludeDirective(headerName, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER), textChangeSaveState,
                pm);
    }

    /**
     * Creates and performs a change which inserts an include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @param textChangeSaveState
     * Sets savestate of TextChange. Can be {@code TextFileChange.KEEP_SAVE_STATE}, {@code TextFileChange.FORCE_SAVE},
     * {@code TextFileChange.LEAVE_DIRTY}
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     * 
     * @since 1.2
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final MutableList<IncludeDirective> includes,
            final int textChangeSaveState, final IProgressMonitor pm) {

        createIncludeIfNotYetIncluded(ast, includes).ifPresent(change -> {
            try {
                change.setSaveMode(textChangeSaveState);
                change.perform(pm);
            } catch (final CoreException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @see #includeIfNotYetIncluded(IASTTranslationUnit, MutableList, int, IProgressMonitor)
     * 
     * @since 1.2
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final IncludeDirective include, final int textChangeSaveState,
            final IProgressMonitor pm) {
        includeIfNotYetIncluded(ast, Lists.mutable.of(include), textChangeSaveState, pm);
    }

    /**
     * @use {@link #createIncludeIfNotYetIncluded(IASTTranslationUnit, IncludeDirective)}
     * 
     * @since 1.1
     */
    @Deprecated
    public static Optional<TextFileChange> createIncludeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName,
            final boolean isSystemInclude) {
        return createIncludeInScopeIfNotYetIncluded(ast, new IncludeDirective(headerName, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER),
                PreprocessorScope.createFrom(ast));
    }

    /**
     * Creates and returns a TextFileChange to insert an include into the passed translation unit. The caller must provide the include name and the
     * information if it is a system include or a user include.
     *
     * <pre>
     * An include name can be something like {@code vector} or {@code foo.h}
     * </pre>
     *
     * @returns The {@link TextFileChange} or {@code null} if already included
     * 
     * @since 1.2
     */
    public static Optional<TextFileChange> createIncludeIfNotYetIncluded(final IASTTranslationUnit ast,
            final MutableList<IncludeDirective> includes) {
        return createIncludeInScopeIfNotYetIncluded(ast, includes, PreprocessorScope.createFrom(ast));
    }

    /**
     * Creates and returns a TextFileChange to insert an include into the passed translation unit. The caller must provide the include name and the
     * information if it is a system include or a user include.
     *
     * <pre>
     * An include name can be something like {@code vector} or {@code foo.h}
     * </pre>
     *
     * @returns The {@link TextFileChange} or {@code null} if already included
     * 
     * @since 1.2
     */
    public static Optional<TextFileChange> createIncludeIfNotYetIncluded(final IASTTranslationUnit ast, final IncludeDirective include) {
        return createIncludeInScopeIfNotYetIncluded(ast, include, PreprocessorScope.createFrom(ast));
    }

    /**
     * @use {@link #createIncludeInScopeIfNotYetIncluded(IASTTranslationUnit, IncludeDirective, PreprocessorScope)}
     * 
     * @since 1.1
     */
    @Deprecated
    public static Optional<TextFileChange> createIncludeInScopeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName,
            final boolean isSystemInclude, final PreprocessorScope scope) {
        return createIncludeInScopeIfNotYetIncluded(ast, new IncludeDirective(headerName, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER),
                scope);
    }

    /**
     * Creates and returns a TextFileChange to insert an include into the passed translation unit. The caller must provide the include name and the
     * information if it is a system include or a user include.
     *
     * <pre>
     * An include name can be something like {@code vector} or {@code foo.h}
     * </pre>
     *
     * @param scope
     * For better performance, the PreprocessorScope tree should be cached if multiple operations are executed.
     *
     * @returns The {@link TextFileChange} or {@code null} if already included
     * 
     * @since 1.2
     */
    public static Optional<TextFileChange> createIncludeInScopeIfNotYetIncluded(final IASTTranslationUnit ast, final List<IncludeDirective> includes,
            final PreprocessorScope scope) {

        final MutableList<IncludeDirective> sortedIncludes = Lists.adapt(includes).select(i -> !isAlreadyIncluded(scope, i.target)).sortThis();

        if (sortedIncludes.isEmpty()) return Optional.empty();

        final IFile file = ast.getOriginatingTranslationUnit().getFile();

        final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap = ASTTranslationUnitUtil.createLinenoOffsetContentMap(ast
                .getOriginatingTranslationUnit());

        final TextFileChange change = new TextFileChange("Add include(s)", file);
        change.setSaveMode(TextFileChange.LEAVE_DIRTY);
        change.setEdit(new MultiTextEdit());

        final String lineSeparator = FileUtil.getLineSeparator(file);

        int offset = 0;
        final StringBuffer includeStmt = createIncludeString(sortedIncludes, lineSeparator);

        final Optional<? extends IASTPreprocessorStatement> previousStatement = scope.findStmtAfterWhichToAddInclude(sortedIncludes.get(0));

        /**
         *  TODO: refactor this, find a way to correctly include a list of mixed statements
         *  like in IncludeInsertionUtilTest#testMultipleMixedSystemAndUserIncludesInsertedInBetween()
         */
        if (previousStatement.isPresent()) {
            final IASTPreprocessorStatement prevStmt = previousStatement.get();
            offset = PreprocessorStatementUtil.getOffsetToInsertAfter(previousStatement, linenoOffsetContentMap);

            final Optional<IASTPreprocessorStatement> nextStatement = Lists.immutable.of(ast.getAllPreprocessorStatements()).dropWhile(
                    s -> s != prevStmt).drop(1).getFirstOptional();

            final IncludeType firstIncludeType = sortedIncludes.getFirst().type;
            final IncludeType lastIncludeType = sortedIncludes.getLast().type;
            final boolean insertAfterPreviousStatement = (prevStmt instanceof IASTPreprocessorIncludeStatement);

            if (nextStatement.isPresent()) {
                final IASTPreprocessorStatement nextStmt = nextStatement.get();
                final boolean insertBeforeNextStatement = (nextStmt instanceof IASTPreprocessorIncludeStatement);

                if (insertAfterPreviousStatement && insertBeforeNextStatement) {
                    /* inbetween two includes */
                    if (((IASTPreprocessorIncludeStatement) prevStmt).isSystemInclude() != (firstIncludeType == IncludeType.SYSTEM)) {
                        includeStmt.insert(0, lineSeparator);
                    }
                    if (((IASTPreprocessorIncludeStatement) nextStmt).isSystemInclude() != (lastIncludeType == IncludeType.SYSTEM)) {
                        includeStmt.append(lineSeparator);
                    }
                } else if (insertAfterPreviousStatement) {
                    if (((IASTPreprocessorIncludeStatement) prevStmt).isSystemInclude() != (firstIncludeType == IncludeType.SYSTEM)) {
                        includeStmt.insert(0, lineSeparator);
                    }
                    if (!ASTTranslationUnitUtil.isFollowedByAWhitespaceLine(prevStmt, linenoOffsetContentMap)) {
                        includeStmt.append(lineSeparator);
                    }
                } else if (insertBeforeNextStatement) {
                    /* use this stmt to insert */
                    if (((IASTPreprocessorIncludeStatement) nextStmt).isSystemInclude() != (lastIncludeType == IncludeType.SYSTEM)) {
                        includeStmt.append(lineSeparator);
                    }
                    if (!ASTTranslationUnitUtil.isLeadByAWhitespaceLine(nextStmt, linenoOffsetContentMap)) {
                        includeStmt.insert(0, lineSeparator);
                    }
                    offset = PreprocessorStatementUtil.getOffsetToInsertBefore(nextStatement);
                } else {
                    if (!ASTTranslationUnitUtil.isFollowedByAWhitespaceLine(prevStmt, linenoOffsetContentMap)) {
                        includeStmt.append(lineSeparator);
                    }
                    if (!ASTTranslationUnitUtil.isLeadByAWhitespaceLine(nextStmt, linenoOffsetContentMap)) {
                        includeStmt.insert(0, lineSeparator);
                    }
                }
            } else {
                if (!(insertAfterPreviousStatement && ((IASTPreprocessorIncludeStatement) prevStmt)
                        .isSystemInclude() == (firstIncludeType == IncludeType.SYSTEM))) {
                    includeStmt.insert(0, lineSeparator);
                }
                if (ASTTranslationUnitUtil.lineNcontainsOnlyWhitespace(0, linenoOffsetContentMap)) {
                    includeStmt.append(lineSeparator);
                }
            }
        } else {
            offset = 0;
            includeStmt.append(lineSeparator);
        }

        change.addEdit(new InsertEdit(offset, includeStmt.toString()));

        return Optional.of(change);
    }

    /**
     * Creates and returns a TextFileChange to insert an include into the passed translation unit. The caller must provide the include name and the
     * information if it is a system include or a user include.
     *
     * <pre>
     * An include name can be something like {@code vector} or {@code foo.h}
     * </pre>
     *
     * @param scope
     * For better performance, the PreprocessorScope tree should be cached if multiple operations are executed.
     *
     * @returns The {@link TextFileChange} or {@code null} if already included
     * 
     * @since 1.2
     */
    public static Optional<TextFileChange> createIncludeInScopeIfNotYetIncluded(final IASTTranslationUnit ast, final IncludeDirective include,
            final PreprocessorScope scope) {
        return createIncludeInScopeIfNotYetIncluded(ast, Lists.mutable.of(include), scope);
    }

    /**
     * Returns if the given header-file is already included in a influencing scope. This
     * function is not able to determine if the include is indirectly included.
     *
     * @return If the {@link IASTPreprocessorIncludeStatement} {@code header} is
     * already included.
     */
    private static boolean isAlreadyIncluded(final PreprocessorScope scope, final String header) {
        return scope.streamUp().anyMatch(s -> s.getIncludeDirectives().anySatisfy((stmt) -> stmt.getName().toString().equals(header)));
    }

    private static StringBuffer createIncludeString(final MutableList<IncludeDirective> includes, final String lineSeparator) {
        PartitionMutableList<IncludeDirective> includePartition = includes.sortThis().partition(i -> i.type == IncludeType.USER);

        final String userIncludes = includePartition.getSelected().makeString(lineSeparator);
        final String systemIncludes = includePartition.getRejected().makeString(lineSeparator);

        final StringBuffer includeStmt = new StringBuffer(userIncludes);

        if (!userIncludes.isEmpty()) {
            includeStmt.append(lineSeparator);
        }

        if (!userIncludes.isEmpty() && !systemIncludes.isEmpty()) {
            includeStmt.append(lineSeparator);
        }

        includeStmt.append(systemIncludes);

        if (!systemIncludes.isEmpty()) {
            includeStmt.append(lineSeparator);
        }

        return includeStmt;
    }
}
