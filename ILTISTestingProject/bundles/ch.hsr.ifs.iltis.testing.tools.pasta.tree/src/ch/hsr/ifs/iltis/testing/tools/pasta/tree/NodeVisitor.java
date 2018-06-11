package ch.hsr.ifs.iltis.testing.tools.pasta.tree;

public interface NodeVisitor<TypeName, SELF extends TreeNode<TypeName, SELF>> {

   public enum AfterVisitBehaviour {
      Continue, Abort
   }

   AfterVisitBehaviour visit(SELF node);

}
