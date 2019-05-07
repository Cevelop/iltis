package ch.hsr.ifs.iltis.cpp.core.ast.utilities;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.utility.ArrayIterate;

import ch.hsr.ifs.iltis.core.core.arrays.ArrayUtil;
import ch.hsr.ifs.iltis.cpp.core.ast.stream.ASTNodeStreams;
import ch.hsr.ifs.iltis.cpp.core.collections.IASTNodeList;
import ch.hsr.ifs.iltis.cpp.core.collections.IASTNodeSet;
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
     * Splits the {@link IASTNodeSet} for the nodes first parent statement's subsequence. Thereby, containment is considered subsequent.
     * 
     * @param <T>
     * The node type.
     * @param nodes
     * The set of nodes to split.
     * @return A list containing the node sets.
     */
    public static <T extends IASTNode> MutableList<? extends IASTNodeSet<T>> splitByStatementSubsequence(IASTNodeSet<T> nodes) {
        return nodes.splitRelative((l, r) -> !areSubsequentOrContained(l, r));
    }

    /**
     * Splits the {@link IASTNodeList} for the nodes first parent statement's subsequence. Thereby, containment is considered subsequent.
     * 
     * @param <T>
     * The node type.
     * @param nodes
     * The list of nodes to split.
     * @return A list containing the node lists.
     */
    public static <T extends IASTNode> MutableList<? extends IASTNodeList<T>> splitByStatementSubsequence(IASTNodeList<T> nodes) {
        return nodes.splitRelative((l, r) -> !areSubsequentOrContained(l, r));
    }

    /**
     * Tests whether two nodes' first parent statements are subsequent or if the first contains the second.
     * 
     * @param <T>
     * The node type.
     * @param first
     * The first node.
     * @param second
     * The second node.
     * @return {@code true} iff the first node's first parent statement is either followed by the second node's parent statement, or if the first node
     * contains the second node.
     */
    public static <T extends IASTNode> boolean areSubsequentOrContained(T first, T second) {
        return testEscalatingUpTheAST(first, second, (nl, nr) -> areSubsequentConsideringOnlyBody(findFirstParentStatement(nl),
                findFirstParentStatement(nr)), ASTEscalationType.BALANCED_ALL) || first.contains(second);
    }

    /**
     * Tests a predicate between two nodes while escalating up the AST as long as the predicate did not test positively.
     * 
     * @see ASTEscalationType
     * 
     * @param left
     * The left node from which to start.
     * @param right
     * The right node from which to start.
     * @param predicate
     * The predicate to test the nodes.
     * @param type
     * The {@link ASTEscalationType} to use.
     * @return {@code true} iff the predicate did test positively for a pair of nodes.
     */
    public static boolean testEscalatingUpTheAST(IASTNode left, IASTNode right, BiPredicate<? super IASTNode, ? super IASTNode> predicate,
            ASTEscalationType type) {
        switch (type) {
        case LEFT:
            for (IASTNode n = left; n != null; n = n.getParent()) {
                if (predicate.test(n, right)) return true;
            }
            break;
        case LEFTFIRST:
            for (IASTNode nl = left, nr = right; !(nl == null && nr == null); nl = getParentSafe(nl), nr = getParentSafe(nr)) {
                for (IASTNode inr = right; inr != null && nr != null && inr != nr.getParent(); inr = inr.getParent()) {
                    if (predicate.test(nl, inr)) return true;
                }
            }
            break;
        case BALANCED:
            for (IASTNode nl = left, nr = right; !(nl == null && nr == null); nl = getParentSafe(nl), nr = getParentSafe(nr)) {
                if (predicate.test(nl, nr)) return true;
            }
            break;
        case BALANCED_ALL:
            for (IASTNode nl = left, nr = right; !(nl == null && nr == null); nl = getParentSafe(nl), nr = getParentSafe(nr)) {
                for (IASTNode inl = left; inl != null && nl != null && inl != nl.getParent(); inl = inl.getParent()) {
                    if (predicate.test(inl, nr)) return true;
                }
                for (IASTNode inr = right; inr != null && nr != null && inr != nr.getParent(); inr = inr.getParent()) {
                    if (predicate.test(nl, inr)) return true;
                }
            }
            break;
        case RIGHTFIRST:
            for (IASTNode nl = left, nr = right; !(nl == null && nr == null); nl = getParentSafe(nl), nr = getParentSafe(nr)) {
                for (IASTNode inl = left; inl != null && nl != null && inl != nl.getParent(); inl = inl.getParent()) {
                    if (predicate.test(inl, nr)) return true;
                }
            }
            break;
        case RIGHT:
            for (IASTNode n = right; n != null; n = n.getParent()) {
                if (predicate.test(left, n)) return true;
            }
            break;
        }
        return false;
    }

    public static IASTNode getParentSafe(IASTNode n) {
        return n == null ? null : n.getParent();
    }

    public enum ASTEscalationType {
        /**
         * Escalates only the left value.
         */
        LEFT,
        /**
         * Escalates both the left and the right value, but tests the current left
         * value against all the right values up to the current escalation level.
         */
        LEFTFIRST,
        /**
         * Escalates both the left and the right value, and compares
         * only the current escalation levels against each other.
         */
        BALANCED,
        /**
         * Escalates both the left and the right value, and tests the current left value against all right values up to
         * the current escalation level, and the current right value against all left values up to the current escalation level.
         */
        BALANCED_ALL,
        /**
         * Escalates both the left and the right value, but tests the current right
         * value against all the left values up to the current escalation level.
         */
        RIGHTFIRST,
        /**
         * Escalates only the right value.
         */
        RIGHT;
    }

    /**
     * Tests if the two nodes are subsequent. Thereby, containment is not considered subsequent, while using the same node for first and second is
     * considered subsequent.
     * 
     * @param first
     * The first node.
     * @param second
     * The second node (which is tested whether it is subsequent to the first node).
     * @return {@code true} iff the second node is subsequent to the first.
     */
    public static boolean areSubsequent(IASTNode first, IASTNode second) {
        if (first == second) return true;
        IASTNode commonParent = findSmallestNodeContaining(first, second);
        int firstLevels = getDistanceToParentNode(first, commonParent);
        int secondLevels = getDistanceToParentNode(second, commonParent);
        if (firstLevels > secondLevels) {
            return areFirstChildren(first, commonParent);
        } else if (firstLevels < secondLevels) {
            return areFirstChildren(second, commonParent);
        } else {
            int idx = ArrayUtil.contains(commonParent.getChildren(), first);
            if (idx == -1) throw new IllegalStateException("Node should be part of the parent node");
            return commonParent.getChildren()[idx + 1] == second;
        }
    }

    /**
     * Tests if the two nodes are subsequent. Thereby, containment is not considered subsequent, while using the same node for first and second is
     * considered subsequent, and for loops and the like the first statement in the body is considered subsequent.
     * 
     * @param first
     * The first node.
     * @param second
     * The second node (which is tested whether it is subsequent to the first node).
     * @return {@code true} iff the second node is subsequent to the first.
     */
    public static boolean areSubsequentConsideringOnlyBody(IASTNode first, IASTNode second) {
        if (first == second) return true;
        IASTNode commonParent = findSmallestNodeContaining(first, second);
        int firstLevels = getDistanceToParentNode(first, commonParent);
        int secondLevels = getDistanceToParentNode(second, commonParent);
        if (firstLevels > secondLevels) {
            return areFirstChildrenConsideringOnlyBody(first, commonParent);
        } else if (firstLevels < secondLevels) {
            return areFirstChildrenConsideringOnlyBody(second, commonParent);
        } else {
            int idx = ArrayUtil.contains(commonParent.getChildren(), first);
            if (idx == -1) throw new IllegalStateException("Node should be part of the parent node");
            return commonParent.getChildren()[idx + 1] == second;
        }
    }

    /**
     * Tests if this node is the first child in its parent. Thereby, the {@link IASTTranslationUnit} is considered to be the first child.
     * 
     * @param node
     * The node to test.
     * @return {@code true} iff this node his parent's first child.
     */
    public static boolean isFirstChild(IASTNode node) {
        if (node.getParent() == null) return true;
        return node.getParent().getChildren()[0] == node;
    }

    /**
     * Tests if all the nodes between child and parent are each the first child in their parents. Thereby, the {@link IASTTranslationUnit} is
     * considered to be the first child.
     * 
     * @param child
     * The first node to test.
     * @param parent
     * The last node to test.
     * @return {@code true} iff all the nodes are their parents' first children.
     */
    public static boolean areFirstChildren(IASTNode child, IASTNode parent) {
        return ASTNodeStreams.parentNodeStream(child, parent.getParent()).allMatch(ASTNavigationUtil::isFirstChild);
    }

    /**
     * Tests if this node is the first child in its parent. Thereby, the {@link IASTTranslationUnit} is considered to be the first child, and for
     * loops and the like the first statement in the body is considered subsequent.
     * 
     * @param node
     * The node to test.
     * @return {@code true} iff this node his parent's first child.
     */
    public static boolean isFirstChildConsideringOnlyBody(IASTNode node) {
        ASTNodeProperty pip = node.getPropertyInParent();
        return !(pip == IASTForStatement.BODY || pip == IASTWhileStatement.BODY || pip == IASTDoStatement.BODY || pip == IASTSwitchStatement.BODY ||
                 pip == IASTIfStatement.THEN || pip == IASTIfStatement.ELSE) || isFirstChild(node);
    }

    /**
     * Tests if all the nodes between child and parent are each the first child in their parents. Thereby, the {@link IASTTranslationUnit} is
     * considered to be the first child, and for loops and the like the first statement in the body is considered subsequent.
     * 
     * @param child
     * The first node to test.
     * @param parent
     * The last node to test.
     * @return {@code true} iff all the nodes are their parents' first children.
     */
    public static boolean areFirstChildrenConsideringOnlyBody(IASTNode child, IASTNode parent) {
        return ASTNodeStreams.parentNodeStream(child, parent.getParent()).allMatch(ASTNavigationUtil::isFirstChildConsideringOnlyBody);
    }

    public static int getDistanceToParentNode(IASTNode child, IASTNode parent) {
        return (int) ASTNodeStreams.parentNodeStream(child, parent).count();
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
