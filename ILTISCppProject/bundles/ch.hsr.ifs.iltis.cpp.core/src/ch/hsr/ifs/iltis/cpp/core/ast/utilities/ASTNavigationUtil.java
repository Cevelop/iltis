package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import java.util.List;
import java.util.function.Predicate;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.utility.ArrayIterate;

import ch.hsr.ifs.iltis.cpp.core.wrappers.CPPVisitor;


/**
 * 
 * @author tstauber
 * 
 * @noextend This class is not intended to be subclassed by clients.
 * 
 * @since 3.0
 *
 */
public class ASTNavigationUtil {

    /**
     * Finds the smallest set of subsequent nodes fulfilling the predicate, laying in the same scope, and containing all the nodes passed.
     * 
     * @param <T>
     * If T is a subtype of IASTNode, the filtering must be handled in the predicate, else a {@link ClassCastException} will be thrown.
     * @param predicate
     * The predicate the nodes must fulfill
     * @param nodes
     * The nodes
     * @return An empty list if no nodes were passed, else a set of nodes.
     */
    public static <T extends IASTNode> MutableList<T> findSmallestSetOfNodesContaining(Predicate<IASTNode> predicate, List<IASTNode> nodes) {
        return findSmallestSetOfNodesContaining(predicate, nodes.toArray(new IASTNode[nodes.size()]));
    }

    /**
     * Finds the smallest set of subsequent nodes fulfilling the predicate, laying in the same scope, and containing all the nodes passed.
     * 
     * @param <T>
     * If T is a subtype of IASTNode, the filtering must be handled in the predicate, else a {@link ClassCastException} will be thrown.
     * @param predicate
     * The predicate the nodes must fulfill
     * @param nodes
     * The nodes
     * @return An empty list if no nodes were passed, else a set of nodes.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IASTNode> MutableList<T> findSmallestSetOfNodesContaining(Predicate<IASTNode> predicate, IASTNode... nodes) {
        if (nodes == null || nodes.length == 0) return Lists.mutable.empty();

        IASTNode n = nodes[0];
        while (n != null && !(ArrayIterate.allSatisfy(nodes, n::contains) && predicate.test(n))) {
            n = n.getParent();
        }

        MutableList<T> res = (MutableList<T>) ArrayIterate.select(n.getChildren(), child -> predicate.test(child) && ArrayIterate.anySatisfy(nodes,
                node -> child.contains(node)));

        return res.isEmpty() ? Lists.mutable.of((T) n) : res;
    }

    /**
     * Finds the smallest node satisfying the predicate and containing all the nodes passed as arguments.
     * 
     * @param <T>
     * If T is a subtype of IASTNode, the filtering must be handled in the predicate, else a {@link ClassCastException} will be thrown.
     * @param predicate
     * The predicate the node must fulfill
     * @param node
     * A node
     * @param nodes
     * More nodes
     * @return The smallest node of type T which contains all the nodes passed.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IASTNode> T findSmallestNodeContaining(Predicate<IASTNode> predicate, IASTNode node, IASTNode... nodes) {
        IASTNode n = node;
        while (n != null && !(ArrayIterate.allSatisfy(nodes, n::contains) && predicate.test(n))) {
            n = n.getParent();
        }
        return (T) n;
    }

    /**
     * Finds the smallest node of type T which contains all the nodes passed as arguments.
     * 
     * @param nodeType
     * T.class (T must extends IASTNode)
     * @param node
     * A node
     * @param nodes
     * More nodes
     * @return The smallest node of type T which contains all the nodes passed.
     */
    public static <T extends IASTNode> T findSmallestNodeOfClassContaining(Class<T> nodeType, IASTNode node, IASTNode... nodes) {
        return findSmallestNodeContaining(n -> nodeType.isInstance(n), node, nodes);
    }

    /**
     * Finds the smallest node which contains all the nodes passed as arguments.
     * 
     * @param node
     * A node
     * @param nodes
     * More nodes
     * @return The smallest node which contains all the passed nodes.
     */
    public static IASTNode findSmallestNodeContaining(IASTNode node, IASTNode... nodes) {
        return findSmallestNodeContaining(n -> true, node, nodes);
    }

    /**
     * Finds the first parent declaration above node.
     * 
     * @param node
     * The node to search from (Not null)
     * @return The first parent declaration or null if none could be found
     */
    public static IASTDeclaration findFirstParentDeclaration(final IASTNode node) {
        for (IASTNode parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof IASTStatement) return (IASTDeclaration) parent;
        }
        return null;
    }

    /**
     * Finds the first parent declaration which has this node as a sub child matching one of the passed properties in the statement.
     * 
     * The result will be casted automatically according to the generic parameter. The caller has to make sure, that the passed property is matching
     * the generic type.
     * 
     * @param node
     * The node to search from (Not null)
     * @return The first parent declaration or null if none could be found
     */
    @SuppressWarnings("unchecked")
    public static <T extends IASTDeclaration> T findFirstParentDeclaration(final IASTNode node, final ASTNodeProperty... property) {
        if (node == null) return null;
        for (IASTNode prev = node, parent = prev.getParent(); parent != null; prev = parent, parent = parent.getParent()) {
            if (parent instanceof IASTStatement && ArrayIterate.anySatisfyWith(property, (p, child) -> child.getPropertyInParent() == p, prev))
                return (T) parent;
        }
        return null;
    }

    /**
     * Finds the first parent statement above the given node.
     * 
     * @param node
     * The node to search from (Not null)
     * @return The first parent statement or null if none could be found
     */
    public static IASTStatement findFirstParentStatement(IASTNode node) {
        for (IASTNode parent = node.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof IASTStatement) return (IASTStatement) parent;
        }
        return null;
    }

    /**
     * 
     * @param node
     * @param clazz
     * @return
     */
    public static boolean isPartOf(final IASTNode node, final Class<? extends IASTNode> clazz) {
        return CPPVisitor.findAncestorWithType(node, clazz).isPresent();
    }

}
