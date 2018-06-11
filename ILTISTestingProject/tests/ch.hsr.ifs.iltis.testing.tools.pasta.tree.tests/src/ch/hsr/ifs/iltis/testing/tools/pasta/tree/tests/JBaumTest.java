package ch.hsr.ifs.iltis.testing.tools.pasta.tree.tests;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;

import org.junit.Test;


public class JBaumTest {

   @Test
   public void testShouldCenterRootNodeOnXCoordinateInSimpleBinaryTree() {
      TestNode<String> root = createBinayTree();
      root.adjust();
      assertEquals(1f, root.x(), 0f);
   }

   @Test
   public void testShouldCenterPlaceChildrenOneLevelOfDepthDeeperThanRoot() {
      TestNode<String> root = createBinayTree();
      root.adjust();
      assertEquals(2, root.getChildren().size());
      for (TestNode<String> child : root.getChildren()) {
         assertEquals(1f, child.y(), 0f);
      }
   }

   @Test
   public void testShouldPositionEachNodeCorrectly() {
      TestNode<Point2D> root = createComplexTree();
      root.adjust();
      int checks = assertTreePositions(root);
      assertEquals(9, checks);
   }

   @Test
   public void testShouldNotCrashWhenAdjustingTree() {
      TestNode<Point2D> root = createBuggedTree();
      root.adjust();
      assertTreePositions(root);
   }

   private int assertTreePositions(TestNode<Point2D> node) {
      Point2D expectedPosition = node.data;
      assertEquals(expectedPosition.getY(), node.y(), 0f);
      assertEquals(expectedPosition.getX(), node.x(), 0f);

      int checks = 1;
      for (TestNode<Point2D> child : node.getChildren()) {
         checks += assertTreePositions(child);
      }
      return checks;
   }

   private TestNode<String> createBinayTree() {
      TestNode<String> root = new TestNode<>("root");
      root.addChild(new TestNode<>("leftChild"));
      root.addChild(new TestNode<>("rightChild"));
      root.adjust();
      return root;
   }

   private TestNode<Point2D> createComplexTree() {
      TestNode<Point2D> root = new TestNode<Point2D>(new Point2D.Double(6.0, 0.0));
      TestNode<Point2D> leftChild = new TestNode<Point2D>(new Point2D.Double(2.0, 1.0));
      TestNode<Point2D> leftChildChild = new TestNode<Point2D>(new Point2D.Double(0.0, 2.0));
      TestNode<Point2D> leftChildChild2 = new TestNode<Point2D>(new Point2D.Double(2.0, 2.0));
      TestNode<Point2D> leftChildChild3 = new TestNode<Point2D>(new Point2D.Double(4.0, 2.0));
      TestNode<Point2D> centerChild = new TestNode<Point2D>(new Point2D.Double(6.0, 1.0));
      TestNode<Point2D> centerChildChild = new TestNode<Point2D>(new Point2D.Double(6.0, 2.0));
      TestNode<Point2D> centerRightChild = new TestNode<Point2D>(new Point2D.Double(8.0, 1.0));
      TestNode<Point2D> rightChild = new TestNode<Point2D>(new Point2D.Double(10.0, 1.0));

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

   private static TestNode<Point2D> createBuggedTree() {
      TestNode<Point2D> root = new TestNode<>(new Point2D.Double(5, 0));
      TestNode<Point2D> leftChild = new TestNode<>(new Point2D.Double(2, 1));
      TestNode<Point2D> leftChildChild = new TestNode<>(new Point2D.Double(0, 2));
      TestNode<Point2D> leftChildChild2 = new TestNode<>(new Point2D.Double(2, 2));
      TestNode<Point2D> leftChildChild3 = new TestNode<>(new Point2D.Double(4, 2));
      TestNode<Point2D> rightChild = new TestNode<>(new Point2D.Double(8, 1));
      TestNode<Point2D> rightChildChild = new TestNode<>(new Point2D.Double(6, 2));
      TestNode<Point2D> rightChildChild2 = new TestNode<>(new Point2D.Double(8, 2));
      TestNode<Point2D> rightChildChild3 = new TestNode<>(new Point2D.Double(10, 2));
      root.addChild(leftChild);
      root.addChild(rightChild);
      leftChild.addChild(leftChildChild);
      leftChild.addChild(leftChildChild2);
      leftChild.addChild(leftChildChild3);
      rightChild.addChild(rightChildChild);
      rightChild.addChild(rightChildChild2);
      rightChild.addChild(rightChildChild3);

      leftChildChild2.addChild(new TestNode<Point2D>(new Point2D.Double(2, 3)));
      rightChildChild2.addChild(new TestNode<Point2D>(new Point2D.Double(8, 3)));
      return root;

   }
}
