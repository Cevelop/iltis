package ch.hsr.ifs.iltis.testing.tools.pasta.tree;

/**
 * The layout algorithm.
 *
 * @author silflow
 *
 */
public class JBaum {

    private static <T, SELF extends TreeNode<T, SELF>> SELF firstWalk(final SELF node, final float siblingDistance, final float branchDistance) {

        if (!node.hasChildren()) {
            if (node.hasLeftSibling()) {
                node.setX(node.leftSibling().x() + node.leftSibling().width() + siblingDistance);
            } else {
                node.setX(0.0f);
            }
        } else {
            SELF defaultAncestor = node.getChildren().get(0);
            for (final SELF child : node.getChildren()) {
                firstWalk(child, siblingDistance, branchDistance);
                defaultAncestor = apportion(child, defaultAncestor, branchDistance);
            }
            executeShifts(node);
            final float midPoint = node.hasChildren() ? (node.leftMostChild().x() + node.rightMostChild().x() + node.rightMostChild().width()) / 2f
                                                      : node.x + node.width / 2f;
            if (node.hasLeftSibling()) {
                node.setX(node.leftSibling().x() + node.leftSibling().width() + siblingDistance);
                node.setMod(node.x() + node.width() / 2 - midPoint);
            } else {
                node.setX(midPoint - (node.width()) / 2);
            }
        }
        return node;
    }

    private static <T, SELF extends TreeNode<T, SELF>> SELF apportion(final SELF node, final SELF defaultAncestor, final float branchDistance) {
        if (node.hasLeftSibling()) {
            SELF innerRight = node;
            SELF outerRight = node;
            SELF innerLeft = node.leftSibling();
            SELF outerLeft = node.leftMostSibling();

            float sInnerRight = node.mod();
            float sOuterRight = node.mod();
            float sInnerLeft = innerLeft.mod();
            float sOuterLeft = outerLeft.mod();

            while (innerLeft.rightMostChild() != null && innerRight.leftMostChild() != null) {
                innerLeft = innerLeft.rightMostChild();
                innerRight = innerRight.leftMostChild();
                outerLeft = outerLeft.leftMostChild();
                outerRight = outerRight.rightMostChild();
                outerRight.setAncestor(node);

                final float shift = (innerLeft.x() + innerLeft.width() + sInnerLeft) - (innerRight.x() + sInnerRight) + branchDistance;
                if (shift > 0) {
                    moveSubtree(ancestor(innerLeft, node, defaultAncestor), node, shift);
                    sInnerRight += shift;
                    sOuterRight += shift;
                }

                sInnerLeft += innerLeft.mod();
                sInnerRight += innerRight.mod();
                sOuterLeft += outerLeft.mod();
                sOuterRight += outerRight.mod();
            }

            if (innerLeft.rightMostChild() != null && outerRight.rightMostChild() == null) {
                outerRight.setThread(innerLeft.rightMostChild());
                outerRight.setMod(outerRight.mod() + sInnerLeft - sOuterRight);
            } else {
                if (innerRight.leftMostChild() != null && outerLeft.leftMostChild() == null) {
                    outerLeft.setThread(innerRight.leftMostChild());
                    outerLeft.setMod(outerLeft.mod() + sInnerRight - sOuterLeft);
                }
                return node;
            }
        }
        return defaultAncestor;
    }

    private static <T, SELF extends TreeNode<T, SELF>> SELF ancestor(final SELF innerLeft, final SELF node, final SELF defaultAncestor) {
        return (node.parent().getChildren().contains(innerLeft.ancestor())) ? innerLeft.ancestor() : defaultAncestor;
    }

    private static <T, SELF extends TreeNode<T, SELF>> void moveSubtree(final SELF wl_anc, final SELF wr_node, final float shift) {
        final float subtrees = wr_node.number() - wl_anc.number();
        wr_node.setChange(wr_node.change() - (shift / subtrees));
        wr_node.setShift(wr_node.shift() + shift);
        wl_anc.setChange(wl_anc.change() + (shift / subtrees));
        wr_node.setX(wr_node.x() + shift);
        wr_node.setMod(wr_node.mod() + shift);
    }

    private static <T, SELF extends TreeNode<T, SELF>> void executeShifts(final SELF node) {
        float shift = 0;
        float change = 0;
        for (int i = node.getChildren().size() - 1; i > -1; --i) {
            final TreeNode<T, SELF> child = node.getChildren().get(i);
            child.setX(child.x() + shift);
            child.setMod(child.mod() + shift);
            change += child.change();
            shift += child.shift() + change;
        }
    }

    private static <T, SELF extends TreeNode<T, SELF>> float secondWalk(final SELF node, final float m, Float min) {
        node.setX(node.x() + m);
        //      node.setY(depth); // TODO(tstauber - Jun 11, 2018) REMOVE AFTER TESTING 
        if (min == null || node.x() < min) {
            min = node.x();
        }
        if (node.hasChildren()) {
            for (final SELF child : node.getChildren()) {
                min = secondWalk(child, m + node.mod(), min);
            }
        }
        return min;
    }

    private static <T, SELF extends TreeNode<T, SELF>> void thirdWalk(final SELF node, final float n) {
        node.setX(node.x() + n);
        for (final SELF child : node.getChildren()) {
            thirdWalk(child, n);
        }
    }

    public static <T, SELF extends TreeNode<T, SELF>> SELF adjustTree(final SELF tree, final float siblingDistance, final float branchDistance) {
        final SELF intermediate = firstWalk(tree, siblingDistance, branchDistance);
        final float min = secondWalk(intermediate, 0f, null);
        if (min < 0) {
            thirdWalk(intermediate, -min);
        }
        return intermediate;
    }

    public static <T, SELF extends TreeNode<T, SELF>> void reset(final SELF node) {
        node.visit(n -> {
            n.setShift(0);
            n.setMod(0);
            n.setX(0);
            n.setChange(0);
            n.setThread(null);
            n.setAncestor(n);
            return NodeVisitor.AfterVisitBehaviour.Continue;
        });
    }
}
