package ch.hsr.ifs.iltis.cpp.core.ast.stream;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.pde.api.tools.annotations.NoExtend;
import org.eclipse.pde.api.tools.annotations.NoInstantiate;


/**
 * Utility for creating relational {@link IASTNode} {@link Stream<IASTNode>}
 * 
 * @since 3.0
 * @noextend This class is not intended to be subclassed by clients.
 */
@NoExtend
@NoInstantiate
public class ASTNodeStreams {

    private ASTNodeStreams() {}

    /**
     * Creates a {@link Stream<IASTNode>} from the "from" node up. The stream continues until the {@link IASTTranslationUnit} is reached.
     * 
     * @param from
     * The from node (included).
     * @return A sequential node stream.
     */
    public static Stream<IASTNode> parentNodeStream(IASTNode from) {
        return conditionalParentNodeStream(from, ignore -> false);
    }

    /**
     * Creates a {@link Stream<IASTNode>} from the "from" node up to, but not including the "to" node.
     * 
     * @param from
     * The from node (included).
     * @param to
     * The to node (excluded).
     * @return A sequential node stream.
     */
    public static Stream<IASTNode> parentNodeStream(IASTNode from, IASTNode to) {
        return conditionalParentNodeStream(from, n -> n == to);
    }

    /**
     * Creates a {@link Stream<IASTNode>} from the "from" node up. The stream continues until either the {@link IASTTranslationUnit} is reached, or
     * the stop condition returned true.
     * 
     * @param node
     * The node from which to start the parent-stream (inclusive).
     * @param stopCondition
     * If a call to {@link Predicate#test(Object)} using the element returned on the last call to {@link Iterator#next()} returns {@code true},
     * {@link Iterator#hasNext()} will return {@code false}.
     * @return A sequential node stream.
     */
    public static Stream<IASTNode> conditionalParentNodeStream(IASTNode node, Predicate<IASTNode> stopCondition) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new ParentNodeIterator(node, stopCondition), Spliterator.NONNULL), false)
                .sequential();
    }

    public static class ParentNodeIterator implements Iterator<IASTNode> {

        private IASTNode                  current;
        private final Predicate<IASTNode> stopCondition;

        public ParentNodeIterator(IASTNode node) {
            this.current = node;
            this.stopCondition = (ignore) -> false;
        }

        public ParentNodeIterator(IASTNode node, Predicate<IASTNode> stopCondition) {
            this.current = node;
            this.stopCondition = stopCondition;
        }

        @Override
        public boolean hasNext() {
            return !stopCondition.test(current) && current != null;
        }

        @Override
        public IASTNode next() {
            IASTNode tmp = current;
            current = current.getParent();
            return tmp;
        }

    }

}
