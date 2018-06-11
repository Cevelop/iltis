package ch.hsr.ifs.iltis.testing.tools.pasta.tree.tests;

import ch.hsr.ifs.iltis.testing.tools.pasta.tree.TreeNode;


class TestNode<Type> extends TreeNode<Type, TestNode<Type>> {

   public TestNode(Type data) {
      super(data);
      this.treatAsLeaf = false;
   }

}
