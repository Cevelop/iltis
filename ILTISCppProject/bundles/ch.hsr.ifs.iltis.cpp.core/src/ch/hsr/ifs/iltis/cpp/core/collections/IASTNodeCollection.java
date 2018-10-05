package ch.hsr.ifs.iltis.cpp.core.collections;

import java.util.Collection;

import org.eclipse.cdt.core.dom.ast.IASTNode;


/**
 * A collection only for IASTNodes. This collection offers some comfort methods for IASTNodes.
 *
 * @author tstauber
 *
 * @param <T>
 * A type of IASTNode
 * 
 * @since 1.1
 */
public interface IASTNodeCollection<T extends IASTNode> extends Collection<T> {

    /**
     * Checks is the node is contained in one of the nodes stored in this list.
     * Enveloping means to contain it hierarchically.
     *
     * @param node
     * The node to check for if it is enveloped by the nodes in this collection
     * @return {@code true} iff the node is enveloped by the nodes in this collection
     */
    public boolean envelops(IASTNode node);

    /**
     * Checks if the nodes contained in this collection envelop all nodes in the passed collection.
     *
     * @param nodes
     * The nodes to check for if they are enveloped by the nodes in this collection
     * @return {@code true} iff all nodes in {@code nodes} are enveloped by the nodes in this collection
     */
    public boolean envelopsAll(Collection<? extends IASTNode> nodes);

    /**
     * Checks if the nodes contained in this collection envelop all nodes in the passed vararg.
     *
     * @param nodes
     * The nodes to check for if they are enveloped by the nodes in this collection
     * @return {@code true} iff all nodes in {@code nodes} are enveloped by the nodes in this collection
     */
    public boolean envelopsAll(IASTNode... nodes);
}
