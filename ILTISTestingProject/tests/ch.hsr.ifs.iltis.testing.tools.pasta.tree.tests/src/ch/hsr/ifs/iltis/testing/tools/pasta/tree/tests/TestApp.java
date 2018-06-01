package ch.hsr.ifs.iltis.testing.tools.pasta.tree.tests;

import javax.swing.JFrame;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.JBaum;
import ch.hsr.ifs.iltis.testing.tools.pasta.tree.TreeNode;


public class TestApp {

   public static void main(String[] args) {

      TreeNode<String> root = new TreeNode<String>("root");
      JBaum.adjustTree(root, 1, 1);
      JFrame jFrame = new JFrame("JBaumDebug");
      jFrame.add(new JBaumDebugView(root));
      jFrame.pack();
      jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      jFrame.setVisible(true);

   }
}
