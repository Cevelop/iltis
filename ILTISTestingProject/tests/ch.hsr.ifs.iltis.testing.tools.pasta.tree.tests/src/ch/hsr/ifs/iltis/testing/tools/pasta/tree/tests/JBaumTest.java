package ch.hsr.ifs.iltis.testing.tools.pasta.tree.tests;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;

import org.junit.Test;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.TreeNode;


public class JBaumTest {

   @Test
   public void testShouldCenterRootNodeOnXCoordinateInSimpleBinaryTree() {
      TreeNode<String> root = createBinayTree();
      root.adjust();
      assertEquals(1d, root.x(), 0.0d);
   }

   @Test
   public void testShouldCenterPlaceChildrenOneLevelOfDepthDeeperThanRoot() {
      TreeNode<String> root = createBinayTree();
      root.adjust();
      assertEquals(2, root.children().size());
      for (TreeNode<String> child : root.children()) {
         assertEquals(1.0d, child.y(), 0.0d);
      }
   }

   @Test
   public void testShouldPositionEachNodeCorrectly() {
      TreeNode<Point2D> root = createComplexTree();
      root.adjust();
      int checks = assertTreePositions(root);
      assertEquals(9, checks);
   }

   @Test
   public void testShouldNotCrashWhenAdjustingTree() {
      TreeNode<Point2D> root = createBuggedTree();
      root.adjust();
      assertTreePositions(root);
   }

   private int assertTreePositions(TreeNode<Point2D> root) {
      Point2D expectedPosition = root.data;
      assertEquals(expectedPosition.getX(), root.x(), 0.0d);
      assertEquals(expectedPosition.getY(), root.y(), 0.0d);

      int checks = 0;
      for (TreeNode<Point2D> child : root.children()) {
         checks += assertTreePositions(child);
      }
      return checks + 1;
   }

   private TreeNode<String> createBinayTree() {
      TreeNode<String> root = new TreeNode<>("root");
      root.addChild(new TreeNode<String>("leftChild"));
      root.addChild(new TreeNode<String>("rightChild"));
      root.adjust();
      return root;
   }

   private TreeNode<Point2D> createComplexTree() {
      TreeNode<Point2D> root = new TreeNode<Point2D>(new Point2D.Double(6.0, 0.0));
      TreeNode<Point2D> leftChild = new TreeNode<Point2D>(new Point2D.Double(2.0, 1.0));
      TreeNode<Point2D> leftChildChild = new TreeNode<Point2D>(new Point2D.Double(0.0, 2.0));
      TreeNode<Point2D> leftChildChild2 = new TreeNode<Point2D>(new Point2D.Double(2.0, 2.0));
      TreeNode<Point2D> leftChildChild3 = new TreeNode<Point2D>(new Point2D.Double(4.0, 2.0));
      TreeNode<Point2D> centerChild = new TreeNode<Point2D>(new Point2D.Double(6.0, 1.0));
      TreeNode<Point2D> centerChildChild = new TreeNode<Point2D>(new Point2D.Double(6.0, 2.0));
      TreeNode<Point2D> centerRightChild = new TreeNode<Point2D>(new Point2D.Double(8.0, 1.0));
      TreeNode<Point2D> rightChild = new TreeNode<Point2D>(new Point2D.Double(10.0, 1.0));

      root.addChild(leftChild);
      root.addChild(centerChild);
      root.addChild(centerRightChild);
      root.addChild(rightChild);
      leftChild.addChild(leftChildChild);
      leftChild.addChild(leftChildChild2);
      leftChild.addChild(leftChildChild3);
      centerChild.addChild(centerChildChild);

      return root;
   }

   private static TreeNode<Point2D> createBuggedTree() {
      TreeNode<Point2D> root = new TreeNode<Point2D>(new Point2D.Double(5, 0));
      TreeNode<Point2D> leftChild = new TreeNode<Point2D>(new Point2D.Double(2, 1));
      TreeNode<Point2D> leftChildChild = new TreeNode<Point2D>(new Point2D.Double(0, 2));
      TreeNode<Point2D> leftChildChild2 = new TreeNode<Point2D>(new Point2D.Double(2, 2));
      TreeNode<Point2D> leftChildChild3 = new TreeNode<Point2D>(new Point2D.Double(4, 2));
      TreeNode<Point2D> rightChild = new TreeNode<Point2D>(new Point2D.Double(8, 1));
      TreeNode<Point2D> rightChildChild = new TreeNode<Point2D>(new Point2D.Double(6, 2));
      TreeNode<Point2D> rightChildChild2 = new TreeNode<Point2D>(new Point2D.Double(8, 2));
      TreeNode<Point2D> rightChildChild3 = new TreeNode<Point2D>(new Point2D.Double(10, 2));
      root.addChild(leftChild);
      root.addChild(rightChild);
      leftChild.addChild(leftChildChild);
      leftChild.addChild(leftChildChild2);
      leftChild.addChild(leftChildChild3);
      rightChild.addChild(rightChildChild);
      rightChild.addChild(rightChildChild2);
      rightChild.addChild(rightChildChild3);

      leftChildChild2.addChild(new TreeNode<Point2D>(new Point2D.Double(2, 3)));
      rightChildChild2.addChild(new TreeNode<Point2D>(new Point2D.Double(8, 3)));
      return root;

   }
}
