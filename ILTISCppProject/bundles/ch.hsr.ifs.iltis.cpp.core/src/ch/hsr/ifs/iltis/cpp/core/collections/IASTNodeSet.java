package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.function.BiPredicate;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;


/**
 * A set specialized for IASTNodes
 * 
 * @author tstauber
 *
 * @param <NodeType>
 * @since 1.1
 */
public class IASTNodeSet<NodeType extends IASTNode> extends TreeSortedSet<NodeType> implements IASTNodeCollection<NodeType> {

    public IASTNodeSet() {
        super();
    }

    public IASTNodeSet(final Iterable<NodeType> iterable) {
        super(iterable);
    }

    public IASTNodeSet(final Comparator<NodeType> comparator) {
        super(comparator);
    }

    public IASTNodeSet(final SortedSet<NodeType> set) {
        super(set);
    }

    public IASTNodeSet(final Comparator<NodeType> comparator, final Iterable<NodeType> iterable) {
        super(comparator, iterable);
    }

    /**
     * Checks is the node is contained in one of the nodes stored in this list
     *
     * @param node
     * @return
     */
    @Override
    public boolean envelops(final IASTNode node) {
        return Iterate.anySatisfy(this, n -> n.contains(node));
    }

    @Override
    public boolean envelopsAll(final Collection<? extends IASTNode> nodes) {
        return envelopsAll(nodes.toArray(new IASTNode[nodes.size()]));
    }

    @Override
    public boolean envelopsAll(final IASTNode... nodes) {
        return ArrayIterate.allSatisfy(nodes, this::envelops);
    }

    @SuppressWarnings("unchecked")
    @Override
    public MutableList<? extends IASTNodeSet<NodeType>> splitRelative(BiPredicate<? super NodeType, ? super NodeType> splitCondition) {

        if (size() <= 1) return Lists.mutable.of(this);
        
        MutableList<IASTNodeSet<NodeType>> lists = Lists.mutable.of(new IASTNodeSet<>());
        
        final Iterator<NodeType> leftIter = iterator();
        final Iterator<NodeType> rightIter = iterator();
        rightIter.next();

        while (leftIter.hasNext()) {
            if (!rightIter.hasNext()) {
                lists.getLast().add(leftIter.next());
            } else {
                NodeType left = leftIter.next();
                NodeType right = rightIter.next();
                lists.getLast().add(left);
                if (splitCondition.test(left, right)) lists.add(new IASTNodeSet<>());
            }
        }
        return lists;
    }

}
