package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;


/**
 * A list specialized for IASTNodes.
 * 
 * @author tstauber
 * @since 1.1
 */
public class IASTNodeList<NodeType extends IASTNode> extends FastList<NodeType> implements IASTNodeCollection<NodeType> {

    public IASTNodeList() {}

    public IASTNodeList(final int initialCapacity) {
        super(initialCapacity);
    }

    public IASTNodeList(final Collection<? extends NodeType> source) {
        super(source);
    }

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

    @Override
    @SuppressWarnings("unchecked")
    public MutableList<IASTNodeList<NodeType>> splitRelative(BiPredicate<? super NodeType, ? super NodeType> splitCondition) {

        if (size() <= 1) return Lists.mutable.of(this);
        
        MutableList<IASTNodeList<NodeType>> lists = Lists.mutable.of(new IASTNodeList<>());
        
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
                if (splitCondition.test(left, right)) lists.add(new IASTNodeList<>());
            }
        }
        return lists;
    }

    /* From IASTNodeList */

    @Override
    public IASTNodeList<NodeType> reject(final Predicate<? super NodeType> predicate) {
        return this.reject(predicate, new IASTNodeList<>());
    }

}
