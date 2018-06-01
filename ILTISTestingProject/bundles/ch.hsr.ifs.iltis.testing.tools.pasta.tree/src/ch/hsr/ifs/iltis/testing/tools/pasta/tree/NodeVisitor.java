package ch.hsr.ifs.iltis.testing.tools.pasta.tree;

public interface NodeVisitor<T> {

   public enum AfterVisitBehaviour {
      Continue, Abort

   }

   AfterVisitBehaviour visit(TreeNode<T> node);

}
