package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import java.util.Optional;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.tuple.Tuples;

import ch.hsr.ifs.iltis.core.core.resources.StringUtil;


/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTTranslationUnitUtil extends ASTNodeUtil {

    /**
     * Crates a map indexed by line number, containing pairs of line-start-offset and line-content.
     *
     * @param tu
     * The translation unit to extract the content from
     * @return The created map
     */
    public static MutableMap<Integer, Pair<Integer, char[]>> createLinenoOffsetContentMap(final ITranslationUnit tu) {
        final char[] contents = tu.getContents();
        final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap = Maps.mutable.empty();
        int lastOffset = 0;
        int lineCounter = 0;
        for (int currentOffset = 0; currentOffset < contents.length; currentOffset++) {
            if (contents[currentOffset] == '\n') {
                final char[] line = new char[currentOffset + 1 - lastOffset];
                System.arraycopy(contents, lastOffset, line, 0, currentOffset + 1 - lastOffset);
                linenoOffsetContentMap.put(lineCounter++, Tuples.pair(lastOffset, line));
                lastOffset = currentOffset + 1;
            }
        }
        return linenoOffsetContentMap;
    }

    /**
     * Detects if a line is following a line containing only whitespace
     *
     * @param node
     * The node to take the start-line from
     * @param linenoOffsetContentMap
     * The map used for content mapping ({@link #createLinenoOffsetContentMap(ITranslationUnit)})
     * @return {@code true} iff the line before the line on which the node starts consists only of whitespace.
     */
    public static boolean isLeadByAWhitespaceLine(final IASTNode node, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        return ASTTranslationUnitUtil.lineNcontainsOnlyWhitespace(node.getFileLocation().getStartingLineNumber(), linenoOffsetContentMap);
    }

    /**
     * Detects if a line is followed by a line containing only whitespace
     *
     * @param node
     * The node to take the end-line from
     * @param linenoOffsetContentMap
     * The map used for content mapping ({@link #createLinenoOffsetContentMap(ITranslationUnit)})
     * @return {@code true} iff the line after the line on which the node ends consists only of whitespace.
     */
    public static boolean isFollowedByAWhitespaceLine(final IASTNode node, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        return ASTTranslationUnitUtil.lineNcontainsOnlyWhitespace(node.getFileLocation().getEndingLineNumber(), linenoOffsetContentMap);
    }

    /**
     * Detects if line X consists of only whitespace
     *
     * @param lineNo
     * The line number to test
     * @param linenoOffsetContentMap
     * The map used for content mapping ({@link #createLinenoOffsetContentMap(ITranslationUnit)})
     * @return {@code true} iff the line consists only of whitespace.
     */
    public static boolean lineNcontainsOnlyWhitespace(final int lineNo, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        if (linenoOffsetContentMap.containsKey(lineNo)) {
            return StringUtil.containsOnlyWhitespace(linenoOffsetContentMap.get(lineNo).getTwo());
        } else {
            return false;
        }
    }

    public static Optional<? extends Pair<Integer, Integer>> getOffsetAndLenghtOfLine(final int lineNo,
            final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        if (linenoOffsetContentMap.containsKey(lineNo)) {
            final Pair<Integer, char[]> pair = linenoOffsetContentMap.get(lineNo);
            return Optional.of(Tuples.twin(pair.getOne(), pair.getTwo().length));
        } else {
            return Optional.empty();
        }
    }

    public static int getLineNo(final int offset, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        final Optional<Pair<Integer, Pair<Integer, char[]>>> reduce = linenoOffsetContentMap.keyValuesView().reduce((last, curr) -> curr.getTwo()
                .getOne() <= offset ? last : curr);
        if (reduce.isPresent()) {
            return reduce.get().getOne();
        } else {
            return 0;
        }
    }

    public static Optional<? extends Pair<Integer, Integer>> getOffsetAndLenghtOfLine(final IASTNode node,
            final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        return getOffsetAndLenghtOfLine(node.getFileLocation().getStartingLineNumber(), linenoOffsetContentMap);
    }

    public static int getOffsetOfPreviousLine(final IASTNode node, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        return getOffsetOfNextLine(node.getFileLocation().getStartingLineNumber(), linenoOffsetContentMap);
    }

    public static int getOffsetOfNextLine(final IASTNode node, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        return getOffsetOfNextLine(node.getFileLocation().getEndingLineNumber(), linenoOffsetContentMap);
    }

    public static int getOffsetOfNextLine(final int lineNo, final MutableMap<Integer, Pair<Integer, char[]>> linenoOffsetContentMap) {
        if (linenoOffsetContentMap.containsKey(lineNo)) {
            return linenoOffsetContentMap.get(lineNo).getOne();
        } else {
            return -1;
        }
    }
}
