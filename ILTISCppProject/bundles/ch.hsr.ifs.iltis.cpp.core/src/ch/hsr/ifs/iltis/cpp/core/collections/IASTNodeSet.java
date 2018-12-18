package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;

/**
 * A set specialized for IASTNodes
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

}
