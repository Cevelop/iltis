package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.collections.api.block.predicate.Predicate;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.utility.ArrayIterate;
import org.eclipse.collections.impl.utility.Iterate;


/**
 * A list specialized for IASTNodes.
 * 
 * @author tstauber
 * @since 1.1
 */
public class IASTNodeList<T extends IASTNode> extends FastList<T> implements IASTNodeCollection<T> {

    public IASTNodeList() {}

    public IASTNodeList(final int initialCapacity) {
        super(initialCapacity);
    }

    public IASTNodeList(final Collection<? extends T> source) {
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

    /* From FastList */

    @Override
    public IASTNodeList<T> reject(final Predicate<? super T> predicate) {
        return this.reject(predicate, new IASTNodeList<>());
    }

}
