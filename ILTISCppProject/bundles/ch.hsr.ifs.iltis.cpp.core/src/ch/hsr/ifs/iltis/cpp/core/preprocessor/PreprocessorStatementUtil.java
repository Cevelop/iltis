package ch.hsr.ifs.iltis.cpp.core.preprocessor;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;

import ch.hsr.ifs.iltis.core.core.resources.FileUtil;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective;
import ch.hsr.ifs.iltis.cpp.core.includes.IncludeDirective.IncludeType;


public class PreprocessorStatementUtil {

    /**
     * A preprocessor statement must always be at a lines beginning.
     * This method returns the offset for inserting a new preprocessor statement after the passed statement.
     * This method uses a line offset map to detect any trailing comments or such things.
     *
     * @param node
     * The node to insert after
     * @param linenoOffsetContentMap
     * The map between line-no, offset and content
     * @return The start offset of the line following the one the node is located on
     */
    public static int getOffsetToInsertAfter(final Optional<? extends IASTNode> node,
            final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        final int nextLine = node.map(n -> n.getFileLocation().getEndingLineNumber()).orElse(0);
        return linenoOffsetContentMap.get(nextLine).getOne();
    }

    /**
     * A preprocessor statement must always be at a lines beginning.
     * This method returns the offset for inserting a new preprocessor statement after the passed statement.
     *
     * @see {@link PreprocessorStatementUtil#getOffsetToInsertAfter(Optional, MutableMap)} creates better results
     *
     * @param node
     * The node to insert after
     * @return The offset of the beginning of the line after the passed node iff a node was passed, else offset 0.
     */
    public static int getOffsetToInsertAfter(final Optional<? extends IASTNode> node) {
        return node.map(n -> n.getFileLocation().getNodeOffset() + n.getFileLocation().getNodeLength() + FileUtil.getLineSeparator(n
                .getTranslationUnit().getOriginatingTranslationUnit().getFile()).length()).orElse(0);
    }

    /**
     * A preprocessor statement must always be at a lines beginning.
     * This method returns the offset for inserting a new preprocessor statement before the passed statement.
     *
     * @param node
     * The node to insert before
     * @return The offset of the beginning of the line before the passed node iff a node was passed, else offset 0.
     */
    public static int getOffsetToInsertBefore(final Optional<? extends IASTNode> node) {
        return node.map(n -> n.getFileLocation().getNodeOffset()).orElse(0);
    }

    /**
     * Compares first by user include < system include and then by include name alphabetically
     *
     * @param l
     * The left include statement
     * @param r
     * The right include statement
     * @return A negative number iff l should be ordered before r, 0 iff l is equal to r, a positive number iff l should be ordered after r
     */
    public static int compareIncludes(final IASTPreprocessorIncludeStatement l, final IASTPreprocessorIncludeStatement r) {
        return compareIncludes(l, new IncludeDirective(r.getName().toString(), r.isSystemInclude() ? IncludeType.SYSTEM : IncludeType.USER));
    }

    /**
     * @use {@link #compareIncludes(IASTPreprocessorIncludeStatement, IncludeDirective)}
     */
    @Deprecated
    public static int compareIncludes(final IASTPreprocessorIncludeStatement l, final String includeName, final boolean isSystemInclude) {
        return compareIncludes(l, new IncludeDirective(includeName, isSystemInclude ? IncludeType.SYSTEM : IncludeType.USER));
    }

    /**
     * Compares first by user include < system include and then by include name alphabetically
     *
     * @param l
     * The include statement
     * @param include
     * The other {@link IncludeDirective}
     * @return A negative number iff l should be ordered before the other include, 0 iff l is equal to the other include, a positive number iff l
     * should be ordered after the other include
     */
    public static int compareIncludes(final IASTPreprocessorIncludeStatement l, final IncludeDirective include) {
        if (l.isSystemInclude() && include.type != IncludeType.SYSTEM) {
            return +1;
        } else if (!l.isSystemInclude() && include.type == IncludeType.SYSTEM) {
            return -1;
        } else {
            /* l.isSystemInclude() == isSystemInclude */
            return l.getName().toString().compareTo(include.target);
        }
    }
}
