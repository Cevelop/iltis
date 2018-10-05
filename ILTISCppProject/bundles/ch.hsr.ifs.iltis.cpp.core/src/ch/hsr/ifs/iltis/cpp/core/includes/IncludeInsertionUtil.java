package ch.hsr.ifs.iltis.cpp.core.includes;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import ch.hsr.ifs.iltis.core.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.core.ast.utilities.ITranslationUnitUtil;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorScope;
import ch.hsr.ifs.iltis.cpp.core.preprocessor.PreprocessorStatementUtil;
import ch.hsr.ifs.iltis.cpp.core.util.constants.CommonCPPConstants;


/**
 * A utility class which provides static methods for adding includes.
 *
 * @author tstauber
 */
public class IncludeInsertionUtil {

    private static StringBuffer getSystemIncludeStatement(final String includeName) {
        return new StringBuffer(CommonCPPConstants.INCLUDE_DIRECTIVE + " <" + includeName + ">");
    }

    private static StringBuffer getUserIncludeStatement(final String includeName) {
        return new StringBuffer(CommonCPPConstants.INCLUDE_DIRECTIVE + " \"" + includeName + "\"");
    }

    private static StringBuffer getIncludeStatement(final String includeName, final boolean isSystemInclude) {
        return isSystemInclude ? getSystemIncludeStatement(includeName) : getUserIncludeStatement(includeName);
    }

    /**
     * Creates and performs a change which inserts an user include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     */
    public static void insertUserIncludeIfNeeded(final IASTTranslationUnit ast, final String headerName) {
        includeIfNotYetIncluded(ast, headerName, false);
    }

    /**
     * Creates and performs a change which inserts a system include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     */
    public static void insertSystemIncludeIfNeeded(final IASTTranslationUnit ast, final String headerName) {
        includeIfNotYetIncluded(ast, headerName, true);
    }

    /**
     * Creates and performs a change which inserts an include into the passed
     * {@link IASTTranslationUnit}. The include directive is only inserted, if there isn't already one for this header.
     *
     * @see #createIncludeIfNotYetIncluded(IASTTranslationUnit)
     * 
     * @since 1.1
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude) {
        includeIfNotYetIncluded(ast, headerName, isSystemInclude, TextFileChange.KEEP_SAVE_STATE);
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
     * @since 1.1
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude,
            final int textChangeSaveState) {
        includeIfNotYetIncluded(ast, headerName, isSystemInclude, textChangeSaveState, new NullProgressMonitor());
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
     * @since 1.1
     */
    public static void includeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName, final boolean isSystemInclude,
            final int textChangeSaveState, final IProgressMonitor pm) {

        createIncludeIfNotYetIncluded(ast, headerName, isSystemInclude).ifPresent(change -> {
            try {
                change.setSaveMode(textChangeSaveState);
                change.perform(pm);
            } catch (final CoreException e) {
                e.printStackTrace();
            }
        });
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
     * @since 1.1
     */
    public static Optional<TextFileChange> createIncludeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName,
            final boolean isSystemInclude) {
        return createIncludeInScopeIfNotYetIncluded(ast, headerName, isSystemInclude, PreprocessorScope.createFrom(ast));
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
     * @since 1.1
     */
    public static Optional<TextFileChange> createIncludeInScopeIfNotYetIncluded(final IASTTranslationUnit ast, final String headerName,
            final boolean isSystemInclude, final PreprocessorScope scope) {

        if (isAlreadyIncluded(scope, headerName)) return Optional.empty();

        final IFile file = ast.getOriginatingTranslationUnit().getFile();

        final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap = ITranslationUnitUtil.createLinenoOffsetContentMap(ast
                .getOriginatingTranslationUnit());

        final TextFileChange change = new TextFileChange("Add Include " + headerName, file);
        change.setSaveMode(TextFileChange.LEAVE_DIRTY);
        change.setEdit(new MultiTextEdit());

        final String lineSep = FileUtil.getLineSeparator(file);

        final StringBuffer includeStmt = getIncludeStatement(headerName, isSystemInclude);
        includeStmt.append(lineSep);

        int offset = 0;
        final Optional<? extends IASTPreprocessorStatement> previousStatement = scope.findStmtAfterWhichToAddInclude(headerName, isSystemInclude);

        if (previousStatement.isPresent()) {
            final IASTPreprocessorStatement prevStmt = previousStatement.get();
            offset = PreprocessorStatementUtil.getOffsetToInsertAfter(previousStatement, linenoOffsetContentMap);

            final Optional<IASTPreprocessorStatement> nextStatement = Lists.immutable.of(ast.getAllPreprocessorStatements()).dropWhile(
                    s -> s != prevStmt).drop(1).getFirstOptional();

            if (nextStatement.isPresent()) {
                final IASTPreprocessorStatement nextStmt = nextStatement.get();

                if (prevStmt instanceof IASTPreprocessorIncludeStatement && nextStmt instanceof IASTPreprocessorIncludeStatement) {
                    /* inbetween two includes */
                    if (((IASTPreprocessorIncludeStatement) prevStmt).isSystemInclude() != isSystemInclude) {
                        includeStmt.insert(0, lineSep);
                    }
                    if (((IASTPreprocessorIncludeStatement) nextStmt).isSystemInclude() != isSystemInclude) {
                        includeStmt.append(lineSep);
                    }
                } else if (prevStmt instanceof IASTPreprocessorIncludeStatement) {
                    if (((IASTPreprocessorIncludeStatement) prevStmt).isSystemInclude() != isSystemInclude) {
                        includeStmt.insert(0, lineSep);
                    }
                    if (!ITranslationUnitUtil.isFollowedByAWhitespaceLine(prevStmt, linenoOffsetContentMap)) {
                        includeStmt.append(lineSep);
                    }
                } else if (nextStmt instanceof IASTPreprocessorIncludeStatement) {
                    /* use this stmt to insert */
                    if (((IASTPreprocessorIncludeStatement) nextStmt).isSystemInclude() != isSystemInclude) {
                        includeStmt.append(lineSep);
                    }
                    if (!ITranslationUnitUtil.isLeadByAWhitespaceLine(nextStmt, linenoOffsetContentMap)) {
                        includeStmt.insert(0, lineSep);
                    }
                    offset = PreprocessorStatementUtil.getOffsetToInsertBefore(nextStatement);
                } else {
                    if (!ITranslationUnitUtil.isFollowedByAWhitespaceLine(prevStmt, linenoOffsetContentMap)) {
                        includeStmt.append(lineSep);
                    }
                    if (!ITranslationUnitUtil.isLeadByAWhitespaceLine(nextStmt, linenoOffsetContentMap)) {
                        includeStmt.insert(0, lineSep);
                    }
                }
            } else {
                if (!(prevStmt instanceof IASTPreprocessorIncludeStatement && ((IASTPreprocessorIncludeStatement) prevStmt)
                        .isSystemInclude() == isSystemInclude)) {
                    includeStmt.insert(0, lineSep);
                }
                if (ITranslationUnitUtil.lineNcontainsOnlyWhitespace(0, linenoOffsetContentMap)) {
                    includeStmt.append(lineSep);
                }
            }
        } else {
            offset = 0;
            includeStmt.append(lineSep);
        }

        change.addEdit(new InsertEdit(offset, includeStmt.toString()));

        return Optional.of(change);
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

}
