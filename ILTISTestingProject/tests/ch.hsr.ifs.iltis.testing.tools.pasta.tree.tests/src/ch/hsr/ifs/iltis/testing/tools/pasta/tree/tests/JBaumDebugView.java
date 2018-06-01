package ch.hsr.ifs.iltis.testing.tools.pasta.tree.tests;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.JBaum;
import ch.hsr.ifs.iltis.testing.tools.pasta.tree.TreeNode;
import ch.hsr.ifs.iltis.testing.tools.pasta.tree.NodeVisitor;


public class JBaumDebugView extends JPanel {

   private static final long serialVersionUID = -1390313572685631394L;
   private final int         xOffset          = 20;
   private final int         yOffset          = 20;
   private final int         nodeWidth        = 20;

   private final TreeNode<String> root;

   public JBaumDebugView(final TreeNode<String> root) {
      super();
      this.root = root;
   }

   @Override
   protected void paintComponent(final Graphics g) {
      super.paintComponent(g);
      this.removeAll();

      root.visit(new NodeVisitor<String>() {

         @Override
         public AfterVisitBehaviour visit(TreeNode<String> node) {
            if (node.parent() != null) {
               drawLineToParent(node, g);
            }
            drawNode(node, g);
            return AfterVisitBehaviour.Continue;
         }
      });
   }

   private int getYCoord(TreeNode<?> node) {
      return (int) (node.y() * nodeWidth) + yOffset;
   }

   private int getXCoord(TreeNode<?> node) {
      return (int) (node.x() * nodeWidth) + xOffset;
   }

   private void drawLineToParent(TreeNode<?> node, Graphics g) {
      g.setColor(Color.GRAY);
      int relativeOffsetX = (int) (node.width() * nodeWidth) / 2;
      int relativeOffsetXParent = (int) (node.parent().width() * nodeWidth) / 2;
      int relativeOffsetY = nodeWidth / 2;
      int sourceX = getXCoord(node) + relativeOffsetX;
      int sourceY = getYCoord(node) + relativeOffsetY;
      int targetX = getXCoord(node.parent()) + relativeOffsetXParent;
      int targetY = getYCoord(node.parent()) + relativeOffsetY;
      g.drawLine(sourceX, sourceY, targetX, targetY);
   }

   private void drawNode(final TreeNode<String> node, Graphics g) {
      g.setColor(Color.ORANGE);
      JButton button = new JButton();
      button.setEnabled(false);
      button.setText("w:" + node.width() + " x:" + node.x());
      button.setBounds(getXCoord(node), getYCoord(node), (int) node.width() * nodeWidth, nodeWidth);

      button.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
               addChild(node);
            }
         }

      });
      this.add(button);
   }

   private void addChild(final TreeNode<String> node) {
      TreeNode<String> child = new TreeNode<String>("child " + (node.children().size() + 1));
      child.setWidth(new Random(System.currentTimeMillis()).nextInt(5) + 1);
      node.addChild(child);
      JBaum.reset(root);
      root.adjust();
      repaint();
   }

   @Override
   public Dimension getPreferredSize() {
      return new Dimension(600, 400);
   }
}
